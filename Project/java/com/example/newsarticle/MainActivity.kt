package com.example.newsarticle

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var searchEditText: EditText
    private lateinit var searchButton: ImageButton
    private lateinit var adapter: NewsAdapter
    private val articles = mutableListOf<Article>()
    private val likedArticles = mutableSetOf<String>()
    private val sharedPref by lazy { getPreferences(Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
        searchEditText = findViewById(R.id.searchEditText)
        searchButton = findViewById(R.id.searchButton)

        // Load liked articles from SharedPreferences
        likedArticles.addAll(sharedPref.getStringSet("liked_articles", setOf()) ?: setOf())

        setupRecyclerView()
        loadTopHeadlines()

        searchButton.setOnClickListener {
            val query = searchEditText.text.toString()
            if (query.isNotEmpty()) {
                searchNews(query)
            } else {
                loadTopHeadlines()
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = NewsAdapter(
            this,
            articles,
            onLikeClickListener = { article ->
                if (likedArticles.contains(article.url)) {
                    likedArticles.remove(article.url)
                    Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show()
                } else {
                    likedArticles.add(article.url)
                    Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show()
                }
                // Save to SharedPreferences
                sharedPref.edit().putStringSet("liked_articles", likedArticles).apply()
                adapter.notifyDataSetChanged()
            },
            likedArticles
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun loadTopHeadlines() {
        progressBar.visibility = View.VISIBLE
        RetrofitClient.instance.getTopHeadlines("us", "6b4958f940344ca497fb183e1a384d42").enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                progressBar.visibility = View.GONE
                Log.d("API_ERROR", "Response code: ${response.code()}, error: ${response.errorBody()?.string()}")
                if (response.isSuccessful) {
                    response.body()?.articles?.let {
                        articles.clear()
                        articles.addAll(it)
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Failed to load news", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun searchNews(query: String) {
        progressBar.visibility = View.VISIBLE
        RetrofitClient.instance.searchNews(query, "6b4958f940344ca497fb183e1a384d42").enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    response.body()?.articles?.let {
                        articles.clear()
                        articles.addAll(it)
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Failed to search news", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}