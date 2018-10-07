package com.example.duchoang.blinkingled;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManager;
import java.util.List;
import java.io.IOException;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Handler mHandler = new Handler();
    private Gpio mLedGpioGreen;
    private Gpio mLedGpioRed;
    private Gpio mLedGpioBlue;
    private int ledCounter = 0;
    private int state;

    @Override
    // Set up things
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Starting BlinkActivity");

        try {
            // 
            String pinName = BoardDefaults.getGPIOForLED();

            // Declare which GPIO ports to be used
            mLedGpioRed = PeripheralManager.getInstance().openGpio("BCM2");
            mLedGpioGreen = PeripheralManager.getInstance().openGpio("BCM3");
            mLedGpioBlue = PeripheralManager.getInstance().openGpio("BCM4");

            // Define them as outputs
            mLedGpioRed.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH);
            mLedGpioGreen.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH);
            mLedGpioBlue.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH);

            // Assign initial states
            mLedGpioRed.setActiveType(Gpio.ACTIVE_LOW);
            mLedGpioGreen.setActiveType(Gpio.ACTIVE_LOW);
            mLedGpioBlue.setActiveType(Gpio.ACTIVE_LOW);

            // Init. the first state
            state = 0;

            // Post handle
            mHandler.post(mBlinkRunnable);

        } catch (IOException e) {
            Log.e(TAG, "Error on PeripheralIO API", e);
        }

    }

    private Runnable mBlinkRunnable = new Runnable() {
        @Override
        public void run() {
            if (mLedGpioBlue == null || mLedGpioGreen == null || mLedGpioRed == null) {
                return;
            }
            try {
                // Toggle between states
                switch(state) {
                    case 0:
                        // After any LEDs ON, any states must get back to state of LEDs OFF all.
                        mLedGpioRed.setValue(false);
                        mLedGpioGreen.setValue(false);
                        mLedGpioBlue.setValue(false);
                        ledCounter++;
                        if (ledCounter == 5 || ledCounter == 9) {
                            state = 2;
                        }
                        else if (ledCounter == 7) {
                            state = 4;
                        }
                        else if (ledCounter == 13) {
                            state = 3;
                        }
                        else {
                            state = 1;
                        }
                        break;
                    case 1:
                        // RGB: 100
                        mLedGpioRed.setValue(true);
                        mLedGpioGreen.setValue(false);
                        mLedGpioBlue.setValue(false);
                        state = 0;
                        break;
                    case 2:
                        // RGB: 110
                        mLedGpioRed.setValue(true);
                        mLedGpioGreen.setValue(true);
                        mLedGpioBlue.setValue(false);
                        state = 0;
                        break;
                    case 3:
                        // RGB: 111
                        mLedGpioRed.setValue(true);
                        mLedGpioGreen.setValue(true);
                        mLedGpioBlue.setValue(true);
                        state = 0;
                        ledCounter = 0;
                        break;
                    case 4:
                        // RGB: 101
                        mLedGpioRed.setValue(true);
                        mLedGpioGreen.setValue(false);
                        mLedGpioBlue.setValue(true);
                        state = 0;
                        break;
                    default:
                        break;
                }

                // 250 mil for the smallest pace as of RED LED (0.5s)
                mHandler.postDelayed(mBlinkRunnable, 250);

            } catch (IOException e) {
                Log.e(TAG, "Error on PeripheralIO API", e);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove pending blink Runnable from the handler.
        mHandler.removeCallbacks(mBlinkRunnable);
        Log.i(TAG, "Closing LED GPIO pin");
        try {
            mLedGpioGreen.close();
            mLedGpioBlue.close();
            mLedGpioRed.close();
        } catch (IOException e) {
            Log.e(TAG, "Error on PeripheralIO API", e);
        } finally {
            mLedGpioRed = null;
            mLedGpioBlue = null;
            mLedGpioGreen = null;
        }
    }
}