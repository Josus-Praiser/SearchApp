package com.josus.myapplication

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList
import java.util.HashMap

class JsonParser {
    private fun parseJsonObject(jsonObject: JSONObject): HashMap<String, String> {
        val dataList = HashMap<String, String>()
        try {
            val name = jsonObject.getString("link")
            dataList["link"] = name
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return dataList
    }

    private fun parseJsonArray(jsonArray: JSONArray?): List<HashMap<String, String>> {
        val dataList: MutableList<HashMap<String, String>> = ArrayList()
        if (jsonArray == null) return dataList
        for (i in 0 until jsonArray.length()) {
            try {
                val data = parseJsonObject(jsonArray[i] as JSONObject)
                dataList.add(data)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return dataList
    }

    fun parseResult(jsonObject: JSONObject): List<HashMap<String, String>> {
        var jsonArray: JSONArray? = null
        try {
            jsonArray = jsonObject.getJSONArray("items")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return parseJsonArray(jsonArray)
    }
}