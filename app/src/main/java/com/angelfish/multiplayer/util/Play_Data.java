package com.angelfish.multiplayer.util;

public class Play_Data {
    private int ID;
    private String NAME;
    private String REMOTE_URL;
    private String FILENAME;
    private int REMOTE_ID;
    private int PLAY_TIMES;
    private long EXPIRE_TIME;
    private int TYPE;
    private int SORT;

    public Play_Data(int ID, String name, String remote_url, String filename, int remote_id, int play_times, long expire_time, int type, int sort) {
        this.ID = ID;
        this.NAME = name;
        this.REMOTE_URL = remote_url;
        this.FILENAME = filename;
        this.REMOTE_ID = remote_id;
        this.PLAY_TIMES = play_times;
        this.EXPIRE_TIME = expire_time;
        this.TYPE = type;
        this.SORT = sort;
    }

    public int getID() {
        return ID;
    }

    public void setID(int id) {
        this.ID = id;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String name) {
        this.NAME = name;
    }

    public String getFILENAME() {
        return FILENAME;
    }

    public void setFILENAME(String filename) {
        this.FILENAME = filename;
    }

    public String getURL() {
        return REMOTE_URL;
    }

    public void setURL(String remote_url) {
        this.REMOTE_URL = remote_url;
    }

    public int getREMOTE_ID() {
        return REMOTE_ID;
    }

    public void setREMOTE_ID(int remote_id) {
        this.REMOTE_ID = remote_id;
    }

    public int getPLAY_TIMES() {
        return PLAY_TIMES;
    }

    public void setPLAY_TIMES(int play_times) {
        this.PLAY_TIMES = play_times;
    }

    public long getEXPIRE_TIME() {
        return EXPIRE_TIME;
    }

    public void setEXPIRE_TIME(long expire_time) {
        this.EXPIRE_TIME = expire_time;
    }

    public int getTYPE() {
        return TYPE;
    }

    public void setTYPE(int type) {
        this.TYPE = type;
    }

    public int getSORT() {
        return SORT;
    }

    public void setSORT(int sort) {
        this.SORT = sort;
    }


}

