// References: https://github.com/leinardi/androidthings-pio/tree/master/pio-softpwm
//             https://developer.android.com/things/sdk/pio/pwm
// Due to limitation of Hardware PWM in PI 3, this program uses Software PWM library from Leinardi, to build gradle please add to 'build.gradle'
// and re-sync your project:
//
// dependencies {
//    implementation 'com.leinardi.android.things:pio-softpwm:0.2'
//}

package com.example.duchoang.blinkingled;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.PeripheralManager;
import com.google.android.things.pio.Pwm;
import com.leinardi.android.things.pio.SoftPwm;
import java.io.IOException;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Handler mHandler = new Handler();
    private static final double MIN_DUTY_CYCLE = 0;
    private static final double MAX_DUTY_CYCLE = 100;
    private static final double DUTY_CYCLE_CHANGE_PER_STEP = 0.1;
    private static final int STEP = 1;
    private double dutyCycle;
    private boolean isIncreasing = true;
    private Pwm mPwmRed;
    private Pwm mPwmGreen;
    private Pwm mPwmBlue;
    private Gpio mButtonGpio;
    private int buttonState;
    private int ledState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Starting BlinkActivity");
        PeripheralManager manager = PeripheralManager.getInstance();
        try {
            //PeripheralManager manager = PeripheralManager.getInstance();
            mPwmRed = SoftPwm.openSoftPwm("BCM2");
            mPwmGreen = SoftPwm.openSoftPwm("BCM3");
            mPwmBlue = SoftPwm.openSoftPwm("BCM4");
            mButtonGpio = PeripheralManager.getInstance().openGpio("BCM21");

            initializePwm(mPwmRed);
            initializePwm(mPwmGreen);
            initializePwm(mPwmBlue);

            mButtonGpio.setDirection(Gpio.DIRECTION_IN);
            mButtonGpio.setEdgeTriggerType(Gpio.EDGE_FALLING);
            mButtonGpio.setActiveType(Gpio.ACTIVE_HIGH);


            ledState = 1;
            buttonState = 1;

            mHandler.post(changePWMRunnable);
            mButtonGpio.registerGpioCallback(mGpioCallback);
        } catch (IOException e) {
            Log.w(TAG, "Unable to access PWM", e);
        }
    }

    private GpioCallback mGpioCallback = new GpioCallback() {
        @Override
        public boolean onGpioEdge(Gpio button) {
            try {
                switch (buttonState) {
                    case 1:
                        if (!button.getValue()) {
                            buttonState = 2;
                        }
                        break;
                    case 2:
                        if (!button.getValue()) {
                            buttonState = 3;
                        }
                        break;
                    case 3:
                        if (!button.getValue()) {
                            buttonState = 4;
                        }
                        break;
                    case 4:
                        if (!button.getValue()) {
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

    private Runnable changePWMRunnable = new Runnable() {
        @Override
        public void run() {
            if (mPwmRed == null || mPwmBlue == null || mPwmGreen == null) {
                Log.w(TAG, "Stopping runnable since mPwm is null");
                return;
            }

            if (isIncreasing) {
                dutyCycle += DUTY_CYCLE_CHANGE_PER_STEP;
            } else {
                dutyCycle -= DUTY_CYCLE_CHANGE_PER_STEP;
            }

            if (dutyCycle > MAX_DUTY_CYCLE) {
                dutyCycle = MAX_DUTY_CYCLE;
                isIncreasing = !isIncreasing;
            } else if (dutyCycle < MIN_DUTY_CYCLE) {
                dutyCycle = MIN_DUTY_CYCLE;
                isIncreasing = !isIncreasing;
            }

            Log.d(TAG, "Changing PWM duty cycle to" + dutyCycle);

            try {
                switch (buttonState) {
                    // Use button inputs to change states of LEDs
                    case 1:
                        mPwmRed.setPwmDutyCycle(dutyCycle);
                        mPwmGreen.setPwmDutyCycle(dutyCycle);
                        mPwmBlue.setPwmDutyCycle(dutyCycle);
                        break;
                    case 2:
                        mPwmRed.setPwmDutyCycle(dutyCycle);
                        mPwmGreen.setPwmDutyCycle(0);
                        mPwmBlue.setPwmDutyCycle(0);
                        break;
                    case 3:
                        mPwmRed.setPwmDutyCycle(0);
                        mPwmGreen.setPwmDutyCycle(dutyCycle);
                        mPwmBlue.setPwmDutyCycle(0);
                        break;
                    case 4:
                        mPwmRed.setPwmDutyCycle(0);
                        mPwmGreen.setPwmDutyCycle(0);
                        mPwmBlue.setPwmDutyCycle(dutyCycle);
                        break;
                    default:
                        break;
                }

                mHandler.postDelayed(changePWMRunnable, STEP);
            } catch (IOException e) {
                Log.e(TAG, "Error on PeripheralIO API", e);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(changePWMRunnable);

        Log.i(TAG, "Closing PWM pin");
        if (mPwmRed != null || mPwmBlue != null || mPwmGreen != null) {
            try {
                mPwmRed.close();
                mPwmBlue.close();
                mPwmGreen.close();
                mButtonGpio.close();
            } catch (IOException e) {
                Log.w(TAG, "Unable to close PWM", e);
            } finally {
                mPwmGreen = null;
                mPwmBlue = null;
                mPwmRed = null;
                mButtonGpio = null;
            }
        }
    }

    public void initializePwm(Pwm pwm) throws IOException {
        pwm.setPwmFrequencyHz(200);
        pwm.setPwmDutyCycle(10);

        // Enable the PWM signal
        pwm.setEnabled(true);
    }
}