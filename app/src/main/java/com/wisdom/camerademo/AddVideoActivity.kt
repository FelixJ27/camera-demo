package com.wisdom.camerademo

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentUris
import android.content.Intent
import android.net.Uri
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import util.CameraUtil
import util.UploadUtil
import java.io.File
import java.util.HashMap

/**
 *@description: 添加录像video
 *@author: Felix J
 *@time: 2019/11/8 11:37
 */
class AddVideoActivity : AppCompatActivity(), UploadUtil.OnUploadProcessListener {

    private lateinit var btnAdd: Button
    private lateinit var mFilePath: String
    private lateinit var vid1: VideoView
    private lateinit var vid2: VideoView
    private lateinit var vid3: VideoView
    private lateinit var linearLayout1: LinearLayout
    private lateinit var linearLayout2: LinearLayout
    private lateinit var linearLayout3: LinearLayout
    private var linearLayoutList: ArrayList<LinearLayout> = arrayListOf()
    private val videoList: ArrayList<VideoView> = arrayListOf()
    private lateinit var btnSubmit: Button
    private var durationList = arrayListOf<Int>()
    private lateinit var delAlertDialog: AlertDialog
    private var delBuilder: AlertDialog.Builder? = null
    private lateinit var delView: View
    private var videoCount: Int = 0
    private lateinit var cameraDialog: Dialog
    private lateinit var uploadImageResult: TextView
    private lateinit var progressDialog: ProgressDialog
    private lateinit var progressBar: ProgressBar
    private var picPath: String? = null
    private val handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                TO_UPLOAD_FILE -> toUploadFile()
                UPLOAD_INIT_PROCESS -> progressBar.max = msg.arg1
                UPLOAD_IN_PROCESS -> progressBar.progress = msg.arg1
                UPLOAD_FILE_DONE -> {
                    val result =
                        "响应码：" + msg.arg1 + "\n响应信息：" + msg.obj + "\n耗时：" + UploadUtil.getRequestTime() + "秒"
                    uploadImageResult.text = result
                }
                else -> {
                }
            }
            super.handleMessage(msg)
        }
    }

    companion object {
        /**
         * 打开相册
         */
        private const val REQUEST_ALBUM = 0
        /**
         * 调用相机
         */
        private const val REQUEST_CAMERA = 1
        /**
         * 去上传文件
         */
        protected const val TO_UPLOAD_FILE = 1
        /**
         * 上传文件响应
         */
        protected const val UPLOAD_FILE_DONE = 2  //
        /**
         * 选择文件
         */
        val TO_SELECT_PHOTO = 3
        /**
         * 上传初始化
         */
        private const val UPLOAD_INIT_PROCESS = 4
        /**
         * 上传中
         */
        private const val UPLOAD_IN_PROCESS = 5
        /***
         * 这里的这个URL是我服务器的javaEE环境URL
         */
        private const val requestURL = "http://192.168.89.66:8081/wms/services/PdaRestService/uploadFile"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_video)
        btnAdd = findViewById(R.id.btnAdd)
        btnSubmit = findViewById(R.id.btnSubmit)
        vid1 = findViewById(R.id.vid1)
        vid2 = findViewById(R.id.vid2)
        vid3 = findViewById(R.id.vid3)
        linearLayout1 = findViewById(R.id.ll1)
        linearLayout2 = findViewById(R.id.ll2)
        linearLayout3 = findViewById(R.id.ll3)
        linearLayoutList = arrayListOf(linearLayout1, linearLayout2, linearLayout3)
        progressBar = findViewById(R.id.progressBar)
        uploadImageResult = findViewById(R.id.uploadImageResult)
        progressDialog = ProgressDialog(this)
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

        //1、使用Dialog、设置style
        cameraDialog = Dialog(this, R.style.AlertDialogWindow)
        //2、设置布局
        val view = View.inflate(this, R.layout.alertdialog_choose_resources, null)
        cameraDialog.setContentView(view)
        val cameraWindow = cameraDialog.window
        cameraWindow!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        cameraWindow.setGravity(Gravity.BOTTOM)
        //设置对话框大小
        cameraWindow.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

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

        initLayout()

        //添加视频
        btnAdd.setOnClickListener {
            cameraDialog.show()
            val btnTakeVideo = cameraDialog.findViewById<Button>(R.id.btnTakePhoto)
            val btnFromAlbum = cameraDialog.findViewById<Button>(R.id.btnFromAlbum)
            val btnCancel = cameraDialog.findViewById<Button>(R.id.btnCancel)
            btnTakeVideo.text = "录像"
            btnTakeVideo.setOnClickListener {
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
                cameraDialog.dismiss()
            }
            btnFromAlbum.setOnClickListener {
                //打开相册
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                //Intent.ACTION_GET_CONTENT = "android.intent.action.GET_CONTENT"
                intent.type = "video/*"
                startActivityForResult(intent, REQUEST_ALBUM)
                cameraDialog.dismiss()
            }
            btnCancel.setOnClickListener {
                cameraDialog.dismiss()
            }
        }
        btnSubmit.setOnClickListener {
            if (videoCount != 0) {
                handler.sendEmptyMessage(TO_UPLOAD_FILE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) { // 如果返回数据 
            when (requestCode) {
                REQUEST_CAMERA -> {
                    add(mFilePath)
                }
                REQUEST_ALBUM -> {
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4及以上系统使用这个方法处理图片
                        handleVideoOnKitKat(data!!)
                    } else {
                        // 4.4以下系统使用这个方法处理图片
                        handleVideoBeforeKitKat(data!!)
                    }
                }
            }
        }
    }

    override fun onUploadDone(responseCode: Int, message: String?) {
        progressDialog.dismiss()
        val msg = Message.obtain()
        msg.what = UPLOAD_FILE_DONE
        msg.arg1 = responseCode
        msg.obj = message
        handler.sendMessage(msg)
    }

    override fun onUploadProcess(uploadSize: Int) {
        val msg = Message.obtain()
        msg.what = UPLOAD_IN_PROCESS
        msg.arg1 = uploadSize
        handler.sendMessage(msg)
    }

    override fun initUpload(fileSize: Int) {
        val msg = Message.obtain()
        msg.what = UPLOAD_INIT_PROCESS
        msg.arg1 = fileSize
        handler.sendMessage(msg)
    }

    private fun toUploadFile() {
        uploadImageResult.text = "正在上传中..."
        progressDialog.setMessage("正在上传文件...")
        progressDialog.show()
        val fileKey = "pic"
        val uploadUtil = UploadUtil.getInstance()
        uploadUtil.setOnUploadProcessListener(this)  //设置监听器监听上传状态

        val params = HashMap<String, String>()
        params["orderId"] = "11111"
        for (i in 0 until videoCount step 1) {
            picPath = Environment.getExternalStorageDirectory().path + "/Sany/temp_" + i + ".3gp"
            uploadUtil.uploadFile(
                picPath, fileKey,
                requestURL, params,
                this
            )
        }
    }

    /**
     * @description 提交按钮
     * @author Felix J
     * @time 2019/11/18 10:36
     */
    private fun initWindow() {
        if (videoCount == 0) {
            btnSubmit.visibility = View.GONE
            progressBar.visibility = View.GONE
        } else {
            btnSubmit.visibility = View.VISIBLE
            progressBar.visibility = View.VISIBLE
        }
    }

    /**
     * @description 删除视频
     * @author Felix J
     * @time 2019/11/14 14:21
     */
    private fun delVideo(index: Int) {
        //videoList[index].setVideoURI(null)
        linearLayoutList[index].visibility = View.GONE
        videoCount--
        initWindow()
    }

    /**
     * @description 初始化VideoView
     * @author Felix J
     * @time 2019/11/14 11:38
     */
    private fun initLayout() {
        for (i in 0 until linearLayoutList.size step 1) {
            linearLayoutList[i].visibility = View.GONE
        }
        initWindow()
    }

    /**
     * @descriptiont 添加视频
     * @author Felix J
     * @time 2019/11/14 11:38
     */
    private fun add(mFilePath: String) {
        for (i in 0 until linearLayoutList.size step 1) {
            if (linearLayoutList[i].visibility == View.GONE) {
                linearLayoutList[i].visibility = View.VISIBLE
                videoList[i].setVideoPath(mFilePath)
                videoList[i].setMediaController(MediaController(this@AddVideoActivity))
                break
            }
        }
        if (videoCount < 3) {
            videoCount++
        }
        initWindow()
    }

    @TargetApi(19)
    private fun handleVideoOnKitKat(data: Intent) {
        var videoPath: String? = null
        val uri = data.data
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            val docId = DocumentsContract.getDocumentId(uri)
            if ("com.android.providers.media.documents" == uri!!.authority) {
                val id =
                    docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                // 解析出数字格式的id
                val selection = MediaStore.Images.Media._ID + "=" + id
                videoPath =
                    getVideoPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, selection)
            } else if ("com.android.providers.downloads.documents" == uri.authority) {
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content: //downloads/public_downloads"),
                    java.lang.Long.valueOf(docId)
                )
                videoPath = getVideoPath(contentUri, null)
            }
        } else if ("content".equals(uri!!.scheme, ignoreCase = true)) {
            // 如果是content类型的Uri，则使用普通方式处理
            videoPath = getVideoPath(uri, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            // 如果是file类型的Uri，直接获取图片路径即可
            videoPath = uri.path
        }
        // 根据图片路径显示图片
        displayVideo(videoPath)
    }

    /**
     * android 4.4以前的处理方式
     * @param data
     */
    private fun handleVideoBeforeKitKat(data: Intent) {
        val uri = data.data
        val videoPath = getVideoPath(uri!!, null)
        displayVideo(videoPath)
    }

    private fun getVideoPath(uri: Uri, selection: String?): String? {
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

    private fun displayVideo(videoPath: String?) {
        if (videoPath != null) {
            videoCount = 0
            for (i in 0 until linearLayoutList.size step 1) {
                videoCount++
                if (linearLayoutList[i].visibility == View.GONE) {
                    videoCount -= 1
                    break
                }
            }
            val destFilePath =
                Environment.getExternalStorageDirectory().path + "/Sany/temp_" + videoCount + ".3gp"
            CameraUtil.copyFile(videoPath, destFilePath)
            add(destFilePath)
        } else {
            Toast.makeText(this, "获取相册录像失败", Toast.LENGTH_SHORT).show()
        }
    }
}
