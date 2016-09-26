package com.hassanabid.fyberapiapp.api;

import java.util.List;

/**
 * Created by hassanabid on 8/5/16.
 */
public class FyberApiResponse {

    public String code;
    public List<Offer> offers;


    public class Offer {
        public String title;
        public String teaser;
        public int payout;
        public Thumbnail thumbnail;

    }

    public class Thumbnail {
        public String hires;
        public String lowres;
    }

}
