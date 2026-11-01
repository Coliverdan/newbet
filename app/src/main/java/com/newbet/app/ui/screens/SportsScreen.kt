package com.newbet.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.newbet.app.R
import com.newbet.app.data.model.BetSelection
import com.newbet.app.data.model.SportCategory
import com.newbet.app.data.model.SportEvent
import com.newbet.app.ui.components.MatchEventCard
import com.newbet.app.ui.components.OddsBox
import com.newbet.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SportsScreen(
    events: List<SportEvent>,
    selectedBets: List<BetSelection>,
    onToggleBet: (BetSelection) -> Unit,
    onNavigateToRegistration: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf("live") } // live, upcoming, boosted
    var sheetEvent by remember { mutableStateOf<SportEvent?>(null) }

    val categories = remember {
        listOf(
            SportCategory("football", "Football", "⚽", 18),
            SportCategory("basketball", "Basketball", "🏀", 6),
            SportCategory("tennis", "Tennis", "🎾", 4),
            SportCategory("mma", "MMA / UFC", "🥊", 2),
            SportCategory("esports", "Esports", "🎮", 5)
        )
    }

    val displayedEvents = remember(selectedFilter, events) {
        when (selectedFilter) {
            "live" -> events.filter { it.isLive }
            "upcoming" -> events.filter { !it.isLive }
            else -> events
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BgCanvas),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Unibet Sub-Header Navigation Tabs
        item {
            Surface(
                color = CardSurface,
                border = androidx.compose.foundation.BorderStroke(0.5.dp, BorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterTabButton(
                        label = "En Direct (Live)",
                        icon = Icons.Default.Stream,
                        isSelected = selectedFilter == "live",
                        badgeCount = events.count { it.isLive },
                        onClick = { selectedFilter = "live" },
                        modifier = Modifier.weight(1f)
                    )

                    FilterTabButton(
                        label = "À Venir",
                        icon = Icons.Default.CalendarToday,
                        isSelected = selectedFilter == "upcoming",
                        badgeCount = null,
                        onClick = { selectedFilter = "upcoming" },
                        modifier = Modifier.weight(1f)
                    )

                    FilterTabButton(
                        label = "Tous",
                        icon = Icons.Default.AllInclusive,
                        isSelected = selectedFilter == "all",
                        badgeCount = null,
                        onClick = { selectedFilter = "all" },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Hero 3D Promotional Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp)
                    .height(150.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onNavigateToRegistration() }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.newbet_hero),
                    contentDescription = "Newbet 3D Hero",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // High Contrast Dark Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            androidx.compose.ui.graphics.Brush.horizontalGradient(
                                listOf(Color(0xEE0A0F0D), Color(0x66000000))
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(UnibetGold)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "CODE PROMO : NEWBET2026",
                            color = Color.Black,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "BONUS 200% SUR PREMIER DÉPÔT",
                        color = TextWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )

                    Text(
                        text = "Jusqu'à 250 000 FCFA avec MTN MoMo, Wave, Orange",
                        color = UnibetGreenHover,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Sports Category Horizontal Carousel
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = CardSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                        modifier = Modifier.clickable { /* filter sport */ }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(cat.iconEmoji, fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(cat.name, fontSize = 12.sp, color = TextWhite, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(CardSurfaceHover)
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    cat.liveCount.toString(),
                                    fontSize = 9.sp,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section Title: Matchs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (selectedFilter == "live") "MATCHS EN DIRECT (LIVE)" else "ÉVÉNEMENTS PRINCIPAUX",
                    color = TextWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )

                Text(
                    text = "${displayedEvents.size} matchs",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        }

        // Event List
        items(displayedEvents) { event ->
            val cur1 = selectedBets.find { it.eventId == event.id && it.marketLabel.contains("(1)") }
            val curX = selectedBets.find { it.eventId == event.id && it.marketLabel.contains("(X)") }
            val cur2 = selectedBets.find { it.eventId == event.id && it.marketLabel.contains("(2)") }

            val selectedMarket = when {
                cur1 != null -> "1"
                curX != null -> "X"
                cur2 != null -> "2"
                else -> null
            }

            MatchEventCard(
                event = event,
                selectedMarket = selectedMarket,
                onOddsSelect = { market, odds ->
                    val label = when (market) {
                        "1" -> "${event.homeTeam} (1)"
                        "X" -> "Match Nul (X)"
                        "2" -> "${event.awayTeam} (2)"
                        else -> market
                    }
                    val bet = BetSelection(
                        eventId = event.id,
                        matchTitle = "${event.homeTeam} vs ${event.awayTeam}",
                        league = event.league,
                        marketLabel = label,
                        odds = odds
                    )
                    onToggleBet(bet)
                },
                onExpandMarkets = { sheetEvent = event },
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
            )
        }
    }

    // Bottom Sheet for "+85 Marchés"
    if (sheetEvent != null) {
        val event = sheetEvent!!
        ModalBottomSheet(
            onDismissRequest = { sheetEvent = null },
            containerColor = CardSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "${event.homeTeam} vs ${event.awayTeam}",
                    color = TextWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${event.league} • Tous les marchés de paris",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Total Buts (Over / Under 2.5)
                Text("TOTAL BUTS (PLUS / MOINS 2.5)", color = UnibetGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val selOver = selectedBets.find { it.eventId == event.id && it.marketLabel.contains("Plus de 2.5") }
                    val selUnder = selectedBets.find { it.eventId == event.id && it.marketLabel.contains("Moins de 2.5") }

                    OddsBox(
                        marketCode = "Plus de 2.5",
                        odds = event.oddsOver25,
                        isSelected = selOver != null,
                        onClick = {
                            onToggleBet(BetSelection(event.id, "${event.homeTeam} vs ${event.awayTeam}", event.league, "Plus de 2.5 Buts", event.oddsOver25))
                        },
                        modifier = Modifier.weight(1f)
                    )

                    OddsBox(
                        marketCode = "Moins de 2.5",
                        odds = event.oddsUnder25,
                        isSelected = selUnder != null,
                        onClick = {
                            onToggleBet(BetSelection(event.id, "${event.homeTeam} vs ${event.awayTeam}", event.league, "Moins de 2.5 Buts", event.oddsUnder25))
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Les deux équipes marquent
                Text("LES DEUX ÉQUIPES MARQUENT", color = UnibetGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val selBttsYes = selectedBets.find { it.eventId == event.id && it.marketLabel.contains("Les 2 marquent (Oui)") }
                    val selBttsNo = selectedBets.find { it.eventId == event.id && it.marketLabel.contains("Les 2 marquent (Non)") }

                    OddsBox(
                        marketCode = "Oui",
                        odds = event.oddsBttsYes,
                        isSelected = selBttsYes != null,
                        onClick = {
                            onToggleBet(BetSelection(event.id, "${event.homeTeam} vs ${event.awayTeam}", event.league, "Les 2 marquent (Oui)", event.oddsBttsYes))
                        },
                        modifier = Modifier.weight(1f)
                    )

                    OddsBox(
                        marketCode = "Non",
                        odds = event.oddsBttsNo,
                        isSelected = selBttsNo != null,
                        onClick = {
                            onToggleBet(BetSelection(event.id, "${event.homeTeam} vs ${event.awayTeam}", event.league, "Les 2 marquent (Non)", event.oddsBttsNo))
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun FilterTabButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    badgeCount: Int?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) UnibetGreenAccent else Color.Transparent,
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else TextSecondary,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else TextSecondary
            )
            if (badgeCount != null && badgeCount > 0) {
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (isSelected) UnibetGold else LiveRed)
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        badgeCount.toString(),
                        color = Color.Black,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}

