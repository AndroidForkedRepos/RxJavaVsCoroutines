package it.codingjam.coroutines

import androidx.lifecycle.MutableLiveData
import it.codingjam.common.UserStats
import kotlinx.coroutines.launch

class ViewModel1_1(private val service: StackOverflowServiceCoroutines) : ViewModel() {

    val state = MutableLiveData<String>()

    fun load() {
        viewModelScope.launch {
            try {
                updateUi("loading users")
                val users = service.getTopUsers()
                updateUi("loading badges")
                val firstUser = users.first()
                val badges = service.getBadges(firstUser.id)
                val user = UserStats(firstUser, badges)
                updateUi(user)
            } catch (e: Exception) {
                updateUi(e)
            }
        }
    }

    private fun updateUi(s: Any) {
        state.value = s.toString()
    }
}