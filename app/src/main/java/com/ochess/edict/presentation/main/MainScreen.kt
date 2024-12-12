package com.ochess.edict.presentation.main

import android.app.Activity
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ochess.edict.MainActivity
import com.ochess.edict.data.GlobalVal
import com.ochess.edict.data.UserStatus
import com.ochess.edict.data.config.MenuConf
import com.ochess.edict.presentation.bookmark.BookmarkScreen
import com.ochess.edict.presentation.bookmark.BookmarkViewModel
import com.ochess.edict.presentation.bookmark.data.BookMark
import com.ochess.edict.presentation.history.HistoryScreen
import com.ochess.edict.presentation.history.HistoryViewModel
import com.ochess.edict.presentation.home.HomeScreen
import com.ochess.edict.presentation.home.JdcScreen
import com.ochess.edict.presentation.home.PAGE_FROM_HISTORY
import com.ochess.edict.presentation.home.PAGE_FROM_HOME
import com.ochess.edict.presentation.home.PAGE_FROM_LEVEL
import com.ochess.edict.presentation.home.WordModelViewModel
import com.ochess.edict.presentation.home.game.LineGameScreen
import com.ochess.edict.presentation.home.homescreen.SwitchMainPage
import com.ochess.edict.presentation.level.LevelScreen
import com.ochess.edict.presentation.level.LevelViewModel
import com.ochess.edict.presentation.listenbook.ListenBookScreen
import com.ochess.edict.presentation.listenbook.ListenScreen
import com.ochess.edict.presentation.main.components.Display
import com.ochess.edict.presentation.main.components.Display.getBitMapByPageScreen
import com.ochess.edict.presentation.main.components.Display.px2dp
import com.ochess.edict.presentation.main.components.Display.setBitMapByPageScreen
import com.ochess.edict.presentation.main.components.InfoDialog
import com.ochess.edict.presentation.navigation.NavScreen
import com.ochess.edict.util.ActivityRun
import com.ochess.edict.util.ActivityRun.Companion.context
import kotlin.math.abs
import kotlin.system.exitProcess


@Composable
fun MainScreen(
    navController: NavHostController,
    scaffoldState: androidx.compose.material.ScaffoldState,
    toggleTheme: () -> Unit = { Unit }
) {
    val levelViewModel: LevelViewModel = hiltViewModel()
    val wordViewModel: WordModelViewModel = hiltViewModel()
    val bookmarkViewModel: BookmarkViewModel = hiltViewModel()
    val historyViewModel: HistoryViewModel = hiltViewModel()

    GlobalVal.bookmarkViewModel = bookmarkViewModel
    GlobalVal.nav = navController
    GlobalVal.wordViewModel = wordViewModel

    val fullWidth = with(LocalContext. current) {
        resources. displayMetrics. widthPixels
    }
    val dragAble = UserStatus.get{
        val defv = !ActivityRun.isHorizontalScreens()
        it.getBoolean("HorizontalDrawAble",defv)
    }
    val dragEnabled = remember {
        mutableStateOf(false)
    }
    var dragDistance by remember { mutableStateOf(0F) }
    var pageIndex by remember {
        mutableStateOf(1)
    }
    val dragEvent = remember{
        object {
            val move:(y:Float) ->Unit ={
                if(pageIndex<2 && it>0) {
                        //最左侧
                }else if(pageIndex>NavScreen.rotesList.size-2 && it<0) {
                        //最右侧
                }else if(dragAble || dragEnabled.value)
                    dragDistance+=it
            }
            val onDragStarted: (y:Offset)->Unit = { offset ->
                dragDistance = 0f
                setBitMapByPageScreen((pageIndex).toString())
            }
            val onDragStopped:()->Unit = {
                if (abs(dragDistance) > fullWidth / 7) {
                    val cOrientation = if (dragDistance > 0) -1 else 1
                    pageIndex = NavScreen.toPage(cOrientation)
                }
//                Animatable(dragDistance).animateTo(0f) {
//                    dragDistance = value
//                }
                dragDistance = 0f
            }
        }
    }

    // 监听屏幕方向变化
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(context, lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_CREATE) {
                val isHs = ActivityRun.isHorizontalScreens()
                if(isHs != Display.isHorizontalScreens.value){
                    Display.isHorizontalScreens.value = isHs
                    MenuConf.tryUpDefConfig()
                }
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
    var nowRoute = GlobalVal.nav.currentDestination?.route ?: "Home"
    Box(modifier = Modifier
        .fillMaxSize()
        .offset(x = px2dp(dragDistance.toInt()))
        .draggable(
            enabled = dragAble && ! nowRoute.startsWith("Home"),
            state = rememberDraggableState(onDelta = {
                dragEvent.move(it)
            }),
            orientation = Orientation.Horizontal,
            onDragStarted = {
                dragEvent.onDragStarted(it)
            },
            onDragStopped = { offset ->
                if (abs(dragDistance) > fullWidth / 5) {
                    val cOrientation = if (dragDistance > 0) -1 else 1
                    pageIndex = NavScreen.toPage(cOrientation)
                }
                Animatable(dragDistance).animateTo(0f) {
                    dragDistance = value
                }
            }
        )
//        .pointerInput(Unit) {
//            forEachGesture {
//                awaitPointerEventScope {
//                    awaitFirstDown(false)
//                    var mvPos = Offset.Zero
//                    do {
//                        val event = awaitPointerEvent()
//                        val points = event.changes
//                        if (!dragEnabled.value && points.size == 2) {
//                            dragEnabled.value = true
//                            dragEvent.onDragStarted(points.first().position)
//                            Log.d(TAG, "SlidingEnable: open")
//                        }
//                        if (dragEnabled.value) {
//                            var pan = event.calculatePan()
//                            mvPos += pan
//                            if (Math.abs(mvPos.x) > Math.abs(mvPos.y)) {
//                                dragEvent.move(pan.x)
//                            }
//                            points.forEach {
//                                if (it.positionChanged()) {
//                                    it.consumeAllChanges()
//                                }
//                            }
//                        }
//                    } while (points.any { it.pressed })
//                    Log.d(TAG, "SlidingEnable: up")
//                    if (dragEnabled.value) {
//                        dragEvent.onDragStopped()
//                        dragEnabled.value = false
//                        Log.d(TAG, "SlidingEnable: close")
//                    }
//                }
//            }
//        }
    ){
        InfoDialog.add()
        Box(modifier = Modifier
            .fillMaxSize()
            .offset(x = px2dp(0 - fullWidth), 0.dp)
            .background(MaterialTheme.colorScheme.surface)){
                Image(painter = BitmapPainter(getBitMapByPageScreen((pageIndex-1).toString())), contentDescription =null, modifier = Modifier.fillMaxSize() )
            }
        Box(modifier = Modifier
            .fillMaxSize()
            .offset(x = px2dp(fullWidth), 0.dp)
            .background(MaterialTheme.colorScheme.surface)){
                Image(painter = BitmapPainter(getBitMapByPageScreen((pageIndex+1).toString())), contentDescription =null, modifier = Modifier.fillMaxSize() )
            }
        NavHost(
            navController = navController,
            startDestination = "${NavScreen.HomeScreen.route}?wordIndex={wordIndex}?fromPage={fromPage}?level={level}"
        ) {
        composable(
            route = "${NavScreen.HomeScreen.route}?wordIndex={wordIndex}?fromPage={fromPage}?level={level}",
            arguments = listOf(
                navArgument("wordIndex") {
                    type = NavType.IntType
                    defaultValue = -1
                },
                navArgument("fromPage") {
                    type = NavType.IntType
                    defaultValue = PAGE_FROM_HOME
                },
                navArgument("level") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) {
            HomeScreen(
                navController,
                it.arguments?.getInt("wordIndex", -1),
                it.arguments?.getInt("fromPage", PAGE_FROM_HOME) ?: PAGE_FROM_HOME,
                wordViewModel, bookmarkViewModel, historyViewModel,
                onToggleTheme = toggleTheme,
                le = it.arguments?.getInt("level", -1)
            )

//            Text("Home Box")
        }

        composable(route = NavScreen.LevelScreen.route) {
            LevelScreen(levelViewModel) { index, entity ->
                val newLevel = entity.level ?: -1
                Log.d("LevelScreen", "select new level = $newLevel")
                navController.navigate("${NavScreen.HomeScreen.route}?wordIndex=$index?fromPage=$PAGE_FROM_LEVEL?level=${entity.level}") {
                    launchSingleTop = true
                }
            }

            //Text("level Box")
        }

        composable(route = NavScreen.BookmarkScreen.route+"?pid={pid}",
            arguments = listOf(
                navArgument("pid") {
                    type = NavType.IntType
                    defaultValue = 0
                })
        ) {
            val pid = it.arguments?.getInt("pid",0)
            if (pid != null) {
                BookMark.changePid(pid)
            }
            BookmarkScreen()
//            BookmarkScreen(bookmarkViewModel) { index ->
//                navController.navigate("${NavScreen.HomeScreen.route}?wordIndex=$index?fromPage=$PAGE_FROM_BOOKMARK?level=${-1}") {
//                    launchSingleTop = true
//                }
//            }
        }

        composable(route = NavScreen.HistoryScreen.route+"?type={type}",
                arguments = listOf(
                    navArgument("type") {
                        type = NavType.IntType
                        defaultValue = -1
                    })
        ) {
            HistoryScreen(it.arguments,historyViewModel) { index ->
                navController.navigate("${NavScreen.HomeScreen.route}?wordIndex=$index?fromPage=${PAGE_FROM_HISTORY}?level=${-1}") {
                    launchSingleTop = true
                }
            }

//            Text("History Box")
        }

        composable(route = NavScreen.routes.ListenBooks+"?aids={aids}",
            arguments = listOf(
                navArgument("aids") {
                    type = NavType.StringType
                    defaultValue = ""
                })
        ) {
            ListenScreen(it.arguments)
//            {
//                navController.navigate("${NavScreen.routes.ListenBook}") {
//                    launchSingleTop = true
//                }
//            }
        }
//        composable(route = NavScreen.routes.Setup) {
//            val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
////            if (mBluetoothAdapter == null) {
////                textView.setText("Missing bluetooth device.")
////                return@composable
////            }
//            val intentBluetooth = Intent()
//            intentBluetooth.setAction(Settings.ACTION_BLUETOOTH_SETTINGS)
//            ActivityRun.context().startActivity(intentBluetooth)
////            val intent: Intent = Intent(getActivity(), MainActivity::class.java)
////            getActivity().finish()
//        }


        composable(route = NavScreen.routes.Setting,
        ) {
            SettingScreen()
        }
        composable(route = NavScreen.routes.Quit) {
            val act = ActivityRun.context() as Activity
            act.finish()
            exitProcess(0)
        }
        //单词表现页
        composable(route = NavScreen.routes.Switch) {
            SwitchMainPage()
        }
        //单词表现页
        composable(route = NavScreen.routes.LineGame+"?words={words}",arguments = listOf(
            navArgument("words") {
                type = NavType.StringListType
                defaultValue = listOf<String>()
            })) {
            val words = when(it.arguments){
                null ->   listOf<String>()
                else -> {
                    val ws = it.arguments?.getString("words","")
                    ws?.split(",")
                }
            }
            if (words != null) {
                LineGameScreen(words)
            }
        }
        //单词表现页
        composable(route = NavScreen.routes.ListenBook+"?words={words}",arguments = listOf(
            navArgument("words") {
                type = NavType.StringListType
                defaultValue = listOf<String>()
            })) {
            val words = when(it.arguments){
                null ->   listOf<String>()
                else -> {
                    val ws = it.arguments?.getString("words","")
                    ws?.split(",")
                }
            }
            if (words != null) {
                ListenBookScreen(words)
            }
        }
        composable(route = NavScreen.routes.Jdc+"?page={page}",
            arguments = listOf(
                navArgument("page") {
                    type = NavType.IntType
                    defaultValue = 0
                })
        ) {
            JdcScreen(it.arguments!!.getInt("page",0))
        }
        composable(route = NavScreen.routes.About) {
            AboutScreen()
        }
    }
    }
}