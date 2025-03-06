package com.example.mysignalchecking;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.CellInfo;
import android.telephony.TelephonyManager;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private LocationManager locationManager;
    private TelephonyManager telephonyManager;
    private TextView locationText, signalText, cellInfoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationText = findViewById(R.id.locationText);
        signalText = findViewById(R.id.signalText);
        cellInfoText = findViewById(R.id.cellInfoText);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        // Проверка и запрос разрешений
        if (checkPermissions()) {
            startLocationUpdates();
            getSignalInfo();
            getCellInfo();
        } else {
            requestPermissions();
        }
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_PHONE_STATE
                ) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_PHONE_STATE
                },
                1
        );
    }

    private void startLocationUpdates() {
        if (checkPermissions()) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000L, // Интервал обновления (1 секунда)
                    1f, // Минимальное расстояние для обновления (1 метр)
                    this
            );
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        locationText.setText("Местоположение: " + latitude + ", " + longitude);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(@NonNull String provider) {}

    @Override
    public void onProviderDisabled(@NonNull String provider) {}

    private void getSignalInfo() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE
        ) == PackageManager.PERMISSION_GRANTED) {
            int signalStrength = telephonyManager.getSignalStrength().getLevel();
            signalText.setText("Сигнал: " + signalStrength + " dBm");
        }
    }

    private void getCellInfo() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED) {
            List<CellInfo> cellInfoList = telephonyManager.getAllCellInfo();
            if (cellInfoList != null) {
                for (CellInfo info : cellInfoList) {
                    if (info.isRegistered()) {
                        String cellId = info.getCellIdentity().toString();
                        String signal = info.getCellSignalStrength().toString();
                        cellInfoText.setText("Данные ячейки: " + cellId + ", Сигнал: " + signal);
                        break;
                    }
                }
            }
        }
    }
}