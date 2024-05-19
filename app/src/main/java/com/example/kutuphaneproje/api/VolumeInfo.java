package com.example.kutuphaneproje.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VolumeInfo{
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("authors")
    @Expose
    private List<String> authors = null;
    @SerializedName("industryIdentifiers")
    @Expose
    private List<IndustryIdentifiers> industryIdentifiers = null;

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public List<String> getAuthors() {
        return authors;
    }
    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }
    public List<IndustryIdentifiers> getIndustryIdentifiers() {
        return industryIdentifiers;
    }
    public void setIndustryIdentifiers(List<IndustryIdentifiers> industryIdentifiers) {
        this.industryIdentifiers = industryIdentifiers;
    }
}
