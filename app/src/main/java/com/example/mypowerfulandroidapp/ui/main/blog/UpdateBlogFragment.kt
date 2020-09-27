package com.example.mypowerfulandroidapp.ui.main.blog

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.mypowerfulandroidapp.R
import com.example.mypowerfulandroidapp.ui.*
import com.example.mypowerfulandroidapp.ui.main.blog.state.BlogStateEvent
import com.example.mypowerfulandroidapp.ui.main.blog.viewmodels.getBlogPost
import com.example.mypowerfulandroidapp.ui.main.blog.viewmodels.getUpdatedBlogUri
import com.example.mypowerfulandroidapp.ui.main.blog.viewmodels.onBlogPostUpdateSuccess
import com.example.mypowerfulandroidapp.ui.main.blog.viewmodels.setUpdatedBlogFields
import com.example.mypowerfulandroidapp.ui.main.create_blog.state.CreateBlogStateEvent
import com.example.mypowerfulandroidapp.util.Constants
import com.example.mypowerfulandroidapp.util.ErrorHandling
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_update_blog.*
import kotlinx.android.synthetic.main.fragment_update_blog.blog_body
import kotlinx.android.synthetic.main.fragment_update_blog.blog_image
import kotlinx.android.synthetic.main.fragment_update_blog.blog_title

import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class UpdateBlogFragment : BaseBlogFragment() {
    private  val TAG = "UpdateBlogFragment"

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
        blog_image.setOnClickListener {
            if (stateChangeListener.isStoragePermissionGranted()) {
                pickFromGallery()
            }
        }
        update_textview.setOnClickListener {
            if (stateChangeListener.isStoragePermissionGranted()) {
                pickFromGallery()
            }
        }
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            stateChangeListener.onDataStateChange(dataState)
            dataState?.data?.let { data ->
                data.data?.getContentIfNotHandled()?.let { blogViewState ->
                    //if this is not null, the blogPost was updated
                    blogViewState.viewBlogFields.blogPost?.let {
                        viewModel.onBlogPostUpdateSuccess(it).let {
                            findNavController().popBackStack()
                        }
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
        mainDependencyProvider.getGlideRequestManager()
            .load(updateBlogImage)
            .into(blog_image)

        updateBlogTitle?.let { blog_title.setText(it) }
        updateBlogBody?.let { blog_body.setText(it) }
    }

    private fun saveChanges() {
        var multiPartBody: MultipartBody.Part? = null

        viewModel.getUpdatedBlogUri()?.let { imageUri ->
            imageUri.path?.let { filePath ->
                val imageFile = File(filePath)
                Log.d(TAG, "publishBlog: imageFile: $imageFile")
                var requestBody = RequestBody.create(
                    MediaType.parse("image/*"),
                    imageFile
                )
                multiPartBody = MultipartBody.Part.createFormData(
                    "image",
                    imageFile.name,
                    requestBody
                )
            }
        }
        multiPartBody?.let { multiPartBody ->
            viewModel.setStatEvent(
                BlogStateEvent.UpdateBlogPostEvent(
                    blog_title.text.toString(),
                    blog_body.text.toString(),
                    multiPartBody
                )
            )
        } ?: showErrorMessage(ErrorHandling.ERROR_MUST_SELECT_IMAGE)

        stateChangeListener.hideSoftKeyboard()
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

    override fun onPause() {
        super.onPause()
        viewModel.setUpdatedBlogFields(
            blog_title.text.toString(),
            blog_body.text.toString(),
            null
        )
    }
    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        val mimeType = arrayOf("image/jpeg", "image/png", "image/jpg")
        //TODO("SOLVE THIS BUG THAT DOES NOT WORK ON API UNDER 19)
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeType)
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(intent, Constants.GALLERY_REQUEST_CODE)
    }

    private fun launchImageCrop(uri: Uri) {
        context?.let {
            CropImage
                .activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(it, this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Constants.GALLERY_REQUEST_CODE -> {
                    data?.data?.let {
                        launchImageCrop(it)
                    } ?: showErrorMessage(ErrorHandling.ERROR_SOMETHING_WRONG_WITH_IMAGE)
                }
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    Log.d(TAG, "onActivityResult: CROPED")
                    val result = CropImage.getActivityResult(data)
                    val resultUri = result.uri
                    Log.d(TAG, "onActivityResult: CROPED resultUri:$resultUri")
                    viewModel.setUpdatedBlogFields(
                        null, null, resultUri
                    )
                }
                CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE -> {
                    showErrorMessage(ErrorHandling.ERROR_SOMETHING_WRONG_WITH_IMAGE)
                }
            }
        }
    }

    private fun showErrorMessage(message: String) {
        stateChangeListener.onDataStateChange(
            DataState(
                data = Data(Event.dataEvent(null), null),
                error = Event(
                    StateError(
                        Response(
                            message = message,
                            responseType = ResponseType.Dialog()
                        )
                    )
                ),
                loading = Loading(false)
            )
        )
    }
}
