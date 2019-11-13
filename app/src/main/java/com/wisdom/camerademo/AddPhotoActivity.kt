package com.wisdom.camerademo

import android.annotation.TargetApi
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Picture
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Base64
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import util.ImageUtil
import java.io.*

/**
 *@description: 添加照片Activity
 *@author: Felix J
 *@time: 2019/11/7 11:34
 */
class AddPhotoActivity : AppCompatActivity() {

    private lateinit var img1: ImageView
    private lateinit var img2: ImageView
    private lateinit var img3: ImageView
    private lateinit var img4: ImageView
    private lateinit var img5: ImageView
    private lateinit var img6: ImageView
    private lateinit var bitmap: Bitmap
    private lateinit var mFilePath: String
    private val imageList: ArrayList<ImageView> = arrayListOf()
    private val REQUEST_CAMERA = 1
    private val SELECT_PHOTO = 0
    private lateinit var cameraDialog: Dialog
    private var photoIndex = 0
    private var preparedPictureList: ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        val index = intent.getIntExtra("index", 0)
        val totalCount = intent.getIntExtra("totalCount", 0)
        //val filePath = intent.getStringExtra("filePath")
        //if (index)

        img1 = findViewById(R.id.img1)
        img2 = findViewById(R.id.img2)
        img3 = findViewById(R.id.img3)
        img4 = findViewById(R.id.img4)
        img5 = findViewById(R.id.img5)
        img6 = findViewById(R.id.img6)

        //1、使用Dialog、设置style
        cameraDialog = Dialog(this,R.style.AlertDialogWindow)
        //2、设置布局
        val view = View.inflate(this,R.layout.alertdialog_choose_resources,null)
        cameraDialog.setContentView(view)
        val cameraWindow = cameraDialog.window
        cameraWindow!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        cameraWindow.setGravity(Gravity.BOTTOM)
        //设置对话框大小
        cameraWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)

        val imgList: ArrayList<ImageView> = arrayListOf(img1, img2, img3, img4, img5, img6)
        for (i in 0 until imgList.size step 1) {
            imageList.add(imgList[i])
        }

        if (index != 0) {
            delPicture(index, totalCount)
        }

        for (i in 0 until imageList.size step 1) {
            imageList[i].setOnClickListener {
                if (imageList[i].drawable != null) {
                    if (imageList[i].drawable.current.constantState == resources.getDrawable(R.drawable.icon_add).constantState) {
                        cameraDialog.show()
                        val btnTakePhoto = cameraDialog.findViewById<Button>(R.id.btnTakePhoto)
                        val btnFromAlbum = cameraDialog.findViewById<Button>(R.id.btnFromAlbum)
                        val btnCancel = cameraDialog.findViewById<Button>(R.id.btnCancel)
                        btnTakePhoto.setOnClickListener {
                            //Environment.getExternalStorageDirectory().path // 获取SD卡路径
                            val file = File(Environment.getExternalStorageDirectory().path + "/Sany")
                            if (!file.exists()) {
                                file.mkdir()
                            }
                            mFilePath = file.path
                            mFilePath = "$mFilePath/temp_$photoIndex.png"// 指定路径
                            val photoUri = Uri.fromFile(File(mFilePath)) // 传递路径 
                            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)// 更改系统默认存储路径  
                            startActivityForResult(intent, REQUEST_CAMERA)
                            cameraDialog.dismiss()
                        }
                        btnFromAlbum.setOnClickListener {
                            //打开相册
                            val intent = Intent(Intent.ACTION_GET_CONTENT)
                            //Intent.ACTION_GET_CONTENT = "android.intent.action.GET_CONTENT"
                            intent.type = "image/*"
                            startActivityForResult(intent, SELECT_PHOTO)
                            cameraDialog.dismiss()
                        }
                        btnCancel.setOnClickListener {
                            cameraDialog.dismiss()
                        }
                    } else {
                        toEveryActivity(index = i + 1)
                    }
                }
            }
        }
    }

    /**
     * @description 跳转照片详情页面
     * @author Felix J
     * @time 2019/11/8 10:12
     */
    private fun toEveryActivity(index: Int) {
        val intent = Intent(this@AddPhotoActivity, EveryPictureActivity::class.java)
        intent.putExtra("index", index)
        intent.putExtra("totalCount", photoIndex)
        var filePath = Environment.getExternalStorageDirectory().path + "/Sany"
        filePath = filePath + "/temp_" + (index - 1) + ".png"
        intent.putExtra("filePath", filePath)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) { // 如果返回数据 
            when (requestCode) {
                REQUEST_CAMERA -> {
                    var fis: FileInputStream? = null
                    try {
                        fis = FileInputStream(mFilePath) // 根据路径获取数据
                        bitmap = BitmapFactory.decodeStream(fis)    //获取图片
                        add(bitmap)
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    } finally {
                        try {
                            fis!!.close()// 关闭流
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
                SELECT_PHOTO -> {
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data!!)
                    } else {
                        // 4.4以下系统使用这个方法处理图片
                        handleImageBeforeKitKat(data!!)
                    }
                }
            }
        }
    }

    @TargetApi(19)
    private fun handleImageOnKitKat(data: Intent) {
        var imagePath: String? = null
        val uri = data.data
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            val docId = DocumentsContract.getDocumentId(uri)
            if ("com.android.providers.media.documents" == uri!!.authority) {
                val id =
                    docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                // 解析出数字格式的id
                val selection = MediaStore.Images.Media._ID + "=" + id
                imagePath =
                    getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection)
            } else if ("com.android.providers.downloads.documents" == uri.authority) {
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content: //downloads/public_downloads"),
                    java.lang.Long.valueOf(docId)
                )
                imagePath = getImagePath(contentUri, null)
            }
        } else if ("content".equals(uri!!.scheme, ignoreCase = true)) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.path
        }
        // 根据图片路径显示图片
        displayImage(imagePath)
    }

    /**
     * android 4.4以前的处理方式
     * @param data
     */
    private fun handleImageBeforeKitKat(data: Intent) {
        val uri = data.data
        val imagePath = getImagePath(uri!!, null)
        displayImage(imagePath)
    }

    private fun getImagePath(uri: Uri, selection: String?): String? {
        var path: String? = null
        // 通过Uri和selection来获取真实的图片路径
        val cursor = contentResolver.query(uri, null, selection, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            }
            cursor.close()
        }
        return path
    }

    private fun displayImage(imagePath: String?) {
        if (imagePath != null) {
            val bitmap = BitmapFactory.decodeFile(imagePath)
            //imageView!!.setImageBitmap(bitmap)
            add(bitmap)
        } else {
            Toast.makeText(this, "获取相册图片失败", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * @description 添加照片
     * @author Felix J
     * @time 2019/11/8 10:11
     */
    private fun add(bitmap: Bitmap) {
        photoIndex = 0
        for (i in 0 until imageList.size step 1) {
            photoIndex++
            if (imageList[i].drawable.current.constantState == resources.getDrawable(R.drawable.icon_add).constantState) {
                imageList[i].setImageBitmap(bitmap)
                if (i != imageList.size - 1) {
                    imageList[i + 1].setImageResource(R.drawable.icon_add)
                    break
                }
            }
        }
    }

    private fun delPicture(index: Int, totalCount: Int) {
        var filePath = Environment.getExternalStorageDirectory().path + "/Sany"
        for (i in 0 until totalCount step 1) {
            val mfilePath = "$filePath/temp_$i.png"
            ImageUtil.setPicture(mfilePath, imageList[i])
        }
        imageList[index - 1].setImageResource(R.drawable.icon_add)
        if (index != imageList.size) {
            imageList[index].setImageDrawable(null)
        }
    }
}
