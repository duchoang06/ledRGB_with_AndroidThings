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
    private static final int INTERVAL_BETWEEN_BLINKS_MS = 1000;
    private Handler mHandler = new Handler();
    private Gpio mLedGpioGreen;
    private Gpio mLedGpioRed;
    private Gpio mLedGpioBlue;
    
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
            state = 1;

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
                // Toggle between states using a simple finite state machine
                switch (state) {
                    case 1:
                        // RGB: 100
                        mLedGpioRed.setValue(true);
                        mLedGpioGreen.setValue(false);
                        mLedGpioBlue.setValue(false);
                        state = 2;
                        break;
                    case 2:
                        // RGB: 010
                        mLedGpioRed.setValue(false);
                        mLedGpioGreen.setValue(true);
                        mLedGpioBlue.setValue(false);
                        state = 3;
                        break;
                    case 3:
                        // RGB: 001
                        mLedGpioRed.setValue(false);
                        mLedGpioGreen.setValue(false);
                        mLedGpioBlue.setValue(true);
                        state = 1;
                        break;
                    default:
                        break;
                }

                // Reschedule the same runnable
                mHandler.postDelayed(mBlinkRunnable, INTERVAL_BETWEEN_BLINKS_MS);

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

        // Close the Gpio pin.
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