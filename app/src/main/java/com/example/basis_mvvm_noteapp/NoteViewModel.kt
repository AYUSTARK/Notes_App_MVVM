package com.example.basis_mvvm_noteapp

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.basis_mvvm_noteapp.db.Note
import com.example.basis_mvvm_noteapp.db.NoteRepository
import kotlinx.coroutines.launch

//To observe the bind-able text in viewModel,so this should implement the observer interface
class NoteViewModel(private val repository: NoteRepository) : ViewModel(),Observable{
    val notes=repository.notes // this have list of all the notes

    //Binding with the textfield
    @Bindable
    val inputTitle= MutableLiveData<String>()
    @Bindable
    val inputNoteDescription= MutableLiveData<String>()

    //Binding the button text
    @Bindable
    val saveOrUpdateButtonText=MutableLiveData<String>()
    @Bindable
    val clearAllOrDeleteButtonText=MutableLiveData<String>()

    //initial initialization to set the value on first start up
    init {
        saveOrUpdateButtonText.value="Save"
        clearAllOrDeleteButtonText.value="Clear All"
    }

    fun saveOrUpdate(){
        val title=inputTitle.value!!
        val description=inputNoteDescription.value!!
        insert(Note(0,title,description)) //the id wouldn't matter since we had made it auto incremental
        //after setting the value changing it to null
        inputTitle.value=null
        inputNoteDescription.value=null

    }
    fun clearAllOrDelete(){
        clearAll()
    }
    //to insert a note
    fun insert(note:Note){
        // now call the insert function of repository passing the note instance
        // we should make this function call from a background thread
        viewModelScope.launch {
            repository.insert(note)
        }
    }

    fun update(note:Note){
        viewModelScope.launch {
            repository.update(note)
        }
    }

    fun delete(note:Note){
        viewModelScope.launch {
            repository.delete(note)
        }
    }
    fun clearAll(){
        viewModelScope.launch {
            repository.deleteAll()
        }
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }
}