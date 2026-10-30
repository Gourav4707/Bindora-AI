# Bindora (Android)

**Bindora** — *Every Rupee Has a Purpose.*

Bindora is an AI-powered personal finance and money management application designed specifically for Indian college students, hostel residents, freshers, and young adults aged 16–23.

---

## 🎨 Visual Identity & Design System

- **Primary Dark Teal**: `#064E46`
- **Primary Green**: `#16A34A`
- **Accent Green**: `#65C466`
- **Background**: `#F8FAFC`
- **Card Background**: `#FFFFFF`
- **Primary Text**: `#16332F`
- **Secondary Text**: `#64748B`
- **Borders**: `#E2E8F0`

---

## 🌟 Core Features

1. **Automatic Transaction Ingestion & Tracking**:
   - Provider-independent tracking architecture.
   - **Local Payment SMS & UPI Notification Listener**: Parses Indian bank SMS (SBI, HDFC, ICICI, Axis, PNB, Kotak, BOB) and UPI notifications (Google Pay, PhonePe, Paytm).
   - **CSV Bank Statement Parser**: Parses standard CSV account statements.
   - **Local Deduplication & Merchant Memory**: Rule-based normalization, fuzzy similarity, and custom user memory overrides.
   - 100% on-device parsing — no credentials, passwords, or bank logins ever requested.

2. **Pocket Money & Expense Tracking**:
   - Multi-category tracking: Food & Canteen, Groceries, Travel & Metro, Books & Stationery, Hostel & Rent, Mobile & Internet, Entertainment, and Health.
   - Income tracking: Pocket Money, Freelance / Stipends, and Scholarships.
   - Payment modes: UPI, Cash, and Card.

3. **Monthly Budgets & Alert Thresholds**:
   - Customizable category spending limits.
   - Smart alert thresholds: 50% warning, 80% threshold, and 100% exceeded.
   - Deterministic insights engine calculating days left in the month and daily safe spending allowances.

4. **Savings Goals & Contributions**:
   - Target goal tracking (e.g. "Hostel Emergency Buffer", "Coding Laptop", "Goa Trip").
   - Real-time progress bars and remaining contribution amounts.

5. **Bindora AI Coach**:
   - Conversational AI coach supporting **Hindi, English, and Hinglish**.
   - Context-aware answers integrating the student's actual current month spending, budget balance, and top expense categories.

6. **Deterministic Education Loan Calculator**:
   - Monthly EMI using standard amortization formula.
   - Moratorium period modeling (course period + grace period).
   - Simple interest vs compound interest during moratorium.
   - Section 80E tax deduction guidance.

7. **Financial Education Academy**:
   - 14 structured modules tailored to Indian students with interactive quizzes and progress tracking.

8. **Paper Trading Stock Simulator**:
   - ₹1,00,000 virtual starting capital for risk-free learning.
   - Top simulated Indian benchmark & equities (NIFTY 50, Reliance, TCS, Infosys, Tata Motors, HDFC Bank, Zomato, ITC).

---

## 🛠️ Architecture & Tech Stack

- **Platform**: Native Android (Kotlin)
- **UI Framework**: Jetpack Compose with Material Design 3 (M3)
- **State Management**: Android Architecture Components `ViewModel` + Kotlin `StateFlow`
- **Local Persistence**: Android Jetpack `Room Database` (SQLite persistence across app restarts)
- **Theme**: BindoraTheme with official fintech design system tokens

---

## 🚀 Building & Testing

- **Compile App**:  
  `compile_applet` or `gradle assembleDebug`

- **Run Local JVM Tests**:  
  `gradle :app:testDebugUnitTest`

---

## 🔒 Privacy & Educational Disclaimers

- Bindora is strictly an educational financial literacy and personal budgeting companion.
- It does not process actual bank transfers, UPI execution, or hold real funds.
- All transaction records and profile settings remain private and encrypted in the device's local Room database.
