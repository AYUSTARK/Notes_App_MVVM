package com.example.basis_mvvm_noteapp

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.basis_mvvm_noteapp.db.Note
import com.example.basis_mvvm_noteapp.db.NoteRepository
import kotlinx.coroutines.launch

//To observe the bind-able text in viewModel,so this should implement the observer interface
class NoteViewModel(private val repository: NoteRepository) : ViewModel(), Observable {
    val notes = repository.notes // this have list of all the notes

    //Binding with the textfield
    @Bindable
    val inputTitle = MutableLiveData<String>()

    @Bindable
    val inputNoteDescription = MutableLiveData<String>()

    //variables to implement update an delete functionality
    private var isUpdateOrDelete = false
    private lateinit var noteToUpdateOrDelete: Note

    //Binding the button text
    @Bindable
    val saveOrUpdateButtonText = MutableLiveData<String>()

    @Bindable
    val clearAllOrDeleteButtonText = MutableLiveData<String>()


    /*
    For Status Message, using for string message but we will
    be using Event class as a Wrapper

    statusMessage is MutableLiveData. ie we can edits its value and can assign
    different values to it but it is private variable. Hence we cannot access
    to it from outside classes. This is the correct coding practice for data security
    and stability
     */
    private val statusMessage = MutableLiveData<Event<String>>()

    /*
    getter for this LiveData,outside classes can just observe the LiveData as we are not
     going to modify this hence we are not going to define this as MutableLiveData
     */
    val message: LiveData<Event<String>>
        get() = statusMessage


    //initial initialization to set the value on first start up
    init {
        saveOrUpdateButtonText.value = "Save"
        clearAllOrDeleteButtonText.value = "Clear All"
    }

    fun saveOrUpdate() {
        //Adding validations on the inputted text
        if(inputTitle.value==null){
            statusMessage.value = Event("Please enter Note Title")
        }else if(inputNoteDescription.value==null){
            statusMessage.value = Event("Please enter Note Description")
        }else {
            //boolean to check if this in update and delete state
            if (isUpdateOrDelete) {
                //set the new updated value of notes object
                noteToUpdateOrDelete.title = inputTitle.value!!
                noteToUpdateOrDelete.description = inputNoteDescription.value!!
                update(noteToUpdateOrDelete)
            } else {

                val title = inputTitle.value!!
                val description = inputNoteDescription.value!!
                insert(
                    Note(
                        0,
                        title,
                        description
                    )
                ) //the id wouldn't matter since we had made it auto incremental
                //after setting the value changing it to null
                inputTitle.value = null
                inputNoteDescription.value = null
            }
        }

    }

    fun clearAllOrDelete() {
        if (isUpdateOrDelete) {
            delete(noteToUpdateOrDelete)
        } else {
            clearAll()
        }
    }

    //to insert a note
    private fun insert(note: Note) {
        // now call the insert function of repository passing the note instance
        // we should make this function call from a background thread
        viewModelScope.launch {
            //return row id for more confirmations
            val newRowId = repository.insert(note)
            if (newRowId > -1) { //return -1 on fail
                //change the mutable live data with success message
                // Not necessary but for learning purpose important
                statusMessage.value = Event("Note Inserted Successfully IDno: $newRowId")
            } else {
                statusMessage.value = Event("Error Obscured in Inserting ")
            }


        }
    }

    // save btn-> update button and clear all btn -> delete btn
    fun initUpdateAndDelete(note: Note) {
        //display the selected tile and description in the testInputField
        inputTitle.value = note.title
        inputNoteDescription.value = note.description

        //update the bool and give the note instance
        isUpdateOrDelete = true
        noteToUpdateOrDelete = note

        //Change display value of btns
        saveOrUpdateButtonText.value = "Update"
        clearAllOrDeleteButtonText.value = "Delete"

        //implement methods

    }

    private fun update(note: Note) {
        viewModelScope.launch {
            val numberOfRowsUpdated = repository.update(note)
            if (numberOfRowsUpdated > 0) {
                //after update changing back ot normal conditions
                inputTitle.value = null
                inputNoteDescription.value = null
                isUpdateOrDelete = false
                noteToUpdateOrDelete = note
                saveOrUpdateButtonText.value = "Save"
                clearAllOrDeleteButtonText.value = "Clear All"
                //change the mutable live data with success message
                statusMessage.value = Event(" $numberOfRowsUpdated rows of Updated Successfully")
            } else {
                statusMessage.value = Event("Error Occurred during Update")
            }
        }
    }

    private fun delete(note: Note) {
        viewModelScope.launch {
            val noOfRowsDeleted = repository.delete(note)

            if (noOfRowsDeleted > 0) {
                //after delete changing back ot normal conditions
                inputTitle.value = null
                inputNoteDescription.value = null
                isUpdateOrDelete = false
                noteToUpdateOrDelete = note
                saveOrUpdateButtonText.value = "Save"
                clearAllOrDeleteButtonText.value = "Clear All"
                //change the mutable live data with success message
                statusMessage.value = Event("$noOfRowsDeleted rows Deleted Successfully")
            } else {
                statusMessage.value = Event("Error Occurred during deleting")
            }
        }
    }

    private fun clearAll() {
        viewModelScope.launch {
            val noOfRowsDeleted = repository.deleteAll()
            if (noOfRowsDeleted > 0) {
                //change the mutable live data with success message
                statusMessage.value = Event("$noOfRowsDeleted rows Deleted Successfully")
            } else {
                statusMessage.value = Event("Error Occurred during deleting")
            }
        }
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }
}