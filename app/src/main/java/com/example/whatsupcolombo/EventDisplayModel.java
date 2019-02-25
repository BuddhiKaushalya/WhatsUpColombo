package com.example.whatsupcolombo;

public class EventDisplayModel {


    //create variables to match firebase name
    public String title;
    public String image;
    public String description;

    //constructor
    public EventDisplayModel() {

    }

    //getters and setters

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}