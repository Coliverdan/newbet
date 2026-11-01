package com.newbet.app.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.newbet.app.data.model.CasinoGame
import com.newbet.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun CasinoScreen(
    casinoGames: List<CasinoGame>,
    userBalanceXOF: Long,
    onWinCasino: (Long) -> Unit,
    onBetCasino: (Long) -> Boolean
) {
    var selectedCategory by remember { mutableStateOf("all") }
    val context = LocalContext.current

    val categories = remember {
        listOf(
            "all" to "🔥 Tous les jeux",
            "crash" to "✈️ Crash Games",
            "slots" to "🎰 Machines à sous",
            "live" to "🎲 Live Casino",
            "mini" to "💣 Mini-Jeux"
        )
    }

    val filteredGames = remember(selectedCategory, casinoGames) {
        when (selectedCategory) {
            "crash" -> casinoGames.filter { it.category.equals("Crash", ignoreCase = true) }
            "slots" -> casinoGames.filter { it.category.equals("Slots", ignoreCase = true) }
            "live" -> casinoGames.filter { it.category.equals("Live", ignoreCase = true) }
            "mini" -> casinoGames.filter { it.category.equals("Mini-Jeux", ignoreCase = true) }
            else -> casinoGames
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BgCanvas),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Aviator Live Crash Showcase Simulator
        item {
            AviatorLiveSimulator(
                userBalanceXOF = userBalanceXOF,
                onWin = onWinCasino,
                onDeductBet = onBetCasino
            )
        }

        // Category Filter Pills
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { (key, label) ->
                    val isSelected = selectedCategory == key
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) UnibetGreenAccent else CardSurface)
                            .border(
                                1.dp,
                                if (isSelected) UnibetNeon else BorderSubtle,
                                RoundedCornerShape(20.dp)
                            )
                            .clickable { selectedCategory = key }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) TextWhite else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Section Title: Catalog
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CATALOGUE OFFICIEL (${filteredGames.size})",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Fournisseurs Certifiés",
                    color = UnibetGreenHover,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Casino Games Grid
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val chunked = filteredGames.chunked(2)
                chunked.forEach { rowGames ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowGames.forEach { game ->
                            Box(modifier = Modifier.weight(1f)) {
                                CasinoGameCard(
                                    game = game,
                                    onPlayClick = {
                                        Toast.makeText(
                                            context,
                                            "Lancement du jeu sécurisé: ${game.title} (${game.provider})",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                )
                            }
                        }
                        if (rowGames.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // Responsible Gaming Footer Note
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, BorderSubtle)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🛡️", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Générateur de Nombres Aléatoires (RNG) Certifié",
                            color = TextWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Tous nos jeux sont audités par eCOGRA et GLI. Taux de redistribution vérifié.",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * Interactive Aviator Crash Game Simulator
 */
@Composable
fun AviatorLiveSimulator(
    userBalanceXOF: Long,
    onWin: (Long) -> Unit,
    onDeductBet: (Long) -> Boolean
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var betAmount by remember { mutableStateOf(1000L) }
    var currentMultiplier by remember { mutableStateOf(1.00) }
    var gameState by remember { mutableStateOf("IDLE") } // IDLE, RUNNING, CASHED_OUT, CRASHED
    var crashPoint by remember { mutableStateOf(2.50) }
    var wonAmount by remember { mutableStateOf(0L) }

    // Crash Loop Animation
    LaunchedEffect(gameState) {
        if (gameState == "RUNNING") {
            currentMultiplier = 1.00
            val targetCrash = (150..550).random() / 100.0 // random between 1.50x and 5.50x
            crashPoint = targetCrash

            while (currentMultiplier < crashPoint && (gameState == "RUNNING" || gameState == "CASHED_OUT")) {
                delay(60)
                currentMultiplier += 0.04 + (currentMultiplier * 0.015)

                if (currentMultiplier >= crashPoint) {
                    if (gameState != "CASHED_OUT") {
                        gameState = "CRASHED"
                    }
                    break
                }
            }

            if (gameState == "CRASHED" || gameState == "CASHED_OUT") {
                delay(3000)
                gameState = "IDLE"
                currentMultiplier = 1.00
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE52538).copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header: Title & Provider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "✈️", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "AVIATOR",
                        color = Color(0xFFE52538),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFE52538).copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "LIVE CRASH",
                            color = Color(0xFFE52538),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = "Spribe Gaming • 97% RTP",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Flight Radar Simulation Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F0B10)),
                contentAlignment = Alignment.Center
            ) {
                // Background Radar Grid lines
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Grid lines
                    drawLine(
                        color = Color.White.copy(alpha = 0.05f),
                        start = Offset(0f, h * 0.25f),
                        end = Offset(w, h * 0.25f),
                        strokeWidth = 1f
                    )
                    drawLine(
                        color = Color.White.copy(alpha = 0.05f),
                        start = Offset(0f, h * 0.5f),
                        end = Offset(w, h * 0.5f),
                        strokeWidth = 1f
                    )
                    drawLine(
                        color = Color.White.copy(alpha = 0.05f),
                        start = Offset(0f, h * 0.75f),
                        end = Offset(w, h * 0.75f),
                        strokeWidth = 1f
                    )

                    // Trajectory flight curve if running
                    if (gameState == "RUNNING" || gameState == "CASHED_OUT" || gameState == "CRASHED") {
                        val progress = (currentMultiplier - 1.0) / 4.0
                        val clampedProgress = progress.coerceIn(0.0, 1.0).toFloat()

                        val endX = w * (0.2f + (0.7f * clampedProgress))
                        val endY = h * (0.85f - (0.65f * clampedProgress))

                        val path = Path().apply {
                            moveTo(w * 0.1f, h * 0.85f)
                            quadraticBezierTo(
                                w * 0.3f, h * 0.85f,
                                endX, endY
                            )
                        }

                        drawPath(
                            path = path,
                            color = if (gameState == "CRASHED") Color(0xFFE52538) else Color(0xFFFFD000),
                            style = Stroke(width = 4f)
                        )
                    }
                }

                // Center Multiplier display
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    when (gameState) {
                        "IDLE" -> {
                            Text(
                                text = "ATTENTE DU PROCHAIN VOL",
                                color = TextMuted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Préparez votre mise",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                        "RUNNING" -> {
                            Text(
                                text = String.format("%.2fx", currentMultiplier),
                                color = TextWhite,
                                fontSize = 38.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "L'AVION S'ENVOLE !",
                                color = UnibetGreenHover,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                        "CASHED_OUT" -> {
                            Text(
                                text = String.format("%.2fx", currentMultiplier),
                                color = UnibetGold,
                                fontSize = 38.sp,
                                fontWeight = FontWeight.Black
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(UnibetGreenDark)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "GAGNÉ: +$wonAmount F CFA",
                                    color = TextWhite,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                        "CRASHED" -> {
                            Text(
                                text = "PARTI EN VOL !",
                                color = Color(0xFFE52538),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "@ ${String.format("%.2fx", crashPoint)}",
                                color = Color(0xFFE52538),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action row: Stake chips & Parier / Encaisser button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Preset chips
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(500L, 1000L, 2500L, 5000L).forEach { amount ->
                            val isSel = betAmount == amount
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) UnibetGreenAccent else CardSurfaceElevated)
                                    .border(
                                        1.dp,
                                        if (isSel) UnibetGold else BorderSubtle,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable(enabled = gameState == "IDLE") {
                                        betAmount = amount
                                    }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${amount / 1000}k",
                                    color = if (isSel) TextWhite else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // CTA Button (Parier ou Encaisser)
                Box(modifier = Modifier.weight(1.2f)) {
                    when (gameState) {
                        "IDLE" -> {
                            Button(
                                onClick = {
                                    val success = onDeductBet(betAmount)
                                    if (success) {
                                        gameState = "RUNNING"
                                    } else {
                                        Toast.makeText(context, "Solde insuffisant pour miser", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFE52538),
                                    contentColor = TextWhite
                                )
                            ) {
                                Text(
                                    text = "PARIER $betAmount F",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                        "RUNNING" -> {
                            val liveCashout = (betAmount * currentMultiplier).toLong()
                            Button(
                                onClick = {
                                    wonAmount = liveCashout
                                    gameState = "CASHED_OUT"
                                    onWin(liveCashout)
                                    Toast.makeText(context, "🎉 Encaissé: +$liveCashout F CFA !", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = UnibetGold,
                                    contentColor = Color.Black
                                )
                            ) {
                                Text(
                                    text = "ENCAISSER $liveCashout F",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                        "CASHED_OUT" -> {
                            Button(
                                onClick = {},
                                enabled = false,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    disabledContainerColor = UnibetGreenDark,
                                    disabledContentColor = TextWhite
                                )
                            ) {
                                Text("ENCAISSÉ AVEC SUCCÈS", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        "CRASHED" -> {
                            Button(
                                onClick = {},
                                enabled = false,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    disabledContainerColor = CardSurfaceElevated,
                                    disabledContentColor = TextMuted
                                )
                            ) {
                                Text("NOUVELLE MANCHE...", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Game card for slots and live casino
 */
@Composable
fun CasinoGameCard(
    game: CasinoGame,
    onPlayClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onPlayClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, BorderSubtle)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Visual header with emoji icon
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(84.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                CardSurfaceElevated,
                                CardSurface
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = game.iconEmoji, fontSize = 42.sp)

                // Hot Badge
                if (game.isHot) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFE52538))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "HOT",
                            color = TextWhite,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                // Category Tag
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = game.category,
                        color = TextWhite,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Game details
            Text(
                text = game.title,
                color = TextWhite,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = game.provider,
                    color = TextMuted,
                    fontSize = 10.sp
                )
                Text(
                    text = "RTP ${game.rtp}",
                    color = UnibetGreenHover,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Play Button
            Button(
                onClick = onPlayClick,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = UnibetGreenAccent,
                    contentColor = TextWhite
                ),
                contentPadding = PaddingValues(vertical = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
            ) {
                Text(
                    text = "JOUER",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}
