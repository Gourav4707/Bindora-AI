        totalSpent: Double,
        budgetLimit: Double,
        categoryMap: Map<String, Double>
    ): List<String> {
        val insights = mutableListOf<String>()
        val calendar = Calendar.getInstance()
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        val maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val daysRemaining = maxDays - currentDay

        val percentUsed = if (budgetLimit > 0) ((totalSpent / budgetLimit) * 100).toInt() else 0

        if (percentUsed >= 100) {
            insights.add("⚠️ You have exceeded 100% of your monthly pocket money with $daysRemaining days left in the month.")
        } else if (percentUsed >= 80) {
            insights.add("🚨 80% Threshold Alert: You have used ${percentUsed}% of your total budget. Stick to mess food to avoid borrowing.")
        } else if (percentUsed >= 50 && currentDay <= 12) {
            insights.add("⚡ High Spending Rate: You spent 50% of your pocket money in the first $currentDay days. Slow down discretionary wants.")
        } else {
            insights.add("✅ Budget On Track: ${100 - percentUsed}% of your monthly allowance is safely remaining.")
        }

        val canteenSpend = categoryMap["Food & Canteen"] ?: 0.0
        if (canteenSpend > 0 && canteenSpend >= (budgetLimit * 0.35)) {
            insights.add("🍔 Food & Canteen accounts for ₹${"%,.0f".format(canteenSpend)} of your expenses. Packing college snacks will save ₹500/month.")
        }

        val goals = savingsGoals.value
        val topGoal = goals.firstOrNull { !it.isArchived }
        if (topGoal != null && topGoal.savedAmount > 0) {
            val progress = ((topGoal.savedAmount / topGoal.targetAmount) * 100).toInt()
            insights.add("🎯 Goal Progress: Your '${topGoal.title}' goal is ${progress}% funded! Adding ₹200 this week keeps you ahead.")
        }

        return insights
    }

    // Auto-Tracking Ingestion and Review Actions
    fun setImportFilter(filter: String) {
        _selectedImportFilter.value = filter
    }

    fun openStatementImportDialog() {
        _showStatementImportDialog.value = true
    }

    fun closeStatementImportDialog() {
        _showStatementImportDialog.value = false
    }

    fun clearImportFeedback() {
        _importActionFeedback.value = null
    }

    fun importCsvStatement(csvContent: String, sourceName: String = "Bank Statement") {
        viewModelScope.launch {
            val result = repository.importTransactionsFromCsv(csvContent, sourceName)
            _importActionFeedback.value = result.message
            _showStatementImportDialog.value = false
        }
    }

    fun simulateIncomingPayment(title: String, text: String) {
        viewModelScope.launch {
            val entity = repository.processIncomingNotification(title, text)
            if (entity != null) {
                _importActionFeedback.value = "Captured transaction of ₹${"%,.0f".format(entity.rawAmount)} to ${entity.cleanMerchant}."
            } else {
                _importActionFeedback.value = "Filtered non-payment alert or OTP (Privacy Protected)."
            }
        }
    }

    fun confirmImportedTransaction(id: Long, finalCategory: String, finalMerchant: String) {
        viewModelScope.launch {
            repository.confirmImportedTransaction(id, finalCategory, finalMerchant)
            _importActionFeedback.value = "Confirmed and added to your monthly budget!"
        }
    }

    fun excludeImportedTransaction(id: Long, reason: String = "Excluded by user") {
        viewModelScope.launch {
            repository.excludeImportedTransaction(id, reason)
            _importActionFeedback.value = "Excluded from your expenses."
        }
    }

    fun resolveDuplicate(id: Long, keepExisting: Boolean) {
        viewModelScope.launch {
            repository.resolveDuplicate(id, keepExisting)
            _importActionFeedback.value = if (keepExisting) "Resolved: duplicate ignored." else "Restored for review."
        }
    }

    fun connectProvider(providerId: String) {
        viewModelScope.launch {
            repository.connectAccount(providerId)
            _importActionFeedback.value = "Provider connection activated."
        }
    }

    fun disconnectProvider(providerId: String) {
        viewModelScope.launch {
            repository.disconnectAccount(providerId)
            _importActionFeedback.value = "Provider disconnected."
        }
    }
}
