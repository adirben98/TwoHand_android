package com.example.twohand_project.Model;

public class Post {
    public String owner;
    public String ownerImg;
    public String location;
    public String price;
    public String description;
    public String productImg;

    public Post(String owner, String ownerImg, String location, String price, String description, String productImg) {
        this.owner = owner;
        this.ownerImg = ownerImg;
        this.location = location;
        this.price = price;
        this.description = description;
        this.productImg = productImg;
    }


}