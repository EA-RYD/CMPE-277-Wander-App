package com.example.wander_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.wander_app.models.DailyWeatherItem;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class WeatherListAdapter extends ArrayAdapter<DailyWeatherItem> {
    public WeatherListAdapter(Context cont, ArrayList<DailyWeatherItem> arr) {
        super(cont, R.layout.weather_item, arr);
        //super(cont, R.layout.weather_item);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        DailyWeatherItem weatherItem = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.weather_item, parent, false);
        }
        TextView day = convertView.findViewById(R.id.dayText);
        ImageView dayPic = convertView.findViewById(R.id.dayImage);
        TextView dayTemp = convertView.findViewById(R.id.dayTemp);

        day.setText(weatherItem.getWeekday());
        dayTemp.setText(String.valueOf(weatherItem.getDegrees()) + " °F");
        String drawableName = weatherItem.getImage();
        Picasso.get().load(getDrawableResourceId(drawableName)).into(dayPic);

        return convertView;
    }

    private int getDrawableResourceId(String drawableName) {
        return getContext().getResources().getIdentifier(drawableName, "drawable", getContext().getPackageName());
    }
}
