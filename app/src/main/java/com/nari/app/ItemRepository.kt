package com.nari.app

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

//import android.database.sqlite.SQLiteDatabase

class ItemRepository(context: Context) {

    private val dbHelper = DbHelper(context)

    fun insertItem(item: Item) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DbHelper.COLUMN_TITLE, item.title)
            put(DbHelper.COLUMN_DESCRIPTION, item.description)
            put(DbHelper.COLUMN_IMAGE, item.image)
            put(DbHelper.COLUMN_CATEGORY, item.category)
        }
        db.insert(DbHelper.TABLE_NAME, null, values)
        db.close()
    }

    fun deleteAllItems() {
        dbHelper.wipeDatabase()

    }

    fun getAllItems(): List<Item> {
        val itemList = mutableListOf<Item>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM ${DbHelper.TABLE_NAME}", null)

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_TITLE))
            val description = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_DESCRIPTION))
            val image = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_IMAGE))
            val category = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_CATEGORY))

            val item = Item(id, title, description, image, category)
            itemList.add(item)
        }

        cursor.close()
        db.close()

        return itemList
    }
    fun getItemById(itemId: Int): Item? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DbHelper.TABLE_NAME,
            null,
            "${DbHelper.COLUMN_ID} = ?",
            arrayOf(itemId.toString()),
            null,
            null,
            null
        )

        return try {
            if (cursor.moveToFirst()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_TITLE))
                val description = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_DESCRIPTION))
                val image = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_IMAGE))
                val category = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_CATEGORY))

                Item(id, title, description, image, category)
            } else {
                null
            }
        } catch (e: Exception) {
            // Handle exceptions (e.g., log, throw, or return a default value)
            null
        } finally {
            cursor?.close()
            db?.close()
        }
    }


    fun getItemsByCategory(category: String): List<Item> {
        val itemList = mutableListOf<Item>()
        val db = dbHelper.readableDatabase
        val selection = "${DbHelper.COLUMN_CATEGORY} = ?"
        val selectionArgs = arrayOf(category)

        val cursor: Cursor = db.query(
            DbHelper.TABLE_NAME,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_TITLE))
            val description = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_DESCRIPTION))
            val image = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_IMAGE))
            val itemCategory = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_CATEGORY))

            val item = Item(id, title, description, image, itemCategory)
            itemList.add(item)
        }

        cursor.close()
        db.close()

        return itemList
    }
}
