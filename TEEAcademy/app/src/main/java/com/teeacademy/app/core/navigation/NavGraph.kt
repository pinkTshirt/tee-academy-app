package com.teeacademy.app.core.navigation

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.teeacademy.app.ui.cases.CaseDetailScreen
import com.teeacademy.app.ui.cases.CaseListScreen
import com.teeacademy.app.ui.figure.FigureScreen
import com.teeacademy.app.ui.glossary.GlossaryScreen
import com.teeacademy.app.ui.home.HomeScreen
import com.teeacademy.app.ui.library.LibraryScreen
import com.teeacademy.app.ui.lesson.LessonScreen
import com.teeacademy.app.ui.module.ModuleScreen
import com.teeacademy.app.ui.quiz.QuizLaunchScreen
import com.teeacademy.app.ui.quiz.QuizResultScreen
import com.teeacademy.app.ui.quiz.QuizRunScreen
import com.teeacademy.app.ui.references.ReferencesScreen
import com.teeacademy.app.ui.search.SearchScreen
import com.teeacademy.app.ui.settings.SettingsScreen
import com.teeacademy.app.ui.video.VideoScreen

private val topLevelIcons = mapOf(
    TopLevelDestination.HOME to Icons.Filled.Home,
    TopLevelDestination.CASES to Icons.Filled.MedicalServices,
    TopLevelDestination.QUIZ to Icons.Filled.Quiz,
    TopLevelDestination.LIBRARY to Icons.Filled.Bookmarks
)

/**
 * Root scaffold. Uses a NavigationRail on wide (tablet) screens and a
 * bottom NavigationBar on narrow (phone) screens, per UX spec Section 10.
 */
@Composable
fun TeeAcademyNavRoot(isWideScreen: Boolean) {
    val navController = rememberNavController()

    if (isWideScreen) {
        Row {
            AppNavRail(navController)
            AppNavHost(navController, Modifier)
        }
    } else {
        Scaffold(
            bottomBar = { AppBottomBar(navController) }
        ) { padding ->
            AppNavHost(navController, Modifier.then(Modifier.padding(padding)))
        }
    }
}

@Composable
private fun AppNavRail(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    NavigationRail {
        TopLevelDestination.entries.forEach { dest ->
            NavigationRailItem(
                selected = currentRoute == dest.route,
                onClick = { navController.navigate(dest.route) },
                icon = { Icon(topLevelIcons[dest]!!, contentDescription = dest.label) },
                label = { Text(dest.label) }
            )
        }
    }
}

@Composable
private fun AppBottomBar(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    NavigationBar {
        TopLevelDestination.entries.forEach { dest ->
            NavigationBarItem(
                selected = currentRoute == dest.route,
                onClick = { navController.navigate(dest.route) },
                icon = { Icon(topLevelIcons[dest]!!, contentDescription = dest.label) },
                label = { Text(dest.label) }
            )
        }
    }
}

@Composable
private fun AppNavHost(navController: NavHostController, modifier: Modifier) {
    NavHost(
        navController = navController,
        startDestination = Dest.Home.route,
        modifier = modifier
    ) {
        composable(Dest.Home.route) {
            HomeScreen(
                onModuleClick = { navController.navigate(Dest.Module.create(it)) },
                onResumeLesson = { navController.navigate(Dest.Lesson.create(it)) },
                onSearchClick = { navController.navigate(Dest.Search.route) },
                onSettingsClick = { navController.navigate(Dest.Settings.route) },
                onGlossaryClick = { navController.navigate(Dest.Glossary.route) },
                onReferencesClick = { navController.navigate(Dest.References.route) },
                onCasesClick = { navController.navigate(Dest.CaseList.route) },
                onQuizClick = { navController.navigate(Dest.QuizLaunch.route) }
            )
        }
        composable(Dest.Module.route) { entry ->
            val moduleId = entry.arguments?.getString("moduleId") ?: return@composable
            ModuleScreen(
                moduleId = moduleId,
                onLessonClick = { navController.navigate(Dest.Lesson.create(it)) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Dest.Lesson.route) { entry ->
            val lessonId = entry.arguments?.getString("lessonId") ?: return@composable
            LessonScreen(
                lessonId = lessonId,
                onFigureClick = { navController.navigate(Dest.Figure.create(it)) },
                onVideoClick = { navController.navigate(Dest.Video.create(it)) },
                onBack = { navController.popBackStack() },
                onNextLesson = { navController.navigate(Dest.Lesson.create(it)) },
                onPreviousLesson = { navController.navigate(Dest.Lesson.create(it)) }
            )
        }
        composable(Dest.Figure.route) { entry ->
            val figureId = entry.arguments?.getString("figureId") ?: return@composable
            FigureScreen(figureId = figureId, onClose = { navController.popBackStack() })
        }
        composable(Dest.Video.route) { entry ->
            val videoId = entry.arguments?.getString("videoId") ?: return@composable
            VideoScreen(videoId = videoId, onClose = { navController.popBackStack() })
        }
        composable(Dest.QuizLaunch.route) {
            QuizLaunchScreen(
                onStartQuiz = { scopeType, scopeId, count, practiceMode ->
                    navController.navigate(Dest.QuizRun.create(scopeType, scopeId, count, practiceMode))
                }
            )
        }
        composable(Dest.QuizRun.route) { entry ->
            val scopeType = entry.arguments?.getString("scopeType") ?: "mixed"
            val scopeId = entry.arguments?.getString("scopeId") ?: ""
            val count = entry.arguments?.getString("count")?.toIntOrNull() ?: 10
            val practiceMode = entry.arguments?.getString("practiceMode")?.toBoolean() ?: true
            QuizRunScreen(
                scopeType = scopeType,
                scopeId = scopeId,
                count = count,
                practiceMode = practiceMode,
                onFinished = { correct, total ->
                    navController.navigate(Dest.QuizResult.create(correct, total)) {
                        popUpTo(Dest.QuizLaunch.route)
                    }
                }
            )
        }
        composable(Dest.QuizResult.route) { entry ->
            val correct = entry.arguments?.getString("correct")?.toIntOrNull() ?: 0
            val total = entry.arguments?.getString("total")?.toIntOrNull() ?: 0
            QuizResultScreen(
                correct = correct,
                total = total,
                onRetake = { navController.popBackStack(Dest.QuizLaunch.route, false) }
            )
        }
        composable(Dest.CaseList.route) {
            CaseListScreen(onCaseClick = { navController.navigate(Dest.CaseDetail.create(it)) })
        }
        composable(Dest.CaseDetail.route) { entry ->
            val caseId = entry.arguments?.getString("caseId") ?: return@composable
            CaseDetailScreen(
                caseId = caseId,
                onLessonRefClick = { navController.navigate(Dest.Lesson.create(it)) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Dest.Search.route) {
            SearchScreen(
                onLessonClick = { navController.navigate(Dest.Lesson.create(it)) },
                onCaseClick = { navController.navigate(Dest.CaseDetail.create(it)) },
                onClose = { navController.popBackStack() }
            )
        }
        composable(Dest.Glossary.route) { GlossaryScreen() }
        composable(Dest.Library.route) {
            LibraryScreen(
                onLessonClick = { navController.navigate(Dest.Lesson.create(it)) },
                onFigureClick = { navController.navigate(Dest.Figure.create(it)) }
            )
        }
        composable(Dest.References.route) { ReferencesScreen() }
        composable(Dest.Settings.route) { SettingsScreen() }
    }
}
