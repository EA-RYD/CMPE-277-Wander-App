package com.example.wander_app;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ItineraryListAdapter extends ArrayAdapter<ItineraryItem> {
    public ItineraryListAdapter(Context cont, ArrayList<ItineraryItem> arr) {
        super(cont, R.layout.itinerary_item, arr);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Log.i(">>ItineraryListAdapter", "getView: " + position);
        ItineraryItem item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.itinerary_item, parent, false);
        }
        ImageView pic = convertView.findViewById(R.id.ivLocationPicture);
        TextView name = convertView.findViewById(R.id.tvLocationName);
        TextView description = convertView.findViewById(R.id.tvDescription);
        String imageUrl = item.getImgUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).into(pic);
        } else {
            pic.setImageResource(R.drawable.default_picture);
        }
        name.setText(item.getLocationName());
        description.setText(item.getDescription());

        return convertView;
    }
}
