package com.example.basis_mvvm_noteapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.basis_mvvm_noteapp.db.NoteRepository
import java.lang.IllegalArgumentException

//Since our ViewModel has a constructor parameter,so we need to create a view model factory class
class NoteViewModelFactory (private val repository: NoteRepository):ViewModelProvider.Factory{
   //these are boiler plate code used for every view model factory
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)){
            return NoteViewModel(repository) as T

        }
       throw IllegalArgumentException("Unknown View Model Class")
    }
}