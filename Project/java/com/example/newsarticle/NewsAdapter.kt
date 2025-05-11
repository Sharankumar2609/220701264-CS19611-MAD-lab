package com.example.newsarticle

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class NewsAdapter(
    private val context: Context,
    private val articles: List<Article>,
    private val onLikeClickListener: (Article) -> Unit,
    private val likedArticles: Set<String>
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val newsImage: ImageView = itemView.findViewById(R.id.newsImage)
        val newsTitle: TextView = itemView.findViewById(R.id.newsTitle)
        val newsDescription: TextView = itemView.findViewById(R.id.newsDescription)
        val newsSource: TextView = itemView.findViewById(R.id.newsSource)
        val newsDate: TextView = itemView.findViewById(R.id.newsDate)
        val likeButton: ImageButton = itemView.findViewById(R.id.likeButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = articles[position]

        holder.newsTitle.text = article.title
        holder.newsDescription.text = article.description ?: "No description available"
        holder.newsSource.text = article.source.name
        holder.newsDate.text = article.publishedAt.substring(0, 10) // Just show date part

        // Load image with Glide
        if (article.urlToImage != null) {
            Glide.with(context)
                .load(article.urlToImage)
                .placeholder(R.drawable.ic_news_placeholder)
                .into(holder.newsImage)
        } else {
            holder.newsImage.setImageResource(R.drawable.ic_news_placeholder)
        }

        // Set like button state
        if (likedArticles.contains(article.url)) {
            holder.likeButton.setImageResource(R.drawable.ic_liked)
        } else {
            holder.likeButton.setImageResource(R.drawable.ic_like)
        }

        holder.likeButton.setOnClickListener {
            onLikeClickListener(article)
        }
    }

    override fun getItemCount(): Int = articles.size
}