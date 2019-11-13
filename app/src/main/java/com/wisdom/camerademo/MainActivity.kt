package com.wisdom.camerademo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.content.Intent
import android.os.Environment


class MainActivity : AppCompatActivity() {
    private lateinit var btnPhotograph: Button
    private lateinit var btnVideo: Button
    private lateinit var btnCancel: Button
    private lateinit var mFilePath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mFilePath = Environment.getExternalStorageDirectory().path // 获取SD卡路径
        mFilePath = "$mFilePath/temp.png"// 指定路径

        btnPhotograph = findViewById(R.id.btnPhotograph)
        btnVideo = findViewById(R.id.btnVideo)
        btnCancel = findViewById(R.id.btnCancel)

        btnPhotograph.setOnClickListener {
            toAddPhotoActivity()
        }

        btnVideo.setOnClickListener {
            toAddVideoActivity()
        }

    }

    private fun toAddPhotoActivity() {
        startActivity(Intent(this@MainActivity, AddPhotoActivity::class.java))
    }

    private fun toAddVideoActivity() {
        startActivity(Intent(this@MainActivity, AddVideoActivity::class.java))
    }
}
