package com.ochess.edict.print

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Picture
import android.os.Bundle
import android.os.Handler
import android.os.ParcelUuid
import android.util.Log
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.platform.LocalView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dothantech.lpapi.LPAPI
import com.dothantech.printer.IDzPrinter
import com.dothantech.printer.IDzPrinter.AddressType
import com.dothantech.printer.IDzPrinter.PrinterAddress
import com.ochess.edict.bluetooth.Constants
import com.ochess.edict.data.UserStatus
import com.ochess.edict.data.config.PathConf
import com.ochess.edict.data.local.entity.HistoryEntity
import com.ochess.edict.presentation.history.HistoryViewModel
import com.ochess.edict.presentation.history.textValue
import com.ochess.edict.util.ActivityRun
import com.ochess.edict.util.ToastUtil
import com.ochess.edict.util.utilImage
import com.ochess.edict.view.WordSearchView
import com.printsdk.PrintSerializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID
import java.util.regex.Pattern


class MPrinter {
    companion object {
        val printer = MPrinter()
        const val DEVICE_NAME = "/dev/ttyS3"
        const val DEVICE_PORT = "9600"
        const val THUMBNAIL_WIDTH = 550
        const val CONNECT_TIMEOUT = 5000

    }

    private var initBluetoothed: Boolean = false
    val mHandler = Handler()
    /**
     * name
     * address
     * type
     * status
     * title
     */
    val status = mutableStateMapOf("status" to  StatusNames.noConnect)
    class StatusNames{
        companion object {
            const val noConnect = "未连接"
            const val connectIng = "连接中..."
            const val connected = "已连接"
            const val connectErr = "连接失败"
            const val startPrint = "开始打印"
            const val printIng = "打印中..."
            const val printEnd = "打印完成"
            const val printErr = "打印失败"
        }
    }
    private val mPrinter: PrintSerializable = PrintSerializable()
    fun checkState() {
        // 检查权限
        if (mPrinter.state != PrintSerializable.CONN_SUCCESS) {
            if (Constants.name != null) { //蓝牙
                //WorkService.workThread.connectBt(Constants.address)
                // mPrinter.open(Constants.address,Constants.name,BluetoothAdapter.getDefaultAdapter())
            } else { // 串口
                mPrinter.open(DEVICE_NAME, DEVICE_PORT)
            }
        }
    }


    //@SuppressLint("MissingPermission")
    fun initBluetooth() {
        val bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (ContextCompat.checkSelfPermission(
                ActivityRun.context,
                Manifest.permission.BLUETOOTH_CONNECT
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            // 请求权限
            ActivityCompat.requestPermissions(
                ActivityRun.context as Activity,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT,Manifest.permission.BLUETOOTH_SCAN
                    ,Manifest.permission.BLUETOOTH_ADMIN
//                    ,Manifest.permission.BLUETOOTH_ADVERTISE
//                    ,Manifest.permission.BLUETOOTH_PRIVILEGED
                ), 11
            )
        } else {
            if (!bluetoothAdapter.isEnabled && !initBluetoothed) {
                // 使能蓝牙
                val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                ActivityRun.context.startActivity(enableIntent)
                initBluetoothed = true
                return
            }
            // 已有权限，执行操作
            //val bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            val devices: Set<BluetoothDevice> = bluetoothAdapter.bondedDevices
            if (devices.isNotEmpty()) {
                var value = ""
                for (item in devices) {
                    if (item.bondState == BluetoothDevice.BOND_BONDED) {
                        Constants.bluetoothList[item.name] = item.address
                        //ToastUtil.showToast(item.name + "-" + item.bluetoothClass)
                        value += " -" + item.name + " > " + item.bluetoothClass + " "
                        textValue = value
                        val bluetoothClass: String = "" + item.bluetoothClass
                        if (bluetoothClass == "40680") {
//                            if (!WorkService.workThread.isConnected()) {
//                                WorkService.workThread.connectBt(item.address)
//                            }else{
//                                break;
//                            }
                            status["name"] = item.name
                            status["address"] = item.address
                            status["type"] = "bluetooth"
                        }
                    }
                }
            } else {
                ToastUtil.showToast("none Data")
            }
        }
    }
    private fun outline(line:String){
        var str=line
        if(str.length>28) str=line.substring(0,28)
//        else if(str.length<30){
//            var sp = " ".repeat(30-str.length-1)
//            str = str+sp+"│"
//        }
        mPrinter.printText(str) ;mPrinter.wrapLines(1)
    }
    private fun printText(printList: ArrayList<HistoryEntity>) {
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val dateNow = Date(System.currentTimeMillis())

        val name=""
        val title = "欧骑士背单词"
        val date: String = dateFormatter.format(dateNow)
        var game = WordSearchView(ActivityRun.context,null)
        mPrinter.open(DEVICE_NAME, DEVICE_PORT)
        if (mPrinter.state == PrintSerializable.CONN_SUCCESS){
            mPrinter.init()

            mPrinter.printText("abc")
            // mWidth       -- 字体宽度    0-7
            // mHeight      -- 字体高度    0-7
            // mBold        -- 是否粗体    0,1
            // mUnderline   -- 是否下划线  0,1
            mPrinter.setFont(1, 1, 0, 0)
            mPrinter.setAlign(1)
            mPrinter.wrapLines(1)
            mPrinter.printText(title)  ;mPrinter.wrapLines(2)

            mPrinter.setAlign(0)
            mPrinter.setFont(0,0, 0, 0)
            mPrinter.printText("姓名: $name") ;mPrinter.wrapLines(1)
            mPrinter.printText("日期：$date") ;mPrinter.wrapLines(1)
            mPrinter.printText("周期：第___周___月___日强化") ;mPrinter.wrapLines(1)

            if( printList.size > 0) {
                val game = WordSearchView(ActivityRun.context,null)
                val br ="----------------------------"  //32  16
                val brs="-------------┼-------------"
                val bg=br.substring(0,13)
                val sp ="             "
                mPrinter.printText("┌$br┐")  ;mPrinter.wrapLines(1)
                var hasNext =printList.size
                for (item in printList) {
                    var bs = " ".repeat((14-item.word.length)/2)
                    var word = bs+item.word+bs
                    if(word.length!=13) word=word.substring(0,13)
                    val groups = Pattern.compile("(.{7})").matcher(item.ch)
                    var ct = groups.groupCount()
                    groups.find()
                    var ch1=groups.group() ; var ch2=sp; var ch3=sp
                    if(ch1.length < 14){
                        bs = " ".repeat((14-ch1.length)/2)
                        ch1 = bs+ch1+bs
                        if(ch1.length!=13) ch1=ch1.substring(0,13)
                    }
                    if(groups.find()) {
                        ch2 = groups.group()
                        if (groups.find()) {
                            ch3 = groups.group()
                            if (groups.find()) {
                                ch3 = ch3.replace(".{3}$","...")
                            }
                        }
                    }else if(groups.find()){
                        ch2 = ch1
                        ch1 = sp
                    }
                    outline("│$word│$ch1│")
                    outline("├$bg┤$ch2│")
                    outline("│$sp│$ch3│")
                    if(hasNext-- >1) {
                        mPrinter.printText("┠$brs┨") ;mPrinter.wrapLines(1)
                    }
                    game.addWord(item.word)
                }
                mPrinter.printText("└$br┘")               ;mPrinter.wrapLines(1)
                mPrinter.wrapLines(1)

                mPrinter.setAlign(1)
                mPrinter.setFont(2,0, 1, 0)
                val strs = game.getWordByStrings()
                for(line in strs) {
                    mPrinter.printText(line)
                    mPrinter.wrapLines(1)
                }
            }
//            mPrinter.Cutter();
//            mPrinter.close()
        }
    }
    fun toBluetooth(printBitmap: Bitmap){
//        if (!WorkService.workThread.isConnected) {
//            WorkService.workThread.connectBt(status["address"])
//        }

        val data = Bundle()
        // data.putParcelable(Global.OBJECT1, mBitmap);
        data.putParcelable(Global.PARCE1, printBitmap)
        data.putInt(Global.INTPARA1, 384)
        data.putInt(Global.INTPARA2, 0)
//        WorkService.workThread.handleCmd(
//            Global.CMD_POS_PRINTPICTURE, data
//        )
    }

    fun toSerial(printBitmap: Bitmap, size:Int=368){
        mPrinter.open(DEVICE_NAME, DEVICE_PORT)
        when (mPrinter.state) {
            PrintSerializable.CONN_SUCCESS -> {
                mPrinter.init()
//                for(i in 1..1000)
//                    mPrinter.printText(" ")
                mPrinter.OpenDrawer(true,true)
                //mPrinter.setFont(0, 0, 0, 0);
                mPrinter.setAlign(1)
                mPrinter.wrapLines(1)
                mPrinter.printImage(printBitmap)
                mPrinter.Cutter()
                mPrinter.Beeper(10)
            }
            PrintSerializable.CONN_CLOSED -> {
                Log.d(HistoryViewModel.TAG, "CONN_CLOSED")
                //toastMessage.value = "Serial CONN_CLOSED"
            }
            PrintSerializable.CONN_FAILED -> {
                // toastMessage.value = "Serial CONN_FAILED"
                Log.d(HistoryViewModel.TAG, "Serial CONN_FAILED")
            }
        }

//        mPrinter.close()
    }

    fun printImg(printBitmap: Bitmap) {
//        var thumBitmap = utilImage.fixSerialImg(printBitmap, 368F)
        printer.upStatus(StatusNames.printIng)
        val thumBitmap = utilImage.thumbnail(printBitmap, THUMBNAIL_WIDTH)
//        toSerial(thumBitmap)
        toJpgFile(printBitmap)
        if(status["type"].equals("bluetooth")) {
            val param = Bundle()
           // lpApi.printBitmap(thumBitmap, param)
        }

    }

    private fun toJpgFile(printBitmap: Bitmap) {
        val dateNow = Date(System.currentTimeMillis())
        val file = SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(dateNow)
        utilImage.bitmap2jpg(printBitmap, PathConf.print+"$file.jpg")
    }
//    private var lpApi: LPAPI = LPAPI.Factory.createInstance(Events(status))
    fun init() {
        if(status["name"] == null) {
            initBluetooth()
//            val blist =  lpApi.getAllPrinterAddresses(null)
//            if (blist.size >0) {
//                for (item in blist) {
//                    Constants.bluetoothList[item.shownName] = item.macAddress
//                    if (item.shownName.startsWith("DT-393G")) {
//                        status["name"] = item.shownName
//                        status["address"] = item.macAddress
//                        status["type"] = "bluetooth"
//                    }
//                }
//            }

            val name = UserStatus().getString("lastPrintConnect")
            if(name.isNotEmpty() && !status["name"].equals(name) && Constants.bluetoothList[name] !=null){
                status["name"] = name
                status["address"] = Constants.bluetoothList[name] ?:""
            }
            if(status["name"] == null) {
                status["status"] = StatusNames.connected
            }
            //lpApi.getAllPrinterAddresses(null);
        }

//        when(lpApi.printerState)
//        {
//            IDzPrinter.PrinterState.Connected ,IDzPrinter.PrinterState.Connected2 -> {
//                if(!canPrint()) {
//                    upStatus(StatusNames.connected)
//                }
//            }
//            IDzPrinter.PrinterState.Connecting ->
//                upStatus(StatusNames.connectIng)
//            IDzPrinter.PrinterState.Disconnected ->
//                upStatus(StatusNames.noConnect)
//            IDzPrinter.PrinterState.Printing ,IDzPrinter.PrinterState.Working->
//                upStatus(StatusNames.printIng)
//        }

        when(status["status"])
        {
            StatusNames.noConnect ->{
                connect()
            }
            StatusNames.connected ->{
                UserStatus().set("lastPrintConnect", status["name"]!!)
            }
        }
    }

    /**
     * 搜索蓝牙列表
     */
    @SuppressLint("MissingPermission")
    fun scanDevice(okdo : (blist: HashMap<String, String>)->Unit){
        val bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val sc:ScanCallback=object:ScanCallback(){
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                super.onScanResult(callbackType, result)
                val device = result!!.device

                when (callbackType) {
                    ScanSettings.CALLBACK_TYPE_FIRST_MATCH -> {
                        Constants.bluetoothList[device.name] = device.address
                        okdo(Constants.bluetoothList)
                    }

                    ScanSettings.CALLBACK_TYPE_MATCH_LOST ->
                        Constants.bluetoothList.remove(device.name)

                    else -> {}
                }
            }
        }
        var scanSettings = ScanSettings.Builder() // 设置扫描模式
            .setScanMode(ScanSettings.SCAN_MODE_BALANCED) // 设置回调类型
            .setReportDelay(0)
            .build()

        val scanFilter = ScanFilter.Builder()
            .setDeviceName("DT")
            .setServiceUuid(ParcelUuid(UUID.randomUUID()))
            .build()
        ////这里不成功似乎权限问题 后面看
        bluetoothAdapter.bluetoothLeScanner.startScan(sc)
//        bluetoothAdapter.bluetoothLeScanner.startScan(arrayListOf(scanFilter),scanSettings,sc)
    }
    private fun connect() {
        upStatus(StatusNames.connectIng)
        val printer =
            PrinterAddress(status["name"], status["address"], AddressType.BLE)
       // lpApi.openPrinterByAddress(printer)

        Handler().postDelayed({
            if(status["status"].equals(StatusNames.connectIng))
                upStatus(StatusNames.connectErr)
        }, CONNECT_TIMEOUT.toLong()) //延时15s执行

    }

    fun upStatus(statusName:String ) {
        status["status"] = statusName
        status["title"] = status["name"] +"(" + status["status"] + ")"
        onStatusChangeFun(statusName)
        Log.d("HistoryScreen", "upStatus: $statusName")

    }
    var onStatusChangeFun :(String) -> Unit={}
    fun onStatusChange(Fun: (String) -> Unit) {
        onStatusChangeFun = Fun
    }

    private fun isConnected(): Boolean {
        return status["status"].equals(StatusNames.connected)
    }
    fun canPrint(): Boolean {
        val cArr = arrayListOf(StatusNames.connectIng,StatusNames.connectErr,StatusNames.noConnect)
        val indexOf = cArr.indexOf(status["status"])
        return indexOf == -1
    }
    @Composable
    fun printView(composeView: View) :Boolean {
            val width = composeView.width
            val height = composeView.height
            // 创建一个空的Bitmap
            var bitmap:Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            // 使用Canvas将Compose View的内容绘制到Bitmap上
            val canvas = Canvas(bitmap)
            val picture = Picture()
            composeView.draw(picture.beginRecording(width, height))
            picture.endRecording()
            picture.draw(canvas)
            if(composeView == LocalView) {
                bitmap = ActivityRun.getSnapshot()!!
            }
            // 如果Compose View不在主线程，需要使用View.post{}来访问它
            printImg(bitmap)
            return true
    }
    fun printView(): Boolean {
        if(!isConnected()) return false
        val  bitmap = ActivityRun.getSnapshot()!!
        printImg(bitmap)
        return true
    }
}

