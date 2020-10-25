package com.congwiny.android11

import android.app.RecoverableSecurityException
import android.content.ContentUris
import android.content.ContentValues
import android.content.IntentSender.SendIntentException
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.IOException
import java.io.OutputStream


class MediaProviderActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MediaProviderActivity"
    }

    private var meinvIv: ImageView? = null
    private val mediaList: ArrayList<Uri> = ArrayList()
    private var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_provider)
        meinvIv = findViewById(R.id.meinv_iv)
    }

    fun queryMediaFile(view: View) {
        val projection = arrayOf(
            MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.ImageColumns.DATA
        )

        val list = ArrayList<Uri>()
        val c = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            null
        )
        if (c != null) {
            while (c.moveToNext()) {
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    c.getLong(0)
                )
                Log.e(TAG, "queryMediaFile content uri=$contentUri, data=" + c.getString(1))
                mediaList.add(contentUri)
            }
            c.close()
        } else {
            Log.e(TAG, "cursor=null")
        }
    }

    fun readMediaFile(view: View) {
        if (mediaList.isNotEmpty()) {
            val uri = mediaList[index % mediaList.size]
            val bitmap = getBitmapFromUri(uri)
            meinvIv?.setImageBitmap(bitmap)
            index++

        }
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        val fileDes = contentResolver.openFileDescriptor(uri, "r")
        if (fileDes != null) {
            val bitmap = BitmapFactory.decodeFileDescriptor(fileDes.fileDescriptor)
            fileDes.close()
            return bitmap
        }
        return null

    }

    fun createMediaFile(view: View) {
        val uri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        val values = ContentValues()
        val dataTaken = System.currentTimeMillis()
        values.put(MediaStore.Images.Media.DATE_TAKEN, dataTaken)
        values.put(MediaStore.Images.Media.DESCRIPTION, "This is an image2")
        values.put(MediaStore.Images.Media.IS_PRIVATE, 1)
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "meimei2.png")
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        values.put(MediaStore.Images.Media.TITLE, "meimei2.png")
        // com.google.android.providers.media.module E/DatabaseUtils: Writing exception to parcel
        // 无所有文件的访问权限会返回   java.lang.IllegalArgumentException: Primary directory Documents not allowed for content://media/external/images/media; allowed directories are [DCIM, Pictures]
        //values.put(MediaStore.Images.Media.RELATIVE_PATH, "Documents/congwiny/meinv")

        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/congwiny/meinv")
        val dataAdded = System.currentTimeMillis()
        values.put(MediaStore.Images.Media.DATE_ADDED, dataAdded)
        val dataModified = System.currentTimeMillis()
        values.put(MediaStore.Images.Media.DATE_MODIFIED, dataModified)
        val insertUri = contentResolver.insert(uri, values)
        if (insertUri == null) {
            Log.e(TAG, "insertUri is null return")
            return
        }
        try {
            val os = contentResolver.openOutputStream(insertUri)

            os?.let {
                val file = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "meinv.jpg"
                )

                val originBmp = BitmapFactory.decodeFile(file.absolutePath)
                val targetBmp = Bitmap.createBitmap(originBmp)
                targetBmp.compress(Bitmap.CompressFormat.PNG, 100, it)
                it.flush()
                it.close()
                Log.e(TAG, "createMediaFile finish insert uri=$insertUri")
            }

            val bmp = getBitmapFromUri(insertUri)
            if (bmp != null) {
                meinvIv?.setImageBitmap(bmp)
            }

        } catch (e: Exception) {
            Log.e(TAG, "createMediaFile exception", e)
        }
    }

    fun modifyMediaFile(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            var os: OutputStream? = null
            val imageUri = Uri.parse("content://media/external/images/media/113")
            try {
                os = contentResolver.openOutputStream(imageUri)
            } catch (e: IOException) {
                Log.e(TAG, "open image fail")
            } catch (e1: RecoverableSecurityException) {
                /**
                 * 2020-10-24 19:28:06.478 1623-32085/com.google.android.providers.media.module E/DatabaseUtils: Writing exception to parcel
                android.app.RecoverableSecurityException: com.congwiny.android11_2 has no access to content://media/external/images/media/101
                at com.android.providers.media.MediaProvider.enforceCallingPermissionInternal(MediaProvider.java:7101)
                at com.android.providers.media.MediaProvider.enforceCallingPermission(MediaProvider.java:7025)
                at com.android.providers.media.MediaProvider.checkAccess(MediaProvider.java:7127)
                at com.android.providers.media.MediaProvider.openFileAndEnforcePathPermissionsHelper(MediaProvider.java:5974)
                at com.android.providers.media.MediaProvider.openFileCommon(MediaProvider.java:5667)
                at com.android.providers.media.MediaProvider.openFile(MediaProvider.java:5622)
                at android.content.ContentProvider.openAssetFile(ContentProvider.java:2004)
                at android.content.ContentProvider.openAssetFile(ContentProvider.java:2068)
                at android.content.ContentProvider$Transport.openAssetFile(ContentProvider.java:498)
                at android.content.ContentProviderNative.onTransact(ContentProviderNative.java:272)
                at android.os.Binder.execTransactInternal(Binder.java:1154)
                at android.os.Binder.execTransact(Binder.java:1123)
                 */
                Log.e(TAG, "get RecoverableSecurityException")
                try {
                    startIntentSenderForResult(
                        e1.userAction.actionIntent.intentSender,
                        100, null, 0, 0, 0
                    )
                } catch (e2: SendIntentException) {
                    Log.e(TAG, "startIntentSender fail")
                }
            }
        }
    }

    fun deleteMediaFile(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val imageUri = Uri.parse("content://media/external/images/media/113")
            try {
                val result = contentResolver.delete(imageUri, null, null)
                Log.e(TAG, "delete result=$result")
            } catch (e2: RecoverableSecurityException) {
                /**
                 * 2020-10-24 19:28:06.478 1623-32085/com.google.android.providers.media.module E/DatabaseUtils: Writing exception to parcel
                android.app.RecoverableSecurityException: com.congwiny.android11_2 has no access to content://media/external/images/media/101
                at com.android.providers.media.MediaProvider.enforceCallingPermissionInternal(MediaProvider.java:7101)
                at com.android.providers.media.MediaProvider.enforceCallingPermission(MediaProvider.java:7025)
                at com.android.providers.media.MediaProvider.checkAccess(MediaProvider.java:7127)
                at com.android.providers.media.MediaProvider.openFileAndEnforcePathPermissionsHelper(MediaProvider.java:5974)
                at com.android.providers.media.MediaProvider.openFileCommon(MediaProvider.java:5667)
                at com.android.providers.media.MediaProvider.openFile(MediaProvider.java:5622)
                at android.content.ContentProvider.openAssetFile(ContentProvider.java:2004)
                at android.content.ContentProvider.openAssetFile(ContentProvider.java:2068)
                at android.content.ContentProvider$Transport.openAssetFile(ContentProvider.java:498)
                at android.content.ContentProviderNative.onTransact(ContentProviderNative.java:272)
                at android.os.Binder.execTransactInternal(Binder.java:1154)
                at android.os.Binder.execTransact(Binder.java:1123)
                 */
                Log.e(TAG, "get RecoverableSecurityException", e2)
                try {
                    startIntentSenderForResult(
                        e2.userAction.actionIntent.intentSender,
                        100, null, 0, 0, 0
                    )
                } catch (e2: SendIntentException) {
                    Log.e(TAG, "startIntentSender fail")
                }
            }catch (e1: SecurityException) {
                //Caused by: java.lang.SecurityException: com.congwiny.android11 has no access to content://media/external/images/media/103
                //访问了一个不存在的uri
                Log.e(TAG, "get SecurityException", e1)

            }
        }
    }

    fun accessFileFromPath(view: View) {
        try {
            val path = "/storage/emulated/0/Pictures/congwiny/meinv/meimei2.png"
            val bmp = BitmapFactory.decodeFile(path)
            meinvIv?.setImageBitmap(bmp)
        } catch (e: Exception) {
            Log.e(TAG, "accessFileFromPath ", e)

        }

    }
}
