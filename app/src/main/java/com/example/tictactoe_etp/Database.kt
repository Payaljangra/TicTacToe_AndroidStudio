package com.example.tictactoe_etp


import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper

class Database(context: Context, dbName: String, factory: CursorFactory?, version: Int) :
    SQLiteOpenHelper(context, dbName, factory, version) {

    override fun onCreate(db: SQLiteDatabase?) {
        val tablePlayer="""
            create table if not exists player(
            	name text,
            	score integer
            );
        """.trimIndent();
        db?.execSQL(tablePlayer)
    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    private fun userExist(name:String):Boolean{
        val db:SQLiteDatabase=readableDatabase
        val cursor=db.rawQuery("select name from player where name=?", arrayOf(name))
        val result:Boolean=cursor.moveToFirst();
        cursor.close()
        db.close()
        return  result
    }
    fun addUser(name:String){
        if(userExist(name)){
            return
        }
        val cv=ContentValues()
        cv.put("name",name)
        cv.put("score",0)
        val db:SQLiteDatabase=writableDatabase
        db.insert("player",null,cv)
        db.close()
    }
    fun getUserData(name: String): Pair<String,Int> {
        val db: SQLiteDatabase = readableDatabase
        val credentials = arrayOf(name)

        val cursor=db.rawQuery("select name,score from player where name=?", credentials)
        val isUserEx=cursor.moveToFirst()

        val res:Pair<String,Int> = if(isUserEx)
            Pair(cursor.getString(0),cursor.getInt(1))
        else
            Pair("Unknown",0)

        cursor.close()
        db.close()
        return res
    }
    fun updateScore(name:String){
        if(!userExist(name)){
            return
        }
        val cv=ContentValues()
        cv.put("score",0)
        val db:SQLiteDatabase=writableDatabase
        db.execSQL("update player set score=score+1 where name=\"${name}\"")
        db.close()
    }
}