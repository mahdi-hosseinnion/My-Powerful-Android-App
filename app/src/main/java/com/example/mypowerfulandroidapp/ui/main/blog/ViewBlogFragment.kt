package com.example.mypowerfulandroidapp.ui.main.blog

import android.os.Bundle
import android.view.*
import androidx.navigation.fragment.findNavController
import com.example.mypowerfulandroidapp.R


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
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        //TODO("check if user is the writer of this blog")
        val isAutherOfBlog = true
        if (isAutherOfBlog) {
            inflater.inflate(R.menu.edit_view_menu, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //TODO("check if user is the writer of this blog")
        val isAutherOfBlog = true
        if (isAutherOfBlog) {
            when (item.itemId) {
                R.id.edit -> {
                    findNavController().navigate(R.id.action_viewBlogFragment_to_updateBlogFragment)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}