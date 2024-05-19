package com.example.kutuphaneproje.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Item {
    @SerializedName("volumeInfo")
    @Expose
    private VolumeInfo volumeInfo;

    @SerializedName("id")
    @Expose
    private String id;

    public VolumeInfo getVolumeInfo() {
        return volumeInfo;
    }
    public void setVolumeInfo(VolumeInfo volumeInfo) {
        this.volumeInfo = volumeInfo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
