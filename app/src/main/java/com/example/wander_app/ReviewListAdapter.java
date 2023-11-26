package com.example.wander_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.wander_app.models.UserReview;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ReviewListAdapter extends ArrayAdapter<UserReview> {
    public ReviewListAdapter(Context cont, ArrayList<UserReview> arr) {
        super(cont, R.layout.review_item, arr);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        UserReview usrRev = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.review_item, parent, false);
        }
        ImageView avatar = convertView.findViewById(R.id.userAvatar);
        TextView userName = convertView.findViewById(R.id.username);
        RatingBar userRating = convertView.findViewById(R.id.ratingBar);
        TextView date = convertView.findViewById(R.id.reviewDate);
        TextView desc = convertView.findViewById(R.id.reviewText);

        Picasso.get().load(usrRev.getAvatarUrl()).into(avatar);
        userName.setText(usrRev.getUsername());
        userRating.setRating(usrRev.getRating());
        date.setText(usrRev.getVisitDate());
        desc.setText(usrRev.getText());

        return convertView;
    }
}
