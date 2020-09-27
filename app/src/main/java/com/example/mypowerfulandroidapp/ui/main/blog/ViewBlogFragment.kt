package com.example.mypowerfulandroidapp.ui.main.blog

import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.mypowerfulandroidapp.R
import com.example.mypowerfulandroidapp.models.BlogPost
import com.example.mypowerfulandroidapp.ui.*
import com.example.mypowerfulandroidapp.ui.main.blog.state.BlogStateEvent
import com.example.mypowerfulandroidapp.ui.main.blog.viewmodels.*
import com.example.mypowerfulandroidapp.util.DateUtils
import com.example.mypowerfulandroidapp.util.SuccessHandling.Companion.RESPONSE_HAS_PERMISSION_TO_EDIT
import com.example.mypowerfulandroidapp.util.SuccessHandling.Companion.SUCCESS_BLOG_DELETED
import kotlinx.android.synthetic.main.fragment_view_blog.*
import java.lang.Exception


class ViewBlogFragment : BaseBlogFragment() {

    private val TAG = "ViewBlogFragment"
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
            confirmDeleteRequest()
        }
    }

    private fun confirmDeleteRequest() {
        val callback = object : AreYouSureCallback {
            override fun proceed() {
                deleteBlogPost()
            }

            override fun cancel() {
                //ignore
            }
        }
        uiCommunicationListener.onUiMessageReceived(
            UIMessage(
                getString(R.string.are_you_sure),
                UiMessageType.AreYouSureDialog(callback)
            )
        )
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
            if (dataState != null) {
                dataState.data?.let { data ->
                    data.data?.let {
                        it.getContentIfNotHandled()?.let { viewState ->
                            viewModel.setIsAuthorOfBlogPost(
                                viewState.viewBlogFields.isAuthorOfBlogPost
                            )
                        }
                    }
                    data.response?.peekContent()?.let { response ->
                        if (response.message == SUCCESS_BLOG_DELETED) {
                            viewModel.removeDeletedBlogPost()
                            findNavController().popBackStack()
                        }
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
        mainDependencyProvider.getGlideRequestManager()
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
                    navUpdateBlogFragment()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun navUpdateBlogFragment() {
        try {
            viewModel.setUpdatedBlogFields(
                viewModel.getBlogPost().title,
                viewModel.getBlogPost().body,
                viewModel.getBlogPost().image.toUri()
            )
            findNavController().navigate(R.id.action_viewBlogFragment_to_updateBlogFragment)
        } catch (e: Exception) {
            Log.e(TAG, "navUpdateBlogFragment: ${e.message}", e)
        }

    }
}