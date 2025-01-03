package com.example.financial_analysis

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var deletedTransaction: Transaction
    private lateinit var transactions: List<Transaction>
    private lateinit var oldTransactions: List<Transaction>
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var db : AppDatabase

    // UI elements
    private lateinit var recyclerView: RecyclerView
    private lateinit var balanceText: TextView
    private lateinit var budgetText: TextView
    private lateinit var expenseText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        recyclerView = findViewById(R.id.recyclerView)
        balanceText = findViewById(R.id.balance)
        budgetText = findViewById(R.id.budget)
        expenseText = findViewById(R.id.expense)


        transactions = arrayListOf( )

        transactionAdapter = TransactionAdapter(transactions)
        linearLayoutManager = LinearLayoutManager(this)

        db = Room.databaseBuilder(this,
            AppDatabase::class.java,
            "transaction").build()

        recyclerView.apply {
            adapter = transactionAdapter
            layoutManager = linearLayoutManager
        }


        val addBtn = findViewById<FloatingActionButton>(R.id.AddBtn)

        //смена на удалить
        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                deleteTransaction(transactions[viewHolder.adapterPosition])
            }
        }

        val swipeHelper = ItemTouchHelper(itemTouchHelper)
        swipeHelper.attachToRecyclerView(recyclerView)

// Устанавливаем обработчик клика для кнопки
        addBtn.setOnClickListener{
            val intent = Intent(this, AddTransactionActivity::class.java)
            startActivity(intent)
        }

    }

    private fun fetchAll(){
        GlobalScope.launch {
            db.transactionDao().insertAll()
            transactions = db.transactionDao().getAll()

            runOnUiThread{
                updateDashboard()
                transactionAdapter.setData(transactions)
            }
        }
    }
    private fun updateDashboard() {
        val totalAmount = transactions.map { it.amount }.sum()
        val budgetAmount = transactions.filter { it.amount > 0 }.map { it.amount }.sum()
        val expenseAmount = totalAmount - budgetAmount


        balanceText.text = String.format("₽%.2f", totalAmount)
        budgetText.text = String.format("₽%.2f", budgetAmount)
        expenseText.text = String.format("₽%.2f", expenseAmount)

    }

    private fun undoDelete(){
        GlobalScope.launch {
            db.transactionDao().insertAll(deletedTransaction)

            transactions = oldTransactions

            runOnUiThread{
                transactionAdapter.setData(transactions)
                updateDashboard()
            }
        }
    }

    private fun showSnackbar(){
        val view = findViewById<View>(R.id.coordinator)
        val snackbar = Snackbar.make(view, "Транзакция удалена!", Snackbar.LENGTH_LONG)
        snackbar.setAction("Отменить"){
            undoDelete()
        }
            .setActionTextColor(ContextCompat.getColor(this, R.color.red))
            .setTextColor(ContextCompat.getColor(this, R.color.white))
            .show()
    }

    private fun deleteTransaction(transaction: Transaction){
        deletedTransaction = transaction
        oldTransactions = transactions

        GlobalScope.launch {
            db.transactionDao().delete(transaction)

            transactions = transactions.filter { it.id != transaction.id }
            runOnUiThread{
                updateDashboard()
                transactionAdapter.setData(transactions)
                showSnackbar()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        fetchAll()
    }
}