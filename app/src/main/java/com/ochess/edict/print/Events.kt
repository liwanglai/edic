package com.ochess.edict.print

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.dothantech.lpapi.LPAPI
import com.dothantech.printer.IDzPrinter.PrintProgress
import com.dothantech.printer.IDzPrinter.PrinterAddress
import com.dothantech.printer.IDzPrinter.PrinterState
import com.dothantech.printer.IDzPrinter.ProgressInfo

class Events(var mPrinter: SnapshotStateMap<String, String>,) : LPAPI.Callback{
    //****************************************************************************************************************************************
    // 所有回调函数都是在打印线程中被调用，因此如果需要刷新界面，需要发送消息给界面主线程，以避免互斥等繁琐操作。
    //****************************************************************************************************************************************

    private val TAG: String="print.Events"

    //****************************************************************************************************************************************
    // 所有回调函数都是在打印线程中被调用，因此如果需要刷新界面，需要发送消息给界面主线程，以避免互斥等繁琐操作。
    //****************************************************************************************************************************************
    // 打印机连接状态发生变化时被调用
    override fun onStateChange(arg0: PrinterAddress, arg1: PrinterState?) {
        val printer: PrinterAddress = arg0
        when (arg1) {
            //连接信息太多 不要更新状态
//            PrinterState.Connected, PrinterState.Connected2 ->                     // 打印机连接成功，发送通知，刷新界面提示
                //MPrinter.printer.upStatus(MPrinter.StatusNames.connected)
            ////这里不成功 不知道为什么断开链接的时候这里没有抛出来
            PrinterState.Disconnected ->                     // 打印机连接失败、断开连接，发送通知，刷新界面提示
                MPrinter.printer.upStatus(MPrinter.StatusNames.connectErr)
            else -> {}
        }
        Log.d(TAG, "onStateChange: "+printer.shownName+" -> "+arg1.toString())
    }

    // 蓝牙适配器状态发生变化时被调用
    override fun onProgressInfo(arg0: ProgressInfo?, arg1: Any?) {

        Log.d(TAG, "onProgressInfo: ")
    }

    override fun onPrinterDiscovery(printerAddress: PrinterAddress?, o: Any?) {
        Log.d(TAG, "onPrinterDiscovery: ")
    }

    // 打印标签的进度发生变化是被调用
    override fun onPrintProgress(
        address: PrinterAddress?,
        bitmapData: Any?,
        progress: PrintProgress?,
        addiInfo: Any?
    ) {
        when (progress) {
            PrintProgress.Success ->                     // 打印标签成功，发送通知，刷新界面提示
                MPrinter.printer.upStatus(MPrinter.StatusNames.printEnd)

            PrintProgress.DataEnded,PrintProgress.Failed ->                     // 打印标签失败，发送通知，刷新界面提示
                MPrinter.printer.upStatus(MPrinter.StatusNames.printErr)
            else -> {
                1
            }
        }
    }

}
