package com.newbet.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.newbet.app.data.model.BetSelection
import com.newbet.app.ui.components.NewbetBottomBar
import com.newbet.app.ui.components.NewbetTopBar
import com.newbet.app.ui.screens.*
import com.newbet.app.ui.theme.BgCanvas
import com.newbet.app.ui.theme.NewbetTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as NewbetApplication
        val affiliateManager = app.affiliateManager
        val sportsRepository = app.sportsRepository

        // Parse deep link if present (e.g. newbet://register?promo=VIP200)
        affiliateManager.handleDeepLink(intent.data)

        setContent {
            NewbetTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route ?: "sports"

                var balanceXOF by remember { mutableStateOf(25_400L) }
                val bonusBalanceXOF by remember { mutableStateOf(5_000L) }
                val isLoggedIn by remember { mutableStateOf(true) }

                val selectedBets = remember { mutableStateListOf<BetSelection>() }
                val events by sportsRepository.events.collectAsState()
                val casinoGames by sportsRepository.casinoGames.collectAsState()
                val placedBets by sportsRepository.placedBets.collectAsState()

                Scaffold(
                    topBar = {
                        if (currentRoute !in listOf("registration", "partners")) {
                            NewbetTopBar(
                                balanceXOF = balanceXOF,
                                isLoggedIn = isLoggedIn,
                                onRegisterClick = { navController.navigate("registration") },
                                onLoginClick = {
                                    Toast.makeText(this@MainActivity, "Connecté en tant que Christian N.", Toast.LENGTH_SHORT).show()
                                },
                                onDepositClick = {
                                    // Open Menu deposit
                                    navController.navigate("menu")
                                }
                            )
                        }
                    },
                    bottomBar = {
                        if (currentRoute !in listOf("registration", "partners")) {
                            NewbetBottomBar(
                                currentRoute = currentRoute,
                                betSlipCount = selectedBets.size,
                                activeBetsCount = placedBets.count { it.status == com.newbet.app.data.model.BetStatus.PENDING },
                                onNavigate = { route ->
                                    if (route != currentRoute) {
                                        navController.navigate(route) {
                                            popUpTo("sports") { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                }
                            )
                        }
                    },
                    containerColor = BgCanvas
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(BgCanvas)
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = "sports"
                        ) {
                            // 1. SPORTS SCREEN (Main Home for Bettors)
                            composable("sports") {
                                SportsScreen(
                                    events = events,
                                    selectedBets = selectedBets,
                                    onToggleBet = { bet ->
                                        val existingIndex = selectedBets.indexOfFirst {
                                            it.eventId == bet.eventId && it.marketLabel == bet.marketLabel
                                        }
                                        if (existingIndex >= 0) {
                                            selectedBets.removeAt(existingIndex)
                                        } else {
                                            val sameEvent = selectedBets.indexOfFirst { it.eventId == bet.eventId }
                                            if (sameEvent >= 0) {
                                                selectedBets[sameEvent] = bet
                                            } else {
                                                selectedBets.add(bet)
                                            }
                                        }
                                    },
                                    onNavigateToRegistration = { navController.navigate("registration") }
                                )
                            }

                            // 2. CASINO SCREEN (Real Aviator Crash, Slots, Live tables)
                            composable("casino") {
                                CasinoScreen(
                                    casinoGames = casinoGames,
                                    userBalanceXOF = balanceXOF,
                                    onWinCasino = { winAmount ->
                                        balanceXOF += winAmount
                                    },
                                    onBetCasino = { betAmount ->
                                        if (balanceXOF >= betAmount) {
                                            balanceXOF -= betAmount
                                            true
                                        } else {
                                            false
                                        }
                                    }
                                )
                            }

                            // 3. BET SLIP SCREEN (Floating Center Coupon)
                            composable("betslip") {
                                BetSlipScreen(
                                    selections = selectedBets,
                                    userBalanceXOF = balanceXOF,
                                    onRemoveSelection = { bet -> selectedBets.remove(bet) },
                                    onClearAll = { selectedBets.clear() },
                                    onPlaceBet = { stake, odds ->
                                        if (balanceXOF >= stake) {
                                            balanceXOF -= stake
                                            sportsRepository.placeBet(selectedBets.toList(), stake)
                                            selectedBets.clear()
                                            true
                                        } else {
                                            false
                                        }
                                    },
                                    onLoadSharedCoupon = { code ->
                                        selectedBets.clear()
                                        // Load 3 high-profile matches
                                        val ucl = events.firstOrNull { it.id == "ucl_1" }
                                        val epl = events.firstOrNull { it.id == "epl_1" }
                                        val laliga = events.firstOrNull { it.id == "laliga_1" }

                                        if (ucl != null) {
                                            selectedBets.add(BetSelection(ucl.id, "${ucl.homeTeam} vs ${ucl.awayTeam}", ucl.league, "${ucl.homeTeam} (1)", ucl.oddsHome))
                                        }
                                        if (epl != null) {
                                            selectedBets.add(BetSelection(epl.id, "${epl.homeTeam} vs ${epl.awayTeam}", epl.league, "Les 2 marquent (Oui)", epl.oddsBttsYes))
                                        }
                                        if (laliga != null) {
                                            selectedBets.add(BetSelection(laliga.id, "${laliga.homeTeam} vs ${laliga.awayTeam}", laliga.league, "${laliga.homeTeam} (1)", laliga.oddsHome))
                                        }
                                    },
                                    onNavigateToSports = { navController.navigate("sports") },
                                    onNavigateToMyBets = { navController.navigate("my_bets") }
                                )
                            }

                            // 4. MY BETS SCREEN (Active Bets with Cashout + Settled History)
                            composable("my_bets") {
                                MyBetsScreen(
                                    placedBets = placedBets,
                                    onCashOutClick = { betId ->
                                        val cashedAmount = sportsRepository.cashOut(betId)
                                        if (cashedAmount > 0) {
                                            balanceXOF += cashedAmount
                                        }
                                    },
                                    onNavigateToSports = { navController.navigate("sports") }
                                )
                            }

                            // 5. ACCOUNT / MENU SCREEN (Player Profile, Mobile Money Cashier, Peer Referral)
                            composable("menu") {
                                MenuScreen(
                                    userBalanceXOF = balanceXOF,
                                    bonusBalanceXOF = bonusBalanceXOF,
                                    onDeposit = { amount ->
                                        balanceXOF += amount
                                    },
                                    onWithdraw = { amount ->
                                        if (balanceXOF >= amount) {
                                            balanceXOF -= amount
                                            true
                                        } else {
                                            false
                                        }
                                    },
                                    onNavigateToPartners = { navController.navigate("partners") }
                                )
                            }

                            // REGISTRATION FLOW (with Promo code attribution)
                            composable("registration") {
                                RegistrationScreen(
                                    affiliateManager = affiliateManager,
                                    onRegistrationSuccess = {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "Bienvenue sur Newbet ! Bonus 200% crédité (+50 000 F CFA) !",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        balanceXOF += 50_000L
                                        navController.popBackStack()
                                    },
                                    onBackClick = { navController.popBackStack() }
                                )
                            }

                            // DEDICATED B2B PARTNER PORTAL (Accessible from Menu > Devenir Partenaire Pro)
                            composable("partners") {
                                PartnerScreen(
                                    affiliateManager = affiliateManager,
                                    onBackClick = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
