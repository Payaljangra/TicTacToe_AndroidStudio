package com.example.tictactoe_etp


import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class UserLoginActivity : AppCompatActivity() {
    private lateinit var pname1:EditText
    private lateinit var pname2:EditText
    private lateinit var btnstart:Button
    private lateinit var db:Database
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_login)

        pname1=findViewById(R.id.pname1)
        pname2=findViewById(R.id.pname2)
        btnstart=findViewById(R.id.btnStart)
        db=Database(this,getString(R.string.DB_NAME),null,1)

        btnstart.setOnClickListener{
            val name1=pname1.text.toString()
            val name2=pname2.text.toString()
            if(name1.isBlank() || name2.isBlank()){
                return@setOnClickListener Toast.makeText(this,"Invalid usernames..",Toast.LENGTH_SHORT).show()
            }
            db.addUser(name1)
            db.addUser(name2)
            val i=Intent(this,MainActivity::class.java)
            i.putExtra("name1",name1)
            i.putExtra("name2",name2)
            startActivity(i)
            finish()
        }
    }

    override fun onDestroy() {
        db.close()
        super.onDestroy()
    }
}