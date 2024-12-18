package com.ochess.edict.presentation.main.extend

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun setTimeout(us:Long,run:()->Unit)  {
    GlobalScope.launch {
        delay(us)
        run()
    }
}