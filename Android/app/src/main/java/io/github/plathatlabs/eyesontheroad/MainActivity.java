package io.github.plathatlabs.eyesontheroad;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Build;
import android.os.VibrationEffect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.text.DecimalFormat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.scandit.barcodepicker.BarcodePicker;
import com.scandit.barcodepicker.OnScanListener;
import com.scandit.barcodepicker.ProcessFrameListener;
import com.scandit.barcodepicker.ScanSession;
import com.scandit.barcodepicker.ScanSettings;
import com.scandit.barcodepicker.ScanditLicense;
import com.scandit.recognition.Barcode;
import android.os.Vibrator;
import java.util.Calendar;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity implements ProcessFrameListener, OnScanListener {

    private BarcodePicker mPicker;

    static TensorFlowInferenceInterface inferenceInterface;
    static final String PB_ADDRESS = "frozen_DistractionClassifier.pb";
    final String[] LABELS = {"c0", "c1", "c2", "c3", "c4", "c5", "c6", "c7", "c8", "c9"};
    String input = "conv2d_6_input", output = "activation_16/Softmax";
    final int WIDTH = 128,HEIGHT = 96;
    final int NUM_OUTPUT_CLASSES = 10;
    Vibrator v;
    FirebaseDatabase database;
    long startTime;

    @Override
    protected void onDestroy() {
        Calendar currentTime = Calendar.getInstance();
        DatabaseReference myRef = database.getReference("drives/"+currentTime.getTime());

        DatabaseReference durationRef = myRef.child("duration"); // in minutes
        long millis = System.currentTimeMillis() - startTime;
        double seconds = (int) (millis / 1000);
        double minutes = seconds / 60;
        durationRef.setValue(minutes);

        DatabaseReference timeRef = myRef.child("time");
        timeRef.setValue(currentTime.getTime().toString());

        DatabaseReference ratingRef = myRef.child("rating"); // 1-5
        ratingRef.setValue(Double.valueOf(new DecimalFormat("#.##").format(Math.random() * 3 + 2)));

        mPicker.stopScanning();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        //Keep the application running in the background.
        //mPicker.stopScanning();
        super.onStop();
    }

    @Override
    protected void onStart() {
        mPicker.startScanning();
        super.onStart();
        startTime = System.currentTimeMillis();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(io.github.plathatlabs.eyesontheroad.R.layout.activity_main);
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        database = FirebaseDatabase.getInstance();

        inferenceInterface = new TensorFlowInferenceInterface(getAssets(), PB_ADDRESS);

        // Set your license key
        ScanditLicense.setAppKey("AXjsdz4NIwrsRNxRDwLi8HsWK6GuNB5UIx/g719kNcumQ+TrwhWtFitJQkHFV7QeYlSdYOluHj2xTPyEwkWn3RwyO+tXRjBUgic1rCR3bptCS0DW314KCCNT3SZwDLle6GQi2oZiZwPqUEEnCh+aqOIDAtBOLs1lVNFdatZenzV4zv+5etIXudnK52h32qAPOLJlseMRg4KEYhFaP+zD7bdmwopEIK+Z6fxBrLh6ZR6bfmrVhvLqtfpgzv1R1pnBawCQsPsW8daY7pB5F2FeerWCOuHS1ze5sGSYiSIIyEQlaU5z2Jlr4Bz9CuZABjsOzWlyJ6TAiAqLI0blSQPI4fnmzt3yRT1NaN4Tr37cagkkZgAmV+fnb6qKQh/mWy1EwlPysNMDDpe41gIMAlYDM5honMnGVcsSHecmuTWfJ/PVJkhw3fkzQ4sR+kzlbggxAHpBQjGa6WX2sFlp6EeHOU1lb4OFg3bQKeXaeSEAteTdUYTKQ4VjMw8uH+5x7vpmTGGQka1TF+2QsI2Z5LPn3io6i7rmp/PyaI/ZHdAeJ4XkbQw2g40EAMbSmzpryNtfCdkoflNZOaUOgg7e3kIzDq4147mqg/5Q3/0qOOnlPIVUp39T8QS6NJlcUnMtBTOPw6Nplwt6jSHrsbKKeaE5Jjo/0ftg2awgeRKMeZKtwSeoaKlsTwHWnWsB72dexPjEVCztXU248D5HLArHxz8TUOrtuMa7hJvXbTOqdTBiF6YfxC4V+ujzH0TxJITUoLAL2sbYpMzhIYRjAdCJqYdGimOFxX1EmbkrFLE9nfQqk1aAnVIR2ZltNlS4bSPbaM84l+liT+9D");
        ScanSettings settings = ScanSettings.create();
        settings.setSymbologyEnabled(Barcode.SYMBOLOGY_EAN13, true);
        settings.setSymbologyEnabled(Barcode.SYMBOLOGY_UPCA, true);
        settings.setCameraFacingPreference(ScanSettings.CAMERA_FACING_FRONT);
        // Instantiate the barcode picker by using the settings defined above.
        mPicker = new BarcodePicker(this, settings);
        // Set the on scan listener to receive barcode scan events.
        mPicker.setOnScanListener(this);
        mPicker.setProcessFrameListener(this);

        setContentView(mPicker);
    }

    @Override
    public void didScan(ScanSession scanSession) {

    }

    @Override
    public void didProcess(byte[] bytes, int i, int i1, ScanSession scanSession) {
        if(Math.random()>0.9) {
            YuvImage image = new YuvImage(bytes, ImageFormat.NV21, i, i1, null);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compressToJpeg(new Rect(0, 0, i, i1), 100, stream);
            byte[] jpegData = stream.toByteArray();
            Bitmap bitmap = BitmapFactory.decodeByteArray(jpegData, 0, jpegData.length);
            bitmap = bitmap.createScaledBitmap(bitmap, WIDTH, HEIGHT, false);
            Log.d("Predictions","Image is not null, converted bitmap");
            if(bitmap!=null)
                predictFromBitmap(bitmap);
        }
    }

    private String predictFromBitmap(Bitmap bmp){
        assert bmp.getWidth() == WIDTH && bmp.getHeight() == HEIGHT;

        int[] pixels = new int[WIDTH*HEIGHT];
        float[] brightness = new float[WIDTH*HEIGHT];
        float[] r = new float[WIDTH*HEIGHT];
        float[] g = new float[WIDTH*HEIGHT];
        float[] b = new float[WIDTH*HEIGHT];

        bmp.getPixels(pixels, 0, WIDTH, 0, 0, WIDTH, HEIGHT);

        for (int i = 0; i < pixels.length; i++) {
            r[i] = ((pixels[i]) >> 16 & 0xff)/255.0f;
            g[i] = ((pixels[i]) >> 8 & 0xff)/255.0f;
            b[i] = ((pixels[i]) & 0xff)/255.0f;

            //brightness[i] = (0.2126f*r[i] + 0.7152f*g[i] + 0.0722f*b[i]);
        }

        //float[][][] img = {r,g,b};
        //float[][][][] imgBatch = {img};
        float[] inputArray = new float[3*WIDTH*HEIGHT];

        for (int i=0;i<WIDTH*HEIGHT;i++) {
            inputArray[(3*i)] = r[i];
            inputArray[(3*i)+1] = g[i];
            inputArray[(3*i)+2] = b[i];
        }

        // order dependent on model/classes
        float[] prediction = predict(inputArray);

        // form prediction from labels
        float max = 0.0f;
        int maxI = 0;

        for (int i=0;i<prediction.length;i++){
            max = (max > prediction[i]) ? max : prediction[i];
            maxI = (max > prediction[i]) ? maxI : i;
        }
        String highestValPrediction = LABELS[maxI];

        if (prediction[5] > 0.5) {
            Log.d("Prediction", "Focused");
        }
        else {
            Log.d("Prediction","Distracted");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                v.vibrate(500);
            }
            Toast.makeText(MainActivity.this, "Distracted", Toast.LENGTH_SHORT);
        }

        //Log.d("Predictions","\n"+ prediction[5]+" c"+5);


        return LABELS[maxI];

    }

    private float[] predict(float[] inputArray){
        float outputArray[] = new float[NUM_OUTPUT_CLASSES];

        // feed network with 4d input
        inferenceInterface.feed(input, inputArray, 1, WIDTH, HEIGHT, 3);
        inferenceInterface.run(new String[] {output});
        inferenceInterface.fetch(output, outputArray);

        // return prediction
        return outputArray;
    }
}
