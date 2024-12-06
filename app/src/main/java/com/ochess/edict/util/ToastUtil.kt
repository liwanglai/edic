package com.ochess.edict.util

import android.content.Context
import android.widget.Toast

class ToastUtil private constructor(){


    companion object {

        private var context: Context? = null

        @JvmStatic
        fun initContext(ctx: Context) {
            context = ctx.applicationContext
        }

        @JvmStatic
        fun showToast(message: String) {
            context?.let {
                Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}