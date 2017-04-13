package com.valevich.stormy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.valevich.stormy.R;
import com.valevich.stormy.adapters.HourAdapter;
import com.valevich.stormy.weather.Hour;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.animators.adapters.SlideInLeftAnimationAdapter;

public class HourlyForecastActivity extends AppCompatActivity {

    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hourly_forecast);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        Parcelable[] parcelable = intent.getParcelableArrayExtra(MainActivity.HOURLY_FORECAST);
        Hour[] mHours = Arrays.copyOf(parcelable, parcelable.length, Hour[].class);

        HourAdapter adapter = new HourAdapter(mHours,this);
        SlideInLeftAnimationAdapter animationAdapter = new SlideInLeftAnimationAdapter(adapter);
        animationAdapter.setDuration(300);
        mRecyclerView.setAdapter(animationAdapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

    }
}
