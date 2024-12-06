package com.ochess.edict.presentation.main.components

import androidx.compose.runtime.Composable
import com.ochess.edict.R
import com.ochess.edict.presentation.navigation.NavScreen

data class BottomNavItem(
    var title: String,
    val icon: Int,
    val route: String
)

@Composable
fun provideBottomNavItems() = listOf(
    BottomNavItem(
        NavScreen.HomeScreen.route,
        R.drawable.home,
        NavScreen.HomeScreen.routeWithArgument
    ),
//    BottomNavItem(
//        NavScreen.LevelScreen.route,
//        R.drawable.home,
//        NavScreen.LevelScreen.route
//    ),
    BottomNavItem(
        "Book",
        R.drawable.book,
        NavScreen.BookmarkScreen.route
    ),

    BottomNavItem(
        "Study",
        R.drawable.study,
        NavScreen.routes.Study
    ),
    BottomNavItem(
        "Recite",
        R.drawable.recite,
        NavScreen.routes.Recite
    ),
    BottomNavItem(
        "Listen",
        R.drawable.yuyintingxie,
        NavScreen.routes.Listen
    ),
    BottomNavItem(
        "Bookmark",
        R.drawable.save,
        NavScreen.routes.BookMark
    ),

    BottomNavItem(
        NavScreen.HistoryScreen.route,
        R.drawable.history,
        NavScreen.HistoryScreen.route
    ),
//    BottomNavItem(
//        "ListenBook",
//        R.drawable.listening,
//        "listenBook"
//    ),
//    BottomNavItem(
//        "Setup",
//        R.drawable.listening,
//        NavScreen.routes.Setup
//    ),
//    BottomNavItem(
//        "Quit",
//        R.drawable.clear,
//        NavScreen.routes.Setup
//    )
    BottomNavItem(
        NavScreen.routes.Setting,
        R.drawable.setting,
        NavScreen.routes.Setting
    ),
    BottomNavItem(
        NavScreen.routes.Quit,
        R.drawable.clear,
        NavScreen.routes.Quit
    )
)