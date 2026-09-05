package com.ndichan.app.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ndichan.app.core.components.NDiChanTopBar
import com.ndichan.app.core.theme.BgCard
import com.ndichan.app.core.theme.BgPrimary
import com.ndichan.app.core.theme.BorderSubtle
import com.ndichan.app.core.theme.CardShape
import com.ndichan.app.core.theme.DividerColor
import com.ndichan.app.core.theme.GoldPrimary
import com.ndichan.app.core.theme.PillShape
import com.ndichan.app.core.theme.TextMuted
import com.ndichan.app.core.theme.TextPrimary
import com.ndichan.app.core.theme.TextSecondary
import java.util.Locale

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val cacheSizeMb = String.format(Locale.getDefault(), "%.1f MB", state.cacheSizeBytes.toFloat() / (1024 * 1024))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary)
    ) {
        NDiChanTopBar(title = "Pengaturan")

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 100.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Group: Pemutar Video
            item {
                SettingsGroup(title = "Pemutar Video") {
                    SettingsToggleItem(
                        icon = Icons.Outlined.Tv,
                        title = "Putar Otomatis Episode Berikutnya",
                        subtitle = "Lanjut memutar video selanjutnya saat episode berakhir",
                        checked = state.autoPlayNext,
                        onCheckedChange = { viewModel.toggleAutoPlayNext() }
                    )
                }
            }

            // Group: Pembaca Manga
            item {
                SettingsGroup(title = "Pembaca Manga") {
                    SettingsToggleItem(
                        icon = Icons.Outlined.MenuBook,
                        title = "Mode Gulir Webtoon",
                        subtitle = "Tampilkan halaman komik bersambung vertikal",
                        checked = state.webtoonMode,
                        onCheckedChange = { viewModel.toggleWebtoonMode() }
                    )
                }
            }

            // Group: Penyimpanan & Cache
            item {
                SettingsGroup(title = "Penyimpanan") {
                    SettingsActionItem(
                        icon = Icons.Outlined.CleaningServices,
                        title = "Bersihkan Cache Aplikasi",
                        subtitle = "Ukuran cache saat ini: $cacheSizeMb",
                        actionText = if (state.isCacheCleared) "Dibersihkan" else "Hapus",
                        onActionClick = { viewModel.clearCache() }
                    )
                }
            }

            // Group: Tentang Aplikasi
            item {
                SettingsGroup(title = "Tentang Aplikasi") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.dp, GoldPrimary, RoundedCornerShape(12.dp))
                            ) {
                                androidx.compose.foundation.Image(
                                    painter = androidx.compose.ui.res.painterResource(id = com.ndichan.app.R.drawable.app_logo),
                                    contentDescription = "NDiChan Logo",
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = "NDiChan",
                                    color = TextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Versi 1.0.0 (Gold Premium Edition)",
                                    color = GoldPrimary,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(0.5.dp)
                                .background(DividerColor)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Aplikasi streaming anime dan reader manga modern dengan desain Dark Gold Premium, arsitektur Clean & MVVM, Jetpack Compose, Media3 ExoPlayer, dan Room Database.",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsGroup(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            color = GoldPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 6.dp, bottom = 8.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CardShape)
                .background(BgCard)
                .border(1.dp, BorderSubtle, CardShape)
        ) {
            content()
        }
    }
}

@Composable
private fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = GoldPrimary,
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                color = TextMuted,
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = BgPrimary,
                checkedTrackColor = GoldPrimary,
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = Color(0xFF22252E)
            )
        )
    }
}

@Composable
private fun SettingsActionItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    actionText: String,
    onActionClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onActionClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = GoldPrimary,
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                color = TextMuted,
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .clip(PillShape)
                .background(Color(0x33D4AF37))
                .clickable { onActionClick() }
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(
                text = actionText,
                color = GoldPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
