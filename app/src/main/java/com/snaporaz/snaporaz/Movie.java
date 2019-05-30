package com.snaporaz.snaporaz;

public class Movie {
    private String title, genres, thumbnail;
    private int percentage;

    public Movie(String title, String genres, String thumbnail, int percentage) {
        this.title = title;
        this.genres = genres;
        this.thumbnail = thumbnail;
        this.percentage = percentage;
    }

    public String getTitle() {
        return title;
    }

    public String getGenres() {
        return genres;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public int getPercentage() {
        return percentage;
    }
}
