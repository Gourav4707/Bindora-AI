package com.example.data.model

import java.text.DecimalFormat

object AICoachEngine {

    private val df = DecimalFormat("#,##0")

    fun generateCoachingResponse(
        query: String,
        profile: UserProfileEntity?,
        totalExpensesThisMonth: Double,
        totalIncomeThisMonth: Double,
        budget: BudgetEntity?,
        topExpenseCategory: String?,
        topCategoryAmount: Double,
        goals: List<SavingsGoalEntity>
    ): String {
        val q = query.trim().lowercase()

        val monthlyBudget = budget?.totalLimit ?: (profile?.monthlyPocketMoney ?: 5000.0)
        val remainingBudget = (monthlyBudget - totalExpensesThisMonth).coerceAtLeast(0.0)
        val budgetPercentUsed = if (monthlyBudget > 0) ((totalExpensesThisMonth / monthlyBudget) * 100).toInt() else 0

        val topCategoryStr = topExpenseCategory ?: "Food & Canteen"
        val topCategoryAmtStr = "₹${df.format(topCategoryAmount)}"

        val goalsSummary = if (goals.isNotEmpty()) {
            goals.joinToString(", ") { "${it.title} (₹${df.format(it.savedAmount)}/₹${df.format(it.targetAmount)})" }
        } else {
            "koi active goal nahi hai abhi"
        }

        // 1. Save Pocket Money / Saving advice
        if (q.contains("save") || q.contains("bacha") || q.contains("bachte") || q.contains("bachau") || q.contains("pocket money")) {
            return """
                🎯 **Pocket Money Saving Blueprint (Student Guide)**

                Aapka current monthly budget **₹${df.format(monthlyBudget)}** hai, jisme se abhi tak **₹${df.format(totalExpensesThisMonth)}** spend ho chuka hai (yani ${budgetPercentUsed}% used).

                💡 **Step-by-Step Action Plan:**
                1. **Pay Yourself First (20% Rule):** Jaise hi parents se pocket money aaye, turant minimum 15-20% (lagbhag ₹${df.format(monthlyBudget * 0.2)}) kisi alag account ya liquid wallet me transfer kar dein.
                2. **Daily Spending Cap:** Remaining balance ko 30 din me divide karein. Aapka safe daily spending limit around **₹${df.format(remainingBudget / 25)}/day** hona chahiye.
                3. **Audit Top Category:** Aapka sabse bada expense **$topCategoryStr** ($topCategoryAmtStr) me ho raha hai. Isko 15% kam karne se mahine ke ₹500–₹800 easily bach jayenge!

                📌 *Pro Tip:* Active goal: $goalsSummary. Thoda sa sacrifice karke roz ₹30 bachaoge toh 3 mahine me emergency buffer ready ho jayega!
            """.trimIndent()
        }

        // 2. Food / Canteen budget getting exhausted quickly
        if (q.contains("food") || q.contains("canteen") || q.contains("khana") || q.contains("swiggy") || q.contains("zomato") || q.contains("jaldi")) {
            return """
                🍔 **Hostel & Canteen Kharcha Control Guide**

                Aapne notice kiya hoga ki canteen aur midnight snacks ka kharcha silently pocket money khatam kar deta hai!

                📊 **Aapka Current Data:**
                - Highest expense area: **$topCategoryStr** ($topCategoryAmtStr spent this month).
                - Overall budget consumed: **${budgetPercentUsed}%**.

                🛠️ **3 Practical Hacks for Hostelers & Day Scholars:**
                1. **The 3-Day Swiggy Rule:** Online food ordering ko hafte me sirf ek fixed din (e.g., Saturday night) tak limit karein.
                2. **Pre-stocked Mess Snacks:** Midnight study hunger ke liye pehle se wholesale me biscuits, peanuts, aur oats hostel room me rakhein, canteen chips ke bajaye.
                3. **Chai & Samosa Cap:** Roz do baar canteen jane ke bajaye room kettle me chai banayein—saving ₹400/month!

                ⚠️ *Disclaimer:* Healthy mess diet ko skip mat karna; sirf discretionary impulse snacking ko cut karna hai.
            """.trimIndent()
        }

        // 3. SIP (Systematic Investment Plan) & Compounding
        if (q.contains("sip") || q.contains("compound") || q.contains("invest") || q.contains("mutual fund")) {
            return """
                📈 **SIP (Systematic Investment Plan) Explained for Students**

                **SIP kya hota hai?**
                SIP ek tareeka hai jisme aap har mahine ek fixed choti amount (jaise ₹250 ya ₹500) kisi diversified Index Mutual Fund me auto-debit ke through invest karte hain.

                🚀 **Power of Compounding Example:**
                Agar aap college 1st year (age 19) se sirf **₹500/month** NIFTY 50 Index Fund me dalte hain:
                - 10 saal me aapka total investment: **₹60,000**
                - 12% average annual return par value: **~₹1,16,000+**
                - 20 saal me yahi value: **~₹5,00,000+** ban sakti hai!

                ✅ **Rules for Beginners:**
                - Emergency fund (₹3,000) banne se pehle SIP start na karein.
                - NIFTY 50 Direct Plan chunein jisme lowest expense ratio hota hai.
                - Har mahine ki 5 tarikh ko auto-SIP set karein.
            """.trimIndent()
        }

        // 4. Education Loan & Moratorium
        if (q.contains("loan") || q.contains("emi") || q.contains("moratorium") || q.contains("interest")) {
            return """
                🎓 **Education Loan & Moratorium Strategy**

                **Moratorium Period Kya Hai?**
                Ye aapke course ki duration (jaise 4 saal B.Tech) + 6 mahine se 1 saal ka grace period hota hai jisme bank aap se full EMI demand nahi karta.

                ⚠️ **The Hidden Trap:**
                Moratorium me EMI nahi bharni hoti, lekin **interest accrue (judta) rehta hai**! Agar 8 Lakh ke loan par interest nahi diya, toh graduation tak principal 10 Lakh+ ho sakta hai.

                💡 **Student Pro-Tips:**
                1. **Simple Interest Servicing:** Agar aap internship stipend se sirf monthly simple interest (around ₹2,500) bhar dete hain, toh banks 1% concession dete hain!
                2. **Section 80E Tax Deduction:** Job lagne ke baad 8 saal tak loan interest par 100% income tax exemption milta hai.
                3. App ke *Loan Calculator* tab me jakar aap apna exact EMI aur moratorium schedule test kar sakte hain!
            """.trimIndent()
        }

        // 5. Credit Score / CIBIL
        if (q.contains("credit") || q.contains("cibil") || q.contains("score") || q.contains("card")) {
            return """
                💳 **Credit Score & CIBIL Demystified for College Students**

                **CIBIL Score Kya Hai?**
                Ye 300 se 900 ke beech ka 3-digit score hota hai jo batata hai ki aap udhar wapis karne me kitne reliable hain. **750+ score prime mana jata hai.**

                🛡️ **How to Build Score Safely:**
                - Students directly unsecured credit card nahi le sakte bina salary slip ke.
                - **FD-Backed Credit Card:** Apne bank me ₹10,000 ki FD banakar ek secure card le sakte hain.
                - **The Golden Rule:** Hamesha Total Bill 100% pay karein. Kabhi sirf 'Minimum Due' pay mat karna (36-42% interest lagta hai!).

                🚫 **Warning:** Kisi bhi sketchy instant lending ya Chinese student loan app se borrow mat karna—ye aapka fresh CIBIL score destroy kar dete hain.
            """.trimIndent()
        }

        // 6. 50/30/20 Rule
        if (q.contains("50/30/20") || q.contains("rule") || q.contains("budget")) {
            return """
                ⚖️ **The 50/30/20 Budgeting Rule for College Life**

                Jab bhi pocket money ya stipend aaye, usko 3 hisso me baantein:

                1. **50% Needs (Zarooratein):** Mess fees, hostel rent, metro pass, notebook xerox, and mobile recharge.
                2. **30% Wants (Shaukh):** Canteen samosa, weekend movie with friends, birthday treats, and shopping.
                3. **20% Savings (Suraksha):** Emergency reserve fund aur future goals ke liye!

                📊 **Aapke Pocket Money (₹${df.format(monthlyBudget)}) ke hisaab se:**
                - Needs: ₹${df.format(monthlyBudget * 0.50)}
                - Wants: ₹${df.format(monthlyBudget * 0.30)}
                - Savings: ₹${df.format(monthlyBudget * 0.20)}

                Aapne abhi tak **₹${df.format(totalExpensesThisMonth)}** spend kiya hai, jisme se bacha hua budget **₹${df.format(remainingBudget)}** hai.
            """.trimIndent()
        }

        // 7. General fallback
        return """
            🤖 **Bindora AI Coach Advice**

            Aapne pucha: *"$query"*

            Aapke current financial overview ke anusar:
            - Monthly Budget: **₹${df.format(monthlyBudget)}**
            - Spent this month: **₹${df.format(totalExpensesThisMonth)}** (${budgetPercentUsed}%)
            - Bacha hua balance: **₹${df.format(remainingBudget)}**
            - Top category: **$topCategoryStr** ($topCategoryAmtStr)

            💡 **Recommendation:**
            Har financial decision me 24-Hour Rule apply karein. Agar koi kharcha zaroori nahi hai toh agle din tak ruk kar sochein.

            Aap mujhse puch sakte hain:
            • "Canteen kharcha kaise kam karun?"
            • "₹500 se SIP kaise shuru karein?"
            • "Education loan par interest calculation samjhao"
            • "CIBIL score kaise banayein?"
        """.trimIndent()
    }
}
