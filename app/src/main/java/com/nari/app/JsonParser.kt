package com.nari.app

import android.content.Context
import org.json.JSONArray

class JsonParser(private val context: Context) {

    fun parseAndSaveJson(jsonString: String) {
        // Clean the database before adding new data
        val itemRepository = ItemRepository(context)
        itemRepository.deleteAllItems()



        val jsonArray = JSONArray(jsonString)

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)

            val title = jsonObject.getString("title")
            val description = jsonObject.getString("description")
            val image = jsonObject.getString("image")
            val category = jsonObject.getString("category")

            val newItem = Item(i, title, description, image, category)

            itemRepository.insertItem(newItem)
        }
    }
}
