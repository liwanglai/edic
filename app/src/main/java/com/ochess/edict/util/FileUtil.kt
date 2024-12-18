package com.ochess.edict.util

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStream
import java.util.Scanner


class FileUtil {
    companion object {

        fun pickFile(
            onReadFile: (data: String, file: String, uri: Uri) -> Unit = { data, file, uri -> },
            onSelectFile: ((file: String) -> Unit)? = null
        ) {
            val act = ActivityRun.context as ComponentActivity
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "text/plain"
            }
            ActivityRun.onActivityResult {
                if (it.resultCode == ComponentActivity.RESULT_OK) {
                    it.data?.data?.also { uri ->
                        if (onSelectFile != null) {
                            onSelectFile(uri.toString())
                        } else {
                            val data = readTextFile(uri)
                            val file = getFilePathByUri(uri) ?: ""
                            onReadFile(data, file, uri)
                        }
                    }
                }
            }
            ActivityRun.launcher.launch(intent)
//            val pickFileLauncher = act.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//                result.resultCode
//            }
//            pickFileLauncher.launch(intent)
        }

        fun readTextFile(uri: Uri): String {
            val stringBuilder = StringBuilder()
            ActivityRun.context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.reader().use { reader ->
                    stringBuilder.append(reader.readText())
                }
            }
            val textToRead = stringBuilder.toString()
            return textToRead
        }

        //获取Uri对应的文件路径,兼容API 26
        private fun getFilePathByUri(uri: Uri?): String? {
            val context = ActivityRun.context
            if (context == null || uri == null) return null

            val scheme = uri.scheme
            // 以 file:// 开头的
            if (ContentResolver.SCHEME_FILE.equals(scheme, ignoreCase = true)) {//使用第三方应用打开
                uri.path
            }

            return getPath(context, uri)
        }

        @SuppressLint("Range")
        private fun getPath(context: Context, contentUri: Uri): String? =
            context.contentResolver.query(contentUri, null, null, null, null).use { c ->
                val column = MediaStore.Files.FileColumns.DISPLAY_NAME
                return if (null != c && c.moveToFirst()) c.getString(c.getColumnIndex(column)) else null
            }

        fun save(joinToString: String) {

//            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
//                addCategory(Intent.CATEGORY_OPENABLE)
//                type = "text/plain"
//            }
//
//            ActivityRun.launcher.launch(intent)
        }
        fun exists(path: String?): Boolean {
            val file = File(path)
            return file.exists()
        }
        fun mkdir(path:String): Boolean {
            val fDir: File = File(path)
            if (!fDir.exists()) {
                if(!fDir.mkdirs())  {
                    mkdir(fDir.parent.toString())
                    return fDir.mkdirs()
                }
                return true
            }
            return false
        }
        var _rootDir: String? = null
        fun rootDir(): String? {
            val context: Context = ActivityRun.context()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val root = context.getExternalFilesDir(null)
                _rootDir = root!!.path
                //            _rootDir = String.valueOf(Environment.getExternalStorageDirectory());
            } else {
                _rootDir = "/mnt/sdcard/es"
            }
            return _rootDir
        }

        fun dcimDir(): String? {
            return Environment.getExternalStorageDirectory().toString()+"/DCIM/"
        }

        fun get(url:String,dOver:(c:String)->Unit){
            val client = OkHttpClient()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val request = Request.Builder().url(url).build()
                    client.newCall(request).execute().use { response ->
                        if (!response.isSuccessful) throw IOException("Unexpected code $response")
                        var textData = response.body?.string() ?: ""
                        dOver(textData)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Handle the exception, maybe update the UI to show an error message
                }
            }
        }
        fun getRes(fileId: Int): String? {
            var result: String? = null
            val ct: Context = ActivityRun.context()
            try {
                val inputStream: InputStream =ct.getResources().openRawResource(fileId)
                val s = Scanner(inputStream).useDelimiter("\\A")
                result = if (s.hasNext()) s.next() else ""
                inputStream.close()
            } catch (e: java.io.IOException) {
                e.printStackTrace()
            }
            return result
        }

        fun put(data:String,path:String): Boolean {
           return put(path,data.toByteArray(),false)
        }
        fun put(path: String?, data: ByteArray?, append: Boolean): Boolean {
            try {
                val file = File(path)
                val outputStream = if ((file.exists() && append)) FileOutputStream(
                    file,
                    append
                ) else FileOutputStream(file)
                outputStream.write(data)
                outputStream.close()
                return true
            } catch (e: FileNotFoundException) {
                val file = File(path)
                val fDir = file.parentFile
                if (!fDir.exists()) {
                    if (fDir.mkdirs()) return put(path, data, append)
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                return false
            }

            return false
        }
        fun getData(file: String, onRequest:(textData:String)->Unit) {
                val client = OkHttpClient()
                fun String.convertANSIToUTF8(): String {
                    val ansiBytes = this.toByteArray(charset("ISO-8859-1")) // 假设是ANSI编码
                    return String(ansiBytes, charset("UTF-8"))
                }
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val request = Request.Builder().url(file).build()
                        client.newCall(request).execute().use { response ->
                            if (!response.isSuccessful) throw IOException("Unexpected code $response")

                            var textData = response.body?.string() ?: ""
                            textData = textData.convertANSIToUTF8()
                            onRequest(textData)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        // Handle the exception, maybe update the UI to show an error message
                    }
                }
            }

        fun mctime(file:String): Long {
            return File(file).lastModified()
        }


        fun getUri(filePath: String): Uri {
            var uri: Uri
            val ct: Context = ActivityRun.context
            uri = if (filePath.matches(Regex(".+\\.\\w+$")) &&  Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                FileProvider.getUriForFile(ct, ct.packageName + ".files", File(filePath))
            } else {
                Uri.fromFile(File(filePath))
            }
            uri = Uri.fromFile(File(filePath))
            return uri
        }
    }
 }