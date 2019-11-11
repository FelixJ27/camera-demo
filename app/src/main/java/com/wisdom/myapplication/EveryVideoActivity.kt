package com.wisdom.myapplication

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView

/**
 *@description: 视频详情Activity
 *@author: Felix J
 *@time: 2019/11/11 13:45
 */
class EveryVideoActivity : AppCompatActivity() {

    private lateinit var vidView: VideoView
    private lateinit var imgDel: ImageView
    private lateinit var txtIndex: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_every_video)

        val index = intent.getIntExtra("index", 0)
        val totalCount = intent.getIntExtra("totalCount", 0)
        imgDel = findViewById(R.id.mRightIv)
        imgDel.setImageResource(R.drawable.icon_rubbish)
        txtIndex = findViewById(R.id.mTitleTv)
        txtIndex.text = "$index/$totalCount"
    }
}
