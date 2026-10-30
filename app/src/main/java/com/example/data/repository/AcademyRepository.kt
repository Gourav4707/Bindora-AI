package com.example.data.repository

import com.example.data.model.AcademyLesson
import com.example.data.model.QuizQuestion

object AcademyRepository {
    val LESSONS: List<AcademyLesson> = listOf(
        AcademyLesson(
            id = "lesson_1",
            title = "1. Budgeting Basics: The Student 50/30/20 Rule",
            readingTime = "4 min",
            studentTag = "Pocket Money",
            summary = "Master the art of splitting your monthly pocket money without surviving on instant noodles at month-end.",
            content = "The 50/30/20 rule is the gold standard of personal budgeting, adjusted for college life. When your parents send your monthly pocket money or you receive your internship stipend, divide it into three distinct buckets:\n\n• 50% for Needs: Mess food, hostel/room rent, daily metro/bus tickets, essential stationery, and phone recharge.\n• 30% for Wants: Weekend outings, cafeteria snacks, Netflix/Spotify subscriptions, and shopping.\n• 20% for Savings: Emergency cash buffer, savings goals (like a new laptop or semester project), or micro-investing.\n\nBy allocating money upfront, you eliminate the constant anxiety of running out of money in the last week of the month.",
            studentExample = "Rahul receives ₹4,000 every month from his parents. Following 50/30/20:\n- ₹2,000 (50%) covers his metro pass and hostel mess dues.\n- ₹1,200 (30%) is reserved for weekend momos, chai, and movies with friends.\n- ₹800 (20%) goes straight into his emergency savings jar.",
            keyTakeaways = listOf(
                "Pay yourself first: Put 20% into savings the day you get your pocket money.",
                "Track small leaks: Two daily ₹20 cutting chais add up to ₹1,200 per month.",
                "Keep separate accounts or digital wallets for wants vs needs."
            ),
            quiz = listOf(
                QuizQuestion(
                    question = "If you receive ₹5,000 monthly pocket money, how much should go into savings under the 50/30/20 rule?",
                    options = listOf("₹500", "₹1,000", "₹2,500", "₹1,500"),
                    correctIndex = 1,
                    explanation = "20% of ₹5,000 is ₹1,000 (0.20 × 5,000 = ₹1,000)."
                ),
                QuizQuestion(
                    question = "Which category does your monthly mobile data recharge belong to?",
                    options = listOf("Wants", "Needs", "Savings", "Investment"),
                    correctIndex = 1,
                    explanation = "Mobile and internet connection are essential for college lectures, assignments, and safety, so they count as a Need."
                )
            )
        ),
        AcademyLesson(
            id = "lesson_2",
            title = "2. Needs vs. Wants: The Canteen Reality Check",
            readingTime = "3 min",
            studentTag = "Mindful Spending",
            summary = "Learn how to identify subconscious impulse spending and maintain a comfortable hostel lifestyle.",
            content = "A Need is something essential for health, safety, and basic college survival (mess food, medicine, commute, textbooks). A Want is something that improves comfort or entertainment (ordering pizza at midnight, branded sneakers, luxury cafe coffee).\n\nHostel life often blurs this line: ordering Swiggy because mess food is bland feels like a need, but it's fundamentally a want. The secret isn't giving up wants entirely—it's using the 24-Hour Rule for discretionary purchases.",
            studentExample = "Priya wanted to buy a ₹2,400 wireless earbud set. She waited 48 hours instead of buying impulsively. She realized her existing wired earphones worked fine, saving her two weeks of pocket money.",
            keyTakeaways = listOf(
                "Use the 24-Hour Rule: Wait 24 to 48 hours before any non-essential purchase over ₹500.",
                "Differentiate between hunger (mess food) and cravings (Zomato burger).",
                "Rewarding yourself is healthy, but schedule it intentionally."
            ),
            quiz = listOf(
                QuizQuestion(
                    question = "What is the 24-Hour Rule designed to prevent?",
                    options = listOf("Bank transaction fees", "Impulse purchases", "UPI fraud", "Delayed assignments"),
                    correctIndex = 1,
                    explanation = "Waiting 24 hours cools down emotional impulse buying and allows rational consideration."
                )
            )
        ),
        AcademyLesson(
            id = "lesson_3",
            title = "3. Emergency Funds for Students",
            readingTime = "4 min",
            studentTag = "Safety Net",
            summary = "Why every college student needs an untouchable ₹3,000 to ₹5,000 emergency fund.",
            content = "Life happens: your smartphone screen shatters before exams, your bicycle gets a flat tire, you fall sick and need antibiotics, or your semester project requires urgent prints. An emergency fund keeps you from borrowing money from friends or frantically calling home in panic.\n\nStart small: saving just ₹200 every week builds a ₹1,000 safety cushion in 5 weeks. Keep this fund in a high-yield savings account or liquid fund with instant UPI access, but completely separate from your daily spending balance.",
            studentExample = "Amit accumulated ₹3,500 in his Emergency Jar. During midterm exams, his laptop charger burned out. He replaced it immediately for ₹1,800 without stress.",
            keyTakeaways = listOf(
                "Aim for a student emergency fund of ₹3,000 to ₹5,000.",
                "Only tap into it for genuine emergencies: medical, urgent repairs, or critical academic needs.",
                "Replenish the fund immediately after using it."
            ),
            quiz = listOf(
                QuizQuestion(
                    question = "Which scenario justifies dipping into your student emergency fund?",
                    options = listOf("A flash sale on Myntra", "Buying concert tickets with roommates", "Sudden fever requiring doctor visit & medicines", "Trying a new cafe in town"),
                    correctIndex = 2,
                    explanation = "Medical emergencies are genuine unpredictable needs; sales and concerts are planned entertainment wants."
                )
            )
        ),
        AcademyLesson(
            id = "lesson_4",
            title = "4. Inflation: The Invisible Thief",
            readingTime = "3 min",
            studentTag = "Macroeconomics",
            summary = "Understand why ₹100 in first year buys less in your final year, and how inflation works in India.",
            content = "Inflation is the general increase in prices and fall in purchasing value of money over time. In India, retail inflation historically averages between 5% and 7% per year.\n\nIf a plate of canteen Maggi costs ₹40 in your fresher year, at 6% inflation, it will cost around ₹48 to ₹50 by your final year. If your money simply sits under your mattress or in a zero-interest envelope, its real value is decreasing every single year.",
            studentExample = "Ten years ago, a cup of college chai was ₹5. Today it is ₹12 to ₹15. That is inflation at work over a decade.",
            keyTakeaways = listOf(
                "Money stored in cash loses buying power over time.",
                "Bank savings accounts offering 2.7%–3% interest usually fail to beat 6% inflation.",
                "Investing in growth assets is the proven shield against inflation."
            ),
            quiz = listOf(
                QuizQuestion(
                    question = "If annual inflation is 6% and your savings account yields 3%, what happens to your real purchasing power?",
                    options = listOf("It increases by 3%", "It decreases by about 3%", "It stays exactly identical", "It doubles"),
                    correctIndex = 1,
                    explanation = "Real return = Nominal interest (3%) - Inflation (6%) = -3% real loss in purchasing power."
                )
            )
        ),
        AcademyLesson(
            id = "lesson_5",
            title = "5. Simple vs. Compound Interest",
            readingTime = "5 min",
            studentTag = "Wealth Builder",
            summary = "Why Einstein famously called Compound Interest the eighth wonder of the world.",
            content = "Simple Interest (SI) calculates returns only on the original principal amount: SI = (P × R × T) / 100.\n\nCompound Interest (CI) calculates returns on both the principal AND the accumulated interest from previous periods ('interest on interest').\n\nFormula: A = P × (1 + r/n)^(nt).\nBecause the base gets larger each cycle, compounding grows exponentially over time. When you are young (aged 18–22), your greatest asset is TIME.",
            studentExample = "If you invest ₹10,000 at 12% annual return:\n- Under Simple Interest, in 30 years you get ₹46,000 total.\n- Under Compound Interest, in 30 years you get nearly ₹3,00,000! That is 6.5x more wealth from the exact same starting cash.",
            keyTakeaways = listOf(
                "Simple interest is linear; compound interest is exponential.",
                "Starting at age 20 gives you a massive 10-year advantage over someone starting at age 30.",
                "Compounding works both ways: it multiplies investments, but also multiplies credit card debt!"
            ),
            quiz = listOf(
                QuizQuestion(
                    question = "What makes compound interest grow significantly faster than simple interest over decades?",
                    options = listOf("Government subsidies", "Earning interest on previously earned interest", "Zero market risk", "Lower tax rates"),
                    correctIndex = 1,
                    explanation = "Compounding generates returns on both the original capital and accumulated returns."
                )
            )
        ),
        AcademyLesson(
            id = "lesson_6",
            title = "6. Saving vs. Investing",
            readingTime = "4 min",
            studentTag = "Wealth Strategy",
            summary = "Know when to keep money safe in a bank and when to put money to work in the market.",
            content = "Saving is putting aside money in safe, liquid instruments for short-term goals (under 1–3 years) or emergencies. Risk is near zero, but returns are low.\n\nInvesting is putting capital into assets like equities, mutual funds, or gold with the expectation of generating capital appreciation over 3–5+ years. There is short-term market fluctuation risk, but substantially higher inflation-beating potential.",
            studentExample = "College fees due next month? Keep it in Savings. Money saved for retirement or down payment in 7 years? Invest in an Index Fund.",
            keyTakeaways = listOf(
                "Never invest your emergency fund or next month's fee into volatile stocks.",
                "Save for safety and liquidity; invest for long-term growth.",
                "Rule of thumb: Money needed within 12 months belongs in bank deposits."
            ),
            quiz = listOf(
                QuizQuestion(
                    question = "Where should money needed for college tuition in 2 months be kept?",
                    options = listOf("Cryptocurrency", "High-growth small-cap stocks", "Safe bank savings / Fixed deposit", "Paper trading"),
                    correctIndex = 2,
                    explanation = "Short-term critical obligations must never be exposed to market volatility."
                )
            )
        ),
        AcademyLesson(
            id = "lesson_7",
            title = "7. Mutual Funds & Index Funds Demystified",
            readingTime = "5 min",
            studentTag = "Investing",
            summary = "How mutual funds pool money to give students instant diversification across India's top 50 companies.",
            content = "A Mutual Fund pools money from thousands of investors to buy a diversified basket of stocks, bonds, or government securities managed by professional Asset Management Companies (AMCs).\n\nAn Index Fund (like a NIFTY 50 Index Fund) simply mirrors the top 50 largest companies in India (Reliance, TCS, HDFC, Infosys, etc.). Because there is no active fund manager guessing stock picks, the fees (Expense Ratio) are ultra-low (often 0.1%–0.2%), making it ideal for student beginners.",
            studentExample = "Buying 1 share of each of India's top 50 companies individually would require lakhs of rupees. Through a NIFTY 50 Index Fund, you can own a slice of all 50 companies for just ₹100!",
            keyTakeaways = listOf(
                "Diversification reduces the risk of any single company failing.",
                "Look for low Expense Ratio (< 0.5%) for index funds.",
                "Direct Plans are cheaper than Regular Plans because they avoid distributor commissions."
            ),
            quiz = listOf(
                QuizQuestion(
                    question = "Why do NIFTY 50 Index Funds usually have lower fees than actively managed funds?",
                    options = listOf("They are sponsored by the RBI", "They mechanically replicate the index without expensive active management", "They only trade once a year", "They are reserved for students"),
                    correctIndex = 1,
                    explanation = "Index funds track an automated benchmark index, reducing analytical and administrative overhead."
                )
            )
        ),
        AcademyLesson(
            id = "lesson_8",
            title = "8. SIP (Systematic Investment Plan) Basics",
            readingTime = "4 min",
            studentTag = "Investing Habit",
            summary = "The easiest habit: how investing ₹500 every month can create life-changing discipline.",
            content = "A Systematic Investment Plan (SIP) is a method of investing a fixed sum into a mutual fund at regular intervals (usually monthly). You don't need to 'time' the stock market.\n\nRupee Cost Averaging: When markets are high, your ₹500 buys fewer units. When markets dip, your ₹500 buys more units. Over time, your average purchase cost smooths out automatically, removing emotion and fear from investing.",
            studentExample = "Neeraj started a ₹500/month SIP in a NIFTY Index Fund in his 1st year of college. Over 4 years, he invested ₹24,000. Compounded at 12%, it grew to over ₹31,000 by graduation day.",
            keyTakeaways = listOf(
                "Consistency beats timing: Start with ₹100 or ₹500 per month.",
                "Rupee cost averaging protects you during market downturns.",
                "Automating your SIP ensures you never skip a month."
            ),
            quiz = listOf(
                QuizQuestion(
                    question = "What is the primary psychological benefit of an automated SIP?",
                    options = listOf("Guaranteed 50% returns", "Eliminates emotional market timing and builds consistent discipline", "Avoids PAN card requirement", "Permits instant cash withdrawal"),
                    correctIndex = 1,
                    explanation = "SIPs automate investing, helping beginners avoid panic selling during dips or greed buying at peaks."
                )
            )
        ),
        AcademyLesson(
            id = "lesson_9",
            title = "9. Equity Stock Markets: Becoming a Part-Owner",
            readingTime = "5 min",
            studentTag = "Stocks",
            summary = "Understand shares, ticker symbols, dividends, and why stock prices move.",
            content = "When you buy a share of a company (like Tata Motors or Infosys), you become a fractional owner of that business. As the company earns profits, you benefit in two ways:\n1. Capital Appreciation: The market value of each share increases as the company grows.\n2. Dividends: A portion of net profits distributed directly into your bank account.\n\nStock prices fluctuate daily based on supply, demand, corporate earnings, interest rates, and economic news.",
            studentExample = "If you own 10 shares of a company at ₹500 each and the price rises to ₹650 after strong sales, your portfolio increases by ₹1,500 in value.",
            keyTakeaways = listOf(
                "Stocks represent fractional ownership in real commercial enterprises.",
                "Never buy stocks based on anonymous Telegram or WhatsApp 'tips'.",
                "Invest only in businesses whose products and economics you understand."
            ),
            quiz = listOf(
                QuizQuestion(
                    question = "What does purchasing a stock of an Indian company make you?",
                    options = listOf("A bank creditor", "A fractional owner of the company", "An employee of the firm", "A government tax agent"),
                    correctIndex = 1,
                    explanation = "Equity shares represent legal fractional ownership of the corporation."
                )
            )
        ),
        AcademyLesson(
            id = "lesson_10",
            title = "10. Credit Scores & CIBIL: Your Financial Reputation",
            readingTime = "4 min",
            studentTag = "Credit Health",
            summary = "Everything freshers need to know about CIBIL scores (300 to 900) before applying for loans.",
            content = "A Credit Score (like CIBIL in India) is a 3-digit number ranging from 300 to 900 that summarizes how reliably you repay borrowed money. Lenders use it to approve education loans, home loans, and credit cards.\n\n• 750+ is considered Excellent.\n• Paying EMIs on time is the single most important factor (35% weight).\n• High credit card balance utilization (> 30% of limit) damages your score.\n\nAs a student, you usually have no credit history (score = -1 or NH). Building a clean score early gives you cheaper interest rates later in life.",
            studentExample = "Rohit applied for an education loan. Because his father had an 810 CIBIL score, the bank offered an interest rate discount of 0.5%, saving ₹45,000 in interest over 7 years.",
            keyTakeaways = listOf(
                "Aim for a score above 750.",
                "Always pay 100% of your credit card bill on time—never pay only the 'Minimum Due'.",
                "Checking your own credit score does NOT lower it."
            ),
            quiz = listOf(
                QuizQuestion(
                    question = "What is the benchmark credit score that Indian banks consider healthy for competitive interest rates?",
                    options = listOf("350+", "500+", "750+", "900 only"),
                    correctIndex = 2,
                    explanation = "A score of 750 or higher is considered prime creditworthiness by Indian lenders."
                )
            )
        ),
        AcademyLesson(
            id = "lesson_11",
            title = "11. Debt Traps & Predatory Student Loan Apps",
            readingTime = "4 min",
            studentTag = "Scam Warning",
            summary = "Beware of instant Buy Now Pay Later (BNPL) and Chinese lending apps targeting college campuses.",
            content = "Instant loan apps promising ₹2,000 to ₹10,000 in 2 minutes without CIBIL checks are often predatory traps. They charge extortionate fees (processing fee + 36% to 100% annualized interest) and demand access to your phone contacts and photo gallery.\n\nSimilarly, Buy Now Pay Later (BNPL) schemes encourage impulsive spending by splitting purchases into 3 parts. Missing one payment triggers massive late penalty fees and damages your fresh credit score permanently.",
            studentExample = "A student borrowed ₹3,000 for a weekend party from an unverified loan app. Within 2 weeks, fees and penalties ballooned the debt to ₹7,500 with harassing calls to relatives.",
            keyTakeaways = listOf(
                "Never install unverified lending apps from outside Google Play Store.",
                "Verify that the lender is registered with the Reserve Bank of India (RBI NBFC).",
                "Never grant contacts or storage permissions to financial apps."
            ),
            quiz = listOf(
                QuizQuestion(
                    question = "What is the safest way to verify if an Indian lending app is legitimate?",
                    options = listOf("Check if it has high Instagram followers", "Verify its registered RBI-licensed bank or NBFC partner", "Check if it offers 0% interest", "Read Telegram reviews"),
                    correctIndex = 1,
                    explanation = "Legitimate lenders in India must operate under RBI banking or NBFC regulations."
                )
            )
        ),
        AcademyLesson(
            id = "lesson_12",
            title = "12. Education Loans: Moratorium & Section 80E",
            readingTime = "6 min",
            studentTag = "Loans",
            summary = "Demystify moratorium periods, simple interest during college, and tax deduction under Section 80E.",
            content = "An Education Loan funds your tuition and hostel expenses. Key concepts every student must know:\n\n1. Moratorium Period: A repayment holiday during your course period + usually 6 months to 1 year after graduation.\n2. Interest during Moratorium: Even though you don't pay EMIs during college, interest is accruing! Many banks calculate simple interest during the study period and compound it once repayment starts. Paying the simple interest during college earns a 1% interest discount at many PSBs.\n3. Tax Benefit (Section 80E): You can deduct 100% of the interest paid on education loans from your taxable salary for up to 8 years once you start working!",
            studentExample = "Ankit paid the ₹3,000 monthly simple interest on his loan using his internship stipend during his final year. His bank lowered his interest rate from 9.5% to 8.5%, saving over ₹80,000.",
            keyTakeaways = listOf(
                "Interest accumulates during the moratorium period unless serviced voluntarily.",
                "Central Sector Interest Subsidy (CSIS) covers interest for eligible economically weaker students.",
                "Section 80E provides unlimited income tax deduction on loan interest paid."
            ),
            quiz = listOf(
                QuizQuestion(
                    question = "Under Section 80E of the Indian Income Tax Act, what portion of education loan repayment is tax-deductible?",
                    options = listOf("Only the principal amount", "Only the interest amount paid", "Both principal and interest up to ₹1.5 Lakh", "None"),
                    correctIndex = 1,
                    explanation = "Section 80E allows full deduction of the interest component without any upper monetary ceiling for up to 8 years."
                )
            )
        ),
        AcademyLesson(
            id = "lesson_13",
            title = "13. Spotting Financial Scams in College",
            readingTime = "4 min",
            studentTag = "Safety & Cyber",
            summary = "Protect yourself against fake part-time Telegram tasks, UPI payment request fraud, and crypto schemes.",
            content = "College students are prime targets for cyber criminals. Common scams:\n\n1. UPI 'Receive' Fraud: A fraudster sends you a UPI Collect Request claiming they are 'sending' you money. Remember: You NEVER need to enter your UPI PIN to receive money!\n2. Fake Task Scams: 'Like YouTube videos and earn ₹50 per like.' They pay ₹200 initially to gain trust, then ask you to deposit ₹5,000 in a crypto wallet to 'unlock VIP tasks.' You lose all your money.\n3. Multi-Level Marketing (MLM): Schemes requiring you to pay an entry fee and recruit roommates are pyramid schemes.",
            studentExample = "Someone claiming to buy an old textbook on OLX asked Sneha to enter her UPI PIN to 'receive the ₹500 advance'. Sneha declined because entering a PIN always debits your account.",
            keyTakeaways = listOf(
                "Never enter your UPI PIN unless YOU are authorizing an outgoing payment.",
                "No legitimate job requires you to pay money upfront to receive tasks.",
                "Never share OTPs, CVV, or bank credentials with anyone."
            ),
            quiz = listOf(
                QuizQuestion(
                    question = "When do you need to enter your 4-digit or 6-digit UPI PIN?",
                    options = listOf("Only when you are paying/sending money from your account", "When someone is transferring money to your account", "To claim a cashback reward", "To check your account IFSC code"),
                    correctIndex = 0,
                    explanation = "Your UPI PIN is exclusively used to authorize outgoing debits from your bank account."
                )
            )
        ),
        AcademyLesson(
            id = "lesson_14",
            title = "14. Freelancing, Stipends & Tax Basics for Freshers",
            readingTime = "4 min",
            studentTag = "Tax & Career",
            summary = "How to handle internship stipends, TDS deductions, and filing your very first ITR.",
            content = "If you earn money through internships, web development, content creation, or tutoring, you are participating in the formal economy.\n\n• TDS (Tax Deducted at Source): Companies often deduct 10% TDS (under Sec 194J) on freelance payments. You can claim this 100% back as a refund by filing an Income Tax Return (ITR-1 or ITR-4) if your annual income is below the taxable threshold (₹7 Lakh under the New Tax Regime).\n• Always request Form 16 or Form 16A from your internship employer to reconcile with your AIS / 26AS statement.",
            studentExample = "Siddharth earned ₹80,000 from freelance coding projects. The client deducted ₹8,000 as TDS. Because his total annual income was under ₹7 Lakh, Siddharth filed ITR and received the entire ₹8,000 back into his account!",
            keyTakeaways = listOf(
                "Keep records of all invoices and bank transaction receipts.",
                "Filing an ITR is beneficial even with zero tax liability to recover TDS refunds.",
                "Under the new tax regime, annual income up to ₹7,00,000 has zero net tax liability."
            ),
            quiz = listOf(
                QuizQuestion(
                    question = "How can a college student recover 10% TDS deducted from an internship stipend if their total income is below the taxable slab?",
                    options = listOf("By asking the bank manager", "By filing an Income Tax Return (ITR)", "TDS can never be refunded", "By filing a police report"),
                    correctIndex = 1,
                    explanation = "Filing an annual ITR enables the Income Tax Department to process and issue a complete refund of excess TDS."
                )
            )
        )
    )
}
