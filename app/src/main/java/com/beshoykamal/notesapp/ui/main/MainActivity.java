package com.beshoykamal.notesapp.ui.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.beshoykamal.notesapp.model.Note;
import com.beshoykamal.notesapp.ui.adapter.NoteAdapter;
import com.beshoykamal.notesapp.model.NoteViewModel;
import com.beshoykamal.notesapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int ADD_REQUES_NOTE = 1;
    public static final int EDIT_REQUES_NOTE = 2;
    private NoteViewModel noteViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /// Go to ADD Activity
        FloatingActionButton actionButton = findViewById(R.id.btn_add_note);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(MainActivity.this, AddNoteActivity.class);
                startActivityForResult(in,ADD_REQUES_NOTE);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        final NoteAdapter adapter = new NoteAdapter();
        recyclerView.setAdapter(adapter);


        noteViewModel =  ViewModelProviders.of(this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {

                // update recyclerview
//                Toast.makeText(MainActivity.this, "onChange", Toast.LENGTH_SHORT).show();
                adapter.setNotes(notes);
            }
        });

        /// To swiped
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT ) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                noteViewModel.delete(adapter.getNoteAD(viewHolder.getAdapterPosition()));
                Toast.makeText(MainActivity.this, "Not Delete", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);
        /// set clicklistener
        adapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                Intent in = new Intent(MainActivity.this,AddNoteActivity.class);
                in.putExtra(AddNoteActivity.EXTRA_TITLE,note.getTitle());
                in.putExtra(AddNoteActivity.EXTRA_DESCRIPTION,note.getDescription());
                in.putExtra(AddNoteActivity.EXTRA_PRIORITY,note.getPriority());
                in.putExtra(AddNoteActivity.EXTRA_ID,note.getId());
                startActivityForResult(in,EDIT_REQUES_NOTE);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.delet_all_notes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all_N:
                noteViewModel.deleteAllNotes();
                Toast.makeText(this, "delete all notes", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_REQUES_NOTE && resultCode == RESULT_OK){
            String title = data.getStringExtra(AddNoteActivity.EXTRA_TITLE);
            String description = data.getStringExtra(AddNoteActivity.EXTRA_DESCRIPTION);
            int priority = data.getIntExtra(AddNoteActivity.EXTRA_PRIORITY,1);

            Note note = new Note(title,description,priority);
            noteViewModel.insert(note);

            Toast.makeText(this, "NOTE SAVED", Toast.LENGTH_SHORT).show();
        }
        else if (requestCode == EDIT_REQUES_NOTE && resultCode == RESULT_OK){
            int id =data.getIntExtra(AddNoteActivity.EXTRA_ID,-1);
            if (id== -1){
                Toast.makeText(this, "NOTE NOT UPDATED", Toast.LENGTH_SHORT).show();
                return;
            }
            String title = data.getStringExtra(AddNoteActivity.EXTRA_TITLE);
            String description = data.getStringExtra(AddNoteActivity.EXTRA_DESCRIPTION);
            int priority = data.getIntExtra(AddNoteActivity.EXTRA_PRIORITY,1);

            Note note = new Note(title,description,priority);
            note.setId(id);
            noteViewModel.update(note);
            Toast.makeText(this, "NOTE UPDATED", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(this, "NOTE NOT SAVED", Toast.LENGTH_SHORT).show();
    }
}
