import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.d3if3082.checknote.model.Notes

class MainViewModel : ViewModel() {
    private val _data = MutableLiveData<List<Notes>>()
    val data: LiveData<List<Notes>> = _data

//    init {
//        _data.value = getDataDummy()
//    }

    private fun getDataDummy(): List<Notes> {
        val data = mutableListOf<Notes>()
        for (i in 1 until 2) {
            data.add(
                Notes(
                    "This is Title",
                    "This is description, lorem ipsum"
                )
            )
        }
        return data
    }

    fun addNote(note: Notes) {
        val currentData = _data.value?.toMutableList() ?: mutableListOf()
        currentData.add(note)
        _data.value = currentData
    }

    fun isDataEmpty(): Boolean {
        return _data.value.isNullOrEmpty()
    }
}