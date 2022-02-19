package com.example.android2project;

import android.widget.ImageView;

public class Apartment {
    private int price,rooms,offerCounter,numOfPictures,undo;
    private String sellerName, apartmentName, date,sellerEmail,time,sellerUID;
    private ImageView profilePic;


    public Apartment()
    {

    }


    public int getNumOfPictures() {
        return numOfPictures;
    }

    public void setNumOfPictures(int numOfPictures) {
        this.numOfPictures = numOfPictures;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSellerUID() {
        return sellerUID;
    }

    public void setSellerUID(String sellerUID) {
        this.sellerUID = sellerUID;
    }


    public Apartment(String sellerName, String apartmentName, String date, int price, int rooms, String sellerEmail, int offerCounter, int numOfPictures, String time, String sellerUID) {
        this.sellerName = sellerName;
        this.apartmentName = apartmentName;
        this.date = date;
        this.price = price;
        this.rooms=rooms;
        this.sellerEmail=sellerEmail;
        this.offerCounter=offerCounter;
        this.numOfPictures=numOfPictures;
        this.time=time;
        this.sellerUID=sellerUID;
        this.undo=0;
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

    public int getOfferCounter() {
        return offerCounter;
    }

    public void setOfferCounter(int offerCounter) {
        this.offerCounter = offerCounter;
    }

    public int getUndo() {
        return undo;
    }

    public void setUndo(int undo) {
        this.undo = undo;
    }


}

