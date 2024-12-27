package com.ochess.edict

import com.ochess.edict.data.config.SettingConf
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

data class aa(var a:Int){
}
    @Test
    fun ref(){

        val a = arrayOf(aa(1),aa(2),aa(3))
        var b = listOf<aa>(aa(2))
        val c =a.intersect(b).toTypedArray()
        System.out.print(c.toString())
        assertEquals(c.size,1)
//        val kClass = SettingConf::class
//        kClass.memberProperties.forEach { property ->
//            System.out.print(property)
//        }
//        assertTrue(chAll.size==3)
    }
}