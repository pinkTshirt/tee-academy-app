package com.teeacademy.app.core.navigation

sealed class Dest(val route: String) {
    data object Home : Dest("home")
    data object Module : Dest("module/{moduleId}") {
        fun create(moduleId: String) = "module/$moduleId"
    }
    data object Lesson : Dest("lesson/{lessonId}") {
        fun create(lessonId: String) = "lesson/$lessonId"
    }
    data object Figure : Dest("figure/{figureId}") {
        fun create(figureId: String) = "figure/$figureId"
    }
    data object Video : Dest("video/{videoId}") {
        fun create(videoId: String) = "video/$videoId"
    }
    data object QuizLaunch : Dest("quiz_launch")
    data object QuizRun : Dest("quiz_run/{scopeType}/{scopeId}/{count}/{practiceMode}") {
        fun create(scopeType: String, scopeId: String, count: Int, practiceMode: Boolean) =
            "quiz_run/$scopeType/$scopeId/$count/$practiceMode"
    }
    data object QuizResult : Dest("quiz_result/{correct}/{total}") {
        fun create(correct: Int, total: Int) = "quiz_result/$correct/$total"
    }
    data object CaseList : Dest("cases")
    data object CaseDetail : Dest("case/{caseId}") {
        fun create(caseId: String) = "case/$caseId"
    }
    data object Search : Dest("search")
    data object Glossary : Dest("glossary")
    data object Library : Dest("library")
    data object References : Dest("references")
    data object Settings : Dest("settings")
}

/** Top-level destinations shown in bottom nav (phone) / nav rail (tablet). */
enum class TopLevelDestination(val route: String, val label: String) {
    HOME(Dest.Home.route, "Home"),
    CASES(Dest.CaseList.route, "Cases"),
    QUIZ(Dest.QuizLaunch.route, "Quiz"),
    LIBRARY(Dest.Library.route, "Library")
}
