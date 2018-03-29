package com.example.kaihuynh.todo.model;

import java.io.Serializable;

public class Todo implements Serializable{
    private int id;
    private String title;
    private String content;
    private String date;
    private byte[] image;

    public Todo(){

    }

    public Todo(int id, String title, String content, String date, byte[] image) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.date = date;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
