package com.example.mypowerfulandroidapp.util

class Constants {

    companion object {

        const val BASE_URL = "https://open-api.xyz/api/"
        const val FORGOT_PASSWORD_URL = "https://open-api.xyz/password_reset/"
        const val PAGINATION_PAGE_SIZE = 10

        const val NETWORK_TIMEOUT = 5000L
        const val TESTING_NETWORK_DELAY = 0L // fake network delay for testing
        const val TESTING_CACHE_DELAY = 0L // fake cache delay for testing

        const val GALLERY_REQUEST_CODE:Int=201
        const val PERMISSION_REQUEST_READ_STORAGE:Int=301
        const val CROP_IMAGE_INTENT_MODE:Int=401
    }
}