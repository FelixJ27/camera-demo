package com.wisdom.camerademo

import android.annotation.TargetApi
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
    //private var cameraBuilder: Dialog.Builder? = null
    private lateinit var cameraView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        mFilePath = Environment.getExternalStorageDirectory().path // 获取SD卡路径
        mFilePath = "$mFilePath/temp.png"// 指定路径

        img1 = findViewById(R.id.img1)
        img2 = findViewById(R.id.img2)
        img3 = findViewById(R.id.img3)
        img4 = findViewById(R.id.img4)
        img5 = findViewById(R.id.img5)
        img6 = findViewById(R.id.img6)

        //cameraDialog = Dialog(this,R.style.AlertDialogWindow)
        //cameraBuilder = AlertDialog.Builder(this)
        //1、使用Dialog、设置style
        cameraDialog = Dialog(this,R.style.AlertDialogWindow)
        //2、设置布局
        val view = View.inflate(this,R.layout.alertdialog_choose_resources,null)
        cameraDialog.setContentView(view)
        val cameraWindow = cameraDialog.window
        cameraWindow!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        cameraWindow!!.setGravity(Gravity.BOTTOM)
        //设置对话框大小
        cameraWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)

        //val inflater = this.layoutInflater
        //cameraView = inflater.inflate(R.layout.alertdialog_choose_resources, null, false)
        //cameraBuilder!!.setView(cameraView)
        //cameraBuilder!!.setCancelable(true)
        //cameraDialog = cameraBuilder!!.create()
        //getPicture()

        val photoUri = Uri.fromFile(File(mFilePath)) // 传递路径  

        val imgList: ArrayList<ImageView> = arrayListOf(img1, img2, img3, img4, img5, img6)
        for (i in 0 until imgList.size step 1) {
            imageList.add(imgList[i])
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

    private fun getPicture() {
        //获取字符串
        val sPreferences = getSharedPreferences("Picture", Context.MODE_PRIVATE)
        val imageBase64 = sPreferences.getString("cameraImage", "")
        //把字符串解码成Bitmap对象
        val byte64 = Base64.decode(imageBase64, 0)
        val bais = ByteArrayInputStream(byte64)
        val bitmap = BitmapFactory.decodeStream(bais)
        //显示图片
        img1.setImageBitmap(bitmap)
    }

    /**
     * @description 跳转照片详情页面
     * @author Felix J
     * @time 2019/11/8 10:12
     */
    private fun toEveryActivity(index: Int) {
        val intent = Intent(this@AddPhotoActivity, EveryPictureActivity::class.java)
        intentBitmap()
        //BitmapFactory.decodeResource(resources, R.drawable)
        intent.putExtra("index", index)
        var totalCount = 0
        for (i in 0 until imageList.size step 1) {
            if (imageList[i].drawable != null
                && imageList[i].drawable.current.constantState != resources.getDrawable(R.drawable.icon_add).constantState
            ) {
                totalCount++
            }
        }
        intent.putExtra("totalCount", totalCount)

        startActivity(intent)
    }

    //传递bitmap
    fun intentBitmap() {
        //把Bitmap转码成字符串
        val baos = ByteArrayOutputStream()
        //压缩图片大小
        bitmap?.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val imageBase64 = String(Base64.encode(baos.toByteArray(), 0))
        //把字符串存到SharedPreferences里面
        val prePicture = getSharedPreferences("everyPicture", Context.MODE_PRIVATE)
        val editor = prePicture.edit()
        editor.putString("everyImage", imageBase64)
        editor.commit()
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
                        //生成缩略图
                        //bitmap = ImageThumbnail().getImageThumbnail(mFilePath, 50, 50)
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
        val uri = data.getData()
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            val docId = DocumentsContract.getDocumentId(uri)
            if ("com.android.providers.media.documents" == uri!!.getAuthority()) {
                val id =
                    docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                // 解析出数字格式的id
                val selection = MediaStore.Images.Media._ID + "=" + id
                imagePath =
                    getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection)
            } else if ("com.android.providers.downloads.documents" == uri!!.getAuthority()) {
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
            if (cursor!!.moveToFirst()) {
                path = cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Images.Media.DATA))
            }
            cursor!!.close()
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
        for (i in 0 until imageList.size step 1) {
            if (imageList[i].drawable.current.constantState == resources.getDrawable(R.drawable.icon_add).constantState) {
                imageList[i].setImageBitmap(bitmap)
                if (i != imageList.size - 1) {
                    imageList[i + 1].setImageResource(R.drawable.icon_add)
                    break
                }
            }
        }
    }
}
