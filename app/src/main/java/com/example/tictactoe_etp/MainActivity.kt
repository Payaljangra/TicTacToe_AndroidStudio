package com.example.tictactoe_etp


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.children
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity() {
    private lateinit var vplayer1_name:TextView
    private lateinit var vplayer2_name:TextView
    private lateinit var tilesContainer:GridLayout
    private lateinit var player1_Scorecontainer:TextView
    private lateinit var player2_Scorecontainer:TextView
    private var player1_turn=true
    private lateinit var setPos:ArrayList<ArrayList<Char>>
    private var filled=0;
    private lateinit var btnQuit:Button
    private lateinit var db:Database
    val activityScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initilize()

        tilesContainer.children.forEachIndexed{idx,tile->tile.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                val imgView=(tile as ImageView)
                val row=idx/3
                val col=idx%3
                println(row)
                if(setPos[row][col] =='.'){
                    filled++;
                    setPos[row][col] = if(player1_turn) '1' else '2';
                    if(player1_turn){
                        imgView.setImageResource(R.drawable.o)
                    }else{
                        imgView.setImageResource(R.drawable.crossed)
                    }
                    if(checkWinner()) {
                        Toast.makeText(this@MainActivity, "${if(player1_turn) "Player1" else "Player2"} Won", Toast.LENGTH_LONG).show()
                        if(player1_turn){
                            player1_Scorecontainer.text= (player1_Scorecontainer.text.toString().toInt()+1).toString()
                            db.updateScore(intent.extras!!.getString("name1")!!)
                        }else{
                            player2_Scorecontainer.text= (player2_Scorecontainer.text.toString().toInt()+1).toString()
                            db.updateScore(intent.extras!!.getString("name2")!!)
                        }
                        resetGame()
                    }else{
                        player1_turn=!player1_turn
                        if(filled==9){
                            resetGame()
                        }
                    }
                }else{
                    Toast.makeText(this@MainActivity,"Invalid move.",Toast.LENGTH_LONG).show()
                }
            }
        })}

        btnQuit.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                val p1_score= player1_Scorecontainer.text.toString().toInt()
                val p2_score= player2_Scorecontainer.text.toString().toInt()
                if(p1_score==p2_score){
                    Toast.makeText(this@MainActivity,"Draw",Toast.LENGTH_LONG).show()
                }else if(p1_score>p2_score){
                    Toast.makeText(this@MainActivity,"Player 1 won by ${p1_score-p2_score} point.",Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(this@MainActivity,"Player 2 won by ${p2_score-p1_score} point.",Toast.LENGTH_LONG).show()
                }

                activityScope.launch {
                    delay(3000)
                    finish()
                }
            }
        })
    }
    override fun onPause() {
        activityScope.cancel()
        super.onPause()
    }
    private fun checkWinner():Boolean{
        println("checking winner")
        println(setPos)
        for(row in 0 until 3){
            val isRowSame=setPos[row][0]!='.' && (setPos[row][0]==setPos[row][1]) && (setPos[row][1]==setPos[row][2])
            if(isRowSame){
                return true
            }
        }
        for(col in 0 until 3){
            val isColSame=setPos[0][col]!='.' && (setPos[0][col]==setPos[1][col]) && (setPos[1][col]==setPos[2][col])
            if(isColSame){
                return true
            }
        }
        val diag1= setPos[0][0]!='.' && setPos[0][0]==setPos[1][1] && setPos[1][1]==setPos[2][2]
        val diag2= setPos[0][2]!='.' && setPos[0][2]==setPos[1][1] && setPos[1][1]==setPos[2][0]
        return diag1 || diag2
    }
    private fun initilize(){
        db= Database(this,getString(R.string.DB_NAME),null,1)
        tilesContainer=findViewById(R.id.tiles_grid)
        player1_Scorecontainer=findViewById(R.id.player1_score)
        player2_Scorecontainer=findViewById(R.id.player2_score)
        btnQuit=findViewById(R.id.btn_quit)

        vplayer1_name=findViewById(R.id.player1_name)
        vplayer2_name=findViewById(R.id.player2_name)
        val name1=intent.extras?.getString("name1") ?: "unknown1"
        val name2=intent.extras?.getString("name2") ?: "unknown2"
        val player1Data=db.getUserData(name1)
        val player2Data=db.getUserData(name2)
        vplayer1_name.text=player1Data.first
        player1_Scorecontainer.text=player1Data.second.toString()
        vplayer2_name.text=player2Data.first
        player2_Scorecontainer.text=player2Data.second.toString()

        initilizePositions();
    }
    private fun initilizePositions(){
        setPos= ArrayList()
        repeat(3){
            val alst=ArrayList<Char>()
            repeat(3){
                alst.add('.')
            }
            setPos.add(alst)
        }
    }
    private fun resetTiles(){
        tilesContainer.children.forEach { tile->
            (tile as ImageView).setImageResource(R.drawable.cool)
        }
    }
    private fun resetGame(){
        resetTiles()
        initilizePositions()
        filled=0
        player1_turn=true
    }
}