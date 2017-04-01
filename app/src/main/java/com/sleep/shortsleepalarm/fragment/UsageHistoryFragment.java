package com.sleep.shortsleepalarm.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.sleep.shortsleepalarm.R;
import com.sleep.shortsleepalarm.adapter.GraphAdapter;
import com.sleep.shortsleepalarm.common.EverydayApplication;
import com.sleep.shortsleepalarm.database.DatabaseHandler;

import java.text.DateFormatSymbols;
import java.util.Calendar;

/**
 * Created by Aravindraj on 1/13/2017.
 */
public class UsageHistoryFragment extends Fragment {
    private RecyclerView monthRecycler1, monthRecycler2, monthRecycler3;
    TextView txt1, txt2, txt3;
    private GraphAdapter mAdapter1, mAdapter2, mAdapter3;
    DatabaseHandler db;
    LinearLayout ll1,ll2,ll3;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_usage_history, container, false);
        init(v);

        setListeners();
        return v;
    }

    private void setListeners() {

    }

    private void init(View v) {
        db = new DatabaseHandler(getContext());

        AdView mAdView = (AdView) v.findViewById(R.id.adView);
        if (EverydayApplication.getInstance().isOnline(getContext())) {
            MobileAds.initialize(getContext(), getResources().getString(R.string.banner_ad_unit_id4));
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        } else
            mAdView.setVisibility(View.GONE);
        monthRecycler1 = (RecyclerView) v.findViewById(R.id.monthRecycler1);
        monthRecycler2 = (RecyclerView) v.findViewById(R.id.monthRecycler2);
        monthRecycler3 = (RecyclerView) v.findViewById(R.id.monthRecycler3);
        ll1 = (LinearLayout) v.findViewById(R.id.ll1);
        ll2 = (LinearLayout) v.findViewById(R.id.ll2);
        ll3 = (LinearLayout) v.findViewById(R.id.ll3);

        txt1 = (TextView) v.findViewById(R.id.txt1);
        txt2 = (TextView) v.findViewById(R.id.txt2);
        txt3 = (TextView) v.findViewById(R.id.txt3);
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        String hgs = new DateFormatSymbols().getMonths()[month - 1];
        if(db.getCompletedMonthModels(month, year).size()==0) {
            ll1.setVisibility(View.VISIBLE);
            monthRecycler1.setVisibility(View.GONE);
        }
        mAdapter1 = new GraphAdapter(getContext(), db.getCompletedMonthModels(month, year));
       txt1.setText(hgs+" , "+year);
        if (month > 1) {
            if(db.getCompletedMonthModels((month - 1),year).size()==0) {
                ll2.setVisibility(View.VISIBLE);
                monthRecycler2.setVisibility(View.GONE);
            }
            mAdapter2 = new GraphAdapter(getContext(), db.getCompletedMonthModels((month - 1), year));
            String mth2 = new DateFormatSymbols().getMonths()[month - 2];
            txt2.setText(mth2+" , "+year);
        } else {
            if(db.getCompletedMonthModels((12),year-1).size()==0) {
                ll2.setVisibility(View.VISIBLE);
                monthRecycler2.setVisibility(View.GONE);
            }
            mAdapter2 = new GraphAdapter(getContext(), db.getCompletedMonthModels(12, year - 1));
            String mth2 = new DateFormatSymbols().getMonths()[12 - 1];
            txt2.setText(mth2+" , "+(year-1));
        }

        if (month > 2) {
            if(db.getCompletedMonthModels((month - 2),year).size()==0) {
                ll3.setVisibility(View.VISIBLE);
                monthRecycler3.setVisibility(View.GONE);
            }
            mAdapter3 = new GraphAdapter(getContext(), db.getCompletedMonthModels(month - 2, year));
            String mth3 = new DateFormatSymbols().getMonths()[month - 3];
            txt3.setText(mth3+" , "+(year-1));
        }else {
            if (month == 2) {
                if(db.getCompletedMonthModels((12),year-1).size()==0) {
                    ll3.setVisibility(View.VISIBLE);
                    monthRecycler3.setVisibility(View.GONE);
                }
                mAdapter3 = new GraphAdapter(getContext(), db.getCompletedMonthModels(12, year-1));
                String mth3 = new DateFormatSymbols().getMonths()[11];
                txt3.setText(mth3+" , "+(year-1));
            }
            if (month == 1) {
                if(db.getCompletedMonthModels((11),year-1).size()==0) {
                    ll3.setVisibility(View.VISIBLE);
                    monthRecycler3.setVisibility(View.GONE);
                }
                mAdapter3 = new GraphAdapter(getContext(), db.getCompletedMonthModels(11, year - 1));
                String mth3 = new DateFormatSymbols().getMonths()[10];
                txt3.setText(mth3+" , "+(year-1));
            }
        }
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        monthRecycler1.setLayoutManager(mLayoutManager);
        monthRecycler1.setAdapter(mAdapter1);
        mAdapter1.notifyDataSetChanged();
        LinearLayoutManager mLayoutManager2 = new LinearLayoutManager(getContext());
        mLayoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        monthRecycler2.setLayoutManager(mLayoutManager2);
        monthRecycler3.setAdapter(mAdapter2);
        mAdapter2.notifyDataSetChanged();
        LinearLayoutManager mLayoutManager3 = new LinearLayoutManager(getContext());
        mLayoutManager3.setOrientation(LinearLayoutManager.HORIZONTAL);
        monthRecycler3.setLayoutManager(mLayoutManager3);
        monthRecycler3.setAdapter(mAdapter3);
        mAdapter3.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Usage History");
    }
}
