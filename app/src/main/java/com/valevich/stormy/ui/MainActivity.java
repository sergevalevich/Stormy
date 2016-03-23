package com.valevich.stormy.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.plattysoft.leonids.ParticleSystem;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.valevich.stormy.location.LocationProvider;
import com.valevich.stormy.R;
import com.valevich.stormy.weather.Current;
import com.valevich.stormy.weather.Day;
import com.valevich.stormy.weather.Forecast;
import com.valevich.stormy.weather.Hour;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements LocationProvider.LocationCallback {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String DAILY_FORECAST = "DAILY_FORECAST";
    public static final String HOURLY_FORECAST = "HOURLY_FORECAST";
    public static final String LOCATION = "LOCATION";

    private Forecast mForecast;
    private LocationProvider mLocationProvider;
    private Alerter mAlerter = new Alerter();

    @Bind(R.id.temperatureLabel)
    TextView mTemperatureLabel;
    @Bind(R.id.humidityValue)
    TextView mHumidityValue;
    @Bind(R.id.precipValue)
    TextView mPrecipValue;
    @Bind(R.id.summaryLabel)
    TextView mSummaryLabel;
    @Bind(R.id.iconImageView)
    ImageView mIconImageView;
    @Bind(R.id.refreshImageView)
    ImageView mRefreshImageView;
    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.locationLabel)
    TextView mLocationLabel;
    @Bind(R.id.dailyButton)
    Button mDailyButton;
    @Bind(R.id.hourlyButton)
    Button mHourlyButton;
    @Bind(R.id.relativeLayout)
    RelativeLayout mRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mLocationProvider = new LocationProvider(this, this);
        mProgressBar.setVisibility(View.INVISIBLE);

        mRefreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getForecast();
            }
        });


        getForecast();
        //letItSnow();


    }


    private void getForecast() {
        if (isNetworkAvailable()) {
            toggleRefresh();
            mLocationProvider.connect();
        } else {
            Toast.makeText(this, R.string.network_unavailable_message,
                    Toast.LENGTH_LONG).show();
            mSummaryLabel.setText(R.string.network_unavailable_message);
        }
    }

    private void getForecast(double latitude, double longitude) {
        String apiKey = getString(R.string.fak);
        String language = getString(R.string.language);
        String forecastUrl = "https://api.forecast.io/forecast/" + apiKey + "/" + latitude + "," + longitude + "?units=us&lang=" + language;


        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(forecastUrl)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        toggleRefresh();
                    }
                });
                mAlerter.alertUserAboutError(MainActivity.this);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        toggleRefresh();
                    }
                });
                try {
                    String jsonData = response.body().string();
                    Log.v(TAG, jsonData);
                    if (response.isSuccessful()) {

                        mForecast = parseForecastDetails(jsonData);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateDisplay();
                            }
                        });

                    } else {
                        mAlerter.alertUserAboutError(MainActivity.this);
                    }
                } catch (IOException | JSONException e) {
                    Log.e(TAG, "Exception caught", e);
                }

            }
        });
    }

    private void toggleRefresh() {
        if (mProgressBar.getVisibility() == View.INVISIBLE) {
            mProgressBar.setVisibility(View.VISIBLE);
            mRefreshImageView.setVisibility(View.INVISIBLE);
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
            mRefreshImageView.setVisibility(View.VISIBLE);
        }

    }

    private void updateDisplay() {
        animateViews();
        mTemperatureLabel.setText(mForecast.getCurrent().getTemperature() + "");
        mHumidityValue.setText(mForecast.getCurrent().getHumidity() + "");
        mPrecipValue.setText(mForecast.getCurrent().getPrecipChance() + "%");
        mSummaryLabel.setText(mForecast.getCurrent().getSummary());

        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), mForecast.getCurrent().getIconId());
        mIconImageView.setImageDrawable(drawable);

        double latitude = mForecast.getCurrent().getLatitude();
        double longitude = mForecast.getCurrent().getLongitude();
        mLocationLabel.setText(mForecast.getCurrent().getCity(this));
    }

    private Forecast parseForecastDetails(String jsonData) throws JSONException {
        Forecast forecast = new Forecast();
        forecast.setCurrent(getCurrentDetails(jsonData));
        forecast.setDailyForecast(getDailyForecast(jsonData));
        forecast.setHourlyForecast(getHourlyForecast(jsonData));

        return forecast;
    }

    private Current getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        JSONObject currently = forecast.getJSONObject("currently");

        Current current = new Current();
        current.setLatitude(forecast.getDouble("latitude"));
        current.setLongitude(forecast.getDouble("longitude"));
        current.setIcon(currently.getString("icon"));
        current.setTemperature(currently.getDouble("temperature"));
        current.setHumidity(currently.getDouble("humidity"));
        current.setPrecipChance(currently.getDouble("precipProbability"));
        current.setSummary(currently.getString("summary"));
        current.setTimezone(forecast.getString("timezone"));


        return current;
    }

    private Hour[] getHourlyForecast(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        JSONObject hourly = forecast.getJSONObject("hourly");
        JSONArray data = hourly.getJSONArray("data");

        Hour[] hours = new Hour[data.length()];
        for (int i = 0; i < data.length(); i++) {
            JSONObject jsonHour = data.getJSONObject(i);
            Hour hour = new Hour();
            hour.setIcon(jsonHour.getString("icon"));
            hour.setSummary(jsonHour.getString("summary"));
            hour.setTemperature(jsonHour.getDouble("temperature"));
            hour.setTime(jsonHour.getLong("time"));
            hour.setTimezone(timezone);

            hours[i] = hour;
        }

        return hours;
    }

    private Day[] getDailyForecast(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        JSONObject daily = forecast.getJSONObject("daily");
        JSONArray data = daily.getJSONArray("data");

        Day[] days = new Day[data.length()];
        for (int i = 0; i < data.length(); i++) {
            JSONObject jsonDay = data.getJSONObject(i);
            Day day = new Day();
            day.setIcon(jsonDay.getString("icon"));
            day.setSummary(jsonDay.getString("summary"));
            day.setTemperatureMax(jsonDay.getDouble("temperatureMax"));
            day.setTime(jsonDay.getLong("time"));
            day.setTimezone(timezone);

            days[i] = day;
        }


        return days;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    private void animateViews() {

        YoYo.with(Techniques.StandUp).
                duration(700).
                playOn(mTemperatureLabel);

        YoYo.with(Techniques.StandUp).
                duration(700).playOn(mPrecipValue);

        YoYo.with(Techniques.StandUp).
                duration(700).playOn(mHumidityValue);

        YoYo.with(Techniques.StandUp).
                duration(700).playOn(mSummaryLabel);

        YoYo.with(Techniques.StandUp).
                duration(700).playOn(mIconImageView);

        YoYo.with(Techniques.StandUp).
                duration(700).playOn(mLocationLabel);


    }

    /*private void letItSnow() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        ParticleSystem snowSystemLeft = new ParticleSystem(this, 80, R.drawable.ic_action_snowflake, 10000);
        snowSystemLeft.setSpeedModuleAndAngleRange(0f, 0.2f, 180, 180)
                .setRotationSpeed(144)
                .setScaleRange(0f,1f)
                .setAcceleration(0.00005f, 90)
                .emit(findViewById(R.id.relativeLayout), 8);
        snowSystemLeft.updateEmitPoint(width, 0);
        ParticleSystem snowSystemRight = new ParticleSystem(this, 80, R.drawable.ic_action_snowflake, 10000);
        snowSystemRight.setSpeedModuleAndAngleRange(0f, 0.2f, 0, 0)
                .setRotationSpeed(144)
                .setScaleRange(0f,1f)
                .setAcceleration(0.00005f, 90)
                .emit(findViewById(R.id.relativeLayout), 8);
    }*/

    @Override
    public void handleNewLocation(Location location) {

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        getForecast(latitude, longitude);

        mLocationProvider.disconnect();

    }

    @OnClick(R.id.dailyButton)
    public void startDailyForecastActivity(View v) {
        if(mForecast != null) {
            Intent intent = new Intent(this, DailyForecastActivity.class);
            intent.putExtra(DAILY_FORECAST, mForecast.getDailyForecast());
            intent.putExtra(LOCATION, mForecast.getCurrent().getCity(MainActivity.this));
            startActivity(intent);
        }
    }

    @OnClick(R.id.hourlyButton)
    public void startHourlyForecastActivity(View view) {
        if(mForecast!=null) {
            Intent intent = new Intent(this, HourlyForecastActivity.class);
            intent.putExtra(HOURLY_FORECAST, mForecast.getHourlyForecast());
            startActivity(intent);
        }
    }




}
