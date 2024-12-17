package com.ochess.edict.presentation.home.components

import android.annotation.SuppressLint
import android.os.Handler
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import com.ochess.edict.data.GlobalVal
import com.ochess.edict.data.config.SpeakConf
import com.ochess.edict.data.plug.SpeakSet
import com.ochess.edict.presentation.home.TTSListener
import com.ochess.edict.presentation.home.WordModelViewModel
import com.ochess.edict.presentation.home.dictionaryStringBuilder
import com.ochess.edict.presentation.main.components.Display.mt
import com.ochess.edict.presentation.navigation.NavScreen
import com.ochess.edict.print.MPrinter
import com.ochess.edict.view.MPopMenu
import kotlinx.coroutines.launch

val TAG: String="UtilButtons"
val spConf =SpeakConf.getByUserConfig()
var mpm:MPopMenu =MPopMenu.SpeakConfigMenu(spConf)

@SuppressLint("RememberReturnType")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UtilButtons( viewModel: WordModelViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(36.dp, 36.dp, 36.dp, 36.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
    CopyButton(
        modifier = Modifier.combinedClickable(
            onLongClick = {
                if (!dictionaryStringBuilder.toString().isNotEmpty()) return@combinedClickable
                if (MPrinter.printer.printView()) {
                    return@combinedClickable
                }
                clipboardManager.setText(buildAnnotatedString {
                    append(dictionaryStringBuilder.toString())
                })

//                coroutineScope.launch {
//                    scaffold.snackbarHostState.showSnackbar(message = "Copied")
//                }
            },
            onClick = {
                clipboardManager.setText(buildAnnotatedString {
                    append(viewModel.currentDictionary.value.word)
                })

                Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show()
//                coroutineScope.launch {
//                    scaffold.snackbarHostState.showSnackbar(message = "Copied")
//
//                }
            }
        )
    ) {

    }

    var clickedState by remember { mutableStateOf(false) }

    val ttsListener by remember {
        mutableStateOf(TTSListener.getInstance(context))
    }
    var clickedType by remember { mutableStateOf(1) }
    //刷新页面不要重复更新配置
    remember {
        SpeakSet(ttsListener.getSpeech(), spConf).update()
        ttsListener.setOnCompleted {
            if (clickedType == 1)
                clickedState = false
        }
    }
    //离开的时候关闭阅读
    DisposableEffect(null) {
        onDispose {
            clickedState = false
        }
    }
    //为了复用方法在外面定义方法 不过这里有一个界面渲染重复定义的问题
    val onDoubleClick = {
        clickedState = !clickedState
        val wordEntity = viewModel.currentDictionary.value
        clickedType = 2
        Thread({
            var n = spConf.repeatNum
            while (n-- > 0 && clickedState) {
                ttsListener.play(wordEntity.word)
            }
            clickedState = false
        }).start()
    }
    val onClick = {
        clickedState = !clickedState
        clickedType = 1
        if (clickedState) {
            val wordEntity = viewModel.currentDictionary.value
            if (wordEntity.word.isNotBlank()) {
                    ttsListener.speak(wordEntity.word)
//                coroutineScope.launch {
//                    scaffold.snackbarHostState.showSnackbar(message = "Speaking")
//                }
            }
        } else {
            ttsListener.stop()
        }
    }
    LocalLifecycleOwner.current.lifecycle.addObserver(ttsListener)
    Column() {
        SpeakButton(
            clicked = clickedState,
            modifier = Modifier.combinedClickable(
                    onDoubleClick = {
                        onDoubleClick()
                    },
                    onClick = {
                        onClick()
                    },
                    onLongClick = {
                        mpm.show { k: String, v: MPopMenu.dataClass ->
                            when (k) {
                                "播放" ->
                                    onClick()

                                "重复播放" ->
                                    onDoubleClick()
                                "听书" -> {
                                    NavScreen.ListenBooks.open()
                                }
                                else -> {
                                    spConf.save(v.name, v.value)
                                    SpeakSet.upMenu(mpm, spConf)
                                    SpeakSet(ttsListener.getSpeech(), spConf).update()
                                    if (k.startsWith("重复次数")) {
                                        onDoubleClick()
                                    } else {
                                        onClick()
                                    }
                                    Handler().postDelayed({
                                        mpm.visible = true
                                    }, 20)
                                }
                            }
                        }
                    },
                )
        ) {}
        mpm.add()
    }
    SaveButton(
        modifier = Modifier.combinedClickable(
            onLongClick = {
                GlobalVal.nav.navigate(NavScreen.BookmarkScreen.route+"?pid=-1")
            },
            onClick = {
                val wordModel = viewModel.wordState.value.wordModel
                if (wordModel != null) {
                    viewModel.insertBookmark(wordModel)
                    Toast.makeText(context, mt("Saved"), Toast.LENGTH_SHORT).show()
//                    coroutineScope.launch {
//                        scaffold.snackbarHostState.showSnackbar(message = "Saved")
//                    }
                }
            }
        )
    ) {

    }
}

    Row (
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
        ){


    }
}
//    ShareButton {
//        val intentFileChooser = Intent(Intent.ACTION_SEND)
//        intentFileChooser.putExtra(
//            Intent.EXTRA_TEXT,
//            "Please download and rate us now: https://play.google.com/store/apps/details?id=$packageName"
//        )
//        intentFileChooser.type = "text/plain"
//        val intent = Intent.createChooser(intentFileChooser, "Share EnglishWhiz")
//        context.startActivity(intent)
//    }