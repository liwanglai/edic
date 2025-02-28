package com.ochess.edict.presentation.main.extend

import android.os.Handler
import androidx.collection.arrayMapOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext


fun bgRun(run:()->Unit)  {
    CoroutineScope(Dispatchers.IO).launch {
        runBlocking {
            withContext(Dispatchers.IO) {
                run()
            }
        }
    }
}
fun MainRun(timeMs:Long,run:()->Unit)  {
    Handler().postDelayed({
        try{
            run()
        }catch (e:Exception){
            e.printStackTrace()
        }
    },timeMs)
}
fun MainRun(run:()->Unit)  {
    runBlocking{
        withContext(Dispatchers.Main) {
            run()
        }
    }
}
val oneRunMap= arrayMapOf<String,Boolean>()
fun oneRun(run:()->Unit){
    val rid = run.javaClass.name
    if(oneRunMap[rid]==null){
        oneRunMap[rid] = true
        run()
    }
}