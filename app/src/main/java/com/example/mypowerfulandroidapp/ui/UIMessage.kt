package com.example.mypowerfulandroidapp.ui

data class UIMessage(
    val message: String,
    val uiMessageType: UiMessageType
)

sealed class UiMessageType {
    class Toast() : UiMessageType()
    class Dialog() : UiMessageType()
    class AreYouSureDialog(
        val callback: AreYouSureCallback
    ) : UiMessageType()

    class None() : UiMessageType()
}
