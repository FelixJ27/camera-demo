package util

import android.content.Context

/**
 * SharedPreferences工具类
 *
 * @author Leon Wong
 */
class SharedPreferencesUtils {

    companion object {

        private const val SP_NAME = "SanyPDA"

        /**
         * PUT 字符串值
         */
        fun put(context: Context, key: String, value: String): Boolean {
            val sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString(key, value)
            return editor.commit()
        }

        /**
         * 获取字符串
         */
        fun getString(context: Context, key: String, defaultValue: String = ""): String {
            val sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getString(key, defaultValue)!!
        }

        /**
         * 删除Key
         */
        fun remove(context: Context, key: String): Boolean {
            val sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.remove(key)
            return editor.commit()
        }
    }
}