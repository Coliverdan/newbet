package com.newbet.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.newbet.app.data.model.CasinoGame
import com.newbet.app.ui.theme.*

@Composable
fun CasinoScreen(
    casinoGames: List<CasinoGame> = emptyList(),
    userBalanceXOF: Long = 0L,
    onWinCasino: (Long) -> Unit = {},
    onBetCasino: (Long) -> Boolean = { true },
    onNavigateToSports: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgCanvas)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Big Lock Icon with Halo
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color(0xFF1E2220))
                .border(2.dp, UnibetGold.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Casino Indisponible",
                tint = UnibetGold,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Big Bold Status Badge
        Surface(
            color = UnibetGold.copy(alpha = 0.15f),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, UnibetGold)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.HourglassTop,
                    contentDescription = null,
                    tint = UnibetGold,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "SECTION CASINO INDISPONIBLE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = UnibetGold,
                    letterSpacing = 1.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Main Title
        Text(
            text = "BIENTÔT DISPONIBLE",
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = TextWhite,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Detailed Explanation
        Text(
            text = "Conformément à la réglementation et aux normes de sécurité des jeux d'argent, nos machines à sous, jeux de table et le jeu Aviator sont actuellement en phase d'audit technique et d'homologation légale.",
            fontSize = 14.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Feature / Audit Status Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Statut du déploiement :",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = UnibetNeon,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Générateur de nombres aléatoires certifié (RNG)",
                        fontSize = 13.sp,
                        color = TextWhite
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = UnibetNeon,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Intégration fournisseurs (Spribe Aviator, Pragmatic)",
                        fontSize = 13.sp,
                        color = TextWhite
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.HourglassTop,
                        contentDescription = null,
                        tint = UnibetGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Validation de conformité des paiements en cours",
                        fontSize = 13.sp,
                        color = UnibetGold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Button redirect to Sports
        Button(
            onClick = onNavigateToSports,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = UnibetGold),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.SportsSoccer,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "ACCÉDER AUX PARIS SPORTIFS",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}
