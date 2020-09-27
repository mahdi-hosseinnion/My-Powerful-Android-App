package com.example.mypowerfulandroidapp.ui.main.create_blog

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import androidx.lifecycle.Observer
import com.example.mypowerfulandroidapp.R
import com.example.mypowerfulandroidapp.ui.*
import com.example.mypowerfulandroidapp.ui.main.create_blog.state.CreateBlogStateEvent
import com.example.mypowerfulandroidapp.ui.main.create_blog.state.CreateBlogViewState
import com.example.mypowerfulandroidapp.util.Constants
import com.example.mypowerfulandroidapp.util.Constants.Companion.GALLERY_REQUEST_CODE
import com.example.mypowerfulandroidapp.util.ErrorHandling.Companion.ERROR_MUST_SELECT_IMAGE
import com.example.mypowerfulandroidapp.util.ErrorHandling.Companion.ERROR_SOMETHING_WRONG_WITH_IMAGE
import com.example.mypowerfulandroidapp.util.SuccessHandling.Companion.SUCCESS_BLOG_CREATED
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_create_blog.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class CreateBlogFragment : BaseCreateBlogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_blog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
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
        subscribeObservers()
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            stateChangeListener.onDataStateChange(dataState)
            dataState?.let { dataState ->
                dataState.data?.response?.let { event ->
                    event.peekContent().message?.let { message ->
                        if (message == SUCCESS_BLOG_CREATED) {
                            viewModel.clearNewBlogFields()
                        }
                    }
                }
            }
        })
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState?.let { viewState ->
                viewState.blogFields.let {
                    setBlogProperties(it.title, it.body, it.image)
                }
            }
        })
    }

    private fun setBlogProperties(title: String?, body: String?, image: Uri?) {
        image?.let {
            mainDependencyProvider.getGlideRequestManager()
                .load(it)
                .into(blog_image)
        } ?: setDefaultImage()
        title?.let { blog_title.setText(it) }
        body?.let { blog_body.setText(it) }
    }

    private fun setDefaultImage() {
        mainDependencyProvider.getGlideRequestManager()
            .load(R.drawable.default_image)
            .into(blog_image)
    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        val mimeType = arrayOf("image/jpeg", "image/png", "image/jpg")
        //TODO("SOLVE THIS BUG THAT DOES NOT WORK ON API UNDER 19)
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeType)
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
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
                GALLERY_REQUEST_CODE -> {
                    data?.data?.let {
                        launchImageCrop(it)
                    } ?: showErrorMessage(ERROR_SOMETHING_WRONG_WITH_IMAGE)
                }
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    Log.d(TAG, "onActivityResult: CROPED")
                    val result = CropImage.getActivityResult(data)
                    val resultUri = result.uri
                    Log.d(TAG, "onActivityResult: CROPED resultUri:$resultUri")
                    viewModel.setNewBlogFields(
                        null, null, resultUri
                    )
                }
                CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE -> {
                    showErrorMessage(ERROR_SOMETHING_WRONG_WITH_IMAGE)
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

    override fun onPause() {
        super.onPause()
        viewModel.setNewBlogFields(
            title = blog_title.text.toString(),
            body = blog_body.text.toString(),
            image = null
        )
    }

    fun publishBlog() {
        var multiPartBody: MultipartBody.Part? = null

        viewModel.getImageUri()?.let { imageUri ->
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
                CreateBlogStateEvent.CreateNewBlogPostEvent(
                    blog_title.text.toString(),
                    blog_body.text.toString(),
                    multiPartBody
                )
            )
        } ?: showErrorMessage(ERROR_MUST_SELECT_IMAGE)

        stateChangeListener.hideSoftKeyboard()

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.publish_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.publish) {
            val callback = object : AreYouSureCallback {
                override fun proceed() {
                    publishBlog()
                }

                override fun cancel() {
                    //ignore
                }
            }
            uiCommunicationListener.onUiMessageReceived(
                UIMessage(
                    getString(R.string.are_you_sure_publish),
                    UiMessageType.AreYouSureDialog(callback)
                )
            )
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}