import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nokaori.genshinaibuilder.domain.model.UserWeapon
import com.nokaori.genshinaibuilder.domain.repository.WeaponRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WeaponViewModel(
    private val weaponRepository: WeaponRepository
) : ViewModel() {

    val userWeapons: StateFlow<List<UserWeapon>> = weaponRepository.getUserWeapons()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addRandomUserWeapon() {
        viewModelScope.launch {
            val allWeapons = weaponRepository.getAllWeapons().first()

            if (allWeapons.isNotEmpty()) {
                val randomBaseWeapon = allWeapons.random()
                val newUserWeapon = UserWeapon(
                    id = 0,
                    weapon = randomBaseWeapon,
                    level = 90,
                    ascension = 6,
                    refinement = 1
                )
                weaponRepository.addUserWeapon(newUserWeapon)
            }
        }
    }
}