package com.rache.racheserver.music;

public class GetConfig {
    private int id;
    private String name;
    private String singer;
    private String album;

    private boolean multilineLyrics;

    private String description;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public boolean isMultilineLyrics() {
        return multilineLyrics;
    }

    public void setMultilineLyrics(boolean multilineLyrics) {
        this.multilineLyrics = multilineLyrics;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
