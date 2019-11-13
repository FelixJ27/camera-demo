package com.wisdom.camerademo

import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.MediaController
import android.widget.Toast
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
    private var videoCount: Int = 0
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


            videoList[i].setOnPreparedListener {
                /*durationList.add(videoList[i].duration)
                Toast.makeText(this, durationList[i].toString(), Toast.LENGTH_LONG).show()*/
            }
        }

        //添加视频
        btnAdd.setOnClickListener {
            if (videoCount < 3) {
                val file = File(Environment.getExternalStorageDirectory().path + "/Sany")
                if (!file.exists()) {
                    file.mkdir()
                }
                mFilePath = file.path
                mFilePath = "$mFilePath/temp_$videoCount.3gp"// 指定路径
                val photoUri = Uri.fromFile(File(mFilePath)) // 传递路径  
                val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)// 更改系统默认存储路径  
                startActivityForResult(intent, REQUEST_CAMERA)
            } else {
                Toast.makeText(this, "视频数量已满3个，无法继续添加视频", Toast.LENGTH_LONG).show()
            }
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
        videoList[videoCount].setVideoPath(mFilePath)
        videoList[videoCount].setMediaController(MediaController(this@AddVideoActivity))
        if (videoCount < 3) {
            videoCount++
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
