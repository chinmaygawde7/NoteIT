package com.example.noteit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class NotesActivity extends AppCompatActivity {

    private FirebaseAuth fAuth;
    private RecyclerView mNotesList;
    private GridLayoutManager gridLayoutManager;

    private DatabaseReference fNotesDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        mNotesList = findViewById(R.id.main_notes_list);

        gridLayoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);

        mNotesList.setHasFixedSize(true);
        mNotesList.setLayoutManager(gridLayoutManager);

        fAuth = FirebaseAuth.getInstance();
        if(fAuth.getCurrentUser() != null){

            fNotesDatabase = FirebaseDatabase.getInstance().getReference().child("Notes").child(fAuth.getCurrentUser().getUid());
        }

        loadData();
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    private void loadData(){

        Query query = fNotesDatabase.orderByChild("timestamp");

        FirebaseRecyclerAdapter<NoteModal, NoteViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<NoteModal, NoteViewHolder>(
                NoteModal.class,
                R.layout.single_note_layout,
                NoteViewHolder.class,
                query
        ) {


            @Override
            protected void populateViewHolder(final NoteViewHolder noteViewHolder, NoteModal noteModal, int i) {

                final String noteId = getRef(i).getKey();

                fNotesDatabase.child(noteId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.hasChild("title") && snapshot.hasChild("timestamp")){

                            String title = snapshot.child("title").getValue().toString();
                            String timestamp = snapshot.child("timestamp").getValue().toString();

                            noteViewHolder.setNoteTitle(title);
                            //noteViewHolder.setNoteTime(timestamp);

                            GetTimeAgo getTimeAgo = new GetTimeAgo();
                            noteViewHolder.setNoteTime(getTimeAgo.getTimeAgo(Long.parseLong(timestamp), getApplicationContext()));

                            noteViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(NotesActivity.this, NewNotesActivity.class);
                                    intent.putExtra("noteId", noteId);
                                    startActivity(intent);
                                }
                            });

                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        };

        mNotesList.setAdapter(firebaseRecyclerAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()){
            case R.id.main_new_note_btn:
                Intent intent = new Intent(this, NewNotesActivity.class);
                startActivity(intent);
                break;
        }

        return true;
    }

    private int dptoPx(int dp){
        Resources r =getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp ,r.getDisplayMetrics()));
    }
}
