package com.example.flashlightapp;

import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class OffActivity extends AppCompatActivity {

    private CameraManager cameraManager;
    private String cameraId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_off);

        ImageButton btnTurnOn = findViewById(R.id.btnTurnOn);
        btnTurnOn.setOnClickListener(v -> {
            turnOnFlashlight();
            Intent intent = new Intent(OffActivity.this, com.example.flashlightapp.MainActivity.class);
            startActivity(intent);
        });

        cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void turnOnFlashlight() {
        try {
            cameraManager.setTorchMode(cameraId, true);
            Toast.makeText(this, "Flashlight is ON", Toast.LENGTH_SHORT).show();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void turnOffFlashlight() {
        try {
            cameraManager.setTorchMode(cameraId, false);
            Toast.makeText(this, "Flashlight is OFF", Toast.LENGTH_SHORT).show();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
}
