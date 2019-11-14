package com.wisdom.camerademo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import util.ImageUtil

/**
 *@description:
 *@author: Felix J
 *@time: 2019/11/7 11:34
 */
class EveryPictureActivity : AppCompatActivity() {

    private lateinit var img: ImageView
    private lateinit var imgDel: ImageView
    private lateinit var txtIndex: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_every_picture)

        val index = intent.getIntExtra("index", 0)
        val totalCount = intent.getIntExtra("totalCount", 0)
        val filePath = intent.getStringExtra("filePath")
        imgDel = findViewById(R.id.mRightIv)
        imgDel.setImageResource(R.drawable.icon_rubbish)

        txtIndex = findViewById(R.id.mTitleTv)
        txtIndex.text = "$index/$totalCount"
        img = findViewById(R.id.img)
        val btnDel = findViewById<ImageView>(R.id.mRightIv)
        btnDel.setOnClickListener {
            returnToAddPhotoActivity(index, filePath, totalCount)
        }
        ImageUtil.setPicture(filePath, img)
    }

    private fun returnToAddPhotoActivity(index: Int, filePath: String, totalCount: Int) {
        val intent = Intent(this@EveryPictureActivity, AddPhotoActivity::class.java)
        intent.putExtra("index", index)
        intent.putExtra("filePath", filePath)
        intent.putExtra("totalCount", totalCount)
        startActivity(intent)
    }
}
