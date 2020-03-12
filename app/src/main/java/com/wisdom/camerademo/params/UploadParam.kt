package com.wisdom.camerademo.params

import org.json.JSONObject

/**
 * @description:
 * @author: Felix J
 * @time: 2019/11/20 15:33
 */
data class UploadParam(
    val orderNum: String
) {
    fun toJson(): JSONObject{
        val jsonObject = JSONObject()
        jsonObject.put("orderNum", orderNum)
        return jsonObject
    }
}