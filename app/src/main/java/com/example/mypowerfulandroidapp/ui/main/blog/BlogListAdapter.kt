package com.example.mypowerfulandroidapp.ui.main.blog

import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.example.mypowerfulandroidapp.R
import com.example.mypowerfulandroidapp.models.BlogPost
import com.example.mypowerfulandroidapp.util.DateUtils
import com.example.mypowerfulandroidapp.util.GenericViewHolder
import kotlinx.android.synthetic.main.layout_blog_list_item.view.*


import java.io.BufferedReader

class BlogListAdapter(
    private val interaction: Interaction? = null,
    private val requestManager: RequestManager
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val TAG = "BlogListAdapter"
    private val NO_MORE_RESULT = -1
    private val BLOG_ITEM = 0
    private val NO_MORE_RESULT_BLOG_MARKER = BlogPost(
        NO_MORE_RESULT, "", "", "", "", 0, ""
    )
    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<BlogPost>() {

        override fun areItemsTheSame(oldItem: BlogPost, newItem: BlogPost): Boolean {
            return oldItem.pk == newItem.pk
        }

        override fun areContentsTheSame(oldItem: BlogPost, newItem: BlogPost): Boolean {
            return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(
        BlogRecyclerChangeCallBack(this),
        AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
    )

    internal inner class BlogRecyclerChangeCallBack(
        private val adapter: BlogListAdapter
    ) : ListUpdateCallback {
        override fun onChanged(position: Int, count: Int, payload: Any?) {
            adapter.notifyItemRangeChanged(position, count, payload)
        }

        override fun onInserted(position: Int, count: Int) {
            adapter.notifyItemRangeChanged(position, count)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            adapter.notifyDataSetChanged()

        }

        override fun onRemoved(position: Int, count: Int) {
            adapter.notifyDataSetChanged()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            NO_MORE_RESULT -> {
                GenericViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.layout_no_more_results,
                        parent,
                        false
                    )
                )
            }
            BLOG_ITEM -> {
                BlogViewHolder(
                    itemView = LayoutInflater.from(parent.context).inflate(
                        R.layout.layout_blog_list_item,
                        parent,
                        false
                    ),
                    interaction = interaction,
                    requestManager = requestManager
                )
            }
            else -> {
                BlogViewHolder(
                    itemView = LayoutInflater.from(parent.context).inflate(
                        R.layout.layout_blog_list_item,
                        parent,
                        false
                    ),
                    interaction = interaction,
                    requestManager = requestManager
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is BlogViewHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (differ.currentList[position].pk > -1) {
            return BLOG_ITEM
        }
        return differ.currentList[position].pk//-1
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<BlogPost>?, isQueryExhausted: Boolean) {
        val newList = list?.toMutableList()
        if (isQueryExhausted) {
            newList?.add(NO_MORE_RESULT_BLOG_MARKER)
        }
        differ.submitList(newList)
    }

    fun preloadGlideImages(list: List<BlogPost>?) {
        list?.let { list ->
            for (blogPost in list) {
                requestManager
                    .load(blogPost.image)
                    .preload()
            }
        }

    }

    class BlogViewHolder
    constructor(
        private val requestManager: RequestManager,
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: BlogPost) = with(itemView) {
            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }
            requestManager
                .load(item.image)
                .transition(withCrossFade())
                .into(itemView.blog_image)
            itemView.blog_title.text = item.title
            itemView.blog_author.text = item.username
            itemView.blog_update_date.text = DateUtils.convertLongToStringDate(item.date_updated)

        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: BlogPost)
    }
}
