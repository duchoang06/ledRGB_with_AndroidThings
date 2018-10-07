package com.example.duchoang.blinkingled;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.PeripheralManager;
import java.io.IOException;


public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static int INTERVAL_BETWEEN_BLINKS_MS = 1000;
    private Handler mHandler = new Handler();
    private Gpio mLedGpioGreen;
    private Gpio mLedGpioRed;
    private Gpio mLedGpioBlue;
    private Gpio mButtonGpio;

    private int ledState;
    private int buttonState;

    @Override
    // Set up things
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Starting BlinkActivity");

        try {

            // Declare which GPIO ports to be used
            mLedGpioRed = PeripheralManager.getInstance().openGpio("BCM2");
            mLedGpioGreen = PeripheralManager.getInstance().openGpio("BCM3");
            mLedGpioBlue = PeripheralManager.getInstance().openGpio("BCM4");
            mButtonGpio = PeripheralManager.getInstance().openGpio("BCM21");

            // Define them as in/out ports
            mLedGpioRed.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH);
            mLedGpioGreen.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH);
            mLedGpioBlue.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH);
            mButtonGpio.setDirection(Gpio.DIRECTION_IN);
            mButtonGpio.setEdgeTriggerType(Gpio.EDGE_FALLING);

            // Assign
            mLedGpioRed.setActiveType(Gpio.ACTIVE_LOW);
            mLedGpioGreen.setActiveType(Gpio.ACTIVE_LOW);
            mLedGpioBlue.setActiveType(Gpio.ACTIVE_LOW);
            mButtonGpio.setActiveType(Gpio.ACTIVE_HIGH);

            // Init. the first state
            ledState = 1;
            buttonState = 1;

            // Post handle
            mHandler.post(mBlinkRunnable);
            mButtonGpio.registerGpioCallback(mGpioCallback);

        } catch (IOException e) {
            Log.e(TAG, "Error on PeripheralIO API", e);
        }

    }

    private GpioCallback mGpioCallback = new GpioCallback() {
        @Override
        public boolean onGpioEdge(Gpio button) {
            try {
                // An FSM for toggling between states of buttons as input
                switch (buttonState) {
                    case 1:
                        if (!button.getValue()) {
                            INTERVAL_BETWEEN_BLINKS_MS = 2000;
                            buttonState = 2;
                        }
                        break;
                    case 2:
                        if (!button.getValue()) {
                            INTERVAL_BETWEEN_BLINKS_MS = 1000;
                            buttonState = 3;
                        }
                        break;
                    case 3:
                        if (!button.getValue()) {
                            INTERVAL_BETWEEN_BLINKS_MS = 500;
                            buttonState = 4;
                        }
                        break;
                    case 4:
                        if (!button.getValue()) {
                            INTERVAL_BETWEEN_BLINKS_MS = 100;
                            buttonState = 1;
                        }
                        break;
                    default:
                        break;

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
            }
            return true;
        }
    };

    private Runnable mBlinkRunnable = new Runnable() {
        @Override
        public void run() {
            if (mLedGpioBlue == null || mLedGpioGreen == null || mLedGpioRed == null) {
                return;
            }
            try {
                // Toggle between LED states
                switch (ledState) {
                    case 1:
                        mLedGpioRed.setValue(true);
                        mLedGpioGreen.setValue(false);
                        mLedGpioBlue.setValue(false);
                        ledState = 2;
                        break;
                    case 2:
                        mLedGpioRed.setValue(false);
                        mLedGpioGreen.setValue(true);
                        mLedGpioBlue.setValue(false);
                        ledState = 3;
                        break;
                    case 3:
                        mLedGpioRed.setValue(false);
                        mLedGpioGreen.setValue(false);
                        mLedGpioBlue.setValue(true);
                        ledState = 1;
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
            mButtonGpio.close();
        } catch (IOException e) {
            Log.e(TAG, "Error on PeripheralIO API", e);
        } finally {
            mLedGpioRed = null;
            mLedGpioBlue = null;
            mLedGpioGreen = null;
            mButtonGpio = null;
        }
    }
}