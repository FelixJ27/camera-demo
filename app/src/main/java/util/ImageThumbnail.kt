package util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils

/**
 *@description: 缩略图
 *@author: Haoran Jiang
 *@time: 2019/11/8 9:44
 */
class ImageThumbnail {
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
}