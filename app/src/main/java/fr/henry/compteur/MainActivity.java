package fr.henry.compteur;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.DecimalFormat;


public class MainActivity extends AppCompatActivity implements LocationListener {

    private float averageSpeed=0;
    private int mesures=0;
    private static DecimalFormat df = new DecimalFormat("0.00");
    private LocationManager lm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        launchLocalisation();
    }

    @Override
    public void onLocationChanged(Location location) {

        TextView currentSpeedTxt = findViewById(R.id.speed_value);

        if (location==null){

            currentSpeedTxt.setText(getString(R.string.speed_none));
        } else {
            float currentSpeed = location.getSpeed() * 3.6f;
            currentSpeedTxt.setText(String.format(getString(R.string.speed_value),df.format(currentSpeed)));

            if (location.getSpeed()>0.1)
                updateAverageSpeed(location.getSpeed());
            else if(averageSpeed!=0 && mesures>5)
                navigateToRecapScreen();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchLocalisation();
            } else {
                new MaterialAlertDialogBuilder(this)
                        .setMessage(getString(R.string.no_permission))
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .show();
            }
        }

    }

    private void launchLocalisation(){
        lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}, 1);
        }
        if (lm != null){
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
        }
        Toast.makeText(this,getString(R.string.waiting_connexion), Toast.LENGTH_SHORT).show();
    }

    private void updateAverageSpeed(float speed){
        mesures++;
        if(averageSpeed==0)
            averageSpeed= speed;
        else
            averageSpeed= ((averageSpeed+speed)/2);
    }

    private void navigateToRecapScreen(){

        Intent intent = new Intent(this, RecapActivity.class);
        Bundle extras = new Bundle();
        extras.putFloat(RecapActivity.AVERAGE_SPEED , averageSpeed);
        intent.putExtras(extras);
        startActivity(intent);

        //on réinitialise les données
        averageSpeed=0;
        mesures=0;
    }

    @Override
    protected void onPause() {
        super.onPause();
        lm.removeUpdates(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        launchLocalisation();
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}