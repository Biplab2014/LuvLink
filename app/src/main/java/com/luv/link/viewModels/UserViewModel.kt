package com.luv.link.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luv.link.models.User
import com.luv.link.repositories.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named
import kotlinx.coroutines.launch

@HiltViewModel
class UserViewModel
@Inject
constructor(
    // @Named("Ktor") private val networkRepository: NetworkRepository
    @Named("Retrofit") private val networkRepository: NetworkRepository
) : ViewModel() {
    private val _user = MutableLiveData<User>()
    val user: LiveData<User> get() = _user

    fun loadUser(userId: Int) {
        viewModelScope.launch {
            try {
                _user.value = networkRepository.getUser(userId)
            } catch (e: Exception) {
                // Log.e("UserViewModel", "Error fetching user", e)
            }
        }
    }
}
