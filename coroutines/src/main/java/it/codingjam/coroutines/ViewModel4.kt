package it.codingjam.coroutines

import androidx.lifecycle.MutableLiveData
import it.codingjam.common.User
import it.codingjam.common.UserStats
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class ViewModel4(private val service: StackOverflowServiceCoroutines) : ViewModel() {

    val state = MutableLiveData<String>()

    fun load() {
        viewModelScope.launch {
            try {
                val users = service.getTopUsers()
                val userStats: List<UserStats> =
                        users.take(5)
                                .map { user -> async { userDetail(user) } }
                                .map { it.await() }
                updateUi(userStats)
            } catch (e: Exception) {
                updateUi(e)
            }
        }
    }

    suspend fun userDetailSync(it: User): UserStats {
        val badges = service.getBadges(it.id)
        val tags = service.getTags(it.id)
        return UserStats(it, badges, tags)
    }

    suspend fun userDetail(it: User): UserStats = coroutineScope {
        val badges = async { service.getBadges(it.id) }
        val tags = service.getTags(it.id)
        UserStats(it, badges.await(), tags)
    }

    private fun updateUi(s: Any) {
        state.value = s.toString()
    }
}