package com.hassanabid.fyberapiapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.hassanabid.fyberapiapp.activities.OfferDetailActivity;
import com.hassanabid.fyberapiapp.activities.RequestOffersActivity;
import com.hassanabid.fyberapiapp.api.FyberApi;
import com.hassanabid.fyberapiapp.api.FyberApiResponse;
import com.hassanabid.fyberapiapp.data.Constants;
import com.hassanabid.fyberapiapp.data.OfferObject;
import com.hassanabid.fyberapiapp.data.Utility;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * An activity representing a list of Offers. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link OfferDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class OfferListActivity extends AppCompatActivity {


    private static final String LOG_TAG = OfferListActivity.class.getSimpleName();


    private boolean mTwoPane;
    private ProgressBar progessBar;
    private TextView emptyTextView;
    private RecyclerView recyclerView;

    private RealmConfiguration mRealmConfig;
    private Realm mRealm;
    private List<FyberApiResponse.Offer> mOffersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_list);

        setupRealm();
        getDeviceIdandInitiateRequest();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Start the request activity", Snackbar.LENGTH_LONG)
                        .setAction("Done", null).show();
                startActivity(new Intent(OfferListActivity.this,RequestOffersActivity.class));
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.offer_list);
        emptyTextView = (TextView) findViewById(R.id.emptyList);
        progessBar = (ProgressBar) findViewById(R.id.progressBar);


        if (findViewById(R.id.offer_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

    }

    private void setupRealm() {
        mRealmConfig = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(mRealmConfig);
        mRealm = Realm.getDefaultInstance();

    }

    private RealmResults<OfferObject> getRealmResults() {

        RealmResults<OfferObject> offers = mRealm.where(OfferObject.class).findAll();
        Log.d(LOG_TAG,"getRealmResults : " + offers.size());
        return offers;
    }

    private void deleteRealmObjects() {

        mRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(OfferObject.class);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d(LOG_TAG,"realm objects deleted");

            }
        });
    }


    private void setupRecyclerView(@NonNull RecyclerView recyclerView, RealmResults<OfferObject> results) {
        progessBar.setVisibility(View.GONE);
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(results));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final RealmResults<OfferObject> mRealmObjects;

        public SimpleItemRecyclerViewAdapter(RealmResults<OfferObject> realmObjects) {
            mRealmObjects = realmObjects;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.offer_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mRealmObject = mRealmObjects.get(position);
            holder.mTitleView.setText(mRealmObjects.get(position).getTitle());
            holder.mTeaserView.setText(mRealmObjects.get(position).getTeaser());
            holder.mPayout.setText(mRealmObjects.get(position).getPayout());

            Glide.with(holder.mThumbnail.getContext())
                    .load(mRealmObjects.get(position).getHiResURL())
                    .centerCrop()
//                    .placeholder(R.drawable.loading_spinner)
                    .crossFade()
                    .into(holder.mThumbnail);

        }

        @Override
        public int getItemCount() {
            return mRealmObjects.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mTitleView;
            public final TextView mTeaserView;
            public final ImageView mThumbnail;
            public final TextView mPayout;
            public OfferObject mRealmObject;


            public ViewHolder(View view) {
                super(view);
                mView = view;
                mTitleView = (TextView) view.findViewById(R.id.title);
                mTeaserView = (TextView) view.findViewById(R.id.teaser);
                mPayout = (TextView) view.findViewById(R.id.payout);
                mThumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mTitleView.getText() + "'";
            }
        }
    }

    private void initiateFyberApiRequest() {

        Long tsLong = System.currentTimeMillis()/1000;
        String timestamp = tsLong.toString();

        String hash_key  = Utility.SHA1(Constants.API_URL,timestamp);
        Log.d(LOG_TAG,"hashkey : " + hash_key.toLowerCase());

        Log.d(LOG_TAG,"initiateFyberApiRequest");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        FyberApi api = retrofit.create(FyberApi.class);
        Call<FyberApiResponse> call = api.getFyberOffers(Constants.APP_ID,
                                        Constants.DEVICE_ID,
                                        Constants.IP_ADDRESS,
                                        Constants.locale,
                                        "2",
                                        timestamp,
                                        "campaign2",
                                        timestamp,
                                        Constants.OFFER_TYPES,
                                         Constants.UID,
                                        hash_key.toLowerCase());
        progessBar.setVisibility(View.VISIBLE);

        call.enqueue(new Callback<FyberApiResponse>() {
            @Override
            public void onResponse(Call<FyberApiResponse> call, Response<FyberApiResponse> response) {

                if (response.isSuccessful()) {
                    Log.d(LOG_TAG, "success - response is " + response.body());
                    mOffersList = response.body().offers;
                    executeRealmWriteTransaction(mOffersList);

                } else {
                    progessBar.setVisibility(View.GONE);
                    Log.d(LOG_TAG, "failure response is " + response.raw().toString());

                }
            }

            @Override
            public void onFailure(Call<FyberApiResponse> call, Throwable t) {
                Log.e(LOG_TAG, " Error :  " + t.getMessage());
            }

        });

    }

    private void executeRealmWriteTransaction (final List<FyberApiResponse.Offer> offers) {

        Log.d(LOG_TAG,"saveRealmObjects : " + offers.size());
        mRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                for (int i = 0; i < offers.size(); i++) {

                    OfferObject cake = realm.createObject(OfferObject.class);
                    cake.setTitle(offers.get(i).title);
                    cake.setHiresURL(offers.get(i).thumbnail.hires);
                    cake.setPayout(offers.get(i).payout);
                    cake.setTeaser(offers.get(i).teaser);
                }


            }
        },new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d(LOG_TAG,"savedRealmObjects");
                setupRecyclerView(recyclerView,getRealmResults());
            }

        },new Realm.Transaction.OnError(){

            @Override
            public void onError(Throwable error) {
                Log.d(LOG_TAG,"error while writing to realm db :" + error.getMessage());
            }
        });
    }

    private void getDeviceIdandInitiateRequest() {

        new AsyncTask<Void,Void,String>() {

            @Override
            protected String doInBackground(Void... params) {
                String adId = null;
                try {
                    AdvertisingIdClient.Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(OfferListActivity.this);
                    adId = adInfo != null ? adInfo.getId() : null;
                    Constants.DEVICE_ID = adId;
                    Log.d(LOG_TAG,"adid :" + adId);


                } catch (java.io.IOException | GooglePlayServicesRepairableException  | GooglePlayServicesNotAvailableException exception) {
                    Log.d(LOG_TAG,"exception");
                }
                return adId;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                final RealmResults<OfferObject> offerObjects = getRealmResults();
                if(offerObjects.size() == 0) {
                    // TODO: show a message for no result
                    emptyTextView.setVisibility(View.VISIBLE);
                    initiateFyberApiRequest();
                }
                else {
                    setupRecyclerView(recyclerView,offerObjects);
                    emptyTextView.setVisibility(View.INVISIBLE);
                }
            }
        }.execute();

    }
}
