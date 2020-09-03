package com.example.mypowerfulandroidapp.ui.main.account

import androidx.lifecycle.LiveData
import com.example.mypowerfulandroidapp.models.AccountProperties
import com.example.mypowerfulandroidapp.repository.main.AccountRepository
import com.example.mypowerfulandroidapp.session.SessionManager
import com.example.mypowerfulandroidapp.ui.BaseViewModel
import com.example.mypowerfulandroidapp.ui.DataState
import com.example.mypowerfulandroidapp.ui.main.account.state.AccountStateEvent
import com.example.mypowerfulandroidapp.ui.main.account.state.AccountViewState
import com.example.mypowerfulandroidapp.util.AbsentLiveData
import javax.inject.Inject

class AccountViewModel
@Inject
constructor(
    val sessionManager: SessionManager,
    val accountRepository: AccountRepository
) : BaseViewModel<AccountStateEvent, AccountViewState>() {


    override fun handleStateEvent(stateEvent: AccountStateEvent): LiveData<DataState<AccountViewState>> {
        return when (stateEvent) {
            is AccountStateEvent.GetAccountPropertiesEvent -> {
                AbsentLiveData.create()
            }
            is AccountStateEvent.UpdateAccountPropertiesEvent -> {
                AbsentLiveData.create()
            }
            is AccountStateEvent.ChangePasswordEvent -> {
                AbsentLiveData.create()
            }
            is AccountStateEvent.None -> {
                AbsentLiveData.create()
            }

        }
    }

    override fun initNewViewState(): AccountViewState {
        return AccountViewState()
    }

    fun setAccountPropertiesDao(accountProperties: AccountProperties) {
        val update = getCurrentViewStateOrNew()
        if (update.accountProperties == accountProperties) {
            return
        }
        update.accountProperties = accountProperties
        _ViewState.value = update
    }

    fun logout() {
        sessionManager.logout()
    }
}