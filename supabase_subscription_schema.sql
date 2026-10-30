-- ==============================================================================
-- BINDORA PREMIUM SUBSCRIPTIONS & UPI PAYMENT VERIFICATION SCHEMA
-- ==============================================================================
-- Application: Bindora — Every Rupee Has a Purpose
-- Database: Supabase PostgreSQL with Row Level Security (RLS)
-- Description: Manages subscription tiers, unique payment orders, payment events,
--              idempotent server-side payment verification, and user entitlements.
-- ==============================================================================

-- 1. SUBSCRIPTION PLANS TABLE
CREATE TABLE IF NOT EXISTS public.subscription_plans (
    id TEXT PRIMARY KEY,                       -- e.g. 'bindora_plus', 'bindora_pro'
    name TEXT NOT NULL,                        -- e.g. 'Bindora Plus'
    slug TEXT NOT NULL UNIQUE,
    tagline TEXT,
    price_monthly NUMERIC(10, 2) NOT NULL,     -- 99.00
    price_annual NUMERIC(10, 2) NOT NULL,      -- 799.00
    currency TEXT DEFAULT 'INR',
    features JSONB NOT NULL DEFAULT '[]'::jsonb,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- Seed initial plans if empty
INSERT INTO public.subscription_plans (id, name, slug, tagline, price_monthly, price_annual, features, is_active)
VALUES
(
    'bindora_plus',
    'Bindora Plus',
    'plus',
    'Smart Budgeting & AI Insights for College Life',
    99.00,
    799.00,
    '[
        "Unlimited AI Coach questions and personalized advice",
        "Multi-bank statement auto-categorization & OCR",
        "Up to 10 active savings goals with visual tracking",
        "Smart overspending and mess allowance alerts",
        "Ad-free premium experience"
    ]'::jsonb,
    true
),
(
    'bindora_pro',
    'Bindora Pro',
    'pro',
    'Ultimate Financial Mastery & Career Wealth Suite',
    199.00,
    1499.00,
    '[
        "Everything in Plus included",
        "Advanced paper trading with live market depth simulation",
        "Education loan repayment & moratorium optimization",
        "Export audited tax-ready expense reports (PDF/Excel)",
        "Priority 1-on-1 fintech mentoring webinars",
        "Exclusive student investor community badge"
    ]'::jsonb,
    true
)
ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    price_monthly = EXCLUDED.price_monthly,
    price_annual = EXCLUDED.price_annual,
    features = EXCLUDED.features;

-- 2. PAYMENT ORDERS TABLE
CREATE TABLE IF NOT EXISTS public.payment_orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_number TEXT NOT NULL UNIQUE,          -- e.g. 'BND-SUB-20261030-9921'
    user_id UUID REFERENCES auth.users(id) ON DELETE CASCADE NOT NULL,
    plan_id TEXT REFERENCES public.subscription_plans(id) NOT NULL,
    plan_name TEXT NOT NULL,
    billing_cycle TEXT NOT NULL CHECK (billing_cycle IN ('MONTHLY', 'ANNUAL')),
    base_amount NUMERIC(10, 2) NOT NULL,
    gst_rate NUMERIC(5, 4) DEFAULT 0.1800,      -- 18% GST
    gst_amount NUMERIC(10, 2) NOT NULL,
    total_amount NUMERIC(10, 2) NOT NULL,
    currency TEXT DEFAULT 'INR' NOT NULL,
    status TEXT NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'SUCCESS', 'FAILED', 'CANCELLED', 'REFUNDED')),
    payment_method TEXT DEFAULT 'UPI' NOT NULL,
    merchant_vpa TEXT DEFAULT 'bindora.pay@razorpay' NOT NULL,
    merchant_name TEXT DEFAULT 'Bindora Technologies Pvt Ltd' NOT NULL,
    upi_txn_ref TEXT,                           -- NPCI Bank RRN / UTR
    provider_order_id TEXT,                     -- Gateway order id (e.g. Razorpay/Cashfree order ID)
    failure_reason TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    completed_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_payment_orders_user_id ON public.payment_orders(user_id);
CREATE INDEX IF NOT EXISTS idx_payment_orders_status ON public.payment_orders(status);
CREATE INDEX IF NOT EXISTS idx_payment_orders_order_number ON public.payment_orders(order_number);

-- 3. USER SUBSCRIPTIONS TABLE
CREATE TABLE IF NOT EXISTS public.user_subscriptions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES auth.users(id) ON DELETE CASCADE NOT NULL UNIQUE,
    plan_id TEXT REFERENCES public.subscription_plans(id) NOT NULL,
    plan_name TEXT NOT NULL,
    billing_cycle TEXT NOT NULL CHECK (billing_cycle IN ('MONTHLY', 'ANNUAL')),
    status TEXT NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'PENDING', 'CANCELLED', 'EXPIRED')),
    starts_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    auto_renew BOOLEAN DEFAULT false NOT NULL,  -- Explicitly disclosed fixed-term manual renewal
    last_order_id UUID REFERENCES public.payment_orders(id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_user_subscriptions_user_id ON public.user_subscriptions(user_id);
CREATE INDEX IF NOT EXISTS idx_user_subscriptions_status ON public.user_subscriptions(status);

-- 4. PAYMENT AUDIT & WEBHOOK EVENTS TABLE (Idempotency)
CREATE TABLE IF NOT EXISTS public.payment_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID REFERENCES public.payment_orders(id) ON DELETE CASCADE,
    event_type TEXT NOT NULL,                   -- e.g. 'UPI_INTENT_DISPATCHED', 'PAYMENT_VERIFIED', 'WEBHOOK_PROCESSED'
    idempotency_key TEXT UNIQUE,                -- Prevents duplicate execution
    payload JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- ==============================================================================
-- ROW LEVEL SECURITY (RLS) POLICIES
-- ==============================================================================
ALTER TABLE public.subscription_plans ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.payment_orders ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.user_subscriptions ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.payment_events ENABLE ROW LEVEL SECURITY;

-- Plans are publicly readable by all authenticated and anonymous clients
CREATE POLICY "Public plans are viewable by everyone"
    ON public.subscription_plans FOR SELECT
    USING (is_active = true);

-- Orders: Users can view their own orders only. They can insert a PENDING order for checkout.
CREATE POLICY "Users can view their own orders"
    ON public.payment_orders FOR SELECT
    USING (auth.uid() = user_id);

CREATE POLICY "Users can create pending orders for themselves"
    ON public.payment_orders FOR INSERT
    WITH CHECK (auth.uid() = user_id AND status = 'PENDING');

-- Users can cancel their own pending order
CREATE POLICY "Users can cancel their own pending order"
    ON public.payment_orders FOR UPDATE
    USING (auth.uid() = user_id AND status = 'PENDING')
    WITH CHECK (auth.uid() = user_id AND status = 'CANCELLED');

-- Subscriptions: Users can only read their own subscription
CREATE POLICY "Users can view their own subscription"
    ON public.user_subscriptions FOR SELECT
    USING (auth.uid() = user_id);

-- Payment events: Service role only (internal audit log)
CREATE POLICY "Users cannot view payment events"
    ON public.payment_events FOR SELECT
    USING (false);

-- ==============================================================================
-- SERVER-SIDE PAYMENT VERIFICATION & ENTITLEMENT ACTIVATION (Idempotent RPC)
-- ==============================================================================
-- This function runs with SECURITY DEFINER to securely elevate privileges
-- when verified via trusted provider webhook or server API check.
CREATE OR REPLACE FUNCTION public.verify_and_activate_subscription(
    p_order_number TEXT,
    p_upi_txn_ref TEXT,
    p_provider_order_id TEXT DEFAULT NULL,
    p_idempotency_key TEXT DEFAULT NULL
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
    v_order RECORD;
    v_duration_days INT;
    v_expires_at TIMESTAMP WITH TIME ZONE;
    v_existing_sub RECORD;
BEGIN
    -- 1. Locate the pending order
    SELECT * INTO v_order
    FROM public.payment_orders
    WHERE order_number = p_order_number
    FOR UPDATE;

    IF NOT FOUND THEN
        RETURN jsonb_build_object('success', false, 'message', 'Order not found');
    END IF;

    -- 2. Idempotency Check: if already completed, return existing success state
    IF v_order.status = 'SUCCESS' THEN
        RETURN jsonb_build_object(
            'success', true,
            'message', 'Order was already processed successfully (Idempotent)',
            'order_number', v_order.order_number,
            'plan_id', v_order.plan_id
        );
    END IF;

    IF v_order.status != 'PENDING' THEN
        RETURN jsonb_build_object('success', false, 'message', 'Order is not in pending state');
    END IF;

    -- 3. Check order expiration (15-minute checkout window)
    IF v_order.expires_at < timezone('utc'::text, now()) THEN
        UPDATE public.payment_orders
        SET status = 'FAILED', failure_reason = 'Order expired before payment was completed'
        WHERE id = v_order.id;
        RETURN jsonb_build_object('success', false, 'message', 'Checkout window expired');
    END IF;

    -- 4. Calculate subscription duration
    IF v_order.billing_cycle = 'ANNUAL' THEN
        v_duration_days := 365;
    ELSE
        v_duration_days := 30;
    END IF;

    -- 5. Calculate new expiration (extend from current expiry if user already has active subscription)
    SELECT * INTO v_existing_sub
    FROM public.user_subscriptions
    WHERE user_id = v_order.user_id AND status = 'ACTIVE' AND expires_at > timezone('utc'::text, now());

    IF FOUND THEN
        v_expires_at := v_existing_sub.expires_at + (v_duration_days || ' days')::INTERVAL;
    ELSE
        v_expires_at := timezone('utc'::text, now()) + (v_duration_days || ' days')::INTERVAL;
    END IF;

    -- 6. Mark order as SUCCESS
    UPDATE public.payment_orders
    SET
        status = 'SUCCESS',
        upi_txn_ref = p_upi_txn_ref,
        provider_order_id = COALESCE(p_provider_order_id, v_order.provider_order_id),
        completed_at = timezone('utc'::text, now())
    WHERE id = v_order.id;

    -- 7. Upsert user subscription
    INSERT INTO public.user_subscriptions (
        user_id,
        plan_id,
        plan_name,
        billing_cycle,
        status,
        starts_at,
        expires_at,
        auto_renew,
        last_order_id,
        updated_at
    )
    VALUES (
        v_order.user_id,
        v_order.plan_id,
        v_order.plan_name,
        v_order.billing_cycle,
        'ACTIVE',
        timezone('utc'::text, now()),
        v_expires_at,
        false,
        v_order.id,
        timezone('utc'::text, now())
    )
    ON CONFLICT (user_id) DO UPDATE SET
        plan_id = EXCLUDED.plan_id,
        plan_name = EXCLUDED.plan_name,
        billing_cycle = EXCLUDED.billing_cycle,
        status = 'ACTIVE',
        expires_at = EXCLUDED.expires_at,
        last_order_id = EXCLUDED.last_order_id,
        updated_at = timezone('utc'::text, now());

    -- 8. Record audit log event
    INSERT INTO public.payment_events (
        order_id,
        event_type,
        idempotency_key,
        payload
    )
    VALUES (
        v_order.id,
        'PAYMENT_VERIFIED_AND_ACTIVATED',
        COALESCE(p_idempotency_key, 'EVT_' || v_order.order_number),
        jsonb_build_object(
            'order_number', v_order.order_number,
            'amount', v_order.total_amount,
            'plan_id', v_order.plan_id,
            'upi_txn_ref', p_upi_txn_ref,
            'expires_at', v_expires_at
        )
    );

    RETURN jsonb_build_object(
        'success', true,
        'message', 'Subscription activated successfully',
        'order_number', v_order.order_number,
        'plan_id', v_order.plan_id,
        'plan_name', v_order.plan_name,
        'expires_at', v_expires_at
    );
END;
$$;
