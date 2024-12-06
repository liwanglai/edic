package com.ochess.edict.print

import android.os.Bundle
import com.dothantech.printer.IDzPrinter

class Params (
    val gapType: Int = -1,
    val gapLength: Int = 3,
    val printDensity: Int = -1,
    val printSpeed: Int = -1){

    // 用于填充的数组及集合列表
    val printDensityList = arrayOf(
        "随打印机设置",
        "1 (最淡)",
        "2",
        "3 (较淡)",
        "4",
        "5",
        "6 (正常)",
        "7",
        "8",
        "9",
        "10",
        "11",
        "12(较深)",
        "13",
        "14",
        "15",
        "16",
        "17",
        "18",
        "19",
        "20(最深)"
    )
     val printSpeedList =
        arrayOf("随打印机设置", "1 (最慢)", "2 (较慢)", "3 (正常)", "4 (较快)", "5 (最快)")
     val gapTypeList = arrayOf("随打印机设置", "连续纸", "定位孔", "间隙纸", "黑标纸")
     val bitmapOrientations = intArrayOf(0, 90, 0, 90)

    // 获取打印时需要的打印参数，不设置就默认跟随打印机设置
    fun getPrintParam(copies: Int, orientation: Int): Bundle? {
        val param = Bundle()

        // 间隔类型
        if (gapType >= 0) {
            param.putInt(IDzPrinter.PrintParamName.GAP_TYPE, gapType)
        }
        // 间隔长度
        if (gapLength >= 0) {
            param.putInt(IDzPrinter.PrintParamName.GAP_LENGTH, gapLength)
        }
        // 打印浓度
        if (printDensity >= 0) {
            param.putInt(IDzPrinter.PrintParamName.PRINT_DENSITY, printDensity)
        }

        // 打印速度
        if (printSpeed >= 0) {
            param.putInt(IDzPrinter.PrintParamName.PRINT_SPEED, printSpeed)
        }

        // 打印页面旋转角度
        if (orientation != 0) {
            param.putInt(IDzPrinter.PrintParamName.PRINT_DIRECTION, orientation)
        }

        // 打印份数
        if (copies > 1) {
            param.putInt(IDzPrinter.PrintParamName.PRINT_COPIES, copies)
        }
        return param
    }
}