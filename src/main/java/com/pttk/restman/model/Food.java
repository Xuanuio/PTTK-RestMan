package com.pttk.restman.model;

public class Food {
    private Integer id;
    private String name;
    private float price;
    private String description;
    private String photo;

    public Food() {}

    public Food(String name, float price, String description, String photo) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.photo = photo;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public float getPrice() { return price; }
    public void setPrice(float price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }
}
