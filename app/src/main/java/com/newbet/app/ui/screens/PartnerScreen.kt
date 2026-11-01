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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.newbet.app.data.affiliate.AffiliateManager
import com.newbet.app.ui.theme.*

@Composable
fun PartnerScreen(
    affiliateManager: AffiliateManager,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val stats = remember { affiliateManager.getPartnerMockStats() }
    val partnerLink = "https://newbet.com/r/${stats.partnerCode}"

    var showWithdrawDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .statusBarsPadding()
    ) {
        // Top Header
        Surface(color = UnibetGreen, modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Retour",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "NEWBET PARTNERS",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Programme Ambassadeur & Sous-Affiliation",
                        color = UnibetNeon,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Main Withdrawable Balance Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, UnibetGold),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "SOLDE DE COMMISSIONS RETIRABLE",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "${String.format("%,d", stats.totalWithdrawableXOF)} FCFA",
                        color = UnibetGold,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { showWithdrawDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = UnibetGold,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier.weight(1f).height(44.dp)
                        ) {
                            Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("RETIRER (MOMO/WAVE)", fontSize = 11.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // My Promo Code & Link Box
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = UnibetGreen.copy(alpha = 0.25f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, UnibetGreen),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "VOS OUTILS DE PARRAINAGE",
                        color = UnibetNeon,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Code Promo Copy Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceDark)
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("VOTRE CODE PROMO", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text(stats.partnerCode, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black)
                        }

                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Promo Code", stats.partnerCode))
                                Toast.makeText(context, "Code promo copié !", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copier", tint = UnibetGold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Link Copy Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceDark)
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("LIEN D'INSTALLATION PARTENAIRE", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text(partnerLink, color = UnibetNeon, fontSize = 12.sp, maxLines = 1)
                        }

                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Partner Link", partnerLink))
                                Toast.makeText(context, "Lien copié !", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Partager", tint = UnibetNeon)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sub-affiliation & RevShare Metrics Grid
            Text(
                text = "DÉTAIL DES COMMISSIONS DU MOIS",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "RevShare Directe",
                    rate = "${stats.revSharePercent}% NGR",
                    value = "${String.format("%,d", stats.revShareCommissionXOF)} XOF",
                    subtitle = "${stats.activePlayers} joueurs actifs",
                    icon = Icons.Default.TrendingUp,
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "Sous-Affiliation",
                    rate = "5% MLM à vie",
                    value = "${String.format("%,d", stats.subAffiliateCommissionXOF)} XOF",
                    subtitle = "${stats.subAffiliateCount} sous-affiliés recrutés",
                    icon = Icons.Default.GroupAdd,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Total Inscrits",
                    rate = "Tous temps",
                    value = stats.totalPlayers.toString(),
                    subtitle = "Joueurs liés à vie",
                    icon = Icons.Default.Person,
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "Dépôts Totaux",
                    rate = "Mises générées",
                    value = "${String.format("%,d", stats.totalDepositsXOF / 1_000_000)}M XOF",
                    subtitle = "Flux de jeu global",
                    icon = Icons.Default.Payments,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Withdraw Dialog
    if (showWithdrawDialog) {
        AlertDialog(
            onDismissRequest = { showWithdrawDialog = false },
            title = { Text("Retrait Mobile Money", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        "Vos commissions (${String.format("%,d", stats.totalWithdrawableXOF)} FCFA) seront transférées immédiatement vers votre compte Mobile Money :",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text("• MTN Mobile Money (Bénin / Côte d'Ivoire)", color = Color.White, fontSize = 12.sp)
                    Text("• Moov Money (Flooz)", color = Color.White, fontSize = 12.sp)
                    Text("• Wave (Sénégal / Côte d'Ivoire)", color = Color.White, fontSize = 12.sp)
                    Text("• Orange Money", color = Color.White, fontSize = 12.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showWithdrawDialog = false
                        Toast.makeText(context, "Demande de retrait transmise avec succès !", Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = UnibetGold, contentColor = Color.Black)
                ) {
                    Text("Confirmer le transfert")
                }
            },
            dismissButton = {
                TextButton(onClick = { showWithdrawDialog = false }) {
                    Text("Fermer", color = TextSecondary)
                }
            },
            containerColor = SurfaceDark
        )
    }
}

@Composable
private fun StatCard(
    title: String,
    rate: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceDarkVariant),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                Icon(imageVector = icon, contentDescription = null, tint = UnibetNeon, modifier = Modifier.size(16.dp))
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(rate, color = UnibetGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(6.dp))

            Text(value, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black)

            Text(subtitle, color = TextMuted, fontSize = 10.sp)
        }
    }
}

