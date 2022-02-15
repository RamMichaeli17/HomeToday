package com.example.android2project;

public class Meeting {

    public String game,date;

    public Meeting()
    {

    }

    public Meeting(String game, String date)
    {
        this.game = game;
        this.date = date;

    }



    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }



}
