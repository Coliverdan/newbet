package com.newbet.app.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.newbet.app.data.model.BetSelection
import com.newbet.app.ui.theme.*

@Composable
fun BetSlipScreen(
    selections: List<BetSelection>,
    userBalanceXOF: Long,
    onRemoveSelection: (BetSelection) -> Unit,
    onClearAll: () -> Unit,
    onPlaceBet: (stake: Long, totalOdds: Double) -> Boolean,
    onLoadSharedCoupon: (String) -> Unit,
    onNavigateToSports: () -> Unit,
    onNavigateToMyBets: () -> Unit
) {
    var stakeInput by remember { mutableStateOf("1000") }
    var betType by remember { mutableStateOf("combine") } // simple, combine, systeme
    var showLoadDialog by remember { mutableStateOf(false) }
    var couponCodeInput by remember { mutableStateOf("") }
    val context = LocalContext.current

    val stakeAmount = stakeInput.toLongOrNull() ?: 0L

    val totalOdds = remember(selections) {
        if (selections.isEmpty()) 1.0
        else selections.fold(1.0) { acc, bet -> acc * bet.odds }
    }

    // Accumulator Bonus (e.g. +10% for 3+ events)
    val bonusRate = if (selections.size >= 3) 0.10 else 0.0
    val baseReturn = (stakeAmount * totalOdds).toLong()
    val bonusAmount = (baseReturn * bonusRate).toLong()
    val totalPotentialReturn = baseReturn + bonusAmount

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgCanvas)
    ) {
        // Coupon Header
        Surface(
            color = CardSurface,
            border = androidx.compose.foundation.BorderStroke(0.5.dp, BorderSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ConfirmationNumber,
                        contentDescription = null,
                        tint = UnibetGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "COUPON DE PARI (${selections.size})",
                        color = TextWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { showLoadDialog = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Charger coupon",
                            tint = UnibetGreenHover,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    if (selections.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(4.dp))
                        TextButton(
                            onClick = onClearAll,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Vider", color = Color(0xFFF87171), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        if (selections.isEmpty()) {
            // Empty State
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ReceiptLong,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(60.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Votre coupon est vide",
                    color = TextWhite,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Sélectionnez des cotes en direct ou chargez le coupon partagé par un pronostiqueur.",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Load shared coupon card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showLoadDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, BorderSubtle)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = null,
                            tint = UnibetGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Charger un code coupon",
                                color = TextWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Collez le code reçu sur Telegram ou WhatsApp (ex: NEW-UCL3)",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onNavigateToSports,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = UnibetGold,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                ) {
                    Text("VOIR LES MATCHS EN DIRECT", fontSize = 12.sp, fontWeight = FontWeight.Black)
                }
            }
        } else {
            // Selections list & Place Bet summary
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp),
                contentPadding = PaddingValues(top = 10.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Bet Type Switcher
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(CardSurface)
                            .border(0.5.dp, BorderSubtle, RoundedCornerShape(8.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("simple" to "Simple", "combine" to "Combiné", "systeme" to "Système").forEach { (key, label) ->
                            val isSel = betType == key
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) UnibetGreenAccent else Color.Transparent)
                                    .clickable { betType = key }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSel) TextWhite else TextSecondary
                                )
                            }
                        }
                    }
                }

                // List of match picks
                items(selections, key = { it.eventId + it.marketLabel }) { bet ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = CardSurface),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, BorderSubtle)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = bet.league,
                                    color = TextMuted,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = bet.matchTitle,
                                    color = TextWhite,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = bet.marketLabel,
                                    color = UnibetGreenHover,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(CardSurfaceElevated)
                                        .border(0.5.dp, BorderSubtle, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = String.format("%.2f", bet.odds),
                                        color = UnibetGold,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                IconButton(
                                    onClick = { onRemoveSelection(bet) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Supprimer",
                                        tint = TextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Summary & Checkout Bottom Area
            Surface(
                color = CardSurface,
                tonalElevation = 12.dp,
                modifier = Modifier.fillMaxWidth(),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Total Odds & Accumulator Bonus
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Cote Totale :", color = TextSecondary, fontSize = 13.sp)
                        Text(
                            text = String.format("%.2f", totalOdds),
                            color = UnibetNeon,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    if (bonusRate > 0) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Bonus Combiné (+10%) :", color = UnibetGold, fontSize = 11.sp)
                            Text(
                                text = "+${String.format("%,d", bonusAmount)} F CFA",
                                color = UnibetGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Stake Input & Quick Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = stakeInput,
                            onValueChange = { stakeInput = it },
                            label = { Text("Mise (XOF)", color = TextSecondary, fontSize = 11.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = CardSurfaceElevated,
                                unfocusedContainerColor = CardSurfaceElevated,
                                focusedBorderColor = UnibetGreenAccent,
                                unfocusedBorderColor = BorderSubtle,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            )
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        listOf("500", "1000", "5000", "10000").forEach { quick ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CardSurfaceElevated)
                                    .border(0.5.dp, BorderSubtle, RoundedCornerShape(8.dp))
                                    .clickable { stakeInput = quick }
                                    .padding(horizontal = 8.dp, vertical = 14.dp)
                            ) {
                                Text(quick, color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Potential Win Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Gain Potentiel :", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "${String.format("%,d", totalPotentialReturn)} F CFA",
                            color = UnibetGold,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Place Bet Button
                    Button(
                        onClick = {
                            if (stakeAmount <= 0) {
                                Toast.makeText(context, "Veuillez entrer une mise valide", Toast.LENGTH_SHORT).show()
                            } else {
                                val success = onPlaceBet(stakeAmount, totalOdds)
                                if (success) {
                                    Toast.makeText(
                                        context,
                                        "Pari de ${String.format("%,d", stakeAmount)} F CFA placé avec succès !",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    onNavigateToMyBets()
                                } else {
                                    Toast.makeText(context, "Solde insuffisant ($userBalanceXOF F). Veuillez recharger.", Toast.LENGTH_LONG).show()
                                }
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = UnibetGold,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(
                            text = "PARIER ${String.format("%,d", stakeAmount)} F CFA",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }
    }

    // Load Coupon Dialog
    if (showLoadDialog) {
        AlertDialog(
            onDismissRequest = { showLoadDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.CloudDownload, contentDescription = null, tint = UnibetGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Charger un Coupon Partagé", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text(
                        "Entrez le code de coupon partagé (ex: TOP3, UCL-VIP, MAX-COTE) :",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = couponCodeInput,
                        onValueChange = { couponCodeInput = it.uppercase() },
                        placeholder = { Text("Ex: UCL-VIP", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val code = couponCodeInput.trim()
                        if (code.isNotEmpty()) {
                            onLoadSharedCoupon(code)
                            showLoadDialog = false
                            Toast.makeText(context, "Coupon $code chargé avec succès !", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = UnibetGold, contentColor = Color.Black)
                ) {
                    Text("Charger", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLoadDialog = false }) {
                    Text("Annuler", color = TextSecondary)
                }
            },
            containerColor = CardSurface
        )
    }
}
