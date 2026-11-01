package com.newbet.app.data.repository

import com.newbet.app.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SportsRepository {

    private val _events = MutableStateFlow(getRealFixtures())
    val events: StateFlow<List<SportEvent>> = _events.asStateFlow()

    private val _casinoGames = MutableStateFlow(getRealCasinoCatalog())
    val casinoGames: StateFlow<List<CasinoGame>> = _casinoGames.asStateFlow()

    private val _placedBets = MutableStateFlow(getInitialMockBets())
    val placedBets: StateFlow<List<PlacedBet>> = _placedBets.asStateFlow()

    fun placeBet(selections: List<BetSelection>, stakeXOF: Long): PlacedBet {
        val totalOdds = selections.fold(1.0) { acc, s -> acc * s.odds }
        val potentialWin = (stakeXOF * totalOdds).toLong()
        val bet = PlacedBet(
            id = "BET-${System.currentTimeMillis() % 100000}",
            date = "Aujourd'hui, 21:15",
            selections = selections,
            stakeXOF = stakeXOF,
            totalOdds = totalOdds,
            potentialWinXOF = potentialWin,
            isLive = selections.any { it.matchTitle.contains("Live", ignoreCase = true) },
            canCashOut = true,
            cashOutAmountXOF = (stakeXOF * 0.95).toLong(),
            status = BetStatus.PENDING
        )
        _placedBets.value = listOf(bet) + _placedBets.value
        return bet
    }

    fun cashOut(betId: String): Long {
        var returnAmount = 0L
        _placedBets.value = _placedBets.value.map { bet ->
            if (bet.id == betId && bet.canCashOut) {
                returnAmount = bet.cashOutAmountXOF
                bet.copy(status = BetStatus.CASHED_OUT, canCashOut = false)
            } else bet
        }
        return returnAmount
    }

    companion object {
        fun getRealFixtures(): List<SportEvent> {
            return listOf(
                SportEvent(
                    id = "ucl_1",
                    sport = "Football",
                    league = "UEFA Champions League",
                    leagueFlag = "🇪🇺",
                    homeTeam = "Real Madrid",
                    awayTeam = "Manchester City",
                    minute = "73'",
                    scoreHome = 2,
                    scoreAway = 1,
                    isLive = true,
                    startTime = "21:00",
                    oddsHome = 2.15,
                    oddsDraw = 3.60,
                    oddsAway = 3.25,
                    oddsOver25 = 1.62,
                    oddsUnder25 = 2.20,
                    oddsBttsYes = 1.50,
                    oddsBttsNo = 2.40,
                    oddsHomeTrend = OddsTrend.UP,
                    oddsAwayTrend = OddsTrend.DOWN
                ),
                SportEvent(
                    id = "epl_1",
                    sport = "Football",
                    league = "Premier League",
                    leagueFlag = "🏴󠁧󠁢󠁥󠁮󠁧󠁿",
                    homeTeam = "Arsenal FC",
                    awayTeam = "Liverpool FC",
                    minute = "41'",
                    scoreHome = 1,
                    scoreAway = 1,
                    isLive = true,
                    startTime = "20:30",
                    oddsHome = 2.40,
                    oddsDraw = 3.30,
                    oddsAway = 2.85,
                    oddsOver25 = 1.70,
                    oddsUnder25 = 2.05,
                    oddsBttsYes = 1.58,
                    oddsBttsNo = 2.25,
                    oddsDrawTrend = OddsTrend.UP
                ),
                SportEvent(
                    id = "ucl_2",
                    sport = "Football",
                    league = "UEFA Champions League",
                    leagueFlag = "🇪🇺",
                    homeTeam = "Paris Saint-Germain",
                    awayTeam = "Bayern Munich",
                    minute = null,
                    scoreHome = null,
                    scoreAway = null,
                    isLive = false,
                    startTime = "Ce soir 21:00",
                    oddsHome = 2.25,
                    oddsDraw = 3.75,
                    oddsAway = 2.90,
                    oddsOver25 = 1.55,
                    oddsUnder25 = 2.35,
                    oddsBttsYes = 1.48,
                    oddsBttsNo = 2.50
                ),
                SportEvent(
                    id = "laliga_1",
                    sport = "Football",
                    league = "La Liga",
                    leagueFlag = "🇪🇸",
                    homeTeam = "FC Barcelone",
                    awayTeam = "Atletico Madrid",
                    minute = null,
                    scoreHome = null,
                    scoreAway = null,
                    isLive = false,
                    startTime = "Demain 21:00",
                    oddsHome = 1.82,
                    oddsDraw = 3.70,
                    oddsAway = 4.40,
                    oddsOver25 = 1.80,
                    oddsUnder25 = 1.95,
                    oddsBttsYes = 1.72,
                    oddsBttsNo = 2.00
                ),
                SportEvent(
                    id = "can_1",
                    sport = "Football",
                    league = "Coupe d'Afrique des Nations",
                    leagueFlag = "🌍",
                    homeTeam = "Sénégal",
                    awayTeam = "Côte d'Ivoire",
                    minute = null,
                    scoreHome = null,
                    scoreAway = null,
                    isLive = false,
                    startTime = "Demain 18:00",
                    oddsHome = 2.10,
                    oddsDraw = 3.10,
                    oddsAway = 3.65,
                    oddsOver25 = 2.15,
                    oddsUnder25 = 1.65,
                    oddsBttsYes = 1.95,
                    oddsBttsNo = 1.75
                ),
                SportEvent(
                    id = "tennis_1",
                    sport = "Tennis",
                    league = "Roland Garros (ATP)",
                    leagueFlag = "🎾",
                    homeTeam = "Carlos Alcaraz",
                    awayTeam = "Jannik Sinner",
                    minute = "Set 3",
                    scoreHome = 1,
                    scoreAway = 1,
                    isLive = true,
                    startTime = "15:00",
                    oddsHome = 1.88,
                    oddsDraw = 0.0,
                    oddsAway = 1.92
                )
            )
        }

        fun getRealCasinoCatalog(): List<CasinoGame> {
            return listOf(
                CasinoGame("aviator", "Aviator", "Crash", "Spribe", "✈️", isHot = true, rtp = "97.0%"),
                CasinoGame("sweet_bonanza", "Sweet Bonanza 1000", "Slots", "Pragmatic Play", "🍬", isHot = true, rtp = "96.5%"),
                CasinoGame("gates_olympus", "Gates of Olympus", "Slots", "Pragmatic Play", "⚡", isHot = true, rtp = "96.5%"),
                CasinoGame("roulette_live", "Lightning Roulette", "Live", "Evolution", "🎡", isHot = true, rtp = "97.3%"),
                CasinoGame("spaceman", "Spaceman", "Crash", "Pragmatic Play", "🚀", isHot = false, rtp = "96.5%"),
                CasinoGame("blackjack_live", "Blackjack VIP", "Live", "Evolution", "🃏", isHot = false, rtp = "99.2%"),
                CasinoGame("mines", "Mines Newbet", "Mini-Jeux", "Spribe", "💣", isHot = true, rtp = "97.0%"),
                CasinoGame("plinko", "Plinko X", "Mini-Jeux", "SmartSoft", "🔴", isHot = false, rtp = "98.5%")
            )
        }

        fun getInitialMockBets(): List<PlacedBet> {
            return listOf(
                PlacedBet(
                    id = "PARI-84920",
                    date = "Aujourd'hui, 20:45",
                    selections = listOf(
                        BetSelection("ucl_1", "Real Madrid vs Man City", "Ligue des Champions", "Real Madrid (1)", 2.15),
                        BetSelection("epl_1", "Arsenal vs Liverpool", "Premier League", "Les 2 marquent (Oui)", 1.58)
                    ),
                    stakeXOF = 5000,
                    totalOdds = 3.40,
                    potentialWinXOF = 17000,
                    isLive = true,
                    canCashOut = true,
                    cashOutAmountXOF = 9200,
                    status = BetStatus.PENDING
                ),
                PlacedBet(
                    id = "PARI-79102",
                    date = "Hier, 22:30",
                    selections = listOf(
                        BetSelection("hist_1", "Barcelone vs Séville", "La Liga", "Barcelone (1)", 1.45)
                    ),
                    stakeXOF = 10000,
                    totalOdds = 1.45,
                    potentialWinXOF = 14500,
                    isLive = false,
                    canCashOut = false,
                    status = BetStatus.WON
                )
            )
        }
    }
}

