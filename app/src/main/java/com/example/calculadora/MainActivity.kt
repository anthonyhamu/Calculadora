    package com.example.calculadora

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor


class MainActivity : AppCompatActivity()    {

    lateinit var valor_A: EditText
    lateinit var valor_B: EditText
    lateinit var soma:Button
    lateinit var sub:Button
    lateinit var mult:Button
    lateinit var div:Button
    lateinit var res: TextView
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var historico: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        setupView()
        calculos()

            historico.setOnClickListener {
                startActivity( Intent(this,historico::class.java))
            }

        }

    fun setupView(){
        valor_A =  findViewById(R.id.ET_valorA)
        valor_B =  findViewById(R.id.ET_valorB)
        soma = findViewById(R.id.btn_Soma)
        sub = findViewById(R.id.btn_sub)
        mult = findViewById(R.id.btn_mult)
        div = findViewById(R.id.btn_div)
        res = findViewById(R.id.tv_res)
        historico = findViewById(R.id.btn_historico)

    }

    fun calculos(){

        soma.setOnClickListener{
            val valorA = valor_A.text.toString().toFloat()
            val valorB = valor_B.text.toString().toFloat()
            val resSoma = valorA + valorB
            res.text = resSoma.toString()
            dbHelper.addCalculo(valorA, valorB, "+", resSoma)
            limparCampos()
        }

        sub.setOnClickListener{
            val valorA = valor_A.text.toString().toFloat()
            val valorB = valor_B.text.toString().toFloat()
            val resSub = valorA - valorB
            res.text = resSub.toString()
            dbHelper.addCalculo(valorA, valorB, "-", resSub)
            limparCampos()

        }

        mult.setOnClickListener{
            val valorA = valor_A.text.toString().toFloat()
            val valorB = valor_B.text.toString().toFloat()
            val resMult = valorA * valorB
            res.text = resMult.toString()
            dbHelper.addCalculo(valorA, valorB, "*", resMult)
            limparCampos()
        }
        div.setOnClickListener{
            val valorA = valor_A.text.toString().toFloat()
            val valorB = valor_B.text.toString().toFloat()
            if(valorB != 0f){
                val resDiv = valorA / valorB
                res.text = resDiv.toString()
                dbHelper.addCalculo(valorA, valorB, "/", resDiv)
                limparCampos()
            }else{
                res.text = "Não é possivel dividir por 0"
            }
        }
    }

    private fun setupDatabase() {
        dbHelper = DatabaseHelper.getInstance(this)
    }
    private fun limparCampos() {
        valor_A.text.clear()
        valor_B.text.clear()
    }


    }

    class DatabaseHelper private constructor(context: Context) :
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        companion object {
            private const val DATABASE_NAME = "calculadora.db"
            private const val DATABASE_VERSION = 1


            private const val TABLE_CALCULOS = "calculos"
            private const val COLUMN_ID = "id"
            private const val COLUMN_VALOR_A = "valor_a"
            private const val COLUMN_VALOR_B = "valor_b"
            private const val COLUMN_OPERACAO = "operacao"
            private const val COLUMN_RESULTADO = "resultado"
            private const val COLUMN_DATA_HORA = "data_hora"


            private const val SQL_CREATE_CALCULOS =
                "CREATE TABLE $TABLE_CALCULOS (" +
                        "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "$COLUMN_VALOR_A REAL," +
                        "$COLUMN_VALOR_B REAL," +
                        "$COLUMN_OPERACAO TEXT," +
                        "$COLUMN_RESULTADO REAL," +
                        "$COLUMN_DATA_HORA DATETIME DEFAULT CURRENT_TIMESTAMP)"

            @Volatile
            private var instance: DatabaseHelper? = null

            fun getInstance(context: Context): DatabaseHelper {
                return instance ?: synchronized(this) {
                    instance ?: DatabaseHelper(context.applicationContext).also { instance = it }
                }
            }
        }

        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(SQL_CREATE_CALCULOS)
        }

        override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
            TODO("Not yet implemented")
        }


        fun addCalculo(valorA: Float, valorB: Float, operacao: String, resultado: Float): Long {
            val db = writableDatabase
            val values = ContentValues()
            values.put(COLUMN_VALOR_A, valorA)
            values.put(COLUMN_VALOR_B, valorB)
            values.put(COLUMN_OPERACAO, operacao)
            values.put(COLUMN_RESULTADO, resultado)
            return db.insert(TABLE_CALCULOS, null, values)
        }

        fun getAllCalculos(): Cursor {
            val db = readableDatabase
            return db.rawQuery("SELECT * FROM $TABLE_CALCULOS", null)
        }
    }






