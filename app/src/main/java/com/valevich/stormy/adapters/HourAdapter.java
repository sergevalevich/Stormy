package com.valevich.stormy.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.valevich.stormy.R;
import com.valevich.stormy.weather.Hour;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by NotePad.by on 24.11.2015.
 */
public class HourAdapter extends RecyclerView.Adapter<HourAdapter.HourViewHolder> {

    private Hour[] mHours;
    private Context mContext;

    public HourAdapter(Hour[] hours,Context c) {
        mContext = c;
        mHours = hours;
    }

    @Override
    public HourViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.hourly_list_item,parent,false);
        return new HourViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HourViewHolder holder, int position) {
        holder.bindHour(mHours[position]);
    }

    @Override
    public int getItemCount() {
        return mHours.length;
    }

    public class HourViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener

    {
        @Bind(R.id.timeLabel) TextView mTimeLabel;
        @Bind(R.id.temperatureLabel)TextView mTemperatureLabel;
        @Bind(R.id.summaryLabel)TextView mSummaryLabel;
        @Bind(R.id.iconImageView)ImageView mIconImageView;


        public HourViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }

        public void bindHour (Hour hour) {
            mTemperatureLabel.setText(hour.getTemperature() + "");
            mSummaryLabel.setText(hour.getSummary());
            mIconImageView.setImageResource(hour.getIconId());
            mTimeLabel.setText(hour.getHourOfTheDay());
        }

        @Override
        public void onClick(View v) {
            String time = mTimeLabel.getText().toString();
            String temperature = mTemperatureLabel.getText().toString();
            String summary = mSummaryLabel.getText().toString();
            String message = String.format(mContext.getString(R.string.hourly_summary_message),time,temperature,summary);
            Toast.makeText(mContext,message,Toast.LENGTH_LONG).show();
        }
    }

}
