package com.newbet.app.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.newbet.app.data.model.BetStatus
import com.newbet.app.data.model.PlacedBet
import com.newbet.app.ui.theme.*

@Composable
fun MyBetsScreen(
    placedBets: List<PlacedBet>,
    onCashOutClick: (String) -> Unit,
    onNavigateToSports: () -> Unit
) {
    var selectedSubTab by remember { mutableStateOf("pending") } // pending, settled
    val context = LocalContext.current

    val pendingBets = remember(placedBets) {
        placedBets.filter { it.status == BetStatus.PENDING }
    }

    val settledBets = remember(placedBets) {
        placedBets.filter { it.status != BetStatus.PENDING }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgCanvas)
    ) {
        // Sub-Navigation Tabs: "En cours" vs "Terminés"
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
                // Pending Tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selectedSubTab == "pending") UnibetGreenAccent else CardSurfaceElevated)
                        .clickable { selectedSubTab = "pending" }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "En Cours",
                            color = if (selectedSubTab == "pending") TextWhite else TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = if (selectedSubTab == "pending") FontWeight.Bold else FontWeight.Medium
                        )
                        if (pendingBets.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(if (selectedSubTab == "pending") UnibetGold else BorderSubtle)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = pendingBets.size.toString(),
                                    color = if (selectedSubTab == "pending") Color.Black else TextMuted,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }

                // Settled Tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selectedSubTab == "settled") UnibetGreenAccent else CardSurfaceElevated)
                        .clickable { selectedSubTab = "settled" }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Terminés (${settledBets.size})",
                        color = if (selectedSubTab == "settled") TextWhite else TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = if (selectedSubTab == "settled") FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        // Bets List
        val currentList = if (selectedSubTab == "pending") pendingBets else settledBets

        if (currentList.isEmpty()) {
            // Empty State
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ReceiptLong,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = if (selectedSubTab == "pending") "Aucun pari en cours" else "Aucun pari terminé",
                    color = TextWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (selectedSubTab == "pending")
                        "Vos paris simples et combinés actifs apparaîtront ici avec l'option de Cash Out."
                    else
                        "L'historique de vos gains et paris réglés sera listé ici.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onNavigateToSports,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = UnibetGold,
                        contentColor = Color.Black
                    )
                ) {
                    Text("PARIER MAINTENANT", fontSize = 12.sp, fontWeight = FontWeight.Black)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(currentList, key = { it.id }) { bet ->
                    PlacedBetCard(
                        bet = bet,
                        onCashOut = {
                            onCashOutClick(bet.id)
                            Toast.makeText(
                                context,
                                "Cash Out effectué ! ${bet.cashOutAmountXOF} F CFA crédités à votre solde.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PlacedBetCard(
    bet: PlacedBet,
    onCashOut: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, BorderSubtle)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: ID, Date & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = bet.id,
                        color = TextWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = bet.date,
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }

                // Status Badge
                when (bet.status) {
                    BetStatus.PENDING -> {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(UnibetGreenDark)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "EN COURS",
                                color = UnibetGreenHover,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    BetStatus.WON -> {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF135B29))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "GAGNÉ",
                                color = Color(0xFF4ADE80),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                    BetStatus.LOST -> {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF58151C))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "PERDU",
                                color = Color(0xFFF87171),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    BetStatus.CASHED_OUT -> {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(CardSurfaceElevated)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "CASH OUT EFFECTUÉ",
                                color = UnibetGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            HorizontalDivider(
                color = BorderSubtle,
                modifier = Modifier.padding(vertical = 10.dp)
            )

            // Selections list
            bet.selections.forEach { selection ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        text = selection.league,
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selection.matchTitle,
                            color = TextWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = String.format("%.2f", selection.odds),
                            color = UnibetGold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Text(
                        text = "Choix : ${selection.marketLabel}",
                        color = UnibetGreenHover,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            HorizontalDivider(
                color = BorderSubtle,
                modifier = Modifier.padding(vertical = 10.dp)
            )

            // Financial Summary Row: Mise, Cote, Gain
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Mise", color = TextMuted, fontSize = 10.sp)
                    Text(
                        "${String.format("%,d", bet.stakeXOF)} F",
                        color = TextWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Cote Totale", color = TextMuted, fontSize = 10.sp)
                    Text(
                        String.format("%.2f", bet.totalOdds),
                        color = UnibetNeon,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        if (bet.status == BetStatus.WON) "Gain Payé" else "Gain Potentiel",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                    Text(
                        "${String.format("%,d", bet.potentialWinXOF)} F CFA",
                        color = if (bet.status == BetStatus.WON) Color(0xFF4ADE80) else UnibetGold,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            // Cash Out Button if available
            if (bet.status == BetStatus.PENDING && bet.canCashOut) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onCashOut,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = UnibetGreenDark,
                        contentColor = TextWhite
                    ),
                    modifier = Modifier.fillMaxWidth().height(42.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, UnibetGreenAccent)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CurrencyExchange,
                                contentDescription = null,
                                tint = UnibetGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "CASH OUT IMMÉDIAT",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Text(
                            text = "${String.format("%,d", bet.cashOutAmountXOF)} F CFA",
                            color = UnibetGold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}

