package com.hassanabid.fyberapiapp.data;

import io.realm.RealmObject;

/**
 * Created by hassanabid on 9/26/16.
 */

public class OfferObject extends RealmObject {

    public String title;
    public String teaser;
    public String hiresURL;
    public String payout;

    // Let your IDE generate getters and setters for you!
    // Or if you like you can even have public fields and no accessors!
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTeaser() {
        return teaser;
    }

    public void setTeaser(String teaser) {
        this.teaser = teaser;
    }

    public String getHiResURL() {
        return hiresURL;
    }

    public void setHiresURL(String hiresURL) {
        this.hiresURL = hiresURL;
    }

    public String getPayout() {
        return payout;
    }

    public void setPayout(String payout) {
        this.payout = payout;
    }

}
