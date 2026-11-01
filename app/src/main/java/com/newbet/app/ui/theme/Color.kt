package com.newbet.app.ui.theme

import androidx.compose.ui.graphics.Color

// Authentic Unibet Color Palette
// 1. Signature Brand Colors
val UnibetGreenHeader = Color(0xFF005A36)   // Rich Unibet Forest Green for Header
val UnibetGreenDark = Color(0xFF004428)     // Deep shade for top bar accents
val UnibetGreenAccent = Color(0xFF107B48)   // Active green tab / Odds selected
val UnibetGreenHover = Color(0xFF169A5A)    // Vibrant live green
val UnibetNeon = Color(0xFF22C55E)          // Vibrant neon green for odds & badges
val UnibetGold = Color(0xFFFFD000)          // Unibet Signature Yellow CTA Button ("S'inscrire")
val UnibetGoldDark = Color(0xFFE5BA00)

// 2. Crisp, Neutral Dark/Light Theme Colors (Zero muddy green in content)
// True neutral dark background - Clean, high contrast, readable
val BgCanvas = Color(0xFF101211)            // Neutral dark canvas (Not green!)
val CardSurface = Color(0xFF1C1F1E)         // Clean neutral card surface
val CardSurfaceHover = Color(0xFF262B29)    // Hover card surface
val CardSurfaceElevated = Color(0xFF262B29) // Elevated card surface
val BorderSubtle = Color(0xFF2E3532)        // Subtle neutral border

// 3. Text & Typography Colors
val TextWhite = Color(0xFFFFFFFF)           // Primary header text
val TextSecondary = Color(0xFF9EABA4)       // Secondary labels & league names
val TextMuted = Color(0xFF6B7872)           // Hint text & timestamps

// 4. Odds & Badges
val OddsBoxBg = Color(0xFF242927)           // Inactive odds button container
val OddsBoxSelected = Color(0xFF107B48)     // Active odds button (Unibet green)
val OddsTextActive = Color(0xFFFFFFFF)
val OddsTextNormal = Color(0xFFE6EAE8)

val LiveRed = Color(0xFFEF4444)             // Live match dot / odds drop
val OddsDownRed = Color(0xFFEF4444)
val OddsUpGreen = Color(0xFF22C55E)         // Odds rising
val CasinoPurple = Color(0xFF7C3AED)        // Casino highlights
val AviatorRed = Color(0xFFE11D48)          // Aviator crash game accent

// Compatibility aliases for legacy screens
val BackgroundDark = BgCanvas
val SurfaceDark = CardSurface
val SurfaceDarkVariant = CardSurfaceElevated
val UnibetGreen = UnibetGreenAccent
