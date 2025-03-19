package com.luv.link.utils

import android.annotation.SuppressLint
import com.luv.link.LuvLinkApplication
import com.luv.link.global.config.GlobalConfig.logFileDirectory
import com.luv.link.global.data.GlobalData.yYmmDdDateFormat
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.io.BufferedWriter
import java.io.File
import java.io.FileNotFoundException
import java.io.FileWriter
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.Date
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

@Module
@InstallIn(SingletonComponent::class)
object FilesUtil {
    fun checkFileExist(fileAndPath: String?): Boolean =
        try {
            fileAndPath?.let {
                val file = File(it)
                file.exists()
            } ?: false
        } catch (e: FileNotFoundException) {
            // Timber.e(e, "File not found during checkFileExist")
            false
        }

    fun createFolderInDataPath(folderName: String): Boolean =
        try {
            val dir = LuvLinkApplication().getExternalFilesDir(folderName)
            if (dir?.exists() == false) {
                dir.mkdirs()
                true
            } else {
                true
            }
        } catch (e: Exception) {
            Timber.e(e, "Error creating folder in data path")
            false
        }

    @SuppressLint("SimpleDateFormat")
    fun logInSdcard(
        tag: String,
        msg: String,
        coroutineScope: CoroutineScope
    ) {
        coroutineScope.launch(Dispatchers.IO) {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
            val sdf1 = SimpleDateFormat(yYmmDdDateFormat)
            val date = Date()
            val filename = logFileDirectory + "logcat" + sdf1.format(date) + "_nibble.txt"
            val message = msg.replace("(\\r|\\n|\\r\\n)+".toRegex(), "  ")
            val directory = File(logFileDirectory)

            try {
                if (checkFileExist(filename)) {
                    FileWriter(filename, true).use { fw ->
                        BufferedWriter(fw).use { bw ->
                            PrintWriter(bw).use { out ->
                                out.println("${sdf.format(date)} || $tag || $message")
                            }
                        }
                    }
                } else if (!directory.exists()) {
                    directory.mkdirs()
                } else {
                    File(filename).createNewFile()
                }
            } catch (e: Exception) {
                Timber.e(e, "Error writing to log file")
            }
        }
    }
}
