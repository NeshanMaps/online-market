package org.neshan.delivery.model;

public class Product extends BaseModel{
    private long price;
    private String imageAddress;

    public long getPrice() {
        return price;
    }

    public Product setPrice(long price) {
        this.price = price;
        return this;
    }

    public String getImageAddress() {
        return imageAddress;
    }

    public Product setImageAddress(String imageAddress) {
        this.imageAddress = imageAddress;
        return this;
    }
}
