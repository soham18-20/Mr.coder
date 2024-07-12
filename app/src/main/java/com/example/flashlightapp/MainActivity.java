package com.example.flashlightapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private ImageButton buttonFlashlight;
    private Button buttonSOS, buttonSlowBlink;
    private boolean isFlashlightOn = false;
    private boolean isSOSOn = false;
    private boolean isSlowBlinkOn = false;
    private CameraManager cameraManager;
    private String cameraId;

    private static final int CAMERA_REQUEST = 50;
    private Handler handler = new Handler();
    private Runnable sosRunnable, slowBlinkRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonFlashlight = findViewById(R.id.button_flashlight);
        buttonSOS = findViewById(R.id.button_sos);
        buttonSlowBlink = findViewById(R.id.button_slow_blink);

        cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);

        try {
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        boolean isFlashAvailable = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (!isFlashAvailable) {
            buttonFlashlight.setEnabled(false);
            buttonSOS.setEnabled(false);
            buttonSlowBlink.setEnabled(false);
        } else {
            buttonFlashlight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
                    } else {
                        toggleFlashlight();
                    }
                }
            });

            buttonSOS.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
                    } else {
                        if (isSOSOn) {
                            stopSOS();
                        } else {
                            startSOS();
                        }
                    }
                }
            });

            buttonSlowBlink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
                    } else {
                        if (isSlowBlinkOn) {
                            stopSlowBlink();
                        } else {
                            startSlowBlink();
                        }
                    }
                }
            });
        }
    }

    private void toggleFlashlight() {
        if (isFlashlightOn) {
            turnOffFlashlight();
        } else {
            turnOnFlashlight();
        }
    }

    private void turnOnFlashlight() {
        try {
            cameraManager.setTorchMode(cameraId, true);
            buttonFlashlight.setImageResource(R.drawable.ic_flashlight_on);
            isFlashlightOn = true;
            showToast("Flashlight turned ON");
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void turnOffFlashlight() {
        try {
            cameraManager.setTorchMode(cameraId, false);
            buttonFlashlight.setImageResource(R.drawable.ic_flashlight_off);
            isFlashlightOn = false;
            showToast("Flashlight turned OFF");
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void startSOS() {
        isSOSOn = true;
        buttonSOS.setText(R.string.sos_on);
        sosRunnable = new Runnable() {
            @Override
            public void run() {
                if (isSOSOn) {
                    toggleFlashlight();
                    handler.postDelayed(this, isFlashlightOn ? 200 : 500);
                }
            }
        };
        handler.post(sosRunnable);
        showToast("SOS started");
    }

    private void stopSOS() {
        isSOSOn = false;
        handler.removeCallbacks(sosRunnable);
        buttonSOS.setText(R.string.sos_off);
        if (isFlashlightOn) {
            turnOffFlashlight();
        }
        showToast("SOS stopped");
    }

    private void startSlowBlink() {
        isSlowBlinkOn = true;
        buttonSlowBlink.setText(R.string.slow_blink_on);
        slowBlinkRunnable = new Runnable() {
            @Override
            public void run() {
                if (isSlowBlinkOn) {
                    toggleFlashlight();
                    handler.postDelayed(this, 1000);
                }
            }
        };
        handler.post(slowBlinkRunnable);
        showToast("Slow blink started");
    }

    private void stopSlowBlink() {
        isSlowBlinkOn = false;
        handler.removeCallbacks(slowBlinkRunnable);
        buttonSlowBlink.setText(R.string.slow_blink_off);
        if (isFlashlightOn) {
            turnOffFlashlight();
        }
        showToast("Slow blink stopped");
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                toggleFlashlight();
            }
        }
    }
}

