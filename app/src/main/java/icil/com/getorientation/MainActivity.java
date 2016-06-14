package icil.com.getorientation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    SensorManager mSm;
    int mOrientCount;
    TextView mTxtOrient;
    float[] mGravity = null;
    float[] mGeometric = null;
    final static int FREQ = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSm = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mTxtOrient = (TextView)findViewById(R.id.result);
    }

    protected void onResume(){
        super.onResume();

        mSm.registerListener(mSeonsorListneer, mSm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
        mSm.registerListener(mSeonsorListneer, mSm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_UI);
    }

    SensorEventListener mSeonsorListneer = new SensorEventListener() {
        float[] mR = new float[9];
        float[] mI = new float[9];
        float[] mV = new float[9];
        @SuppressLint("SetTextI18n")
        @Override
        public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    mGravity = event.values.clone();
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    mGeometric = event.values.clone();
                    break;
            }

            if(mOrientCount++ % FREQ != 0) return;

            if(mGravity != null && mGeometric != null) {
                SensorManager.getRotationMatrix(mR, mI, mGravity, mGeometric);
                float inclination = SensorManager.getInclination(mI);
                SensorManager.getOrientation(mR, mV);

                mTxtOrient.setText(
                        ("회수 = " + mOrientCount / FREQ + "회\n")
                        + "\nGra : " + dumpValues(mGravity)
                        + "\nMag : " + dumpValues(mGeometric)
                        + "\n\nR : \n" + dumpMatrix(mR)
                        + "\nI : \n" + dumpMatrix(mI)
                        + "\ninclination : " + inclination
                        + "\n\nRot : \n" + dumpMatrix(mV)
                        + "\n\nTop : "
                        + "\nx : " + String.format("%.3f", Math.cos(mV[0])*Math.cos(mV[1]))
                        + "\ny : " + String.format("%.3f", Math.sin(mV[0])*Math.cos(mV[1]))
                        + "\nz : " + String.format("%.3f", -Math.cos(mV[1]-Math.PI/2))

                        + "\n\nLeft : "
                        + "\nx : " + String.format("%.3f", -Math.cos(mV[0])*Math.sin(mV[1])*Math.sin(mV[2]) + Math.sin(mV[0])*Math.cos(mV[2]))
                        + "\ny : " + String.format("%.3f", -Math.sin(mV[0])*Math.sin(mV[1])*Math.sin(mV[2]) - Math.cos(mV[0])*Math.cos(mV[2]))
                        + "\nz : " + String.format("%.3f", Math.cos(mV[1])*Math.sin(mV[2]))

                        + "\n\nBack : "
                        + "\nx : " + String.format("%.3f", -Math.cos(mV[0])*Math.sin(mV[1])*Math.cos(mV[2]) + Math.sin(mV[0])*Math.sin(mV[2]))
                        + "\ny : " + String.format("%.3f", -Math.sin(mV[0])*Math.sin(mV[1])*Math.cos(mV[2]) - Math.cos(mV[0])*Math.sin(mV[2]))
                        + "\nz : " + String.format("%.3f", Math.cos(mV[1])*Math.sin(mV[2]-Math.PI/2))
                );
            }
        }

        //| cos(yaw)cos(pitch) -cos(yaw)sin(pitch)sin(roll)-sin(yaw)cos(roll) -cos(yaw)sin(pitch)cos(roll)+sin(yaw)sin(roll)|
        //| sin(yaw)cos(pitch) -sin(yaw)sin(pitch)sin(roll)+cos(yaw)cos(roll) -sin(yaw)sin(pitch)cos(roll)-cos(yaw)sin(roll)|
        //| sin(pitch)          cos(pitch)sin(roll)                            cos(pitch)sin(roll)                          |

        String dumpValues(float[] v) {
            return String.format("%.2f, %.2f, %.2f", v[0], v[1], v[2]);
        }

        String dumpMatrix(float[] m) {
            return String.format("%.2f, %.2f, %.2f\n%.2f, %.2f, %.2f\n%.2f, %.2f, %.2f\n", m[0], m[1], m[2], m[3], m[4], m[5], m[6], m[7], m[8]);
        }

        float Radian2Deegree(float radian) {
            return radian*180/(float)Math.PI;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
}
