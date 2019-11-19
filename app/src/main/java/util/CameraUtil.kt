package util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.widget.ImageView
import java.io.*
import android.util.Log


/**
 *@description: 图像处理util
 *@author: Felix J
 *@time: 2019/11/13 15:47
 */
class CameraUtil {

    companion object {
        /**
         * @description 生成缩略图
         * @author Felix J
         * @time 2019/11/13 15:48
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

        /**
         * @description 设置照片
         * @author Felix J
         * @time 2019/11/13 15:52
         */
        fun setPicture(mFilePath: String, imageView: ImageView, reqCode: Int) {
            var fis: FileInputStream? = null
            try {
                fis = FileInputStream(mFilePath) // 根据路径获取数据
                //val bitmap = BitmapFactory.decodeStream(fis)
                //val bos = BufferedOutputStream(FileOutputStream(File(mFilePath)))
                //bitmap?.compress(Bitmap.CompressFormat.JPEG, 30, bos)
                val options = BitmapFactory.Options()
                options.inSampleSize = 2//图片宽高都为原来的二分之一，即图片为原来的四分之一
                val bitmap: Bitmap
                bitmap = if (reqCode == 1) {
                    BitmapFactory.decodeStream(fis, null, options)!!
                } else {
                    getImageThumbnail(mFilePath, 80, 80)
                }
                imageView.setImageBitmap(bitmap)
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

        /**
         * @description 复制文件
         * @author Felix J
         * @time 2019/11/14 15:04
         */
        fun copyFile(srcFilePath: String, destFilePath: String): Boolean {
            try {
                val oldFile = File(srcFilePath)
                if (!oldFile.exists()) {
                    Log.e("--Method--", "copyFile:  oldFile not exist.")
                    return false
                } else if (!oldFile.isFile) {
                    Log.e("--Method--", "copyFile:  oldFile not file.")
                    return false
                } else if (!oldFile.canRead()) {
                    Log.e("--Method--", "copyFile:  oldFile cannot read.")
                    return false
                }

                /* 如果不需要打log，可以使用下面的语句
                if (!oldFile.exists() || !oldFile.isFile() || !oldFile.canRead()) {
                    return false;
                }
                */

                val fileInputStream = FileInputStream(srcFilePath)
                val fileOutputStream = FileOutputStream(destFilePath)
                val buffer = ByteArray(1024)
                var byteRead: Int
                do {
                    byteRead = fileInputStream.read(buffer)
                    if (byteRead != -1) {
                        fileOutputStream.write(buffer, 0, byteRead)
                    }else{
                        break
                    }
                } while (true)
               /* while (-1 != (byteRead = fileInputStream.read(buffer))) {
                    fileOutputStream.write(buffer, 0, byteRead)
                }*/
                fileInputStream.close()
                fileOutputStream.flush()
                fileOutputStream.close()
                return true
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
        }
    }
}