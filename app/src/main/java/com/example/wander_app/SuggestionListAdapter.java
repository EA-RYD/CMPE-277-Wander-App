package com.example.wander_app;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SuggestionListAdapter  extends ArrayAdapter<Suggestion> {
    private OnDetailsListener onDetailsListener;
    public interface OnDetailsListener {
        void onDetails(int position);
    }

    public SuggestionListAdapter(Context cont, ArrayList<Suggestion> arr, OnDetailsListener onDetailsListener) {
        super(cont, R.layout.suggestion_item, arr);
        this.onDetailsListener = onDetailsListener;
    }

    public View getView(int position, @Nullable View convertView, @Nullable ViewGroup parent) {
        Log.i(">>SuggestionListAdapter", "getView: " + position);
        Suggestion item = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.suggestion_item, parent, false);
        }
        TextView locationName = convertView.findViewById(R.id.tvLocationName);
        TextView description = convertView.findViewById(R.id.tvDescription);
        ImageView pic = convertView.findViewById(R.id.ivLocationPicture);
        String imageUrl = item.getImg();

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).into(pic);
        } else {
            pic.setImageResource(R.drawable.default_picture);
        }
        locationName.setText(item.getName());
        description.setText(item.getDescription());

        Button button = convertView.findViewById(R.id.btDetails);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDetailsListener != null) {
                    onDetailsListener.onDetails(position);
                }
            }
        });

        CheckBox checkBox= convertView.findViewById(R.id.cbSuggestion);

        checkBox.setChecked(item.getChecked());

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean newState = checkBox.isChecked();
                item.setChecked(newState);
            }
        });

        return convertView;

    }
}
