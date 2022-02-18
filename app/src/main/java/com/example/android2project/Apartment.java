package com.example.android2project;

public class Apartment {
    private int profilePic, postPic,price,rooms;
    private String sellerName, apartmentName, date,sellerEmail;


    public Apartment()
    {

    }

    public Apartment(int profilePic, int postPic, String sellerName, String apartmentName, String date, int price, int rooms) {
        this.profilePic = profilePic;
        this.postPic = postPic;
        this.sellerName = sellerName;
        this.apartmentName = apartmentName;
        this.date = date;
        this.price = price;
        this.rooms=rooms;

    }


    public Apartment(String sellerName, String apartmentName, String date, int price, int rooms, String sellerEmail) {
        this.sellerName = sellerName;
        this.apartmentName = apartmentName;
        this.date = date;
        this.price = price;
        this.rooms=rooms;
        this.sellerEmail=sellerEmail;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getApartmentName() {
        return apartmentName;
    }

    public void setApartmentName(String apartmentName) {
        this.apartmentName = apartmentName;
    }


    public int getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(int profilePic) {
        this.profilePic = profilePic;
    }

    public int getPostPic() {
        return postPic;
    }

    public void setPostPic(int postPic) {
        this.postPic = postPic;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getRooms() {
        return rooms;
    }

    public void setRooms(int rooms) {
        this.rooms = rooms;
    }

    public String getSellerEmail() {
        return sellerEmail;
    }

    public void setSellerEmail(String sellerEmail) {
        this.sellerEmail = sellerEmail;
    }


}

