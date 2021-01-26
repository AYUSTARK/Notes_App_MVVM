 package com.example.basis_mvvm_noteapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.basis_mvvm_noteapp.databinding.ActivityMainBinding
import com.example.basis_mvvm_noteapp.db.NoteDatabase
import com.example.basis_mvvm_noteapp.db.NoteRepository

 class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private lateinit var noteViewModel:NoteViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setting to get the data binding object
        binding=DataBindingUtil.setContentView(this,R.layout.activity_main)
        /*
           Then we need a reference variable for a NoteViewModel instance, bur before
           getting the viewModel Instance using the Factory class we need to create a
           ViewModelFactory instance , to create a NoteViewModelFactory instance we
           need to pass a DAO instance as an argument. Let's make a DAO instance
           first
        */
        //We already created a room database class which provides a singleton database instance
        val dao=NoteDatabase.getInstance(application).noteDAO
        //making repository instance using DAO instance
        val repository=NoteRepository(dao)
        val factory=NoteViewModelFactory(repository)
        //now to get noteViewModel instance
        noteViewModel=ViewModelProvider(this,factory).get(NoteViewModel::class.java)
        //assign the view model instance to the bindView Object
        binding.myViewModel=noteViewModel
        // Since we are intending to use live data binding we need to provide a life cycle  owner
        binding.lifecycleOwner=this
        displayNotesList()
    }
     //Creating a function to observe the list of data in database table
     private fun displayNotesList(){
         noteViewModel.notes.observe(this, Observer {
             Log.i("TAG",it.toString())
         })
     }
}