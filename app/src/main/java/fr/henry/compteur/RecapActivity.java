package fr.henry.compteur;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;

public class RecapActivity extends AppCompatActivity {

    static final String AVERAGE_SPEED = "averageSpeed";
    private Button mButton;
    private static DecimalFormat df = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recap);

        Bundle extras = getIntent().getExtras();
        float averageSpeed =0;
        if(extras!=null)
            averageSpeed = extras.getFloat(AVERAGE_SPEED,0);
        averageSpeed = averageSpeed* 3.6f; //passage en km/h

        TextView averageSpeedTxt = findViewById(R.id.average_speed_value);
        averageSpeedTxt.setText(String.format(getString(R.string.speed_value),df.format(averageSpeed)));

        mButton = findViewById(R.id.close_btn);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });
    }
}
