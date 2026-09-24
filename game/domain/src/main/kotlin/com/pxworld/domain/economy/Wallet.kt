package com.pxworld.domain.economy

data class LedgerReason(val kind: String, val reference: String = "") {
    override fun toString(): String = if (reference.isEmpty()) kind else "$kind:$reference"
}

data class LedgerEntry(
    val sequence: Long,
    val currency: String,
    val delta: Long,
    val balanceAfter: Long,
    val reason: LedgerReason,
)

class InsufficientFunds(val currency: String, val required: Long, val available: Long) :
    IllegalStateException("need $required $currency but only $available available")

data class Wallet(val balances: Map<String, Long> = emptyMap(), val nextSequence: Long = 1L) {

    fun balance(currency: String): Long = balances[currency] ?: 0L

    fun credit(currency: String, amount: Long, reason: LedgerReason): WalletChange {
        require(amount > 0) { "credit amount must be positive, was $amount" }
        return apply(currency, amount, reason)
    }

    fun debit(currency: String, amount: Long, reason: LedgerReason): WalletChange {
        require(amount > 0) { "debit amount must be positive, was $amount" }
        val available = balance(currency)
        if (available < amount) throw InsufficientFunds(currency, amount, available)
        return apply(currency, -amount, reason)
    }

    private fun apply(currency: String, delta: Long, reason: LedgerReason): WalletChange {
        val after = balance(currency) + delta
        val entry = LedgerEntry(nextSequence, currency, delta, after, reason)
        return WalletChange(Wallet(balances + (currency to after), nextSequence + 1), entry)
    }
}

data class WalletChange(val wallet: Wallet, val entry: LedgerEntry)
