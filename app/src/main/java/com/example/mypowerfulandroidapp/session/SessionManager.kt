package com.example.mypowerfulandroidapp.session

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mypowerfulandroidapp.models.AuthToken
import com.example.mypowerfulandroidapp.persistence.AuthTokenDao
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager
@Inject
constructor(
    val authTokenDao: AuthTokenDao,
    val application: Application
) {
    private  val TAG = "SessionManager"
    val _cachedToken: MutableLiveData<AuthToken> = MutableLiveData()

    val cachedToken: LiveData<AuthToken>
        get() = _cachedToken

    fun login(authToken: AuthToken) {
        setValue(authToken)
    }
    fun logout(){
        Log.d(TAG, "logout: loging out...")
        GlobalScope.launch(IO){
            var errorMessage:String?=null
            try {
                cachedToken.value!!.account_pk?.let {
                    authTokenDao.nullifyToken(it)
                }
            }catch (e:CancellationException){
                Log.e(TAG, "logout: ${e.message}")
                errorMessage=e.message
            }catch (e:Exception){
                Log.e(TAG, "logout: ${e.message}" )
                errorMessage=errorMessage+"\n"+e.message
            }finally {
                errorMessage?.let {
                    Log.e(TAG, "logout: $errorMessage")
                }
                Log.d(TAG, "logout: finally...")
                setValue(null)
            }

        }
    }
    private fun setValue(authToken: AuthToken?) {
        Log.d(TAG, "setValue: login ...")
        GlobalScope.launch(Main) {
            if (_cachedToken.value != authToken) {
                _cachedToken.value = authToken
            }
        }
    }
    fun isConnectedToTheInternet():Boolean{
        val cm=application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        try {
            return cm.activeNetworkInfo.isConnected
        }catch (e:Exception){
            Log.e(TAG, "isConnectedToTheInternet: ${e.message}" )
        }
        return false
    }
}