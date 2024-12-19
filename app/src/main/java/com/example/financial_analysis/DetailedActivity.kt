package com.example.financial_analysis

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.room.Room
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DetailedActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed)



        // Находим все элементы интерфейса с помощью findViewById
        val labelInput = findViewById<EditText>(R.id.labelInput)
        val amountInput = findViewById<EditText>(R.id.amountInput)
        val descriptionInput = findViewById<EditText>(R.id.descriptionInput)
        val descriptionLayout = findViewById<EditText>(R.id.descriptionLayout)
        val labelLayout = findViewById<TextInputLayout>(R.id.labelLayout)
        val amountLayout = findViewById<TextInputLayout>(R.id.amountLayout)
        val updateBtn = findViewById<Button>(R.id.updateBtn)


        val transaction = intent.getSerializableExtra("transaction") as Transaction

        labelInput.setText(transaction.label)
        amountInput.setText(transaction.amount.toString())
        descriptionInput.setText(transaction.description)




        labelInput.addTextChangedListener {
            updateBtn.visibility = View.VISIBLE
            if (it!!.count() > 0)
                labelLayout.error = null
        }

        amountInput.addTextChangedListener {
            updateBtn.visibility = View.VISIBLE
            if (it!!.count() > 0)
                amountLayout.error = null
        }

        descriptionInput.addTextChangedListener {
            updateBtn.visibility = View.VISIBLE
        }





        // Устанавливаем обработчик для кнопки
        updateBtn.setOnClickListener {
            val label = labelInput.text.toString()  // Получаем текст из поля labelInput
            val description = descriptionInput.text.toString()  // Получаем текст из поля labelInput
            val amount = amountInput.text.toString().toDoubleOrNull()  // Получаем число из поля amountInput

            if(label.isEmpty()){
                labelLayout.error = "Введите правильно название!"
            }
            else if(amount == null){
                amountLayout.error = "Введите правильно цену!"
            }
            else{
                val transaction = Transaction(0,label,amount, description)
                update(transaction)
            }

        }
        // Находим кнопку по идентификатору с помощью findViewById
        val closeBtn = findViewById<ImageButton>(R.id.closeBtn)

// Устанавливаем обработчик клика для кнопки
        closeBtn.setOnClickListener {
            finish()  // Закрывает текущую активность
        }
    }

    private fun update(transaction: Transaction) {
        val db = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "transaction"
        ).build()

        GlobalScope.launch {
            db.transactionDao().update(transaction)
            finish()
        }
    }
}