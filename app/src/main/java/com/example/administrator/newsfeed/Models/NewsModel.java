package com.example.administrator.newsfeed.Models;

/**
 * Created by Administrator on 9/14/2017.
 */

public class NewsModel {
    /**
     * ID : 1
     * TITLE : Fed official says weak data caused by weather, should not slow taper
     * URL : http://www.latimes.com/business/money/la-fi-mo-federal-reserve-plosser-stimulus-economy-20140310,0,1312750.story\?track=rss
     * PUBLISHER : Los Angeles Times
     * CATEGORY : b
     * HOSTNAME : www.latimes.com
     * TIMESTAMP : 1394470370698
     */

    private int ID;
    private String TITLE;
    private String URL;
    private String PUBLISHER;
    private String CATEGORY;
    private String HOSTNAME;
    private long TIMESTAMP;
    boolean like = false, bookMark = false;

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public boolean isBookMark() {
        return bookMark;
    }

    public void setBookMark(boolean bookMark) {
        this.bookMark = bookMark;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getTITLE() {
        return TITLE;
    }

    public void setTITLE(String TITLE) {
        this.TITLE = TITLE;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getPUBLISHER() {
        return PUBLISHER;
    }

    public void setPUBLISHER(String PUBLISHER) {
        this.PUBLISHER = PUBLISHER;
    }

    public String getCATEGORY() {
        return CATEGORY;
    }

    public void setCATEGORY(String CATEGORY) {
        this.CATEGORY = CATEGORY;
    }

    public String getHOSTNAME() {
        return HOSTNAME;
    }

    public void setHOSTNAME(String HOSTNAME) {
        this.HOSTNAME = HOSTNAME;
    }

    public long getTIMESTAMP() {
        return TIMESTAMP;
    }

    public void setTIMESTAMP(long TIMESTAMP) {
        this.TIMESTAMP = TIMESTAMP;
    }
}
