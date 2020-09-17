package com.example.mypowerfulandroidapp.ui.main.blog

import android.opengl.Visibility
import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.mypowerfulandroidapp.R
import com.example.mypowerfulandroidapp.models.BlogPost
import com.example.mypowerfulandroidapp.ui.DataStateChangeListener
import com.example.mypowerfulandroidapp.ui.main.blog.state.BlogStateEvent
import com.example.mypowerfulandroidapp.ui.main.blog.viewmodels.isAuthorOfBlogPost
import com.example.mypowerfulandroidapp.ui.main.blog.viewmodels.setIsAuthorOfBlogPost
import com.example.mypowerfulandroidapp.util.DateUtils
import com.example.mypowerfulandroidapp.util.SuccessHandling.Companion.RESPONSE_HAS_PERMISSION_TO_EDIT
import kotlinx.android.synthetic.main.fragment_view_blog.*


class ViewBlogFragment : BaseBlogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_blog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        checkIsAuthorOfBlogPost()
        subscribeObservers()
        delete_button.setOnClickListener {
            deleteBlogPost()
        }
    }

    private fun deleteBlogPost() {
        viewModel.setStatEvent(
            BlogStateEvent.DeleteBlogPostEvent()
        )
    }

    private fun checkIsAuthorOfBlogPost() {
        viewModel.setIsAuthorOfBlogPost(false)
        viewModel.setStatEvent(BlogStateEvent.CheckAuthorOfBlogPost())
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            stateChangeListener.onDataStateChange(dataState)
            dataState.data?.let { data ->
                data.data?.let {
                    it.getContentIfNotHandled()?.let { viewState ->
                        viewModel.setIsAuthorOfBlogPost(
                            viewState.viewBlogFields.isAuthorOfBlogPost
                        )
                    }
                }
            }
        })
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState.viewBlogFields.blogPost?.let { blogPost ->
                setBlogProperties(blogPost)
            }
            if (viewState.viewBlogFields.isAuthorOfBlogPost) {
                adaptViewToEditMode()
            }
        })
    }

    private fun adaptViewToEditMode() {
        activity?.invalidateOptionsMenu()
        delete_button.visibility = View.VISIBLE
    }

    private fun setBlogProperties(blogPost: BlogPost) {
        requestManager
            .load(blogPost.image)
            .into(blog_image)
        blog_title.text = blogPost.title
        blog_author.text = blogPost.username
        blog_body.text = blogPost.body
        blog_update_date.text = DateUtils.convertLongToStringDate(
            blogPost.date_updated
        )
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (viewModel.isAuthorOfBlogPost()) {
            inflater.inflate(R.menu.edit_view_menu, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (viewModel.isAuthorOfBlogPost()) {
            when (item.itemId) {
                R.id.edit -> {
                    findNavController().navigate(R.id.action_viewBlogFragment_to_updateBlogFragment)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}