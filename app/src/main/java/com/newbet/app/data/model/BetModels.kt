package com.newbet.app.data.model

data class SportCategory(
    val id: String,
    val name: String,
    val iconEmoji: String,
    val liveCount: Int = 0
)

data class SportEvent(
    val id: String,
    val sport: String,
    val league: String,
    val leagueFlag: String,
    val homeTeam: String,
    val awayTeam: String,
    val minute: String? = null,
    val scoreHome: Int? = null,
    val scoreAway: Int? = null,
    val isLive: Boolean = false,
    val startTime: String = "20:00",
    val oddsHome: Double,
    val oddsDraw: Double,
    val oddsAway: Double,
    val oddsOver25: Double = 1.75,
    val oddsUnder25: Double = 2.05,
    val oddsBttsYes: Double = 1.68,
    val oddsBttsNo: Double = 2.10,
    val odds1X: Double = 1.22,
    val oddsX2: Double = 1.55,
    val oddsHomeTrend: OddsTrend = OddsTrend.NEUTRAL,
    val oddsDrawTrend: OddsTrend = OddsTrend.NEUTRAL,
    val oddsAwayTrend: OddsTrend = OddsTrend.NEUTRAL
)

enum class OddsTrend {
    UP, DOWN, NEUTRAL
}

data class BetSelection(
    val eventId: String,
    val matchTitle: String,
    val league: String,
    val marketLabel: String, // e.g. "Victoire Bayern Munich (1)"
    val odds: Double
)

data class PlacedBet(
    val id: String,
    val date: String,
    val selections: List<BetSelection>,
    val stakeXOF: Long,
    val totalOdds: Double,
    val potentialWinXOF: Long,
    val isLive: Boolean = true,
    val canCashOut: Boolean = true,
    val cashOutAmountXOF: Long = 0,
    val status: BetStatus = BetStatus.PENDING
)

enum class BetStatus {
    PENDING, WON, LOST, CASHED_OUT
}

data class CasinoGame(
    val id: String,
    val title: String,
    val category: String, // Crash, Slots, Live, Table
    val provider: String, // Spribe, Pragmatic Play, Evolution
    val iconEmoji: String,
    val isHot: Boolean = false,
    val rtp: String = "97.0%"
)

data class TransactionItem(
    val id: String,
    val type: String, // Dépôt, Retrait, Gain
    val method: String, // Wave, MTN MoMo, Orange Money
    val amountXOF: Long,
    val date: String,
    val status: String // Validé, En cours
)
