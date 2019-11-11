package com.wisdom.myapplication

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.MediaController
import android.widget.VideoView
import java.io.File

/**
 *@description: 添加录像video
 *@author: Felix J
 *@time: 2019/11/8 11:37
 */
class AddVideoActivity : AppCompatActivity() {

    private lateinit var btnAdd: Button
    private lateinit var mFilePath: String
    private lateinit var bitmap: Bitmap
    private lateinit var vid1: VideoView
    private lateinit var vid2: VideoView
    private lateinit var vid3: VideoView
    private val videoList: ArrayList<VideoView> = arrayListOf()
    private var durationList = arrayListOf<Int>()
    private val REQUEST_CAMERA = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_video)
        btnAdd = findViewById(R.id.btnAdd)
        vid1 = findViewById(R.id.vid1)
        vid2 = findViewById(R.id.vid2)
        vid3 = findViewById(R.id.vid3)
        val vidList = arrayListOf(vid1, vid2, vid3)
        for (i in 0 until vidList.size step 1) {
            videoList.add(vidList[i])
        }

        for (i in 0 until videoList.size step 1) {
            videoList[i].setOnClickListener {
                toEveryVideoActivity(i)
            }


            videoList[i].setOnPreparedListener{
            }
        }


        mFilePath = Environment.getExternalStorageDirectory().path // 获取SD卡路径
        mFilePath = "$mFilePath/temp.3gp"// 指定路径
        val photoUri = Uri.fromFile(File(mFilePath)) // 传递路径  
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)// 更改系统默认存储路径  
        btnAdd.setOnClickListener {
            startActivityForResult(intent, REQUEST_CAMERA)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) { // 如果返回数据 
            if (requestCode == REQUEST_CAMERA) {
                add()
            }
        }
    }

    private fun add() {
        for (i in 0 until videoList.size step 1) {
            // videoList[i].setOnPreparedListener {
            if (videoList[i].duration == -1) {
                //videoList[i].setVideoURI(Uri.parse(mFilePath))
                videoList[i].setVideoPath(mFilePath)
                videoList[i].setMediaController(MediaController(this@AddVideoActivity))
                videoList[i].start()
                break
            }
        }
    }

    private fun toEveryVideoActivity(index: Int) {
        val intent = Intent(this@AddVideoActivity, EveryVideoActivity::class.java)
        intent.putExtra("index", index)
        var totalCount = 0
        for (i in 0 until videoList.size step 1) {
            if (videoList[i].drawableState != null) {
                totalCount++
            }

        }
        intent.putExtra("totalCount", totalCount)
        startActivity(intent)
    }

}
