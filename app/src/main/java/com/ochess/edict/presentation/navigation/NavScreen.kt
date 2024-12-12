package com.ochess.edict.presentation.navigation

import com.ochess.edict.data.GlobalVal
import com.ochess.edict.util.ActivityRun
import kotlinx.coroutines.flow.MutableStateFlow

sealed class NavScreen(val route: String) {


    object HomeScreen: NavScreen(route = "Home") {
        const val routeWithArgument = "Home?wordIndex=1?fromPage=2?level={level}"
    }
    object LevelScreen: NavScreen(route = "Level")
    object BookmarkScreen: NavScreen(route = "Bookmark")
    object HistoryScreen: NavScreen(route = "History")
    object ListenBook: NavScreen(route = routes.ListenBook)
    object ListenBooks: NavScreen(route = routes.ListenBooks)
    object Settring:NavScreen(route = routes.Setting)
    object LineGame:NavScreen(route = routes.LineGame)
    object routes {
        val About: String = "About"
        val LineGame: String = "LineGame"
        val Switch: String = "SwitchPage"
        const val Listen: String = "Jdc?page=3"
        const val Recite: String = "Jdc?page=2"
        const val Study: String = "Jdc?page=1"
        const val BookMark: String = "Bookmark?pid=-1"
        const val Book: String = "Bookmark"
        const val Jdc: String="Jdc"
        const val Home="Home"
        const val Setting="Setting"
        const val ListenBook="ListenBook"
        const val ListenBooks="ListenBooks"
        const val Quit="quit"
        const val Setup="Setup"
        const val History = "History"
        const val HistorySearchByEdinhouse = "History?type=0"
    }
    fun open(s: String="") {
        GlobalVal.nav.navigate(route+s)
    }

    companion object {
        val lastUptime = MutableStateFlow(0L)
        val simpleRouteTitles= listOf(routes.Home,routes.Book,routes.History,routes.Setting)
        val rotesList = listOf(routes.Switch,routes.Home,routes.Book,routes.History,routes.Setting)
        var showPageIndex =1
        fun openHome(fromPage: Int, wordIndex:Int = 0, level:Int =-1) {
            val route = "${routes.Home}?wordIndex=${wordIndex}?fromPage=${fromPage}?level=${level}"
            ActivityRun.runOnUiThread {
                GlobalVal.nav.navigate(route)
            }
        }

        fun openJdc(i: Int) {
            val route = "${routes.Jdc}?page=${i}"
            ActivityRun.runOnUiThread {
                GlobalVal.nav.navigate(route)
            }
        }

        fun toPage(cOrientation: Int): Int {
            showPageIndex += cOrientation
            showPageIndex = Math.max(Math.min(showPageIndex,4),0)
            val route = rotesList[showPageIndex]
            GlobalVal.nav.navigate(route)
            return showPageIndex
        }

        fun openRoute(route: String) {
            ActivityRun.runOnUiThread {
                GlobalVal.nav.navigate(route)
            }
        }

        fun refrash() {
            lastUptime.value = System.currentTimeMillis()
        }
    }

}