package com.example.financial_analysis

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class AddTransactionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        // Находим все элементы интерфейса с помощью findViewById
        val labelInput = findViewById<EditText>(R.id.labelInput)
        val amountInput = findViewById<EditText>(R.id.amountInput)
        val descriptionInput = findViewById<EditText>(R.id.descriptionInput)
        val labelLayout = findViewById<TextInputLayout>(R.id.labelLayout)
        val amountLayout = findViewById<TextInputLayout>(R.id.amountLayout)
        val addTransactionBtn = findViewById<Button>(R.id.addTransactionBtn)

        // Устанавливаем обработчик для кнопки
        addTransactionBtn.setOnClickListener {
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
                insert(transaction)
            }

        }
        // Находим кнопку по идентификатору с помощью findViewById
        val closeBtn = findViewById<ImageButton>(R.id.closeBtn)

// Устанавливаем обработчик клика для кнопки
        closeBtn.setOnClickListener {
            finish()  // Закрывает текущую активность
        }
    }

    private fun insert(transaction: Transaction){
        val db = Room.databaseBuilder(this,
            AppDatabase::class.java,
            "transaction").build()

        GlobalScope.launch {
            db.transactionDao().insertAll(transaction)
            finish()
        }
    }
}

