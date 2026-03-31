package com.example.quoson.mkryptos;

public class ListKey {
    private String id;
    private String name;
    private String algorithm;
    private String myTime;

    public ListKey( String i, String n, String a, String m ) {
        this.id = i;
        this.name = n;
        this.algorithm = a;
        this.myTime = m;
    }

    public void setID( String i ) {
        this.id = i;
    }

    public void setName( String n ) {
        this.name = n;
    }

    public void setAlgorithm( String a ) {
        this.algorithm = a;
    }

    public void setMyTime( String m ) {
        this.myTime = m;
    }

    public String getID(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    public String getAlgorithm(){
        return this.algorithm;
    }

    public String getTime(){
        return this.myTime.substring(0,12);
    }
}
