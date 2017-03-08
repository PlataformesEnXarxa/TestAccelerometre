package udl.eps.testaccelerometre;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class TestAccelerometreActivity extends Activity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor sensorAccelerometre, sensorLight;
    private boolean color = false;
    private TextView view, viewLight;
    private float maxRange;
    private float lValue;
    private long lastUpdateAccel, lastUpdateLight, maxTimeAccel = 200, maxTimeLight = 8000;

  
/** Called when the activity is first creaed. */

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        TextView viewAccel;
        ScrollView scrollView;
        view = (TextView) findViewById(R.id.textViewAccelerometro);
        viewAccel = (TextView) findViewById(R.id.textView);
        viewLight = (TextView) findViewById(R.id.textViewLight);
        scrollView = (ScrollView)findViewById(R.id.scrollView);

        view.setBackgroundColor(Color.GREEN);
        scrollView.setBackgroundColor(Color.YELLOW);

        sensorManager = (SensorManager) getSystemService(this.SENSOR_SERVICE);
        sensorAccelerometre = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        if (sensorAccelerometre != null) {
            String textAccel;
            textAccel = viewAccel.getText().toString();
            float resolution = sensorAccelerometre.getResolution();
            maxRange = sensorAccelerometre.getMaximumRange();
            float power = sensorAccelerometre.getPower();

            textAccel = textAccel + getString(R.string.msgResolution) + resolution +
                    getString(R.string.msgMaxRange) + maxRange +
                        getString(R.string.msgPower) + power;
            viewAccel.setText(textAccel);

            sensorManager.registerListener(this, sensorAccelerometre,
                    SensorManager.SENSOR_DELAY_NORMAL);
          // register this class as a listener for the accelerometer sensor
        }
        else{
            viewAccel.setText(R.string.noAccel);
        }
        if(sensorLight != null){
            String textLight;
            maxRange = sensorLight.getMaximumRange();
            textLight = viewLight.getText().toString();
            textLight = textLight + getString(R.string.msgMaxRange)+ maxRange;
            viewLight.setText(textLight);
            lValue = 0;
            sensorManager.registerListener(this, sensorLight, SensorManager.SENSOR_DELAY_NORMAL);
        }
        else{
            viewLight.setText(R.string.noLightSensor);
        }
        lastUpdateAccel = System.currentTimeMillis();
        lastUpdateLight = System.currentTimeMillis();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()){
            case Sensor.TYPE_ACCELEROMETER:
                getAccelerometer(event);
                break;
            case Sensor.TYPE_LIGHT:
                getLight(event);
                break;
        }

  }

    private void getLight(SensorEvent event) {
        float[] values = event.values;
        float light = values[0];
        long actualTime = System.currentTimeMillis();
        if (diffValue(lValue, light)){
            lValue = light;
            if ((actualTime - lastUpdateLight) < maxTimeLight){
                return;
            }
            lastUpdateLight = actualTime;
            String textLight = viewLight.getText().toString();
            textLight = textLight + getString(R.string.txtValue) + lValue +
                    getString(R.string.hopLine) +
                        getIntensity(lValue, maxRange)+ getString(R.string.txtIntensity);
            viewLight.setText(textLight);
        }
    }

    private String getIntensity(float lValue, float maxRange) {
        float valBalance = maxRange/3;
        float valBalanceM = valBalance * 2;
        String low = "LOW ";
        String medium = "MEDIUM ";
        String high = "HIGH ";
        if (lValue < valBalance){
            return low;
        }
        else if (lValue >= valBalance && lValue < valBalanceM){
            return medium;
        }
        else return high;
    }

    public boolean diffValue(float x, float y){
        return Math.abs(x - y) >= 2000;
    }

    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];

        float accelationSquareRoot = (x * x + y * y + z * z)
            / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        long actualTime = System.currentTimeMillis();
        if (accelationSquareRoot >= 2) {
            if (actualTime - lastUpdateAccel < maxTimeAccel) {
            return;
            }
            lastUpdateAccel = actualTime;
            Toast.makeText(this, R.string.shuffed, Toast.LENGTH_SHORT).show();
            if (color) {
            view.setBackgroundColor(Color.GREEN);
            } else {
                view.setBackgroundColor(Color.RED);
            }
            color = !color;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        sensorManager.unregisterListener(this, sensorAccelerometre);
        sensorManager.unregisterListener(this, sensorLight);
    }
    @Override
    protected void onResume(){
        super.onResume();
        sensorManager.registerListener(this, sensorLight, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorAccelerometre, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this,sensorAccelerometre);
        sensorManager.unregisterListener(this,sensorLight);
    }
}