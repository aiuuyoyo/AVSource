package com.q.s.quicksearch.models;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Entity mapped to table NOVEL.
 */
public class Novel implements Parcelable {

    private Long id;
    private String title;
    private String content;

    public Novel() {
    }

    public Novel(Long id) {
        this.id = id;
    }

    public Novel(Long id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.title);
        dest.writeString(this.content);
    }

    protected Novel(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.title = in.readString();
        this.content = in.readString();
    }

    public static final Parcelable.Creator<Novel> CREATOR = new Parcelable.Creator<Novel>() {
        public Novel createFromParcel(Parcel source) {
            return new Novel(source);
        }

        public Novel[] newArray(int size) {
            return new Novel[size];
        }
    };
}