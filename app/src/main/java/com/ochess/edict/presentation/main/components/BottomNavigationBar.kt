package com.ochess.edict.presentation.main.components

import androidx.collection.arraySetOf
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ochess.edict.data.config.MenuConf
import com.ochess.edict.presentation.home.HomeEvents
import com.ochess.edict.presentation.main.components.Display.mt
import com.ochess.edict.presentation.navigation.NavScreen
import com.ochess.edict.presentation.ui.theme.EnglishWhizTheme

@Composable
@Preview
@ExperimentalAnimationApi
fun BottomNavigationBar(
    navController: NavController,
    itemClick: (BottomNavItem) -> Unit,
) {
    val reset = NavScreen.lastUptime.collectAsState()
    val backStackEntry by navController.currentBackStackEntryAsState()
    if(reset.value > -1)
    Card(
            modifier = Modifier
                .fillMaxWidth(),
//            elevation = CardDefaults.cardElevation(8.dp),
            shape = RoundedCornerShape(0.dp)
    ) {
        val items = provideBottomNavItems()
        var nowRoute = backStackEntry?.destination?.route ?: "Home"
        when(MenuConf.type().value) {
            MenuConf.bottom -> {
                if (!nowRoute.startsWith("Home") || HomeEvents.downMenuOpen) //隐藏home
                    Row(
                        modifier = Modifier.padding(8.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val smr = NavScreen.simpleRouteTitles
                        val typeSet = arraySetOf<String>()
                        items.filter {
                            val type = it.route.split("?")[0]
                            val rt = type in smr && !typeSet.contains(type) && it.title.length>0
                            typeSet.add(type)
                            rt
                        }.forEach { item ->
                            val selected = item.route.startsWith(nowRoute.split("?").first())
                            item.title = mt(item.title)
                            NavItem(
                                item = item,
                                onClick = { itemClick(item) },
                                selected = selected,
                                modifier = Modifier
                            )
                        }
                    }
            }
            MenuConf.right ->
                LazyColumn (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(1.dp)
                        .border(
                            1.dp,// 边框粗细1dp
                            Color.White,// 线性渲染
                            RoundedCornerShape(10.dp)// 形状带圆角
                        )
                        .alpha(0.5f)
                    //modifier = Modifier.padding(8.dp)
                    //horizontalArrangement = Arrangement.SpaceAround,
                    //verticalAlignment = Alignment.CenterVertically
                ) {
                    itemsIndexed(items) { index,item ->
                        val selected = nowRoute?.startsWith(item.route)
                        item.title = mt(item.title)
                        if (selected != null) {
                            NavItem(
                                item = item,
                                onClick = { itemClick(item) },
                                selected = selected,
                                modifier = Modifier
                                    .padding(0.dp, 10.dp)
                                    .width(180.dp)
                                    .border(2.dp, Color.Gray, RoundedCornerShape(12.dp)),hasIco = true
                            )
                        }
                    }
                }
        }
    }
}

@ExperimentalAnimationApi
@Preview(showBackground = true)
@Composable
fun BottomNavigationPreview() {
    EnglishWhizTheme {
        BottomNavigationBar(
            rememberNavController()
        ) {}
    }
}