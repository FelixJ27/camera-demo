package com.wisdom.camerademo

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
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
    private lateinit var linearLayout1: LinearLayout
    private lateinit var linearLayout2: LinearLayout
    private lateinit var linearLayout3: LinearLayout
    private val videoList: ArrayList<VideoView> = arrayListOf()
    private var durationList = arrayListOf<Int>()
    private lateinit var delAlertDialog: AlertDialog
    private var delBuilder: AlertDialog.Builder? = null
    private lateinit var delView: View
    private var videoCount: Int = 0
    private val REQUEST_CAMERA = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_video)
        btnAdd = findViewById(R.id.btnAdd)
        vid1 = findViewById(R.id.vid1)
        vid2 = findViewById(R.id.vid2)
        vid3 = findViewById(R.id.vid3)
        linearLayout1 = findViewById(R.id.ll1)
        linearLayout2 = findViewById(R.id.ll2)
        linearLayout3 = findViewById(R.id.ll3)
        val linearLayoutList = arrayListOf(linearLayout1, linearLayout2, linearLayout3)
        val vidList = arrayListOf(vid1, vid2, vid3)
        for (i in 0 until vidList.size step 1) {
            videoList.add(vidList[i])
        }

        delBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        delView = inflater.inflate(R.layout.alertdialog_del, null, false)
        delBuilder!!.setView(delView)
        delBuilder!!.setCancelable(false)
        delAlertDialog = delBuilder!!.create()

        for (i in 0 until videoList.size step 1) {

            videoList[i].setOnPreparedListener {
                durationList.add(videoList[i].duration)
                /*Toast.makeText(this, durationList[i].toString(), Toast.LENGTH_LONG).show()*/
                //initVideoView(durationList)
            }

            //长按删除视频
            linearLayoutList[i].setOnLongClickListener {
                //Toast.makeText(this, "长按事件", Toast.LENGTH_LONG).show()
                delAlertDialog.show()
                delView.findViewById<Button>(R.id.btnSure).setOnClickListener {
                    delVideo(i)
                    delAlertDialog.dismiss()
                }
                delView.findViewById<Button>(R.id.btnCancel).setOnClickListener {
                    delAlertDialog.dismiss()
                }
                false
            }

            /*videoList[i].setOnTouchListener { v, event ->
                Toast.makeText(this, "setOnTouchListener事件", Toast.LENGTH_LONG).show()
                false
            }*/
        }

        initVideoView()

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

    /**
     * @description 删除视频
     * @author Felix J
     * @time 2019/11/14 14:21
     */
    private fun delVideo(index: Int) {
        videoList[index].setVideoURI(null)
        videoCount--
    }

    /**
     * @description 初始化VideoView
     * @author Felix J
     * @time 2019/11/14 11:38
     */
    private fun initVideoView() {
        for (i in 0 until videoList.size step 1) {
            videoList[i].visibility = View.GONE
        }
    }

    /**
     * @descriptiont 添加视频
     * @author Felix J
     * @time 2019/11/14 11:38
     */
    private fun add() {
        videoList[videoCount].visibility = View.VISIBLE
        videoList[videoCount].setVideoPath(mFilePath)
        videoList[videoCount].setMediaController(MediaController(this@AddVideoActivity))
        if (videoCount < 3) {
            videoCount++
        }
    }


}
