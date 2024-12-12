package com.ochess.edict.presentation.home.homescreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import com.ochess.edict.presentation.main.extend.MText as Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ochess.edict.data.config.BookConf
import com.ochess.edict.data.config.MenuConf
import com.ochess.edict.data.model.WordExtend
import com.ochess.edict.presentation.home.HomeEvents
import com.ochess.edict.presentation.home.nowBookShowType
import com.ochess.edict.presentation.main.components.Display
import com.ochess.edict.presentation.main.components.Display.mt
import com.ochess.edict.util.ActivityRun

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Preview
fun SwitchMainPage(
    ap: MutableState<Float> = mutableStateOf(1f),
) {
    val itemGroups = MenuConf.modeGroups()
    var cells = if(ActivityRun.isHorizontalScreens()) GridCells.Adaptive(200.dp) else  GridCells.Fixed(3)
    val pSize = Display.getScreenSize()
    //根据扩展词信息删除无效的视图
    if(nowBookShowType.equals("word_shows")) {
        val ex = BookConf.instance.wordEx()
        val p = itemGroups["word_shows"]
        if(p!=null && ex.data.size+ex.isize==0) {
            val i = p.indexOf(MenuConf.mode.wordScapesGame)
            p.removeAt(i)
        }
        if(p!=null && ex.esize==0) {
            val i = p.indexOf(MenuConf.mode.wordExtGame)
            p.removeAt(i)
        }
    }

    LazyColumn (
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .alpha(ap.value)
            .fillMaxWidth()
    ){
        val itemsAll = itemGroups.entries.toList()
        var itemH = Display.px2dp(pSize.y / 5)
        if(!ActivityRun.isHorizontalScreens()) itemH= itemH*4/5
        items(itemsAll){
            //只能有一个分组被显示
            if(it.key != nowBookShowType) return@items
            Box(modifier = Modifier.fillMaxWidth()) {
                Column (modifier = Modifier.padding(5.dp,20.dp,5.dp,10.dp)){
                    Title(it.key)
                    Spacer(modifier = Modifier.height(1.dp)) // 可以替换为你自己的分隔符组件
                    Divider()
                }
            }
            LazyVerticalGrid(
                columns = cells,
                modifier = Modifier.fillMaxWidth()
                    .heightIn(0.dp,2000.dp)
            ) {
                val iw = Display.px2dp(pSize.x / 6)
                val bc = Color.Gray.copy(alpha = 0.2f)
                itemsIndexed(it.value) { index, item ->
                    Column(modifier = Modifier
                        .padding(10.dp)
                        .width(iw)
                        .clip(RoundedCornerShape(14.dp)) // 设置圆角半径
                        .background(bc)
//                        .border(2.dp,
//                            bc,
//                            RoundedCornerShape(15.dp)
//                        )
                        .clickable {
                            HomeEvents.SwitchMainPage.onItemClick(item)
                        }
                    ) {
                        Text(
                            item.name,
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                            modifier = Modifier.fillMaxWidth()
                                .padding(5.dp)
                        )
                        Image(
                            painter = BitmapPainter(Display.getBitMapByPageScreen(item.name,iw+30.dp,itemH)),
                            contentDescription = item.name,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp)) // 可以替换为你自己的分隔符组件
                    }
                }
            }
        }
    }
}
