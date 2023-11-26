package com.example.wander_app.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class UserReview implements Parcelable {

    private String text;
    private int rating;
    private String username;
    private String avatarUrl;
    private String visitDate;

    public UserReview(String text, int rating, String username, String avatar, String visitDate) {
        this.text = text;
        this.rating = rating;
        this.username = username;
        this.avatarUrl = avatar;
        this.visitDate = visitDate;
    }

    protected UserReview(Parcel in) {
        text = in.readString();
        rating = in.readInt();
        username = in.readString();
        avatarUrl = in.readString();
        visitDate = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeInt(rating);
        dest.writeString(username);
        dest.writeString(avatarUrl);
        dest.writeString(visitDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserReview> CREATOR = new Creator<UserReview>() {
        @Override
        public UserReview createFromParcel(Parcel in) {
            return new UserReview(in);
        }

        @Override
        public UserReview[] newArray(int size) {
            return new UserReview[size];
        }
    };

    public String getText() {
        return text;
    }

    public int getRating() {
        return rating;
    }

    public String getUsername() {
        return username;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getVisitDate() {
        return visitDate;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setVisitDate(String visitDate) {
        this.visitDate = visitDate;
    }
}
