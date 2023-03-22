package database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.media.Image
import android.net.Uri



class MyDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "my_database.db"

        // Table utilisateur
        const val TABLE_USER = "user"
        const val COLUMN_USER_ID = "_id"
        const val COLUMN_USER_USERNAME = "username"
        const val COLUMN_USER_PASSWORD = "password"
        const val COLUMN_USER_EMAIL = "email"


    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Création de la table utilisateur
        val CREATE_USER_TABLE = "CREATE TABLE $TABLE_USER " +
                "($COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_USER_USERNAME TEXT, " +
                "$COLUMN_USER_PASSWORD TEXT, " +
                "$COLUMN_USER_EMAIL TEXT)"
        db?.execSQL(CREATE_USER_TABLE)


    }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Suppression des tables lors de la mise à niveau de la base de données
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        onCreate(db)
    }

    fun addUser(username: String, password: String, email: String): Long {
        val values = ContentValues()
        values.put(COLUMN_USER_USERNAME, username)
        values.put(COLUMN_USER_PASSWORD, password)
        values.put(COLUMN_USER_EMAIL, email)
        return writableDatabase.insert(TABLE_USER, null, values)
    }

    fun deleteUser(id: Long): Int {
        val selection = "$COLUMN_USER_ID = ?"
        val selectionArgs = arrayOf(id.toString())
        return writableDatabase.delete(TABLE_USER, selection, selectionArgs)
    }


    fun getAllUsers(): Cursor {
        val selectQuery = "SELECT * FROM $TABLE_USER"
        return readableDatabase.rawQuery(selectQuery, null)
    }


}

class ImageDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "images.db"
        const val DATABASE_VERSION = 1

        const val TABLE_NAME = "images"
        const val COLUMN_ID = "_id"
        const val COLUMN_NAME = "name"
        const val COLUMN_URI = "uri"
        const val COLUMN_PATH = "path"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_NAME TEXT, " +
                "$COLUMN_URI TEXT, " +
                "$COLUMN_PATH TEXT)"
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertImage(name: String, uri: String, path: String) {
        val values = ContentValues()
        values.put(COLUMN_NAME, name)
        values.put(COLUMN_URI, uri)
        values.put(COLUMN_PATH, path)

        val db = writableDatabase
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun deleteImage(name: String) {
        val db = writableDatabase
        db.delete(TABLE_NAME, "$COLUMN_NAME = ?", arrayOf(name))
        db.close()
    }

    class Image(val name: String, val uri: Uri, val path: String)
    fun getAllImages(): ArrayList<Image> {
        val images = ArrayList<Image>()

        val db = readableDatabase
        val cursor = db.query(TABLE_NAME, null, null, null, null, null, null)

        if (cursor.moveToFirst()) {
            do {
                val name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
                val uri = cursor.getString(cursor.getColumnIndex(COLUMN_URI))
                val path = cursor.getString(cursor.getColumnIndex(COLUMN_PATH))

                images.add(Image(name, Uri.parse(uri), path))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return images
    }

    fun insertImage(name: String, uri: String) {
        insertImage(name, uri, "")
    }

}



