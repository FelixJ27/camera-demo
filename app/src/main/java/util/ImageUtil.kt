package util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.widget.ImageView
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException

/**
 *@description: 图像处理util
 *@author: Felix J
 *@time: 2019/11/13 15:47
 */
class ImageUtil {

    companion object {
        /**
         * @description 生成错略图
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
         * @description 获取照片
         * @author Felix J
         * @time 2019/11/13 15:52
         */
        fun setPicture(mFilePath: String, imageView: ImageView) {
            var fis: FileInputStream? = null
            try {
                fis = FileInputStream(mFilePath) // 根据路径获取数据
                val bitmap = BitmapFactory.decodeStream(fis)
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
    }

}