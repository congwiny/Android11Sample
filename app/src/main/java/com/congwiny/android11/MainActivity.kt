package com.congwiny.android11

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.storage.StorageManager
import android.provider.Settings
import android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.congwiny.android11.filemanager.FileMainActivity
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadSampleListener
import com.liulishuo.filedownloader.FileDownloader
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import java.io.File


class MainActivity : AppCompatActivity() {

    private var imageView: ImageView? = null

    companion object {
        const val IMAGE_URL =
            "http://n.sinaimg.cn/sinacn10112/566/w1018h1148/20191111/fd6e-iieqapt6530904.jpg"
        const val IMAGE_URL2 =
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1603042397985&di=b507241827170742673ac2438844dd52&imgtype=0&src=http%3A%2F%2Fimg3.imgtn.bdimg.com%2Fit%2Fu%3D2802998753%2C362775541%26fm%3D214%26gp%3D0.jpg"
        const val IMAGE_URL3 =
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1603042394835&di=2dcdf983f0f4558fb2db549238c987c8&imgtype=0&src=http%3A%2F%2Fimg.mp.itc.cn%2Fupload%2F20170122%2F2bbd086e66c344729f3e67668de7adfb_th.jpg"
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imageView = findViewById(R.id.meinv_iv)
        AndPermission.with(this)
            .runtime()
//            .permission(
//                Permission.ACCESS_FINE_LOCATION,
//                Permission.ACCESS_COARSE_LOCATION
//            )
            .permission(Permission.Group.STORAGE)
            .onGranted {
                Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show()
            }.onDenied {
                Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show()
            }
            .start()

        imageView?.postDelayed({
            Log.e(TAG, "requst ACCESS_BACKGROUND_LOCATION")
            AndPermission.with(this)
                .runtime()
                .permission(
                    Permission.ACCESS_BACKGROUND_LOCATION
                )
                .onGranted {
                    Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show()
                }.onDenied {
                    Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show()
                }
                .start()
        }, 10000)
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
                    Log.e(TAG, "downloadAndroidData error:" + Log.getStackTraceString(e))
                }
            })
            .start()
    }

    //target 26可以。。卸载了数据会保留
    // target30不可以
    fun downloadCongwiny(view: View) {
        val file = File(Environment.getExternalStorageDirectory(), "congwiny/meinv.jpg")
        FileDownloader.getImpl().create(IMAGE_URL3)
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
                        MediaScannerConnection.scanFile(
                            this@MainActivity,
                            arrayOf(file.absolutePath), arrayOf("image/jpeg"), null
                        )
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
        intent.setClass(this, FileMainActivity::class.java)
        startActivity(intent)
    }

    fun requestAllFile(view: View) {
        val intent = Intent(ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
        startActivity(intent)
    }

    fun mediaprovider(view: View) {
        val intent = Intent()
        intent.setClass(this, MediaProviderActivity::class.java)
        startActivity(intent)
    }

    fun deleteDownloadFile(view: View) {
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "meinv.jpg"
        )
        val result = file.delete()
        Log.e(TAG, "deleteDownloadFile result=$result")
    }

    fun actionManageStorage(view: View) {
        val intent = Intent(StorageManager.ACTION_MANAGE_STORAGE)
        startActivityForResult(intent, 110)
    }

    fun testSAF(view: View) {
        val intent = Intent()
        intent.setClass(this, SAFActivity::class.java)
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun testOverlayPermission(view: View) {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        intent.data = Uri.parse("package:$packageName")
        startActivity(intent)
    }

    fun testForegroundType(view: View) {
        val intent = Intent()
        intent.setClass(this, MyNavigationService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        }

        imageView?.postDelayed({
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.e(TAG,"delay testForegroundType")
                startForegroundService(intent)
            }
        }, 1000 * 60)
    }

    fun testToast(view: View) {
        imageView?.postDelayed({
            val toast = Toast(this@MainActivity)
            toast.view = TextView(this).apply {
                //NotificationService: Toast already killed. pkg=com.congwiny.android11 token=android.os.BinderProxy@39859dc
                setText("toast view")
                setTextColor(Color.RED)
            }
            toast.show()
            //Toast.makeText(this@MainActivity,"toast test",Toast.LENGTH_SHORT).show()
            Log.e(TAG,"showToast...")
        }, 60*1000)
    }


    //添加权限可访问 <uses-permission android:name="android.permission.QUERY_ALL_PACKAGES"/>
    //或者 <queries>
    fun testAppVisibility(view: View) {

        try {
            val info = packageManager.getPackageInfo("com.congwiny.android11_2", 0)
            if (info != null) {
                Log.e(TAG, "testAppVisibility info=$info")
            } else {
                Log.e(TAG, "testAppVisibility getInfo is null")
            }
            val pkgs = packageManager.getInstalledPackages(0)
            pkgs.forEach {
                Log.e(TAG, "testAppVisibility pkg=" + it.packageName)
            }
        } catch (e: Exception) {
            /**
             *   android.content.pm.PackageManager$NameNotFoundException: com.congwiny.android11_2
            at android.app.ApplicationPackageManager.getPackageInfoAsUser(ApplicationPackageManager.java:206)
            at android.app.ApplicationPackageManager.getPackageInfo(ApplicationPackageManager.java:178)
             */
            Log.e(TAG, "testAppVisibility info", e)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            110 -> {
                Log.e(TAG, "onActivityResult data=$data")
                val intent = Intent(StorageManager.ACTION_CLEAR_APP_CACHE)
                startActivityForResult(intent, 120)
            }
            120 -> {
                Log.e(TAG, "onActivityResult data=$data")
            }
            130 -> {
                Log.e(TAG, "onActivityResult data=$data")
            }
        }

    }
}
