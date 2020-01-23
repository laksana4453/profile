package com.example.lowcostspectrum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class analyzeGraph extends AppCompatActivity {
    private final int PICK_IMAGE = 1;
    private Uri imageUri;
    private ImageView mImageView;
    private LineChart chart;
    private HorizontalScrollView contentGraph;
    private final int CODE_IMG_GALLERY = 1;
    private final String SAMPLE_CROPPED_IMG_NAME = "SampleCropImg";
    private TextView peak_txt,n_txt,peak_txt2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze_graph);
        init();
        peak_txt = (TextView) findViewById(R.id.peak);
        peak_txt2 = (TextView) findViewById(R.id.peak2);
        n_txt = (TextView) findViewById(R.id.n);
        int peak = getIntent().getIntExtra("peak",0);
        int n = getIntent().getIntExtra("number_n",0) ;
        Log.d("1234",n + " " + peak);
        peak_txt.setText(String.valueOf(peak));
        n_txt.setText(String.valueOf(n));


        chart = (LineChart) findViewById(R.id.chart1);
        contentGraph = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);

//        if(savedInstanceState!=null){
//            String stringUri = savedInstanceState.getString("UriString");
//            imageUri = Uri.parse(stringUri);
//            mImageView.setImageURI(imageUri);
//        }

    }
    public double calculate(double x) {
        int peak = Integer.parseInt(peak_txt.getText().toString());
        int n = Integer.parseInt(n_txt.getText().toString());
        double d = 1.0/(600*Math.pow(10,3));
        double lamda = 460/Math.pow(10,9);
        double l = Math.sqrt(Math.pow((d*peak)/(n*lamda),2)- Math.pow(peak,2));
        double sin = x/Math.sqrt(Math.pow(x,2)+Math.pow(l,2));
        return   (d*sin)/n *Math.pow(10,9) + 5;
    }
    private void init() {
        this.mImageView = findViewById(R.id.ImageView);
    }
    public void analyzeImage(View view){
        contentGraph.setVisibility(View.VISIBLE);
        BitmapDrawable drawable = (BitmapDrawable) mImageView.getDrawable();
        Bitmap imageBitmap = drawable.getBitmap();
        double  R ,G ,B;
        int colorPixel;
        int width = imageBitmap.getWidth();
        int height = imageBitmap.getHeight();
        double[][] imageArray= new double[width][3];

        for(int x = 0; x < width; x++ ){
            for(int y = 0; y < height;y++){
                colorPixel = imageBitmap.getPixel(x,y);
                R = Color.red(colorPixel);
                G = Color.green(colorPixel);
                B = Color.blue(colorPixel);

                R =  ((0.07*R)+(0.52*G)+(0.23*B))/255;
                imageArray[x][0] = R/255;
                imageArray[x][1] = R/255;
                imageArray[x][2] = R/255;


            }
        }

        double[][] M = new double[][] { { 0.49, 0.31, 0.20 }, { 0.17697, 0.81240, 0.01063 }, { 0.00, 0.01, 0.99 } };
        double X,Y,Z;
        List<Double> Xarr= new ArrayList<Double>();
        List<Double> Yarr= new ArrayList<Double>();
        List<Double> Zarr= new ArrayList<Double>();

        for(int i = 0;i< width;i++ ){
            X = M[0][0]*imageArray[i][2] + M[0][1]*imageArray[i][1] + M[0][2]*imageArray[i][0];
            Y = M[1][0]*imageArray[i][2] + M[1][1]*imageArray[i][1] + M[1][2]*imageArray[i][0];
            Z = M[2][0]*imageArray[i][2] + M[2][1]*imageArray[i][1] + M[2][2]*imageArray[i][0];
            Xarr.add(X*1000);
            Yarr.add(Y*10);
            Zarr.add(Z*10);


        }
        createGraph(Xarr);

    }
    private void createGraph(List<Double> Yaxis) {
        chart.setBackgroundColor(Color.WHITE);

        // disable description text
        chart.getDescription().setEnabled(false);

        // enable touch gestures
        chart.setTouchEnabled(true);

        // set listeners

        chart.setDrawGridBackground(false);

        // create marker to display box when values are selected
        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);

        // Set the marker to the chart
        mv.setChartView(chart);
        chart.setMarker(mv);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        // chart.setScaleXEnabled(true);
        // chart.setScaleYEnabled(true);

        // force pinch zoom along both axis
        chart.setPinchZoom(true);
        // draw points over time
        chart.animateX(1500);

        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();

        // draw legend entries as lines
        l.setForm(Legend.LegendForm.LINE);
        ArrayList<Entry> values = new ArrayList<>();
        // add point in graph
        Log.d("1234","maxValue"+Collections.max(Yaxis));

        List<Double> Xarr= new ArrayList<Double>();
        double[] peak = new double[1];
        for(int i = 0 ; i < Yaxis.size() ; i++) {
            if (Yaxis.get(i) == Collections.max(Yaxis)) {
                peak[0] = i;
            }
        }
        Log.d("789",peak[0]+"");
        peak_txt2.setText(String.valueOf(peak[0]));

        for(int i = 0 ; i < Yaxis.size() ; i++){

            Float y = Float.valueOf("" + Yaxis.get(i));
            Double x = Double.valueOf(i);
//            Log.d("123","peak-x : "+peak[0]);

            values.add(new Entry( Float.valueOf((float) calculate(x)),y));

        }

//        values.add(new Entry(0, 4));

        LineDataSet set1;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.notifyDataSetChanged();
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, "DataSet 1");

            set1.setDrawIcons(false);

            // draw dashed line
            set1.enableDashedLine(10f, 5f, 0f);

            // black lines and points
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);

            // line thickness and point size
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);

            // draw points as solid circles
            set1.setDrawCircleHole(false);

            // customize legend entry
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);

            // text size of values
            set1.setValueTextSize(9f);

            // draw selection line as dashed
            set1.enableDashedHighlightLine(10f, 5f, 0f);

            // set the filled area
            set1.setDrawFilled(true);
            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return chart.getAxisLeft().getAxisMinimum();
                }
            });

            // set color of filled area
            if (Utils.getSDKInt() >= 18) {
                // drawables only supported on api level 18 and above
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_red);
                set1.setFillDrawable(drawable);
            } else {
                set1.setFillColor(Color.BLACK);
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1); // add the data sets

            // create a data object with the data sets
            LineData data = new LineData(dataSets);

            // set data
            chart.setData(data);

        }
    }
    public  void PickImage(View view){

        CropImage.activity().start(analyzeGraph.this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);


            if(resultCode == RESULT_OK){
                imageUri = result.getUri();
                mImageView.setImageURI(imageUri);
            }
            else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){

                Exception e = result.getError();
                Toast.makeText(this,"Possible error is : " + e , Toast.LENGTH_SHORT).show();
            }

        }
    }
    public void setting_mod(View view){
        Intent intent = new Intent(getApplicationContext(),SettingActivity.class);
        startActivity(intent);
        finish();
    }
//    @Override
//    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
//        super.onSaveInstanceState(outState, outPersistentState);
//        outState.putString("UriString",imageUri.toString());
//    }
//
//    @Override
//    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        mImageView.setImageURI( Uri.parse(savedInstanceState.getString("UriString")));
//    }
}
