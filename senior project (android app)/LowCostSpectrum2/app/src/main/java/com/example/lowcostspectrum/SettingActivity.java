package com.example.lowcostspectrum;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SettingActivity extends AppCompatActivity {
    TextView number_n;
    private Uri imageUri;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        number_n = (TextView) findViewById(R.id.number_n);
        imageView = (ImageView) findViewById(R.id.ImageSetting);

    }
    public void add_n (View view){
         int n = Integer.parseInt(number_n.getText().toString());
         n++;
         number_n.setText(String.valueOf(n));

    }
    public void minus_n (View view){
        int n = Integer.parseInt(number_n.getText().toString());
        n--;
        number_n.setText(String.valueOf(n));
    }
    public  void PickImage(View view){

        CropImage.activity().start(SettingActivity.this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);


            if(resultCode == RESULT_OK){
                imageUri = result.getUri();
                imageView.setImageURI(imageUri);
            }
            else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){

                Exception e = result.getError();
                Toast.makeText(this,"Possible error is : " + e , Toast.LENGTH_SHORT).show();
            }

        }
    }
    public void analyzeImage() {
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap imageBitmap = drawable.getBitmap();
        double R, G, B;
        int colorPixel;
        int width = imageBitmap.getWidth();
        int height = imageBitmap.getHeight();
        double[][] imageArray = new double[width][3];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                colorPixel = imageBitmap.getPixel(x, y);
                R = Color.red(colorPixel);
                G = Color.green(colorPixel);
                B = Color.blue(colorPixel);

                R = ((0.07 * R) + (0.52 * G) + (0.23 * B)) / 255;
                imageArray[x][0] = R / 255;
                imageArray[x][1] = R / 255;
                imageArray[x][2] = R / 255;


            }
        }

        double[][] M = new double[][]{{0.49, 0.31, 0.20}, {0.17697, 0.81240, 0.01063}, {0.00, 0.01, 0.99}};
        double X, Y, Z;
        List<Double> Xarr = new ArrayList<Double>();
        List<Double> Yarr = new ArrayList<Double>();
        List<Double> Zarr = new ArrayList<Double>();

        for (int i = 0; i < width; i++) {
            X = M[0][0] * imageArray[i][2] + M[0][1] * imageArray[i][1] + M[0][2] * imageArray[i][0];
            Y = M[1][0] * imageArray[i][2] + M[1][1] * imageArray[i][1] + M[1][2] * imageArray[i][0];
            Z = M[2][0] * imageArray[i][2] + M[2][1] * imageArray[i][1] + M[2][2] * imageArray[i][0];
            Xarr.add(X * 1000);
            Yarr.add(Y * 10);
            Zarr.add(Z * 10);


        }
        int peak = 0;
        for (int i = 0; i < Xarr.size(); i++) {
            if (Xarr.get(i) == Collections.max(Xarr)) {
                peak = i;
            }
        }
        Log.d("1234","" + peak);
        Intent intent = new Intent(getApplicationContext(),analyzeGraph.class);
        intent.putExtra("peak", peak);
        intent.putExtra("number_n",Integer.parseInt(number_n.getText().toString()) );
        startActivity(intent);
//        intent.putExtra("order_n",Integer.parseInt(number_n.getText().toString()));

    }
    public void send_parameter(View view){
//        Intent intent = new Intent(getApplicationContext(),analyzeGraph.class);
          analyzeImage();
//        Log.d("peak",""+analyzeImage());
//        intent.putExtra("peak", analyzeImage());
//        intent.putExtra("order_n",Integer.parseInt(number_n.getText().toString()));
//        startActivity(intent);
//        finish();
    }
}
