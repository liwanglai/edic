package com.ochess.edict.presentation.home.homescreen

import android.annotation.SuppressLint
import android.os.Handler
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollDispatcher
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import com.ochess.edict.data.config.MenuConf
import com.ochess.edict.presentation.home.HomeEvents
import com.ochess.edict.presentation.home.TAG
import com.ochess.edict.presentation.home.nowBookShowType
import com.ochess.edict.presentation.home.viewMode
import com.ochess.edict.presentation.main.components.Display
import com.ochess.edict.presentation.main.components.Display.px2dp
import com.ochess.edict.presentation.navigation.NavScreen
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlin.math.abs

@SuppressLint("RememberReturnType")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SlidingEnable(enabled:Boolean,contentBox: @Composable BoxScope.() -> Unit) {
    /*** 上下滑动效果  */
     val fullHeight = with(LocalContext.current) {
        resources.displayMetrics.heightPixels
    }
    //出现小程序页面的滚动高度
    val limitHight = fullHeight / 5
    //拖拽的长度
    var dragDistance by remember { mutableStateOf(0F) }
    //主页的top
    var boxTop by remember { mutableStateOf(0F) }
    //拖拽完成的百分比 0~1
    var scrollPercent = remember { mutableStateOf(0f) }
    //当前显示的页面 (0 1 2)
    var showPageIndex by remember { mutableStateOf(1) }
    HomeEvents.onBackBefore{
        val rt = showPageIndex!=0
        if(!rt) {
            showPageIndex=1
            scrollPercent.value=0f
            boxTop = 0f
        }
        rt
    }
    //显示加载页
    var showLoadingPage by remember { mutableStateOf(false) }
    //尝试展示的页面
    var tryShowPageVisables by remember { mutableStateOf(arrayOf(false, false, false)) }
    // 动画实例
    HomeEvents.showMainPage = {
        MainScope().launch {
//            Animatable(boxTop).animateTo(
//                0f, animationSpec = tween(durationMillis = 500)
//            ) {
//                boxTop = value
//            }
            boxTop = 0f
            showPageIndex = 1
            tryShowPageVisables = arrayOf(false, false, false)
            scrollPercent.value = 0f
            if(it.length>0) {
                Handler().postDelayed({
                    Display.setBitMapByPageScreen(it)
                }, 200)
            }
        }
    }

    //更新当前页面
    fun upPageIndex(relChange: Boolean = false) {
        var n = 0;
        when {
            //往上
            dragDistance < -5 -> n = 1
            //保持不变offset
            dragDistance < 5 -> n = 0
            //往下
            else -> n = -1
        }
        if (relChange) {
            showPageIndex += n
            showPageIndex = Math.max(Math.min(showPageIndex, 2), 0)
            tryShowPageVisables = arrayOf(false, false, false)
        } else if (showPageIndex + n in (0..tryShowPageVisables.size - 1)) {
            tryShowPageVisables[showPageIndex + n] = true
        }
        Log.d(
            TAG,
            "DraggableGestureScreen end upPageIndex: $showPageIndex " + tryShowPageVisables.toList()
                .joinToString(",")
        )
    }
    //当前拖拽的值修改事件 包括停止拖拽之后值得动态效果
    LaunchedEffect(key1 = dragDistance) {
        val absDragHeight = abs(dragDistance)
        if (absDragHeight > limitHight && !(true in tryShowPageVisables)) {
            upPageIndex()
            showLoadingPage = false
        }
    }
    //分发给父容器
    val dispatch = remember {
        NestedScrollDispatcher()
    }
    val dragEnabled = remember {
        mutableStateOf(false)
    }
    var childTop = 0
    fun onDelta(it:Float)  {
            //滑动距离
            dragDistance += it
            if(boxTop==0f){
                if(Math.abs(dragDistance)>30) {
                    boxTop += it
                }
                return
            }
            if (dragDistance > 0 && dragDistance < limitHight) showLoadingPage = true
            //主页的top值
            boxTop += it

            //百分比
            val pre = Math.min(1f, abs(dragDistance) / (fullHeight/3))
            scrollPercent.value = if (showPageIndex == 1) pre else 1 - pre
    }
    val onDrawEnd :(y:Float)->Unit ={ offset ->
        childTop = 0
        Log.d(TAG, "DraggableGestureScreen end dragDistance: $dragDistance $offset ")
        val fHeight = fullHeight.toFloat()
        dragDistance += offset
        //如果拖拽的距离大于限制高度的3倍则切换
        if (abs(dragDistance) > limitHight) {
            upPageIndex(true)
        }
        dragDistance = 0f
//        MainScope().launch {
//            //动画修改主页的top值
//            Animatable(boxTop).animateTo(
//                when (showPageIndex) {
//                    0 -> fHeight
//                    1 -> 0f
//                    2 -> -fHeight
//                    else -> 0f
//                }
//            ) {
//                boxTop = value
//            }
//        }

        boxTop = when (showPageIndex) {
                    0 -> fHeight-100
                    1 -> 0f
                    2 -> -fHeight
                    else -> 0f
                }
        //重置参数值
        tryShowPageVisables = arrayOf(false, false, false)
        scrollPercent.value = if (showPageIndex == 1) 0f else 1f
        Log.d(TAG,"DraggableGestureScreen end showPageIndex: $showPageIndex $scrollPercent " + tryShowPageVisables)
    }
    val connection = remember {
        object : NestedScrollConnection {
            //返回自身消费了的偏移量 从而控制子模块的滑动
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val v = available.y.toInt()
                //滑动开始之后内部滑动就不要进行了
                if (enabled && boxTop * childTop <= 0) {
                    onDelta(available.y)
//                    dispatch.dispatchPreScroll(Offset(0f,available.y),NestedScrollSource.Drag)
                    Log.d("SwitchMainPage: ", "$available")
                    return available
                }
                return Offset.Zero;
//                return super.onPreScroll(available, source) ?: Offset.Zero
//                return available
            }

            override fun onPostScroll(
                consumed: Offset,   //当前子组件消费的距离
                available: Offset,  //当前父容器可以再次消费的剩余距离
                source: NestedScrollSource
            ): Offset {
                childTop += consumed.y.toInt()
                //子组件滑动完成之后， 如果还有可用的距离，那么父组件就消费
                if(enabled && consumed.y==0.0f && Math.abs(available.y)>0){
                    onDelta(available.y)
//                    dispatch.dispatchPostScroll(Offset(0f,0f),Offset(0f,available.y),NestedScrollSource.Drag)
                    Log.d("SwitchMainPage: ", "$consumed,$available")
                }
                return super.onPostScroll(consumed, available, source)
            }
        }
    }
    var scale by remember { mutableStateOf(1f) }
    var m_rotation by remember { mutableStateOf(0f) }
    Box(modifier = Modifier
        .fillMaxSize()
//        .nestedScroll(connection, dispatch)
        //获取点输入事件
        .pointerInput(Unit) {
            //手指松开事件
            forEachGesture {
                awaitPointerEventScope {
                    awaitFirstDown(false)
                    var mvPos = Offset.Zero
                    do {
                        val event = awaitPointerEvent()
                        val points = event.changes
                        val pan = event.calculatePan()
                        mvPos+=pan
//                        if (!dragEnabled.value && points.size == 2) {
                        if (!dragEnabled.value &&
                            (
                                    points.size == 2 ||
                                    (HomeEvents.status.openDrowUp && ((showPageIndex ==1 && mvPos.y > 10) || (showPageIndex ==0 && mvPos.y < -10)))

                            )
                        ) {
                            dragEnabled.value = true
                            Log.d(TAG, "SlidingEnable: open")
                        }
                        if(dragEnabled.value) {
                            if(abs(mvPos.y)>abs(mvPos.x)*2) {
                                onDelta(pan.y)
                            }
                        }
                    } while (points.any { it.pressed })
                    Log.d(TAG, "SlidingEnable: up")
                    if (boxTop != 0f) {
                        onDrawEnd(0f)
                        Handler().postDelayed({
                            onDrawEnd(0f)
                        },200)
                    }
                    if (dragEnabled.value) {
                        dragEnabled.value = false
                        Log.d(TAG, "SlidingEnable: close")
                    }
                }
            }
            //设置dragEnabled 二个手指是否按下
//            coroutineScope{
//                while (true) {
//                    // down事件
//                    awaitPointerEventScope {
//                        awaitFirstDown()
//                    }
//                    //等待第二个按键按下到弹起
//                    awaitPointerEventScope {
//                        do {
//                            val event = awaitPointerEvent()
//                            val points = event.changes
//                            if (!dragEnabled.value && points.size == 2) {
//                                dragEnabled.value = true
//                                Log.d(TAG, "SlidingEnable: open")
//                            }
//                        } while (points.any { it.pressed })
//                        if(dragEnabled.value) {
//                            dragEnabled.value = false
//                            Log.d(TAG, "SlidingEnable: close")
//                        }
//                    }
//                }
//            }

            //手指缩放事件
            detectTransformGestures(
                panZoomLock = true,
                onGesture = { centroid, pan, zoom, rotation ->
                    scale *= zoom
                    m_rotation += rotation

                }
            )
        }
        .graphicsLayer(
            scaleX = scale,
            scaleY = scale,
            rotationZ = m_rotation,
        )
//        //拖拽事件
//        .draggable(
//            state = rememberDraggableState(onDelta = onDelta),
//            Orientation.Vertical,
////            enabled = dragEnabled.value,
//            //开始拖拽
//            onDragStarted = { offset ->
//                dragDistance = 0f
//            },
//            //拖拽结束
//            onDragStopped = {
//                onDrawEnd(it)
//            }
//        )

    ) {
        //展示页面为0或 尝试展示的页面为0 则展示分组页
        if (showPageIndex == 0 || tryShowPageVisables[0]) {
//            GroupInfoPage(scrollPercent)
            SwitchMainPage(scrollPercent)
            //展示页面为2 或 尝试展示的页面为2 则展示主页选择页
        } else if (showPageIndex == 2 || tryShowPageVisables[2]) {
//            SwitchMainPage(scrollPercent)
        }
        //loading加载页
        if (showLoadingPage) {
            LoadingPage(scrollPercent.value * 3.33)
        }
        val state = rememberScrollState()
        Box(
            content = contentBox,
            modifier = Modifier
                .fillMaxSize()
//                .fillMaxWidth()
//                .height(px2dp(fullHeight))
//                .verticalScroll(state = state)
                .offset(y = px2dp(boxTop.toInt()))
                .alpha(1 - scrollPercent.value)
        )
    }
}