package com.ochess.edict.presentation.home.homescreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ochess.edict.data.GlobalVal
import com.ochess.edict.data.config.PageConf
import com.ochess.edict.data.config.PageConf.DicType
import com.ochess.edict.domain.model.WordModel
import com.ochess.edict.presentation.home.WordModelViewModel
import com.ochess.edict.presentation.home.components.UtilButtons
import com.ochess.edict.presentation.home.dictionaryStringBuilder
import com.ochess.edict.presentation.level.LevelViewModel
import com.ochess.edict.presentation.navigation.NavScreen
import com.ochess.edict.view.ClickAbelText
import kotlinx.coroutines.InternalCoroutinesApi

@Composable
fun SearchContent(
    wordModel: WordModel?,
    wordViewModel: WordModelViewModel
) {

    val levelViewModel: LevelViewModel = hiltViewModel()

    UtilButtons( wordViewModel)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp, 0.dp, 18.dp, 36.dp)
    ) {
//        var lName by remember { mutableStateOf("") }
//        LaunchedEffect(wordModel?.word) {
//            lName = ""
//            if(wordModel?.word==null) return@LaunchedEffect
//            levelViewModel.getLevel(wordModel!!.level?:0) {
//                lName = it
//            }
//        }
//        Text(
//            text = lName,
//            style = MaterialTheme.typography.subtitle1,
//            fontWeight = FontWeight.Bold,
//            textAlign = TextAlign.Start,
//            modifier = Modifier.clickable {
//                GlobalVal.nav.navigate(NavScreen.LevelScreen.route) {
//                    launchSingleTop = true
//                }
//            },
//            color = MaterialTheme.colors.onSurface
//        )


        wordModel?.let {
            dictionaryStringBuilder = dictionaryStringBuilder.clear()
            dictionaryStringBuilder.append(it.word).append("\n")

            var moreVisible by remember {
                mutableStateOf(false)
            }
            if(PageConf.getBoolean(PageConf.homePage.DefaultShowDetails,true)) {
                moreVisible = true
            }
            val dicType = remember {
                PageConf.getInt(PageConf.homePage.DicType,1)
            }
            var showCh by remember {
                mutableStateOf( listOf(DicType.en_cn.ordinal,DicType.encn.ordinal).contains(dicType))
            }
            val showEn = listOf(DicType.en_en.ordinal,DicType.encn.ordinal).contains(dicType)

            Text(
                text = "${it.ch ?: " "}",
                fontStyle = FontStyle.Normal,
                fontSize = 20.sp,
                lineHeight = TextUnit(20f, TextUnitType.Sp),
                style = MaterialTheme.typography.subtitle2.copy(fontWeight = FontWeight.Normal),
                color = Color.Black,
                modifier = Modifier.clickable{
                    showCh = !showCh
                }
            )

            if(!moreVisible) {
                Text(
                    "详情",
                    Modifier
                        .clickable {
                            moreVisible = true
                        }
                        .align(Alignment.End)
                )
            }
            if(moreVisible) {
                it.meanings?.forEachIndexed { index, meaning ->
                    val defChVisable = remember{ mutableStateOf(false) }
                    val examChVisable = remember{ mutableStateOf(false) }
                    val dEn = remember{ mutableStateOf(false) }
                    val eEn = remember{ mutableStateOf(false) }
                    dictionaryStringBuilder.append(meaning.speechPart).append("\n")
                    if(meaning.speechPart!=null) {
                        Text(
                            text = meaning.speechPart,
                            style = MaterialTheme.typography.subtitle1,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Start,
                            color = MaterialTheme.colors.onSurface
                        )
                    }
                    dictionaryStringBuilder.append("${index + 1}. ${meaning.def}").append("\n")

                    if((showCh || defChVisable.value) && meaning.def_ch!=null) {
                        Text(text="${index + 1}. "+meaning.def_ch, modifier = Modifier.clickable{
                            if(dicType == DicType.en_cn.ordinal){
                                dEn.value=!dEn.value
                            }
                        })
                    }
                    if(showEn || dEn.value) {
                        ClickAbelText(
                            text = "${index + 1}. ${meaning.def}",
                            style = MaterialTheme.typography.subtitle2,
                            lineHeight = TextUnit(18f, TextUnitType.Sp),
                            color = MaterialTheme.colors.onSurface,
                            modifier = Modifier.clickable{
                                defChVisable.value = !defChVisable.value
                            },
                            onDbClick = {
                                defChVisable.value = !defChVisable.value
                            }
                        )
                    }
                    if (!meaning.labels.isNullOrEmpty()) {
                        val label = meaning.labels.map { label -> label.name }.toString()
                            .removePrefix("[")
                            .removeSuffix("]").replace(",", " •")
                        dictionaryStringBuilder.append(label).append("\n")
                        Text(
                            text = label,
                            fontStyle = FontStyle.Italic,
                            style = MaterialTheme.typography.subtitle2.copy(fontWeight = FontWeight.Normal),
                            color = Color.Gray
                        )
                    }
                    if (!meaning.example.isNullOrEmpty()) {
                        val etitle = when(dicType){
                            0->"Example:"
                            else -> "例句："
                        }
                        val example = etitle+"${meaning.example}"
                        dictionaryStringBuilder.append(example).append("\n")
                        if((examChVisable.value) && meaning.example_ch!=null) {
                            Text(text=meaning.example_ch, modifier = Modifier.clickable{
                                eEn.value = !eEn.value
                            })
                        }
                        ClickAbelText(
                            text = example,
                            fontStyle = FontStyle.Italic,
                            lineHeight = TextUnit(16f, TextUnitType.Sp),
                            style = MaterialTheme.typography.subtitle2.copy(fontWeight = FontWeight.Normal),
                            color = Color.Gray,
                            modifier = Modifier.clickable{
                                examChVisable.value = !examChVisable.value
                            },
                            onDbClick = {
                                examChVisable.value = !examChVisable.value
                            }
                        )
                    }
                    if (!meaning.synonyms.isNullOrEmpty()) {
                        val snomon = when(dicType){
                            0->"Synonym(s)"
                            else -> "同义词"
                        }
                        val synonym = "${snomon}: ${
                            meaning.synonyms.toString()
                                .removePrefix("[")
                                .removeSuffix("]")
                        }"
                        dictionaryStringBuilder.append(synonym).append("\n")
                        ClickAbelText(
                            text = synonym,
                            fontStyle = FontStyle.Italic,
                            lineHeight = TextUnit(16f, TextUnitType.Sp),
                            style = MaterialTheme.typography.subtitle2.copy(fontWeight = FontWeight.Normal),
                            color = Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Divider(
                        thickness = 0.4.dp,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

        }
    }
}