package com.iotserv.frpc.frpc

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.app.ActivityCompat
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader


object Utils {
    private const val REQUEST_EXTERNAL_STORAGE = 1
    private const val REQUEST_OPEN_DOC = 2
    private val PERMISSIONS_STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    fun verifyStoragePermissions(activity: Activity) {
        // Check if we have write permission
        val permission = ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                activity,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
        }
    }

    fun doBrowseFile(activity: Activity) {
        var chooseFileIntent = Intent(Intent.ACTION_GET_CONTENT)
        chooseFileIntent.type = "*/*"
        chooseFileIntent.addCategory(Intent.CATEGORY_OPENABLE)
        chooseFileIntent = Intent.createChooser(chooseFileIntent, "Choose a file")
        activity.startActivityForResult(chooseFileIntent, REQUEST_OPEN_DOC)
    }

    fun parseBrowseIntent(requestCode: Int, intent: Intent?): Uri? {
        if (requestCode == REQUEST_OPEN_DOC) {
            intent?.data?.also { uri ->
                return uri
            }
        }
        return null
    }

    fun readTextFromUri(context: Context, uri: Uri): String {
        val stringBuilder = StringBuilder()
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                var line: String? = reader.readLine()
                while (line != null) {
                    stringBuilder.append(line)
                    line = reader.readLine()
                }
            }
        }
        return stringBuilder.toString()
    }

    fun saveFile(context: Context, uri: Uri, path: String) {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            File(path).outputStream().use {
                inputStream.copyTo(it)
            }
        }
    }
}