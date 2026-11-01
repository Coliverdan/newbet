package com.newbet.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.newbet.app.ui.theme.*

@Composable
fun MenuScreen(
    userBalanceXOF: Long,
    bonusBalanceXOF: Long = 5000L,
    onDeposit: (Long) -> Unit,
    onWithdraw: (Long) -> Boolean,
    onNavigateToPartners: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = remember { context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }

    var showDepositDialog by remember { mutableStateOf(false) }
    var showWithdrawDialog by remember { mutableStateOf(false) }
    var showLimitsDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BgCanvas),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Profile Header Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, BorderSubtle)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar Initials with Gold Ring
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(UnibetGreenDark)
                            .border(2.dp, UnibetGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "CN",
                            color = TextWhite,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Christian N.",
                                color = TextWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Vérifié",
                                tint = UnibetGreenHover,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                clipboardManager.setPrimaryClip(ClipData.newPlainText("UserID", "NB-948210"))
                                Toast.makeText(context, "ID Joueur copié : NB-948210", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Text(
                                text = "ID: NB-948210",
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copier",
                                tint = TextMuted,
                                modifier = Modifier.size(12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(UnibetGreenDark)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "KYC NIVEAU 2 • VÉRIFIÉ",
                                color = UnibetGreenHover,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }

        // 2. Balances Breakdown Card (Solde Réel + Bonus)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, UnibetGreenDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "PORTEFEUILLE JOUEUR",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Solde Retirable (Cash)", color = TextSecondary, fontSize = 11.sp)
                            Text(
                                text = "${String.format("%,d", userBalanceXOF)} F CFA",
                                color = UnibetGold,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Paris Gratuits (Bonus)", color = TextSecondary, fontSize = 11.sp)
                            Text(
                                text = "${String.format("%,d", bonusBalanceXOF)} F CFA",
                                color = UnibetGreenHover,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Dépôt & Retrait Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { showDepositDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = UnibetGold,
                                contentColor = Color.Black
                            )
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("DÉPÔT", fontSize = 12.sp, fontWeight = FontWeight.Black)
                        }

                        OutlinedButton(
                            onClick = { showWithdrawDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                            border = androidx.compose.foundation.BorderStroke(1.dp, UnibetGreenAccent)
                        ) {
                            Icon(imageVector = Icons.Default.NorthEast, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("RETRAIT", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 3. Mobile Money Methods (Wave, MTN, Orange, Moov)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PAIEMENT MOBILE MONEY SÉCURISÉ",
                            color = TextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Frais 0% • Instantané",
                            color = UnibetGreenHover,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PaymentProviderChip(name = "Wave", icon = "🌊", sub = "0% Frais", modifier = Modifier.weight(1f)) {
                            showDepositDialog = true
                        }
                        PaymentProviderChip(name = "MTN MoMo", icon = "🟡", sub = "*880#", modifier = Modifier.weight(1f)) {
                            showDepositDialog = true
                        }
                        PaymentProviderChip(name = "Orange", icon = "🟠", sub = "*144#", modifier = Modifier.weight(1f)) {
                            showDepositDialog = true
                        }
                        PaymentProviderChip(name = "Moov", icon = "🔵", sub = "*155#", modifier = Modifier.weight(1f)) {
                            showDepositDialog = true
                        }
                    }
                }
            }
        }

        // 4. Parrainage d'Amis (Viral Player-to-Player Referral)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, UnibetGold.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🎁", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "PARRAINE TES AMIS",
                                color = UnibetGold,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.8.sp
                            )
                            Text(
                                text = "Gagnez 1 000 F de Freebet chacun !",
                                color = TextWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Partage ton code joueur à tes amis lors de leur inscription. Dès leur premier pari, vous recevez tous les deux un pari gratuit de 1 000 F CFA sans condition !",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Promo Code Box with Copy
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(CardSurfaceElevated)
                            .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Ton code de parrainage", color = TextMuted, fontSize = 9.sp)
                            Text(
                                "NEW-CHRIS24",
                                color = TextWhite,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        }

                        Button(
                            onClick = {
                                clipboardManager.setPrimaryClip(ClipData.newPlainText("PromoCode", "NEW-CHRIS24"))
                                Toast.makeText(context, "Code NEW-CHRIS24 copié !", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = UnibetGreenAccent,
                                contentColor = TextWhite
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("COPIER", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 5. Account Settings & Security Options
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    AccountMenuRow(
                        icon = Icons.Default.History,
                        title = "Historique des Transactions",
                        subtitle = "Dépôts, retraits et gains de paris",
                        onClick = {
                            Toast.makeText(context, "Historique : 3 dépôts, 2 retraits validés", Toast.LENGTH_SHORT).show()
                        }
                    )
                    HorizontalDivider(color = BorderSubtle, modifier = Modifier.padding(horizontal = 14.dp))

                    AccountMenuRow(
                        icon = Icons.Default.Shield,
                        title = "Sécurité & 2FA",
                        subtitle = "Empreinte digitale / Face ID activée",
                        onClick = {
                            Toast.makeText(context, "Paramètres de sécurité biométrique actifs", Toast.LENGTH_SHORT).show()
                        }
                    )
                    HorizontalDivider(color = BorderSubtle, modifier = Modifier.padding(horizontal = 14.dp))

                    AccountMenuRow(
                        icon = Icons.Default.Balance,
                        title = "Jeu Responsable & Limites",
                        subtitle = "Plafonds de mise et auto-exclusion",
                        onClick = { showLimitsDialog = true }
                    )
                    HorizontalDivider(color = BorderSubtle, modifier = Modifier.padding(horizontal = 14.dp))

                    AccountMenuRow(
                        icon = Icons.Default.HeadsetMic,
                        title = "Support Client 24/7",
                        subtitle = "Live Chat & WhatsApp (+229 90 00 00 00)",
                        onClick = {
                            Toast.makeText(context, "Ouverture du Live Chat Newbet...", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }

        // 6. Discreet B2B Partner Portal Link (Dedicated for influencers / site owners)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
                    .clickable(onClick = onNavigateToPartners),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, BorderSubtle)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.BusinessCenter,
                        contentDescription = null,
                        tint = UnibetGreenHover,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Devenir Partenaire Affilié Pro (B2B)",
                            color = TextWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Pour webmasters, tipsters et influenceurs (RevShare 40%)",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // 7. Version & Logout
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Newbet Native Android • v3.1.0 • Licence Loterie Nationale",
                    color = TextMuted,
                    fontSize = 10.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = {
                        Toast.makeText(context, "Déconnexion sécurisée...", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF87171)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF87171).copy(alpha = 0.5f))
                ) {
                    Icon(imageVector = Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("SE DÉCONNECTER", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Deposit Dialog
    if (showDepositDialog) {
        var depositAmountInput by remember { mutableStateOf("5000") }
        var selectedMethod by remember { mutableStateOf("Wave") }

        AlertDialog(
            onDismissRequest = { showDepositDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.AccountBalanceWallet, contentDescription = null, tint = UnibetGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Dépôt Mobile Money", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text("Choisissez votre méthode de recharge :", color = TextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Wave", "MTN", "Orange", "Moov").forEach { m ->
                            val isSel = selectedMethod == m
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) UnibetGreenAccent else CardSurfaceElevated)
                                    .border(1.dp, if (isSel) UnibetGold else BorderSubtle, RoundedCornerShape(6.dp))
                                    .clickable { selectedMethod = m }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = m,
                                    color = if (isSel) TextWhite else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = depositAmountInput,
                        onValueChange = { depositAmountInput = it },
                        label = { Text("Montant (XOF)", color = TextSecondary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("1000", "2000", "5000", "10000").forEach { quick ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CardSurfaceElevated)
                                    .clickable { depositAmountInput = quick }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(quick, color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = depositAmountInput.toLongOrNull() ?: 0L
                        if (amt >= 500) {
                            onDeposit(amt)
                            showDepositDialog = false
                            Toast.makeText(context, "Dépôt de $amt F CFA via $selectedMethod validé instantanément !", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "Montant minimum: 500 F CFA", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = UnibetGold, contentColor = Color.Black)
                ) {
                    Text("Valider le dépôt", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDepositDialog = false }) {
                    Text("Annuler", color = TextSecondary)
                }
            },
            containerColor = CardSurface
        )
    }

    // Withdraw Dialog
    if (showWithdrawDialog) {
        var withdrawAmountInput by remember { mutableStateOf("5000") }
        var phoneNumberInput by remember { mutableStateOf("97000000") }

        AlertDialog(
            onDismissRequest = { showWithdrawDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.NorthEast, contentDescription = null, tint = UnibetGreenHover)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Retrait Mobile Money", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text("Entrez le montant à retirer vers votre compte Wave / MTN / Orange :", color = TextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = withdrawAmountInput,
                        onValueChange = { withdrawAmountInput = it },
                        label = { Text("Montant (XOF)", color = TextSecondary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = phoneNumberInput,
                        onValueChange = { phoneNumberInput = it },
                        label = { Text("Numéro Mobile Money", color = TextSecondary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = withdrawAmountInput.toLongOrNull() ?: 0L
                        val success = onWithdraw(amt)
                        if (success) {
                            showWithdrawDialog = false
                            Toast.makeText(context, "Retrait de $amt F CFA envoyé vers le $phoneNumberInput !", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "Solde insuffisant pour ce retrait.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = UnibetGreenAccent, contentColor = TextWhite)
                ) {
                    Text("Confirmer le retrait", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showWithdrawDialog = false }) {
                    Text("Annuler", color = TextSecondary)
                }
            },
            containerColor = CardSurface
        )
    }

    // Responsible Gaming Limits Dialog
    if (showLimitsDialog) {
        AlertDialog(
            onDismissRequest = { showLimitsDialog = false },
            title = { Text("Jeu Responsable", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        "Chez Newbet, votre bien-être est notre priorité. Définissez vos limites de sécurité :",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("• Plafond de dépôt hebdomadaire : 50 000 F CFA", color = TextWhite, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("• Limite de temps par session : 60 minutes", color = TextWhite, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("• Auto-exclusion temporaire : Disponible 24h/24", color = TextWhite, fontSize = 12.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLimitsDialog = false
                        Toast.makeText(context, "Limites enregistrées avec succès.", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = UnibetGold, contentColor = Color.Black)
                ) {
                    Text("Enregistrer")
                }
            },
            containerColor = CardSurface
        )
    }
}

@Composable
private fun PaymentProviderChip(
    name: String,
    icon: String,
    sub: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(CardSurfaceElevated)
            .border(0.5.dp, BorderSubtle, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icon, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(name, color = TextWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(sub, color = TextMuted, fontSize = 8.sp)
        }
    }
}

@Composable
private fun AccountMenuRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(CardSurfaceElevated),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = UnibetGold,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = TextWhite,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                color = TextSecondary,
                fontSize = 11.sp
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(18.dp)
        )
    }
}

