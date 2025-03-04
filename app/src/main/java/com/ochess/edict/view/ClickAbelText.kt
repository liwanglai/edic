package com.ochess.edict.view

import android.graphics.Color
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Handler
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.compose.foundation.layout.padding
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.ochess.edict.R
import com.ochess.edict.data.DictionaryDatabase
import com.ochess.edict.data.GlobalVal
import com.ochess.edict.presentation.history.HistoryWords
import com.ochess.edict.presentation.home.TTSListener
import com.ochess.edict.util.ActivityRun
import java.util.Locale
import androidx.compose.ui.graphics.Color as Color0


@OptIn(ExperimentalUnitApi::class)
@Composable
@Preview
fun ClickAbelText(
    text: String="word",
    modifier: Modifier = Modifier,
    color: Color0 = Color0.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
    single:Boolean =false,
    onDbClick: (String) -> Unit = {}
    ) {


    val tags = text.split(" ")
        //.filter{ it.length>2 }
        .toList()
    val spannable = SpannableString(text)
    var i=0
    val colorInt = color.toArgb()
    val context = LocalContext.current
    val ttsListener = TTSListener.getInstance(context)
    val popupView = LayoutInflater.from(context).inflate(R.layout.float_info, null)
    val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,true)

    var word=""
    popupView.setOnClickListener {
        popupWindow.dismiss()
        if (word.isNotEmpty()) {
            GlobalVal.wordViewModel.searcher(word,true)
        }
    }
    fun showPopup(view: View,ch:String,startIndex:Int) {
        popupView.findViewById<TextView>(R.id.tvMessage).text = ch
        val widget:TextView = view as TextView
        var x = widget.totalPaddingLeft
        var y = widget.totalPaddingTop
        val w = widget.width
        val h = widget.lineHeight
        val W = (widget.paint.measureText(text.substring(0,startIndex))).toInt()

        if(single) {
            GlobalVal.floatText.value = ch
            val rect = Rect()
            view.getGlobalVisibleRect(rect)
            x = rect.left-x
            y = rect.top-y
            x += W%w - GlobalVal.floatSize.value.width/2
            y += W/w * h - widget.measuredHeight - GlobalVal.floatSize.value.height-250

            GlobalVal.floatOffet.value = IntOffset(x,y)
            return
        }
        //if(popupView.width==0){
            popupWindow.showAsDropDown(view, x,y)
        //}
        Handler().postDelayed({
            x += W%w - popupView.measuredWidth/2
            y += W/w * h - widget.measuredHeight - popupView.height
            popupWindow.dismiss()
            popupWindow.showAsDropDown(view, x,y)
            //popupWindow.showAtLocation(view, Gravity.CENTER, x,y)
        },50)
    }
    for (tag in tags){
        val startIndex =i
        val endIndex = i+tag.length
        val clickableSpan = object :ClickableSpan(){
            override fun onClick(widget: View) {
                //widget.getParent().requestDisallowInterceptTouchEvent(false);
                if (!tag.matches(Regex(".*[a-z].*"))) return
                //widget.layout()
                clearBeforeclickAbleSpan(widget,spannable,startIndex,endIndex,ForegroundColorSpan(colorInt))
                spannable.setSpan(ForegroundColorSpan(Color.RED),startIndex,endIndex,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                (widget as TextView).text = spannable
                Handler().post {
                    val beforword = word
                    word = tag.lowercase(Locale.getDefault())
                    if(beforword.equals(word)){
                        onDbClick(word)
                        return@post
                    }
                    if (tag.matches(Regex(".*\\W.*"))) {
                        word = word.replace(Regex("^\\W+|\\W+$"),"")
                    }
                    val ch = DictionaryDatabase.getDatabase(ActivityRun.context).ch(word)
                    popupWindow.dismiss()
                    if(ch.isNotEmpty()) {
                        showPopup(widget, ch,endIndex-tag.length/2)
                    }
                    ttsListener.speak(tag)
                }
                //Toast.makeText(widget.context,"clickd $tag",Toast.LENGTH_SHORT).show();
            }

            private fun clearBeforeclickAbleSpan(
                widget: View,
                spannable: SpannableString,
                startIndex: Int,
                endIndex: Int,
                what: ForegroundColorSpan
            ) {
                beforeClikableButton.clearBeforeAndAddNew(widget as TextView,spannable,startIndex,endIndex,what)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = colorInt
                ds.bgColor = Color.TRANSPARENT
            }
        }
        spannable.setSpan(clickableSpan,i,endIndex,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
       // spannable.setSpan(ForegroundColorSpan(colorInt),i,endIndex,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        i=text.indexOf(" ",endIndex)+1
    }
    var fsize = fontSize
    if(fontSize == TextUnit.Unspecified) {
        fsize = TextUnit(14f, TextUnitType.Sp)
    }
    val verticalPadding = (lineHeight.value - fsize.value) / 2
    AndroidView(factory = {
            //加载AndroidView布局。
            TextView(it)
        },
        modifier = modifier
            .padding(vertical = verticalPadding.dp)
    ) {
        it.setTextIsSelectable(true)
        it.text = spannable
        if(fontStyle?.value ==1){
            it.setTypeface(null, Typeface.ITALIC)
        }
        it.setTextColor(colorInt)
        it.setTextSize(fontSize.value)
        it.movementMethod = LinkMovementMethod.getInstance()
    }

}

class beforeClikableButton {
    companion object {
        fun clearBeforeAndAddNew(widget: TextView, sp: SpannableString, si: Int, ei: Int, what: Any) {
            if(endIndex > 0) {
                spannable.setSpan(what, startIndex, endIndex,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                if(widget != tv) tv.text= spannable
            }
            endIndex = ei
            startIndex = si
            spannable = sp
            tv = widget
        }
        private var endIndex:Int=-1
        private var startIndex: Int=0
        private lateinit var spannable: SpannableString
        lateinit var tv: TextView
    }
}