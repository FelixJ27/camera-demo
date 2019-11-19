package com.wisdom.camerademo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import util.CameraUtil
import util.SharedPreferencesUtils

/**
 *@description:
 *@author: Felix J
 *@time: 2019/11/7 11:34
 */
class EveryPictureActivity : AppCompatActivity() {

    private lateinit var img: ImageView
    private lateinit var imgDel: ImageView
    private lateinit var txtIndex: TextView
    private lateinit var delAlertDialog: AlertDialog
    private var delBuilder: AlertDialog.Builder? = null
    private lateinit var delView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_every_picture)

        val index = intent.getIntExtra("index", 0)
        //val totalCount = intent.getIntExtra("totalCount", 0)
        val totalCount = SharedPreferencesUtils.getString(this, "totalCount", "0")
        val filePath = intent.getStringExtra("filePath")
        imgDel = findViewById(R.id.mRightIv)
        imgDel.setImageResource(R.drawable.icon_rubbish)
        delBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        delView = inflater.inflate(R.layout.alertdialog_del, null, false)
        delBuilder!!.setView(delView)
        delBuilder!!.setCancelable(false)
        delAlertDialog = delBuilder!!.create()
        txtIndex = findViewById(R.id.mTitleTv)
        txtIndex.text = "$index/$totalCount"
        img = findViewById(R.id.img)
        val btnDel = findViewById<ImageView>(R.id.mRightIv)
        btnDel.setOnClickListener {
            delAlertDialog.show()
            delView.findViewById<Button>(R.id.btnSure).setOnClickListener {
                returnToAddPhotoActivity(index, filePath, totalCount)
                delAlertDialog.dismiss()
            }

            delView.findViewById<Button>(R.id.btnCancel).setOnClickListener {
                delAlertDialog.dismiss()
            }
        }
        CameraUtil.setPicture(filePath, img ,1)
    }

    private fun returnToAddPhotoActivity(index: Int, filePath: String, totalCount: String) {
        val intent = Intent(this@EveryPictureActivity, AddPhotoActivity::class.java)
        intent.putExtra("index", index)
        intent.putExtra("filePath", filePath)
        intent.putExtra("totalCount", totalCount)
        startActivityForResult(intent, 2)
        finish()
    }
}
