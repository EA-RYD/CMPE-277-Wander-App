package com.example.wander_app.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class VacationPicture implements Parcelable {
    private String title;
    private String imageUrl;

    public VacationPicture(String title, String image) {
        this.title = title;
        imageUrl = image;
    }

    public VacationPicture() {

    }

    protected VacationPicture(Parcel in) {
        title = in.readString();
        imageUrl = in.readString();
    }

    public static final Creator<VacationPicture> CREATOR = new Creator<VacationPicture>() {
        @Override
        public VacationPicture createFromParcel(Parcel in) {
            return new VacationPicture(in);
        }

        @Override
        public VacationPicture[] newArray(int size) {
            return new VacationPicture[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return imageUrl;
    }

    public void setImage(String image) {
        imageUrl = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {

        dest.writeString(title);
        dest.writeString(imageUrl);
    }
}
