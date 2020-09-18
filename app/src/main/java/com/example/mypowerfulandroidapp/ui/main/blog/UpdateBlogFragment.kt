package com.example.mypowerfulandroidapp.ui.main.blog

import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import com.example.mypowerfulandroidapp.R
import com.example.mypowerfulandroidapp.ui.main.blog.state.BlogStateEvent
import kotlinx.android.synthetic.main.fragment_update_blog.*
import kotlinx.android.synthetic.main.layout_blog_filter.*
import okhttp3.MultipartBody

class UpdateBlogFragment : BaseBlogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_blog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        subscribeObservers()
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            stateChangeListener.onDataStateChange(dataState)
            dataState?.data?.let { data ->
                data.data?.getContentIfNotHandled()?.let { blogViewState ->
                    //if this is not null, the blogPost was updated
                    blogViewState.viewBlogFields.blogPost?.let {
                        //TODO("onBlogPostUpdated")
                    }
                }
            }
        })
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState.updateBlogFields.let {
                setBlogProperties(
                    it.updateBlogTitle,
                    it.updateBlogBody,
                    it.updateBlogImage
                )
            }
        })
    }

    private fun setBlogProperties(
        updateBlogTitle: String?,
        updateBlogBody: String?,
        updateBlogImage: Uri?
    ) {
        requestManager
            .load(updateBlogImage)
            .into(blog_image)

        updateBlogTitle?.let { blog_title.setText(it) }
        updateBlogBody?.let { blog_body.setText(it) }
    }

    private fun saveChanges() {
        val image: MultipartBody.Part? = null
        viewModel.setStatEvent(
            BlogStateEvent.UpdateBlogPostEvent(
                blog_title.text.toString(),
                blog_body.text.toString(),
                image
            )
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.update_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> {
                saveChanges()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
