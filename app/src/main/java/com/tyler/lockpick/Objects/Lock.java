package com.tyler.lockpick.Objects;

import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.design.widget.FloatingActionButton;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

/**
 * Created by tyler on 8/27/16.
 */
public class Lock {
    private final SensorManager mSensorManager;
    private final ImageView lock_background;
    private final int lock_width = 10;
    private final int lock_height = 100;

    private ArrayList<Pins> pins = new ArrayList<>();
    private Point center = new Point();
    public lockRotation lockRotation = new lockRotation();
    private Rect lock_rect = new Rect();
    public boolean toolbox_on;

    public Lock(SensorManager mSensorManager, ImageView lock_background, Point center){
        toolbox_on = false;
        this.mSensorManager = mSensorManager;
        this.lock_background = lock_background;
        this.center.x = center.x/2;
        this.center.y = center.y/2;
    }

    public void rotateLock(float tilt){
        lock_background.setRotation(tilt);
        float offset = 2 * center.x / 3;

        float x_center = center.x;
        float y_center = center.y - offset;
        float x_pivot = lock_width;
        float y_pivot = lock_height;

        lock_background.setX(x_center);
        lock_background.setY(y_center);
        lock_background.setPivotX(x_pivot);
        lock_background.setPivotY(y_pivot);
    }

    public class lockRotation implements SensorEventListener {

        private final float[] mAccelerometerReading = new float[3];
        private final float[] mMagnetometerReading = new float[3];

        private final float[] mRotationMatrix = new float[9];
        private final float[] mOrientationAngles = new float[3];

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Do something here if sensor accuracy changes.
            // You must implement this callback in your code.
        }

        public void start() {
            // Get updates from the accelerometer and magnetometer at a constant rate.
            // To make batch operations more efficient and reduce power consumption,
            // provide support for delaying updates to the application.
            //
            // In this example, the sensor reporting delay is small enough such that
            // the application receives an update before the system checks the sensor
            // readings again.
            mSensorManager.registerListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_NORMAL,SensorManager.SENSOR_DELAY_UI);
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                    SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        }

        public void stop() {
            // Don't receive any more updates from either sensor.
            mSensorManager.unregisterListener(this);
        }

        // Get readings from accelerometer and magnetometer. To simplify calculations,
        // consider storing these readings as unit vectors.
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                System.arraycopy(event.values, 0, mAccelerometerReading,
                        0, mAccelerometerReading.length);
            }
            else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                System.arraycopy(event.values, 0, mMagnetometerReading,
                        0, mMagnetometerReading.length);
            }
            updateOrientationAngles();
        }

        // Compute the three orientation angles based on the most recent readings from
        // the device's accelerometer and magnetometer.
        public void updateOrientationAngles() {
            // Update rotation matrix, which is needed to update orientation angles.
            SensorManager.getRotationMatrix(mRotationMatrix, null,
                    mAccelerometerReading, mMagnetometerReading);

            // "mRotationMatrix" now has up-to-date information.
            SensorManager.getOrientation(mRotationMatrix, mOrientationAngles);

            // "mOrientationAngles" now has up-to-date information.
            // Azimuth is the direction the device is facing from 0-360
            float azimuth = mOrientationAngles[0]*180/(float)Math.PI;
            float tilt = mOrientationAngles[1]*180/(float)Math.PI;
            float roll = mOrientationAngles[2]*180/(float)Math.PI;


            // Show or hide the toolbox based on device orientation
            // Rotate the lock based on the rotation data
            if (tilt < 0) tilt = 360 + tilt;
            tilt = abs(360 - tilt);
            if (250 < tilt && tilt < 300 || 65 < tilt && tilt < 90 || abs(roll) < 40) {
                toolbox_on = true;
            } else {
                //System.out.println(tilt);
                toolbox_on = false;
            }
            rotateLock(tilt);
        }
    }

    /**
     * Get all images that need to be used for collision detection
     * This one doesn't include pins
     */
    public List<ImageView> getImages(){
        List<ImageView> images = new ArrayList<>();
        images.add(lock_background);
        return images;
    }
}
