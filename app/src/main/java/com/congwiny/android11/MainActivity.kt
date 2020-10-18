package com.congwiny.android11

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.congwiny.android11.filemanager.FileMainActivity
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadSampleListener
import com.liulishuo.filedownloader.FileDownloader
import com.yanzhenjie.permission.Action
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import java.io.File


class MainActivity : AppCompatActivity() {

    private var imageView:ImageView? = null
    companion object {
        const val IMAGE_URL =
            "http://n.sinaimg.cn/sinacn10112/566/w1018h1148/20191111/fd6e-iieqapt6530904.jpg"
        const val IMAGE_URL2 =
            "http://img.mp.itc.cn/upload/20170302/2c3597d777d5407d938404b7d1c1a992_th.jpg"
        const val IMAGE_URL3 =
            "https://hbimg.huabanimg.com/921eef0fcf1c50e611f2c2e1b8ba2a708314d4d837671-X2w943_fw658/format/webp"
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imageView = findViewById(R.id.meinv_iv)
        AndPermission.with(this)
            .runtime()
            .permission(Permission.Group.STORAGE)
            .onGranted {
                Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show()
            }.onDenied {
                Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show()
            }
            .start()
    }

    //不管什么target, android 11其他应用上访问不了

    fun downloadAndroidData(view: View) {
        val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "meinv.jpg")
        FileDownloader.getImpl().create(IMAGE_URL)
            .setPath(file.absolutePath)
            .setListener(object : FileDownloadSampleListener() {
                override fun started(task: BaseDownloadTask?) {
                    Log.e(TAG, "downloadAndroidData started")
                }
                override fun completed(task: BaseDownloadTask) {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "下载到AndroidData完成", Toast.LENGTH_SHORT)
                            .show()
                        Glide.with(this@MainActivity).load(file).into(imageView!!)
                    }
                    Log.e(TAG, "downloadAndroidData completed")
                }

                override fun error(task: BaseDownloadTask, e: Throwable) {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "下载到AndroidData失败", Toast.LENGTH_SHORT)
                            .show()
                    }
                    Log.e(TAG, "downloadAndroidData error:"+Log.getStackTraceString(e))
                }
            })
            .start()
    }

    //target 26可以。。卸载了数据会保留
    // target30不可以
    fun downloadCongwiny(view: View) {
        val file = File(Environment.getExternalStorageDirectory(), "congwiny/meinv.jpg")
        FileDownloader.getImpl().create(IMAGE_URL2)
            .setPath(file.absolutePath)
            .setListener(object : FileDownloadSampleListener() {
                override fun completed(task: BaseDownloadTask) {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "下载到Congwiny完成", Toast.LENGTH_SHORT)
                            .show()
                        Glide.with(this@MainActivity).load(file).into(imageView!!)
                    }
                    Log.e(TAG, "downloadCongwiny completed")

                }

                override fun error(task: BaseDownloadTask?, e: Throwable) {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "下载到Congwiny失败", Toast.LENGTH_SHORT)
                            .show()
                    }
                    Log.e(TAG, "downloadCongwiny", e)
                }
            })
            .start()
    }

    fun downloadDownload(view: View) {
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "meinv.jpg"
        )
        FileDownloader.getImpl().create(IMAGE_URL3)
            .setPath(file.absolutePath)
            .setListener(object : FileDownloadSampleListener() {
                override fun completed(task: BaseDownloadTask) {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "下载到Download完成", Toast.LENGTH_SHORT)
                            .show()
                        Glide.with(this@MainActivity).load(file).into(imageView!!)

                    }
                    Log.e(TAG, "downloadDownload completed")

                }

                override fun error(task: BaseDownloadTask?, e: Throwable) {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "下载到Download失败", Toast.LENGTH_SHORT)
                            .show()
                    }
                    Log.e(TAG, "downloadDownload", e)
                }
            })
            .start()
    }

    fun openFileManager(view: View) {
        val intent = Intent()
        intent.setClass(this,FileMainActivity::class.java)
        startActivity(intent)
    }
}
