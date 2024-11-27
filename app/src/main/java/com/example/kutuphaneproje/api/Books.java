package com.example.kutuphaneproje.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Books {
    @SerializedName("items")
    @Expose
    private List<Item> items = null;
    @SerializedName("totalItems")
    @Expose
    private Integer totalItems;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Integer getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }

}
