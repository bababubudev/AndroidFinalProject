package com.example.mobilefinalproject.dataclass

import androidx.compose.ui.graphics.vector.ImageVector

data class NavItem(
  val title: String,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector,
  val hasNews: Boolean,
  val badgeCount: Int? = null
)

data class NavSidebar(
  val title: String,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector,
  val hasNews: Boolean,
  val badgeCount: Int? = null,
  val route: String? = null
)

data class TabItem(
  val title: String,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector,
)

data class ToggleableInfo(
  val isChecked: Boolean,
  val text: String,
)