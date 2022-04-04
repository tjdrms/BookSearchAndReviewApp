package com.example.bookreviewapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.bookreviewapp.adapter.BookAdapter
import com.example.bookreviewapp.adapter.HistoryAdapter
import com.example.bookreviewapp.api.BookService
import com.example.bookreviewapp.databinding.ActivityMainBinding
import com.example.bookreviewapp.model.BookDto
import com.example.bookreviewapp.model.History
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var bookAdapter: BookAdapter
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var bookService: BookService

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBookRecyclerView()
        initHistoryRecyclerView()

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "BooksearchDB"
        ).build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://openapi.naver.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        bookService = retrofit.create(BookService::class.java)

        bookService.getBooksByName("<b>", 100,"M6m1P7uQrs2VSoUAc6Rn", "6xF9Be6zUa")
            .enqueue(object: Callback<BookDto> {

                override fun onResponse(call: Call<BookDto>, response: Response<BookDto>) {
                    if (response.isSuccessful.not()) {
                        Log.e(TAG, "NOT!! SUCCESS")
                        return
                    }

                    response.body()?.let {
                        Log.d(TAG, it.toString())
                        it.books.forEach { book ->
                            Log.d(TAG, book.toString())
                        }
                        bookAdapter.submitList(it.books)
                    }
                }

                override fun onFailure(call: Call<BookDto>, t: Throwable) {
                    Log.e(TAG, t.toString())
                }

            })

    }
    private fun search(keyword: String) {
        bookService.getBooksByName(keyword, 50, "M6m1P7uQrs2VSoUAc6Rn", "6xF9Be6zUa")
            .enqueue(object: Callback<BookDto> {

                override fun onResponse(call: Call<BookDto>, response: Response<BookDto>) {

                    hideHistoryView()
                    saveSearchKeyword(keyword)

                    if (response.isSuccessful.not()) {
                        Log.e(TAG, "NOT!! SUCCESS")
                        return
                    }

                    bookAdapter.submitList(response.body()?.books.orEmpty())
                }

                override fun onFailure(call: Call<BookDto>, t: Throwable) {
                    hideHistoryView()
                    Log.e(TAG, t.toString())
                }

            })
    }

    private fun initBookRecyclerView() {
        bookAdapter = BookAdapter()

        binding.bookRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.bookRecyclerView.adapter = bookAdapter
    }

    private fun  initHistoryRecyclerView() {
        historyAdapter = HistoryAdapter(historyDeleteClikedListner = {
             deleteSearchKeyword(it)
        })

        binding.historyRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.historyRecyclerView.adapter = historyAdapter
        initSearchEditText()
    }

    private fun initSearchEditText() {
        binding.searchEditText.setOnKeyListener { view, i, keyEvent ->
            if (i == KeyEvent.KEYCODE_ENTER && keyEvent.action == MotionEvent.ACTION_DOWN) {
                search(binding.searchEditText.text.toString())
                return@setOnKeyListener true
            }

            return@setOnKeyListener false
        }
        binding.searchEditText.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                showHistoryView()
            }
            return@setOnTouchListener false
        }
    }

    private fun showHistoryView() {
        Thread {
            val keywords = db.historyDao().getAll().reversed()

            runOnUiThread {
                binding.historyRecyclerView.isVisible = true
                historyAdapter.submitList(keywords.orEmpty())
            }
        }.start()

        binding.historyRecyclerView.isVisible = true
    }

    private fun hideHistoryView() {
        binding.historyRecyclerView.isVisible = false
    }

    private fun saveSearchKeyword(keyword: String) {
        Thread {
            db.historyDao().insertHistory(History(null, keyword))
        }.start()
    }

    private  fun deleteSearchKeyword(keyword: String) {
        Thread {
            db.historyDao().delete(keyword)
            showHistoryView()
        }.start()
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}