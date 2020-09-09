package com.example.mypowerfulandroidapp.ui.main.blog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.mypowerfulandroidapp.R
import com.example.mypowerfulandroidapp.models.BlogPost
import com.example.mypowerfulandroidapp.ui.main.blog.state.BlogStateEvent
import com.example.mypowerfulandroidapp.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_blog.*
import kotlinx.coroutines.processNextEventInCurrentThread
import javax.inject.Inject

class BlogFragment : BaseBlogFragment(),
    BlogListAdapter.Interaction {
    private val TAG = "BlogFragment"

    @Inject
    lateinit var requestManager: RequestManager

    lateinit var recyclerAdapter: BlogListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        subscribeObservers()
        executeSearchQuery()
    }

    private fun executeSearchQuery() {
        viewModel.setQuery("")
        viewModel.setStatEvent(BlogStateEvent.BlogSearchEvent())
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner) { dataState ->
            stateChangeListener.onDataStateChange(dataState)
            dataState.data?.let { data ->
                data.data?.let { event ->
                    event.getContentIfNotHandled()?.let { blogViewState ->
                        blogViewState.let {
                            Log.d(
                                TAG,
                                "subscribeObservers: Data State blog list: ${it.blogFields.blogList}"
                            )
                            viewModel.setBlogListData(it.blogFields.blogList)
                        }
                    }
                }
            }
        }
        viewModel.viewState.observe(viewLifecycleOwner) { blogViewState ->
            Log.d(
                TAG,
                "subscribeObservers: ViewState blog list: ${blogViewState.blogFields.blogList}"
            )
            recyclerAdapter.submitList(blogViewState.blogFields.blogList, true)
        }
    }

    private fun initRecyclerView() {
        blog_post_recyclerview.apply {
            layoutManager = LinearLayoutManager(this@BlogFragment.context)
            val topSpacingItemDecoration = TopSpacingItemDecoration(30)
            removeItemDecoration(topSpacingItemDecoration)
            addItemDecoration(topSpacingItemDecoration)
            recyclerAdapter = BlogListAdapter(
                this@BlogFragment,
                requestManager
            )
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    if (lastPosition == recyclerAdapter.itemCount.minus(1)) {
                        Log.d(TAG, "onScrollStateChanged: attempt to load next page ...")
                        //TODO("loading next page using viewModel")
                    }
                }
            })
            adapter = recyclerAdapter
        }
    }

    override fun onItemSelected(position: Int, item: BlogPost) {
        Log.d(TAG, "onItemSelected: the $position selected $item")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        blog_post_recyclerview.adapter = null
    }
}