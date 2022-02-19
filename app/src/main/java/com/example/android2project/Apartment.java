package com.example.android2project;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

public class Apartment implements Parcelable {
    private int price,rooms,offerCounter,numOfPictures,floor,totalFloors,squareMeter,parkings;
    private String sellerName, address, date,sellerEmail,time,sellerUID, enteringDate,city;
    private ImageView profilePic;
    private boolean forRental,airConditioner, elevator, storeroom, balcony, mamad, kosherKitchen, renovated, furnished;



    public Apartment()
    {

    }

    public Apartment(int price, int rooms, int offerCounter, int numOfPictures, int floor, int totalFloors, int squareMeter, int parkings, String sellerName, String address, String date, String sellerEmail, String time, String sellerUID, String enteringDate, String city, ImageView profilePic, boolean forRental, boolean airConditioner, boolean elevator, boolean storeroom, boolean balcony, boolean mamad, boolean kosherKitchen, boolean renovated, boolean furnished) {
        this.price = price;
        this.rooms = rooms;
        this.offerCounter = offerCounter;
        this.numOfPictures = numOfPictures;
        this.floor = floor;
        this.totalFloors = totalFloors;
        this.squareMeter = squareMeter;
        this.parkings = parkings;
        this.sellerName = sellerName;
        this.address = address;
        this.date = date;
        this.sellerEmail = sellerEmail;
        this.time = time;
        this.sellerUID = sellerUID;
        this.enteringDate = enteringDate;
        this.city = city;
        this.profilePic = profilePic;
        this.forRental = forRental;
        this.airConditioner = airConditioner;
        this.elevator = elevator;
        this.storeroom = storeroom;
        this.balcony = balcony;
        this.mamad = mamad;
        this.kosherKitchen = kosherKitchen;
        this.renovated = renovated;
        this.furnished = furnished;
    }

    protected Apartment(Parcel in) {
        price = in.readInt();
        rooms = in.readInt();
        offerCounter = in.readInt();
        numOfPictures = in.readInt();
        floor = in.readInt();
        totalFloors = in.readInt();
        squareMeter = in.readInt();
        parkings = in.readInt();
        sellerName = in.readString();
        address = in.readString();
        date = in.readString();
        sellerEmail = in.readString();
        time = in.readString();
        sellerUID = in.readString();
        enteringDate = in.readString();
        city = in.readString();
        forRental = in.readByte() != 0;
        airConditioner = in.readByte() != 0;
        elevator = in.readByte() != 0;
        storeroom = in.readByte() != 0;
        balcony = in.readByte() != 0;
        mamad = in.readByte() != 0;
        kosherKitchen = in.readByte() != 0;
        renovated = in.readByte() != 0;
        furnished = in.readByte() != 0;
    }

    public static final Creator<Apartment> CREATOR = new Creator<Apartment>() {
        @Override
        public Apartment createFromParcel(Parcel in) {
            return new Apartment(in);
        }

        @Override
        public Apartment[] newArray(int size) {
            return new Apartment[size];
        }
    };

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getTotalFloors() {
        return totalFloors;
    }

    public void setTotalFloors(int totalFloors) {
        this.totalFloors = totalFloors;
    }

    public int getSquareMeter() {
        return squareMeter;
    }

    public void setSquareMeter(int squareMeter) {
        this.squareMeter = squareMeter;
    }

    public int getParkings() {
        return parkings;
    }

    public void setParkings(int parkings) {
        this.parkings = parkings;
    }

    public String getEnteringDate() {
        return enteringDate;
    }

    public void setEnteringDate(String enteringDate) {
        this.enteringDate = enteringDate;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public ImageView getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(ImageView profilePic) {
        this.profilePic = profilePic;
    }

    public boolean isForRental() {
        return forRental;
    }

    public void setForRental(boolean forRental) {
        this.forRental = forRental;
    }

    public boolean isAirConditioner() {
        return airConditioner;
    }

    public void setAirConditioner(boolean airConditioner) {
        this.airConditioner = airConditioner;
    }

    public boolean isElevator() {
        return elevator;
    }

    public void setElevator(boolean elevator) {
        this.elevator = elevator;
    }

    public boolean isStoreroom() {
        return storeroom;
    }

    public void setStoreroom(boolean storeroom) {
        this.storeroom = storeroom;
    }

    public boolean isBalcony() {
        return balcony;
    }

    public void setBalcony(boolean balcony) {
        this.balcony = balcony;
    }

    public boolean isMamad() {
        return mamad;
    }

    public void setMamad(boolean mamad) {
        this.mamad = mamad;
    }

    public boolean isKosherKitchen() {
        return kosherKitchen;
    }

    public void setKosherKitchen(boolean kosherKitchen) {
        this.kosherKitchen = kosherKitchen;
    }

    public boolean isRenovated() {
        return renovated;
    }

    public void setRenovated(boolean renovated) {
        this.renovated = renovated;
    }

    public boolean isFurnished() {
        return furnished;
    }

    public void setFurnished(boolean furnished) {
        this.furnished = furnished;
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


    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(price);
        parcel.writeInt(rooms);
        parcel.writeInt(offerCounter);
        parcel.writeInt(numOfPictures);
        parcel.writeInt(floor);
        parcel.writeInt(totalFloors);
        parcel.writeInt(squareMeter);
        parcel.writeInt(parkings);
        parcel.writeString(sellerName);
        parcel.writeString(address);
        parcel.writeString(date);
        parcel.writeString(sellerEmail);
        parcel.writeString(time);
        parcel.writeString(sellerUID);
        parcel.writeString(enteringDate);
        parcel.writeString(city);
        parcel.writeByte((byte) (forRental ? 1 : 0));
        parcel.writeByte((byte) (airConditioner ? 1 : 0));
        parcel.writeByte((byte) (elevator ? 1 : 0));
        parcel.writeByte((byte) (storeroom ? 1 : 0));
        parcel.writeByte((byte) (balcony ? 1 : 0));
        parcel.writeByte((byte) (mamad ? 1 : 0));
        parcel.writeByte((byte) (kosherKitchen ? 1 : 0));
        parcel.writeByte((byte) (renovated ? 1 : 0));
        parcel.writeByte((byte) (furnished ? 1 : 0));
    }
}

