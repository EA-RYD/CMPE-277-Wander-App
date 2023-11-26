package com.example.wander_app.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DailyWeatherItem implements Parcelable {

    private String weekday;
    private String image;
    private String degrees;
    private String rain;
    private String wind;

    public DailyWeatherItem(String day, String img, String degrees) {
        this.weekday = day;
        this.image = img;
        this.degrees = degrees;
    }

    public DailyWeatherItem(String day, String degrees, String precipitation, String wind) {
        this.degrees = degrees;

        this.weekday = dayOfWeekGen(day);
        this.rain = precipitation;
        this.wind = wind;
        determineImageFromMetrics();
    }

    private String dayOfWeekGen(String dateString) {
        Date date = parseDate(dateString);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        String dayName = getDayName(dayOfWeek);
        return dayName;
    }

    private static Date parseDate(String dateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            return sdf.parse(dateString);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getDayName(int dayOfWeek) {
        String[] days = {"", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        return days[dayOfWeek];
    }

    private void determineImageFromMetrics() {
        Double windSpeed = Double.parseDouble(wind);
        Double precipitation = Double.parseDouble(rain);

        if (windSpeed > 20) {
            image = "weather_windy";
        } else if (precipitation > 10) {
            image = "weather_rain";
        } else {
            Double temperature = Double.parseDouble(degrees);

            if (temperature > 25) {
                image = "weather_sunny";
            } else if (temperature > 15) {
                image = "weather_sunny_cloudy";
            } else {
                image = "weather_cloudy";
            }
        }
    }


    protected DailyWeatherItem(Parcel in) {
        weekday = in.readString();
        image = in.readString();
        degrees = in.readString();
    }

    public static final Creator<DailyWeatherItem> CREATOR = new Creator<DailyWeatherItem>() {
        @Override
        public DailyWeatherItem createFromParcel(Parcel in) {
            return new DailyWeatherItem(in);
        }

        @Override
        public DailyWeatherItem[] newArray(int size) {
            return new DailyWeatherItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(weekday);
        parcel.writeString(image);
        parcel.writeString(degrees);
    }

    public String getWeekday() {
        return weekday;
    }

    public void setWeekday(String weekday) {
        this.weekday = weekday;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDegrees() {
        return degrees;
    }

    public void setDegrees(String degrees) {
        this.degrees = degrees;
    }
}
