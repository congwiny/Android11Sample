package com.congwiny.android11

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View

class SAFActivity : AppCompatActivity() {

    companion object{
        const val TAG = "SAFActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saf)
    }

    fun actionOpenDocument(view: View) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.setType("*/*")
        startActivityForResult(intent,5)
    }
    fun actionOpenDocumentTree(view: View) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        startActivityForResult(intent,6)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            5 ->{
                Log.e(TAG, "onActivityResult 5 data=$data")
            }
            6 ->{
                Log.e(TAG, "onActivityResult 6 data=$data")
            }
        }
    }
}
