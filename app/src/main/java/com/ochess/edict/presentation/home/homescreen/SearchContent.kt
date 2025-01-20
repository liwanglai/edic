package com.ochess.edict.presentation.home.homescreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ochess.edict.data.config.PageConf
import com.ochess.edict.data.config.PageConf.DicType
import com.ochess.edict.data.local.entity.Meaning
import com.ochess.edict.data.local.entity.Meaning.Companion.typeMap
import com.ochess.edict.domain.model.WordModel
import com.ochess.edict.presentation.home.WordModelViewModel
import com.ochess.edict.presentation.home.components.UtilButtons
import com.ochess.edict.presentation.home.dictionaryStringBuilder
import com.ochess.edict.presentation.level.LevelViewModel
import com.ochess.edict.presentation.main.components.Display.mt
import com.ochess.edict.presentation.main.extend.bgRun
import com.ochess.edict.view.ClickAbelText

@Composable
fun SearchContent(
    wordModel: WordModel?,
    wordViewModel: WordModelViewModel
) {

    val levelViewModel: LevelViewModel = hiltViewModel()
    val tColor = MaterialTheme.colorScheme.onBackground

    UtilButtons( wordViewModel)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp, 0.dp, 18.dp, 36.dp)
    ) {

        wordModel?.let {
            dictionaryStringBuilder = dictionaryStringBuilder.clear()
            dictionaryStringBuilder.append(it.word).append("\n")

            val dicType = remember {
                PageConf.getInt(PageConf.homePage.DicType, 2)
            }
            var showCh by remember {
                mutableStateOf(
                    listOf(
                        DicType.en_cn.ordinal,
                        DicType.encn.ordinal
                    ).contains(dicType)
                )
            }
            val showEn = listOf(DicType.en_en.ordinal, DicType.encn.ordinal).contains(dicType)
            val moreN = 3
            val mSize = if(it.meanings!=null) it.meanings.size else 0
            var moreVisible by remember {
                mutableStateOf(mSize<=moreN)
            }
            if(mSize==1 && it.meanings!![0].def==null){
                moreVisible = true
            }
            @Composable
            fun runOneMeaning(index:Int, meaning:Meaning){
                val defChVisable = remember {  mutableStateOf(false) }
                val examChVisable = remember {mutableStateOf(false) }
                val dEn =  remember { mutableStateOf(false) }
                val eEn =  remember { mutableStateOf(false) }

                dictionaryStringBuilder.append(meaning.speechPart).append("\n")
                var typeName =""
                var typeZhName =""

                if (meaning.speechPart != null) {
                    typeName = "("+meaning.speechPart+")"
                    if(meaning.speechPart in typeMap.keys){
                        typeZhName = "("+typeMap[meaning.speechPart]+")"
                    }
//                    Text(
//                        text = meaning.speechPart,
//                        style = MaterialTheme.typography.titleSmall,
//                        fontWeight = FontWeight.Bold,
//                        textAlign = TextAlign.Start,
//                        color = MaterialTheme.colorScheme.onSurface
//                    )
                }

                dictionaryStringBuilder.append("${index + 1}. ${meaning.def}").append("\n")

                if ((showCh || defChVisable.value) && meaning.def_ch != null) {
                    var defch = ""
                    if(meaning.def_ch!=null){
                        defch = if(meaning.def_ch.startsWith("（")) meaning.def_ch.substring(1,meaning.def_ch.length-2) else meaning.def_ch
                    }
                    Text(text = "${index + 1}. $typeZhName " + defch, modifier = Modifier.clickable {
                        if (dicType == DicType.en_cn.ordinal) {
                            dEn.value = !dEn.value
                        }
                    }, color = tColor)
                }
                if ((showEn || dEn.value) && meaning.def != null) {
                    ClickAbelText(
                        text = "${index + 1}. ${typeName} " + (if(meaning.def==null) "" else meaning.def),
                        style = MaterialTheme.typography.titleSmall,
                        lineHeight = TextUnit(18f, TextUnitType.Sp),
                        color = tColor,
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
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Normal),
                        color = Color.Gray
                    )
                }
                if (!meaning.example.isNullOrEmpty()) {
                    val etitle = when (dicType) {
                        0 -> "Example: "
                        else -> "例句: "
                    }
                    val example = etitle + "${meaning.example}"
                    dictionaryStringBuilder.append(example).append("\n")
                    if ((showCh || examChVisable.value) && meaning.example_ch != null) {
                        Text(text =etitle+ meaning.example_ch, modifier = Modifier.clickable {
                            eEn.value = !eEn.value
                        })
                    }
                    ClickAbelText(
                        text = example,
                        fontStyle = FontStyle.Italic,
                        lineHeight = TextUnit(16f, TextUnitType.Sp),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Normal),
                        color = Color.Gray,
                        onDbClick = {
                            examChVisable.value = !examChVisable.value
                        }
                    )
                }
                if (!meaning.synonyms.isNullOrEmpty()) {
                    val snomon = when (dicType) {
                        0 -> "Synonym(s)"
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
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Normal),
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Divider(
                    thickness = 0.4.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                )
                Spacer(modifier = Modifier.height(4.dp))
            }


            Text(
                text = "${it.ch ?: " "}",
//                fontStyle = FontStyle.Italic,
                //fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = TextUnit(20f, TextUnitType.Sp),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Normal),
                color = tColor,
                modifier = Modifier.clickable {
                    showCh = !showCh
                }.padding(bottom = 15.dp)
            )
            //级别
            var lName by remember { mutableStateOf("") }
            LaunchedEffect(wordModel?.word) {
                if (wordModel?.word == null) return@LaunchedEffect
                if (lName.length > 0 && lName != wordModel.word) {
                    moreVisible = false
                }
                lName = ""
                bgRun {
                    levelViewModel.getLevel(wordModel) {
                        lName = it
                    }
                }
            }
            Text(
                text = lName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start,
//                    modifier = Modifier.clickable {
//                        GlobalVal.nav.navigate(NavScreen.LevelScreen.route) {
//                            launchSingleTop = true
//                        }
//                    },
                color = Color.LightGray,
                modifier = Modifier.padding(top=15.dp)
            )
            val moreMeaning= if(mSize!! >moreN){
                it.meanings?.subList(0,moreN)!!.forEachIndexed{ index, meaning ->
                        runOneMeaning(index,meaning)
                }
                it.meanings?.subList(moreN,mSize)
            }else{
                it.meanings?.forEachIndexed{ index, meaning ->
                    runOneMeaning(index,meaning)
                }
                listOf<Meaning>()
            }

            if(!moreVisible){
                Text(
                    mt("more"),
                    Modifier
                        .clickable {
                            moreVisible = true
                        }
                        .align(Alignment.End)
                    , color = tColor
                )
            }else {
                moreMeaning.forEachIndexed { index, meanning ->
                    runOneMeaning(index + moreN, meanning)
                }
            }
        }

    }
}