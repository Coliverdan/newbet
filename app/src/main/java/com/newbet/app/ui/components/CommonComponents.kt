package com.newbet.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.newbet.app.R
import com.newbet.app.data.model.OddsTrend
import com.newbet.app.data.model.SportEvent
import com.newbet.app.ui.theme.*

@Composable
fun NewbetTopBar(
    balanceXOF: Long,
    isLoggedIn: Boolean,
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit,
    onDepositClick: () -> Unit
) {
    Surface(
        color = UnibetGreenHeader,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Brand Logo & Title
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.newbet_logo),
                        contentDescription = "Newbet Logo",
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "NEWBET",
                            color = TextWhite,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "OFFICIAL SPORTSBOOK",
                            color = UnibetGreenHover,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                    }
                }

                // Balance or Auth CTA
                if (isLoggedIn) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(UnibetGreenDark)
                            .clickable(onClick = onDepositClick)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "SOLDE",
                                color = TextMuted,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${String.format("%,d", balanceXOF)} F",
                                color = UnibetGold,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(UnibetGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Dépôt",
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedButton(
                            onClick = onLoginClick,
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.SolidColor(Color.White.copy(alpha = 0.5f))
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text("Connexion", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = onRegisterClick,
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = UnibetGold,
                                contentColor = Color.Black
                            ),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text("S'inscrire", fontSize = 12.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NewbetBottomBar(
    currentRoute: String,
    betSlipCount: Int,
    activeBetsCount: Int = 1,
    onNavigate: (String) -> Unit
) {
    Surface(
        color = BgCanvas,
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(0.5.dp, BorderSubtle),
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                icon = Icons.Default.SportsSoccer,
                label = "Sports",
                isSelected = currentRoute == "sports",
                onClick = { onNavigate("sports") }
            )

            BottomNavItem(
                icon = Icons.Default.Casino,
                label = "Casino",
                isSelected = currentRoute == "casino",
                badgeText = "HOT",
                onClick = { onNavigate("casino") }
            )

            // Center Floating BetSlip Button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .offset(y = (-6).dp)
                    .clickable { onNavigate("betslip") }
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(UnibetGreenAccent)
                        .border(
                            2.dp,
                            if (betSlipCount > 0) UnibetGold else Color.Transparent,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ConfirmationNumber,
                        contentDescription = "Coupon de pari",
                        tint = TextWhite,
                        modifier = Modifier.size(24.dp)
                    )
                }

                if (betSlipCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 4.dp, y = (-2).dp)
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(UnibetGold),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = betSlipCount.toString(),
                            color = Color.Black,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            BottomNavItem(
                icon = Icons.Default.Receipt,
                label = "Mes Paris",
                isSelected = currentRoute == "my_bets",
                countBadge = if (activeBetsCount > 0) activeBetsCount else null,
                onClick = { onNavigate("my_bets") }
            )

            BottomNavItem(
                icon = Icons.Default.Person,
                label = "Compte",
                isSelected = currentRoute == "menu",
                onClick = { onNavigate("menu") }
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    badgeText: String? = null,
    countBadge: Int? = null,
    onClick: () -> Unit
) {
    Box(contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) UnibetGold else TextSecondary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) TextWhite else TextSecondary
            )
        }

        if (badgeText != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 6.dp, y = (-2).dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(AviatorRed)
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
                Text(badgeText, color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Black)
            }
        }

        if (countBadge != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 6.dp, y = (-2).dp)
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(UnibetGreenAccent),
                contentAlignment = Alignment.Center
            ) {
                Text(countBadge.toString(), color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun OddsBox(
    marketCode: String,
    odds: Double,
    trend: OddsTrend = OddsTrend.NEUTRAL,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) OddsBoxSelected else OddsBoxBg,
        label = "oddsBg"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(
                1.dp,
                if (isSelected) UnibetGreenHover else BorderSubtle,
                RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = marketCode,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else TextSecondary
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = String.format("%.2f", odds),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isSelected) UnibetGold else OddsTextNormal
                )

                if (trend != OddsTrend.NEUTRAL) {
                    Icon(
                        imageVector = if (trend == OddsTrend.UP) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = if (trend == OddsTrend.UP) OddsUpGreen else LiveRed,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MatchEventCard(
    event: SportEvent,
    selectedMarket: String?,
    onOddsSelect: (market: String, odds: Double) -> Unit,
    onExpandMarkets: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = BorderStroke(1.dp, BorderSubtle)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: League & Live Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = event.leagueFlag, fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = event.league,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (event.isLive) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(LiveRed.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(LiveRed)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = event.minute ?: "EN DIRECT",
                            color = LiveRed,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                } else {
                    Text(
                        text = event.startTime,
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Teams & Score
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = event.homeTeam,
                        fontSize = 14.sp,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = event.awayTeam,
                        fontSize = 14.sp,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (event.isLive && event.scoreHome != null && event.scoreAway != null) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(CardSurfaceHover)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${event.scoreHome} - ${event.scoreAway}",
                            fontSize = 16.sp,
                            color = UnibetGold,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 1X2 Primary Odds
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OddsBox(
                    marketCode = "1",
                    odds = event.oddsHome,
                    trend = event.oddsHomeTrend,
                    isSelected = selectedMarket == "1",
                    onClick = { onOddsSelect("1", event.oddsHome) },
                    modifier = Modifier.weight(1f)
                )

                if (event.oddsDraw > 0) {
                    OddsBox(
                        marketCode = "X",
                        odds = event.oddsDraw,
                        trend = event.oddsDrawTrend,
                        isSelected = selectedMarket == "X",
                        onClick = { onOddsSelect("X", event.oddsDraw) },
                        modifier = Modifier.weight(1f)
                    )
                }

                OddsBox(
                    marketCode = "2",
                    odds = event.oddsAway,
                    trend = event.oddsAwayTrend,
                    isSelected = selectedMarket == "2",
                    onClick = { onOddsSelect("2", event.oddsAway) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Extra Markets Link
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onExpandMarkets)
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "+85 marchés (Plus/Moins 2.5, Buts, Buteurs)",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
