package com.ndichan.app.core.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val BgPrimary = Color(0xFF0D0E11)
val BgSecondary = Color(0xFF15171C)
val BgTertiary = Color(0xFF1E2128)
val BgCard = Color(0xFF181A20)
val BgCardElevated = Color(0xFF22252E)
val BgSurface = Color(0xFF121418)

val GoldPrimary = Color(0xFFD4AF37)
val GoldLight = Color(0xFFE8C868)
val GoldDark = Color(0xFFA88922)
val GoldMuted = Color(0xFF4A3E1B)
val GoldAccent = Color(0xFFF0D58C)

val TextPrimary = Color(0xFFF6F7F9)
val TextSecondary = Color(0xFFA0A3AB)
val TextTertiary = Color(0xFF6B6E78)
val TextMuted = Color(0xFF484B54)

val BorderSubtle = Color(0xFF252832)
val BorderGold = Color(0x33D4AF37)
val DividerColor = Color(0xFF1A1D24)

val GlassBackground = Color(0xCC15171C)
val OverlayGradient = Brush.verticalGradient(
    colors = listOf(
        Color.Transparent,
        Color(0x990D0E11),
        Color(0xFA0D0E11)
    )
)

val GoldGradient = Brush.horizontalGradient(
    colors = listOf(
        GoldDark,
        GoldPrimary,
        GoldLight
    )
)

val CardGoldGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF242017),
        Color(0xFF181A20)
    )
)
