package com.wisdom.myapplication

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Message
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import java.io.*
import android.media.ThumbnailUtils




class AddActivity : AppCompatActivity() {

    private lateinit var img1: ImageView
    private lateinit var img2: ImageView
    private lateinit var img3: ImageView
    private lateinit var img4: ImageView
    private lateinit var img5: ImageView
    private lateinit var img6: ImageView
    private lateinit var img7: ImageView
    private lateinit var img8: ImageView
    private lateinit var img9: ImageView
    private lateinit var bitmap: Bitmap
    private lateinit var mFilePath: String
    private lateinit var imgAlert: AlertDialog
    private var imgBuilder: AlertDialog.Builder? = null
    private lateinit var imgView: View
    private val REQUEST_CAMERA = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        mFilePath = Environment.getExternalStorageDirectory().path // 获取SD卡路径
        mFilePath = "$mFilePath/temp.png"// 指定路径

        img1 = findViewById(R.id.img1)
        img2 = findViewById(R.id.img2)
        img3 = findViewById(R.id.img3)
        img4 = findViewById(R.id.img4)
        img5 = findViewById(R.id.img5)
        img6 = findViewById(R.id.img6)
        img7 = findViewById(R.id.img7)
        img8 = findViewById(R.id.img8)
        img9 = findViewById(R.id.img9)

        imgBuilder = AlertDialog.Builder(this)
        imgView = layoutInflater.inflate(R.layout.activity_main, null, false)
        imgBuilder!!.setView(imgView)
        imgBuilder!!.setCancelable(false)
        imgAlert = imgBuilder!!.create()

        getPicture()

        img1.setOnClickListener {
            toEveryActivity()
        }

        val photoUri = Uri.fromFile(File(mFilePath)) // 传递路径  
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)// 更改系统默认存储路径  
        img2.setOnClickListener {
            if (img2.drawable.current.constantState == resources.getDrawable(R.drawable.add).constantState) {
                startActivityForResult(intent, REQUEST_CAMERA)
            } else {
                toEveryActivity()
            }
        }

        img3.setOnClickListener {
            if (img3.drawable.current.constantState == resources.getDrawable(R.drawable.add).constantState) {
                startActivityForResult(intent, REQUEST_CAMERA)
            } else {
                toEveryActivity()
            }
        }
        img4.setOnClickListener {
            if (img4.drawable.current.constantState == resources.getDrawable(R.drawable.add).constantState) {
                startActivityForResult(intent, REQUEST_CAMERA)
            } else {
                toEveryActivity()
            }
        }
        img5.setOnClickListener {
            if (img5.drawable.current.constantState == resources.getDrawable(R.drawable.add).constantState) {
                startActivityForResult(intent, REQUEST_CAMERA)
            } else {
                toEveryActivity()
            }
        }
        img6.setOnClickListener {
            if (img6.drawable.current.constantState == resources.getDrawable(R.drawable.add).constantState) {
                startActivityForResult(intent, REQUEST_CAMERA)
            } else {
                toEveryActivity()
            }
        }
        img7.setOnClickListener {
            if (img7.drawable.current.constantState == resources.getDrawable(R.drawable.add).constantState) {
                startActivityForResult(intent, REQUEST_CAMERA)
            } else {
                toEveryActivity()
            }
        }
        img8.setOnClickListener {
            if (img8.drawable.current.constantState == resources.getDrawable(R.drawable.add).constantState) {
                startActivityForResult(intent, REQUEST_CAMERA)
            } else {
                toEveryActivity()
            }
        }
        img9.setOnClickListener {
            if (img9.drawable.current.constantState == resources.getDrawable(R.drawable.add).constantState) {
                startActivityForResult(intent, REQUEST_CAMERA)
            } else {
                toEveryActivity()
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

    private fun toEveryActivity() {
        val intent = Intent(this@AddActivity, EveryoneActivity::class.java)
        startActivity(intent)
    }

    /**
     * 获取缩略图
     * @param imagePath:文件路径
     * @param width:缩略图宽度
     * @param height:缩略图高度
     * @return
     */
    fun getImageThumbnail(imagePath: String, width: Int, height: Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true //关于inJustDecodeBounds的作用将在下文叙述
        var bitmap = BitmapFactory.decodeFile(imagePath, options)
        val h = options.outHeight//获取图片高度
        val w = options.outWidth//获取图片宽度
        val scaleWidth = w / width //计算宽度缩放比
        val scaleHeight = h / height //计算高度缩放比
        var scale = 1//初始缩放比
        if (scaleWidth < scaleHeight) {//选择合适的缩放比
            scale = scaleWidth
        } else {
            scale = scaleHeight
        }
        /*if (scale <= 0) {//判断缩放比是否符合条件
            be = 1
        }*/
        options.inSampleSize = scale
        // 重新读入图片，读取缩放后的bitmap，注意这次要把inJustDecodeBounds 设为 false
        options.inJustDecodeBounds = false
        bitmap = BitmapFactory.decodeFile(imagePath, options)
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
        bitmap = ThumbnailUtils.extractThumbnail(
            bitmap,
            width,
            height,
            ThumbnailUtils.OPTIONS_RECYCLE_INPUT
        )
        return bitmap
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) { // 如果返回数据 
            if (requestCode == REQUEST_CAMERA) {
                var fis: FileInputStream? = null
                try {
                    fis = FileInputStream(mFilePath) // 根据路径获取数据
                    bitmap = BitmapFactory.decodeStream(fis)    //获取图片
                    //生成缩略图
                    bitmap = getImageThumbnail(mFilePath,50,50)
                    change(bitmap)
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
        }
    }

    private fun change(bitmap: Bitmap) {
        val imgList: ArrayList<ImageView> =
            arrayListOf(img1, img2, img3, img4, img5, img6, img7, img8, img9)
        for (i in 0 until imgList.size step 1) {
            if (imgList[i].drawable.current.constantState == resources.getDrawable(R.drawable.add).constantState) {
                imgList[i].setImageBitmap(bitmap)
                if (i != imgList.size - 1) {
                    imgList[i + 1].setImageResource(R.drawable.add)
                    break
                }
            }
        }
    }
}
