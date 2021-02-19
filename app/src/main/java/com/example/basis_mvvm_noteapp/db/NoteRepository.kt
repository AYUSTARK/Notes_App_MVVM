package com.example.basis_mvvm_noteapp.db

//Adding instance of NoteDao interface as constructor parameter, we are going to call the function
class NoteRepository(private val dao: NoteDAO ) {
    /*
        notes is a variable of LiveData<List<Note>>
        we don't need to run this on a background thread Room does it itself since it is a LiveData return type
     */
    val notes=dao.getAllNotes()

    /*
        All other DAO functions should be called from a background threads
        we will use coroutines in ViewModel class to execute them but to
        support that we need to define these functions a suspending functions
     */
    suspend fun insert(note:Note):Long{
        //this function wil return newly inserted row ID
        return dao.insertNote(note)
    }
    suspend fun update(note:Note):Int{
        return dao.updateNote(note)
    }
    suspend fun  delete(note:Note):Int{
        return dao.deleteNote(note)
    }
    suspend fun deleteAll():Int{
        return dao.deleteAll()
    }


}