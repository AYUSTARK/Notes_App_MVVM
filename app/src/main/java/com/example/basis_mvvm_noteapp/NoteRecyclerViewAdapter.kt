package com.example.basis_mvvm_noteapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.basis_mvvm_noteapp.databinding.ListItemBinding
import com.example.basis_mvvm_noteapp.db.Note
import com.example.basis_mvvm_noteapp.generated.callback.OnClickListener

//List of Notes as parameter
// Unit here signifies this function doesn't return anything
class NoteRecyclerViewAdapter(private val clickListener: (Note)->Unit): RecyclerView.Adapter<MyViewHolder>() {

    private val noteList=ArrayList<Note>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater=LayoutInflater.from(parent.context)
        val binding:ListItemBinding=
            DataBindingUtil.inflate(layoutInflater,R.layout.list_item,parent,false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(noteList[position],clickListener)
    }

    override fun getItemCount(): Int {
        return noteList.size
    }

    //function to set the list of subscribers to set to notes
    fun setList(notes:List<Note>){
        noteList.clear()
        noteList.addAll(notes)
    }


}
/*
   As you know this class should extend the recyclerviewAdapter
   class when we are extending we need to provide an adapter
   class as object type. Therefore, we need to create another
   class which extends these recycler view adapter class. Hence
   creating a separate class.
   */
class MyViewHolder(private val binding:ListItemBinding):RecyclerView.ViewHolder(binding.root){
    /*
    Since we are going to use data binding, we need to add the data binding object
    of the list item layout as the constructor parameter

    We mainly use this class to bind values to each list item
     */
    fun bind(note: Note,clickListener: (Note)->Unit){
        binding.noteTitleTextView.text=note.title
        binding.noteDescriptionTextView.text=note.description
        binding.listItemLayout.setOnClickListener{
            // Code to pass the selected Note Instance this will pass the ListItemClicked function in the main activity
            clickListener(note)
        }
    }

}