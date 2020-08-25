package com.example.mypowerfulandroidapp.ui.auth

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.navigation.fragment.findNavController
import com.example.mypowerfulandroidapp.R
import com.example.mypowerfulandroidapp.ui.DataState
import com.example.mypowerfulandroidapp.ui.DataStateChangeListener
import com.example.mypowerfulandroidapp.ui.Response
import com.example.mypowerfulandroidapp.ui.ResponseType
import com.example.mypowerfulandroidapp.util.Constants
import kotlinx.android.synthetic.main.fragment_forgot_password.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.ClassCastException

class ForgotPasswordFragment : BaseAuthFragment() {
    private val TAG = "ForgotPasswordFragment"
    private lateinit var mWebView: WebView
    lateinit var onDataStateChangeListener: DataStateChangeListener
    private val wbInteractionCallBack: WebAppInterface.OnWebInteractionCallBack =
        object : WebAppInterface.OnWebInteractionCallBack {
            override fun onSuccess(email: String) {
                Log.d(TAG, "onSuccess: reset password link have been send for email: $email")
                onPasswordResetListSent()
            }

            override fun onError(errorMsg: String) {
                Log.e(TAG, "onError: $errorMsg")
                onDataStateChangeListener.onDataStateChange(
                    DataState.error<Any>(
                        response = Response(
                            message = errorMsg,
                            responseType = ResponseType.Dialog()
                        )
                    )
                )
            }

            override fun onLoading(isLoading: Boolean) {
                GlobalScope.launch(Main) {
                    onDataStateChangeListener.onDataStateChange(
                        DataState.loading(isLoading, null)
                    )
                }
            }
        }

    private fun onPasswordResetListSent() {
        GlobalScope.launch(Main) {
            parent_view.removeView(webview)
            webview.destroy()
            val animation = TranslateAnimation(
                password_reset_done_container.width.toFloat()
                , 0f, 0f, 0f
            )
            animation.duration = 500
            password_reset_done_container.animation = animation
            password_reset_done_container.visibility = View.VISIBLE

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mWebView = view.findViewById(R.id.webview)
        loadWebView()
        return_to_launcher_fragment.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    @SuppressLint("SetJavaScriptEnable")
    fun loadWebView() {
        onDataStateChangeListener.onDataStateChange(
            DataState.loading(true, null)
        )
        webview.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                onDataStateChangeListener.onDataStateChange(
                    DataState.loading(false, null)
                )
            }
        }
        webview.loadUrl(Constants.FORGOT_PASSWORD_URL)
        webview.settings.javaScriptEnabled = true
        webview.addJavascriptInterface(
            WebAppInterface(wbInteractionCallBack),
            "AndroidTextListener"
        )
    }

    class WebAppInterface
    constructor(
        private val callBack: OnWebInteractionCallBack
    ) {
        @JavascriptInterface
        fun onSuccess(email: String) {
            callBack.onSuccess(email)
        }
        @JavascriptInterface
        fun onError(errorMessage: String) {
            callBack.onError(errorMessage)
        }
        @JavascriptInterface
        fun onLoading(isLoading: Boolean) {
            callBack.onLoading(isLoading)
        }

        interface OnWebInteractionCallBack {
            fun onSuccess(email: String)
            fun onError(errorMsg: String)
            fun onLoading(isLoading: Boolean)
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            onDataStateChangeListener = context as DataStateChangeListener
        } catch (e: ClassCastException) {
            Log.e(TAG, "onAttach: e \n $context should implement DataStateChangeListener")
        }
    }
}