package com.newbet.app.data.affiliate

import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AttributionSource {
    NONE, DEEP_LINK, CLIPBOARD, MANUAL_PROMO, DYNAMIC_APK
}

data class PartnerStats(
    val partnerCode: String,
    val partnerName: String,
    val totalPlayers: Int,
    val activePlayers: Int,
    val totalDepositsXOF: Long,
    val netGamingRevenueXOF: Long,
    val revSharePercent: Int, // e.g. 35%
    val revShareCommissionXOF: Long,
    val subAffiliateCount: Int,
    val subAffiliateCommissionXOF: Long, // 5% MLM
    val totalWithdrawableXOF: Long
)

class AffiliateManager(private val context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("newbet_affiliate_prefs", Context.MODE_PRIVATE)

    private val _currentPromoCode = MutableStateFlow(getSavedPromoCode())
    val currentPromoCode: StateFlow<String> = _currentPromoCode.asStateFlow()

    private val _isAttributed = MutableStateFlow(prefs.getBoolean("is_attributed", false))
    val isAttributed: StateFlow<Boolean> = _isAttributed.asStateFlow()

    fun handleDeepLink(uri: Uri?) {
        if (uri == null) return
        val codeFromQuery = uri.getQueryParameter("ref") 
            ?: uri.getQueryParameter("promo")
            ?: uri.lastPathSegment

        if (!codeFromQuery.isNullOrBlank()) {
            saveAttribution(codeFromQuery.uppercase(), AttributionSource.DEEP_LINK)
        }
    }

    fun checkClipboardForReferral() {
        if (_isAttributed.value) return // Already attributed, locked for life
        try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            val clip = clipboard?.primaryClip
            if (clip != null && clip.itemCount > 0) {
                val text = clip.getItemAt(0).text?.toString()?.trim()
                if (text != null && (text.startsWith("NEWBET") || text.startsWith("NEW-") || text.length in 4..12)) {
                    // Check if it's a valid promo pattern
                    saveAttribution(text.uppercase(), AttributionSource.CLIPBOARD)
                }
            }
        } catch (_: Exception) {}
    }

    fun saveAttribution(code: String, source: AttributionSource) {
        val cleanCode = code.trim().uppercase()
        if (cleanCode.isBlank()) return

        prefs.edit()
            .putString("promo_code", cleanCode)
            .putString("attribution_source", source.name)
            .putLong("attribution_timestamp", System.currentTimeMillis())
            .putBoolean("is_attributed", true)
            .apply()

        _currentPromoCode.value = cleanCode
        _isAttributed.value = true
    }

    fun getSavedPromoCode(): String {
        return prefs.getString("promo_code", "NEWBET2026") ?: "NEWBET2026"
    }

    fun validatePromoCode(input: String): PromoValidationResult {
        val code = input.trim().uppercase()
        return if (code.length >= 3) {
            PromoValidationResult(
                isValid = true,
                partnerName = "Ambassadeur VIP Newbet",
                bonusMultiplier = 200,
                message = "Bonus de 200% activé jusqu'à 250 000 FCFA !"
            )
        } else {
            PromoValidationResult(
                isValid = false,
                partnerName = "",
                bonusMultiplier = 0,
                message = "Code promo non reconnu"
            )
        }
    }

    fun getPartnerMockStats(): PartnerStats {
        val code = getSavedPromoCode()
        val ngr = 14_850_000L // 14.8M FCFA
        val revSharePct = 35
        val revShare = (ngr * revSharePct) / 100
        val subAffComm = 420_000L // 5% des sous-affiliés
        return PartnerStats(
            partnerCode = code,
            partnerName = "Top Parieur Afrique",
            totalPlayers = 1_420,
            activePlayers = 890,
            totalDepositsXOF = 38_500_000L,
            netGamingRevenueXOF = ngr,
            revSharePercent = revSharePct,
            revShareCommissionXOF = revShare,
            subAffiliateCount = 14,
            subAffiliateCommissionXOF = subAffComm,
            totalWithdrawableXOF = revShare + subAffComm
        )
    }
}

data class PromoValidationResult(
    val isValid: Boolean,
    val partnerName: String,
    val bonusMultiplier: Int,
    val message: String
)

