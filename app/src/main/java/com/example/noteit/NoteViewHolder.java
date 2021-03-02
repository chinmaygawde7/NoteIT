package com.example.noteit;


import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class NoteViewHolder extends RecyclerView.ViewHolder {

    View mView;
    TextView textTitle, textTime;
    CardView noteCard;

    public NoteViewHolder(@NonNull View itemView) {
        super(itemView);

        mView = itemView;

        textTime = mView.findViewById(R.id.note_time);
        textTitle = mView.findViewById(R.id.note_title);
        noteCard = mView.findViewById(R.id.note_card);
    }

    public void setNoteTitle(String title) {
        textTitle.setText((title));
    }

    public void setNoteTime(String time) {
        textTime.setText(time);
    }
}
