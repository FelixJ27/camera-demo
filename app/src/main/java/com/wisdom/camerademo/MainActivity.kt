package com.wisdom.camerademo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.content.Intent
import android.os.Environment
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast


class MainActivity : AppCompatActivity() {
    private lateinit var btnPhotograph: Button
    private lateinit var btnVideo: Button
    private lateinit var btnCancel: Button
    private lateinit var mFilePath: String
    private lateinit var mRightIv: ImageView
    private lateinit var editOrderCode: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        editOrderCode = findViewById(R.id.editOrderCode)
        mRightIv = findViewById(R.id.mRightIv)
        mRightIv.setImageResource(R.drawable.icon_camera)

        mFilePath = Environment.getExternalStorageDirectory().path // 获取SD卡路径
        mFilePath = "$mFilePath/temp.png"// 指定路径

        btnPhotograph = findViewById(R.id.btnPhotograph)
        btnVideo = findViewById(R.id.btnVideo)
        btnCancel = findViewById(R.id.btnCancel)

        btnPhotograph.setOnClickListener {
            if (editOrderCode.text.toString() != "") {
                toAddPhotoActivity()
            } else {
                Toast.makeText(this, "请输入订单号", Toast.LENGTH_LONG).show()
            }
        }

        btnVideo.setOnClickListener {
            if (editOrderCode.text.toString() != "") {
                toAddVideoActivity()
            } else {
                Toast.makeText(this, "请输入订单号", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun toAddPhotoActivity() {
        startActivity(Intent(this@MainActivity, AddPhotoActivity::class.java))
    }

    private fun toAddVideoActivity() {
        startActivity(Intent(this@MainActivity, AddVideoActivity::class.java))
    }
}
