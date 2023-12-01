package com.example.chatting3;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ChatHolder extends RecyclerView.ViewHolder {
    private final TextView mNameField;
    private final TextView mTextField;
    private final TextView mTimeField;

    public ChatHolder(@NonNull View itemView) {
        super(itemView);
        mNameField = itemView.findViewById(R.id.name);
        mTextField = itemView.findViewById(R.id.content);
        mTimeField = itemView.findViewById(R.id.time);
    }

    public void bind(@NonNull Chat chat) {
        setName(chat.getSenderName());
        setMessage(chat.getSendMessage());
        setTime(chat.getTimestamp());
    }

    private void setName(@Nullable String name) { mNameField.setText(name); }

    private void setMessage(@Nullable String text) { mTextField.setText(text); }

    private void setTime(@Nullable Timestamp time) {
        if (time != null) {
            Date date = time.toDate();

            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

            String formattedTime = dateFormat.format(date);

            mTimeField.setText(formattedTime);
        }
    }

}

