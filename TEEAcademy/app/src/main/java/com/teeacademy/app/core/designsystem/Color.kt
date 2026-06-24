package com.teeacademy.app.core.designsystem

import androidx.compose.ui.graphics.Color

/**
 * Fixed clinical palette per UX spec Section 11 (Dark Mode Rules):
 * dynamic/wallpaper-derived color is intentionally NOT used, so that
 * severity badges (mild/moderate/severe) and MK/ADV labels stay
 * consistent regardless of device theme.
 */

// Brand / neutral
val BrandPrimary = Color(0xFF0B5F73)        // deep clinical teal
val BrandPrimaryDark = Color(0xFF4FB8D6)
val SurfaceLight = Color(0xFFFAFAFA)
val SurfaceDark = Color(0xFF121417)
// Slightly-off-black for dark mode image mattes (never pure black, see UX spec 11)
val ImageMatteDark = Color(0xFF1C1F22)
val ImageMatteLight = Color(0xFFE9ECEE)

// Severity / label colors — must hold WCAG AA contrast in both themes
val SeverityMild = Color(0xFF2E7D32)
val SeverityModerate = Color(0xFFB8860B)
val SeveritySevere = Color(0xFFC62828)
val SeverityVerySevere = Color(0xFF8E0000)

val LabelMustKnowBg = Color(0xFF0B5F73)
val LabelMustKnowFg = Color(0xFFFFFFFF)
val LabelAdvancedBg = Color(0xFFFFFFFF)
val LabelAdvancedFg = Color(0xFF0B5F73)
val LabelAdvancedBorder = Color(0xFF0B5F73)

// Callouts
val KeyPointBg = Color(0xFFE7F1F5)
val KeyPointBgDark = Color(0xFF173138)
val PitfallBg = Color(0xFFFFF4E0)
val PitfallBgDark = Color(0xFF3A2F12)
val PitfallAccent = Color(0xFFB8860B)
