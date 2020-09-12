package com.example.mypowerfulandroidapp.ui.main.blog

import android.app.SearchManager
import android.content.Context.SEARCH_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.mypowerfulandroidapp.R
import com.example.mypowerfulandroidapp.models.BlogPost
import com.example.mypowerfulandroidapp.ui.DataState
import com.example.mypowerfulandroidapp.ui.main.blog.state.BlogViewState
import com.example.mypowerfulandroidapp.ui.main.blog.viewmodels.*
import com.example.mypowerfulandroidapp.util.ErrorHandling
import com.example.mypowerfulandroidapp.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_blog.*

/*import android.app.SearchManager
import android.content.ComponentName
import android.content.Context.SEARCH_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mypowerfulandroidapp.R
import com.example.mypowerfulandroidapp.models.BlogPost
import com.example.mypowerfulandroidapp.ui.DataState
import com.example.mypowerfulandroidapp.ui.main.blog.state.BlogStateEvent
import com.example.mypowerfulandroidapp.ui.main.blog.state.BlogViewState
import com.example.mypowerfulandroidapp.ui.main.blog.viewmodels.*
import com.example.mypowerfulandroidapp.util.ErrorHandling
import com.example.mypowerfulandroidapp.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_blog.**/

class BlogFragment : BaseBlogFragment(),
    BlogListAdapter.Interaction,
    SwipeRefreshLayout.OnRefreshListener {
    private val TAG = "BlogFragment"


    lateinit var recyclerAdapter: BlogListAdapter
    private lateinit var searchView: SearchView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        swipe_refresh.setOnRefreshListener(this)


        initRecyclerView()
        subscribeObservers()
        if (savedInstanceState == null) {
            viewModel.loadFirstPage()
        }
    }


    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner) { dataState ->
            if (dataState != null) {
                handlePagination(dataState)
                stateChangeListener.onDataStateChange(dataState)
            }
        }
        viewModel.viewState.observe(viewLifecycleOwner) { blogViewState ->
            Log.d(
                TAG,
                "subscribeObservers: ViewState blog list: ${blogViewState.blogFields.blogList}"
            )
            recyclerAdapter.submitList(
                blogViewState.blogFields.blogList,
                blogViewState.blogFields.isQueryExhausted
            )
        }
    }

    private fun handlePagination(dataState: DataState<BlogViewState>) {
        //handle incoming data from DataState
        dataState.data?.let { data ->
            data.data?.let { event ->
                event.getContentIfNotHandled()?.let {
                    viewModel.handleIncomingBlogListData(it)
                }
            }
        }
        // check for pagination end (ex:No more result)
        // must do this b/c server will return ApiErrorResponse if page is not valid
        // -> Meaning there no more data!
        dataState.error?.let { event ->
            event.peekContent().let { stateError ->
                stateError.response.message?.let {
                    if (ErrorHandling.isPaginationDone(it)) {
                        //handle the error message event so it doesn't play on ui
                        event.getContentIfNotHandled()
                        //set query exhausted to update recyclerView with
                        //(NO more result...) list item
                        viewModel.setQueryExhausted(true)
                    }
                }

            }
        }


    }

    private fun initSearchView(menu: Menu) {
        activity?.apply {
            val searchManager: SearchManager = getSystemService(SEARCH_SERVICE) as SearchManager
            searchView = menu.findItem(R.id.action_search).actionView as SearchView
            searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
            searchView.maxWidth = Integer.MAX_VALUE
            searchView.setIconifiedByDefault(true)
            searchView.isSubmitButtonEnabled = true
        }

        // ENTER ON COMPUTER KEYBOARD OR ARROW ON VIRTUAL KEYBOARD
        val searchPlate = searchView.findViewById(R.id.search_src_text) as EditText
        searchPlate.setOnEditorActionListener { v, actionId, event ->

            if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED
                || actionId == EditorInfo.IME_ACTION_SEARCH
            ) {
                val searchQuery = v.text.toString()
                Log.e(TAG, "SearchView: (keyboard or arrow) executing search...: ${searchQuery}")
                viewModel.setQuery(searchQuery).let {
                    onBlogSearchOrFilter()
                }
            }
            true
        }

        // SEARCH BUTTON CLICKED (in toolbar)
        val searchButton = searchView.findViewById(R.id.search_go_btn) as View
        searchButton.setOnClickListener {
            val searchQuery = searchPlate.text.toString()
            Log.e(TAG, "SearchView: (button) executing search...: ${searchQuery}")
            viewModel.setQuery(searchQuery).let {
                onBlogSearchOrFilter()
            }

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
                        viewModel.nextPage()
                    }
                }
            })
            adapter = recyclerAdapter
        }
    }

    private fun onBlogSearchOrFilter() {
        viewModel.loadFirstPage().let {
            resetUI()
        }
    }

    private fun resetUI() {
        blog_post_recyclerview.smoothScrollToPosition(0)
        stateChangeListener.hideSoftKeyboard()
        focusable_view.requestFocus()
    }

    override fun onItemSelected(position: Int, item: BlogPost) {
        viewModel.setBlogPost(item)
        findNavController().navigate(R.id.action_blogFragment_to_viewBlogFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        blog_post_recyclerview.adapter = null
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)
        initSearchView(menu)
    }

    override fun onRefresh() {
        onBlogSearchOrFilter()
        swipe_refresh.isRefreshing=false
    }
}






















