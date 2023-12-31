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

import com.bumptech.glide.Glide;
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
        Suggestion item = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.suggestion_item, parent, false);
        }
        TextView locationName = convertView.findViewById(R.id.tvLocationName);
        TextView description = convertView.findViewById(R.id.tvDescription);
        ImageView pic = convertView.findViewById(R.id.ivLocationPicture);
        String imageUrl = item.getImgUrl();
        Context cont = getContext();
        Glide.with(cont).load(imageUrl).placeholder(R.drawable.default_picture).into(pic);
//        if (imageUrl != null && !imageUrl.isEmpty()) {
//            Log.i(">>SuggestionListAdapter", "getView: Updating img url to the list view:" + imageUrl);
//            Picasso.get().load(imageUrl).into(pic);
//        } else {
//            Log.i(">>SuggestionListAdapter", "getView: Updating default img url to the list view");
//            pic.setImageResource(R.drawable.default_picture);
//        }
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

        button.setEnabled(item.getBtnEnabled());

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
