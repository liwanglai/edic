package com.ochess.edict.data

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.collection.ArrayMap
import androidx.collection.arrayMapOf
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fasterxml.jackson.databind.ObjectMapper
import com.ochess.edict.presentation.main.components.Display.mt
import com.ochess.edict.util.ActivityRun
import com.ochess.edict.view.MPopMenu
import java.util.Date

/**
 * 用户状态
 *
 */
class UserStatus() {
    private val TAG: String="UserStatus"
    var config : SharedPreferences
    var name: String="config"
    var modePrivate: Int = Context.MODE_PRIVATE
    fun set(k:String,v:Any) {
        if(v==null) return
        val sharedPreferences = config
        val editor = sharedPreferences.edit()
        editor.putString(k,v.toString())
        editor.apply()
    }
    fun get(k:String,def:Int=0) :Int{
        val v = getString(k)
        return if(!v.equals(""))  v.toInt() else def
    }


    @Composable
    @SuppressLint("UnrememberedMutableState")
    fun getMutableStatus(name:String) : MutableState<Int>{
        var rt = statusMapCache.get(name)
        if(rt ==null) {
            val v = get(name)
            rt = mutableStateOf(v)
            statusMapCache.set(name,rt)
        }else{
            set(name, rt.value)
        }
        return rt
    }

    @Throws(IllegalArgumentException::class)
    fun <T> getData(name:String,dataclass:Class<T>): T? {
        var map = getString(name)
        if(map.equals("")) return null
        val objectMapper = ObjectMapper()
        val rt = objectMapper.readValue(map,dataclass)
        return rt
    }
    fun setData(name: String, obj:Any) {
        val mapper = ObjectMapper()
        val jsonString = mapper.writeValueAsString(obj)
        Log.d(TAG, "setData: "+jsonString)
        set(name,jsonString)
    }
    fun getString(k:String) :String{
        return config.getString(k,"").toString()
    }

    @Composable
    fun booleanButton(name: String,defV: Any?=false) {
        val btMap = arrayMapOf(false to "close", true to "open")
        var v by remember {
            val dv = if(defV==null) false else defV as Boolean
            mutableStateOf(config.getBoolean(name,dv))
        }
        val value = btMap[v]
        Button({
                    v = !v
                    config.edit().apply{
                        putBoolean(name,v)
                        apply()
                    }
                },
                modifier = Modifier.padding(5.dp)
        ){
            Text(mt(name)+": "+mt(value!!), color = if(v) Color.Green else Color.Gray)
        }
    }

    @Composable
    fun switchButton(name: String, wordLenList: Any,defV:Any?=0) {
        var pmenu:MPopMenu=MPopMenu.Unspecified
        if(wordLenList.javaClass.equals(pmenu.javaClass)){
            pmenu = wordLenList as MPopMenu
        }else {
            pmenu = MPopMenu((wordLenList as List<String>).mapIndexed { index, it ->
                val v = if (it.matches(Regex("\\d+"))) it.toInt() else index
                MPopMenu.dataClass(it, value = v)
            })
        }
        pmenu.upMtTitle()
        val dv = if(defV ==null) 0 else defV as Int
        switchButton(name,pmenu,dv)
    }
    @Composable
    fun switchButton(name: String, pmenu: MPopMenu,defV:Int) {
        var v by remember {
            mutableStateOf(config.getInt(name,defV))
        }

        Row {
            Button(
                onClick = {
                    pmenu.show { k, vv ->
                        v = vv.value as Int
                        config.edit().apply {
                            putInt(name, v)
                            apply()
                        }
                    }
                },
                modifier = Modifier.padding(9.dp)
            ) {
                Text(mt(name) + ":" + pmenu.items[v].title)
            }
            pmenu.add()
        }
    }


    init {
        config = ActivityRun.context.getSharedPreferences(name, modePrivate)

    }

    companion object {
        val defInterface = UserStatus()
        private const val KEY_EXPIRATION_TIME = "_expiration_time"
        fun set(timeMs:Long=-1,setf:(c: SharedPreferences.Editor)->String) {
            val editer = defInterface.config.edit()
            val key=setf(editer)
            if(timeMs>0) {
                editer.putLong(key + KEY_EXPIRATION_TIME, Date().getTime() + timeMs)
            }
            editer.apply()
        }

        fun <T> get(getf: (c:SharedPreferences)->T) :T {
            return getf(defInterface.config)
        }
        fun remove(key: String) {
            val editor: SharedPreferences.Editor = defInterface.config.edit()
            editor.remove(key)
            editor.remove(key + KEY_EXPIRATION_TIME)
            editor.apply()
        }
        fun <T> get(key:String,getf: SharedPreferences.(k:String)->T) :T {
            val expirationTime: Long = defInterface.config.getLong(key + KEY_EXPIRATION_TIME, -1)
            if (expirationTime != -1L && Date().time > expirationTime) {
                // 已过期，清除该键值对
                remove(key)
            }
            return getf.invoke(defInterface.config,key)
        }

        val statusMapCache: ArrayMap<String, MutableState<Int>> = arrayMapOf<String,MutableState<Int>>()
    }
}