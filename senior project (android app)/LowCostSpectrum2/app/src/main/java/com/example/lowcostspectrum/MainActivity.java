package com.example.lowcostspectrum;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    sleep(2*1000);
                    Intent intent = new Intent(getApplicationContext(),analyzeGraph.class);
                    startActivity(intent);
                    finish();

                }catch (Exception ex){

                }
            }
        };
        thread.start();
    }
}
