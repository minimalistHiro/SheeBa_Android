package com.hiroki.sheeba.data

import androidx.compose.ui.graphics.painter.Painter

data class BottomNavigationItem(
    val title: String,
    val navTitle: String,
    val selectedIcon: Painter,
    val unselectedIcon: Painter,
)