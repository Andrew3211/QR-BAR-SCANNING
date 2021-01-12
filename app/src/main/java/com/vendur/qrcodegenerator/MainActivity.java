package com.vendur.qrcodegenerator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class MainActivity extends AppCompatActivity {
    private Button generate,scan;
    private EditText my_text;
    private ImageView qr_code;
    private Toast backToast, notificationToast;
    private long backPressedTime;
    private MediaPlayer btnSound;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        generate = findViewById(R.id.buttonGenerate);
        scan = findViewById(R.id.buttonScan);
        my_text = findViewById(R.id.text);
        qr_code = findViewById(R.id.qr_code);

        btnSound = MediaPlayer.create(this, R.raw.sound);


//код для кнопки Generate - начало
        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = my_text.getText().toString();
                playSound(btnSound);
                if (text != null && !text.isEmpty()) {
                    try{
                        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                        BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 500, 500);
                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                        qr_code.setImageBitmap(bitmap);
                    } catch (WriterException e){
                        e.printStackTrace();
                    }
                }
            }
        });
//код для кнопки Generate - конец


//код для кнопки Scan - начало
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSound(btnSound);
                IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                intentIntegrator.setCameraId(0);
                intentIntegrator.setOrientationLocked(false);
                intentIntegrator.setPrompt("Scanning...");
                intentIntegrator.setBeepEnabled(true);
                intentIntegrator.setBarcodeImageEnabled(true);
                intentIntegrator.initiateScan();
            }
        });
    }
//код для кнопки Scan - конец


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if (result != null && result.getContents() != null){


            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Scan result")
                    .setMessage(result.getContents())
                    .setPositiveButton("Copy", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            notificationToast = Toast.makeText(getBaseContext(),"Copied successfully", Toast.LENGTH_SHORT);
                            notificationToast.show();
                            ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                            ClipData data = ClipData.newPlainText("result", result.getContents());
                            manager.setPrimaryClip(data);
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }) .create().show();

        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    //сис.кнопка НАЗАД - начало
    @Override
    public void onBackPressed() {
        if(backPressedTime + 2000 > System.currentTimeMillis()){
            backToast.cancel();
            super.onBackPressed();
            return;
        } else  {
            backToast = Toast.makeText(getBaseContext(), "Click again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }

        backPressedTime = System.currentTimeMillis();
    }
    //сис.кнопка НАЗАД - конец


//код для звука при нажатии на кнопку - начало
    private void playSound(MediaPlayer btnSound) {
        btnSound.start();
    }
//код для звука при нажатии на кнопку - конец
}