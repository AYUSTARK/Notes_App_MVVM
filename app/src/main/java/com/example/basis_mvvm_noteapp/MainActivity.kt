package com.example.basis_mvvm_noteapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.basis_mvvm_noteapp.databinding.ActivityMainBinding
import com.example.basis_mvvm_noteapp.db.Note
import com.example.basis_mvvm_noteapp.db.NoteDatabase
import com.example.basis_mvvm_noteapp.db.NoteRepository

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var adapter: NoteRecyclerViewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setting to get the data binding object
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        /*
           Then we need a reference variable for a NoteViewModel instance, bur before
           getting the viewModel Instance using the Factory class we need to create a
           ViewModelFactory instance , to create a NoteViewModelFactory instance we
           need to pass a DAO instance as an argument. Let's make a DAO instance
           first
        */
        //We already created a room database class which provides a singleton database instance
        val dao = NoteDatabase.getInstance(application).noteDAO
        //making repository instance using DAO instance
        val repository = NoteRepository(dao)
        val factory = NoteViewModelFactory(repository)
        //now to get noteViewModel instance
        noteViewModel = ViewModelProvider(this, factory).get(NoteViewModel::class.java)
        //assign the view model instance to the bindView Object
        binding.myViewModel = noteViewModel
        // Since we are intending to use live data binding we need to provide a life cycle  owner
        binding.lifecycleOwner = this
        initRecyclerView()

        //Observe the LiveData for the message
        noteViewModel.message.observe(
            this,
            Observer {
                //this awaits repetitions
                it.getContentIfNOtHandled()?.let {
                    Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                }
            },
        )
    }

    private fun initRecyclerView() {
        binding.noteRecyclerView.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        //To minimize code inconsistency we will only create only one adapter
        adapter=NoteRecyclerViewAdapter { selectedItem: Note -> listItemClicked(selectedItem) }
        binding.noteRecyclerView.adapter=adapter
        displayNotesList()
    }


    //Creating a function to observe the list of data in database table
    private fun displayNotesList() {
        noteViewModel.notes.observe(this, Observer {
            Log.i("TAG", it.toString())
            /*
            -> binding.noteRecyclerView.adapter=NoteRecyclerViewAdapter(it,{selectedItem:Note->listItemClicked(selectedItem)})

            similar as below the second parameter is a lambda expression to pass the

           ->  binding.noteRecyclerView.adapter=NoteRecyclerViewAdapter(it) { selectedItem: Note ->
                    listItemClicked(
                        selectedItem
                    )
                }
            there is inefficiency in the above code since every time we insert a note, delete a note
            , update a note . We create a new NoteRecyclerViewAdapter object.
            So changing from the general way of creating a new object every time. Taking it to init

             */
            adapter.setList(it)
            adapter.notifyDataSetChanged()
            /*
            Earlier we were creating new adapter object for every change in data but now we are
             use teh same adapter object for all
             */
        })
    }

    private fun listItemClicked(note: Note) {
        //Toast.makeText(this,"Selected name is ${note.title}",Toast.LENGTH_LONG).show()
        noteViewModel.initUpdateAndDelete(note)
    }
}