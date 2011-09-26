package sundy.android.demo.sensor;

import java.io.IOException;

import sundy.android.demo.R;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/**
 * @author Administrator
 *
 */
public class SensorPlayerActivity extends Activity implements SensorEventListener {
    /** Called when the activity is first created. */
	private SensorManager sensorManager ;
	private Sensor sensor ;
	private static MediaPlayer mediaPlayer  ;
	private Boolean isPlaying  = false ;
	
	private float x, y, z, last_x, last_y, last_z;
	 private long lastUpdate;
	 // record times of useful shakes for test
	 private int mTimes;
	 // 越小越灵敏
	 private static final int SHAKE_THRESHOLD = 3000;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_sensorpalyer);
        
        sensorManager = (SensorManager)this.getSystemService(SENSOR_SERVICE)  ;
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)  ;
        
        mediaPlayer = MediaPlayer.create(this, R.raw.maria) ;
        
        Button button1 =(Button)findViewById(R.id.button1) ;
        button1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!mediaPlayer.isPlaying())
			      {		    	 
			    	    Log.i("sundy", "start") ;
						mediaPlayer.start() ;  	  
			      }
			      else
			      {
			    	  Log.i("sundy", "stop") ;
			    	  mediaPlayer.pause() ;
			    	  
			      }
			}
		}) ;
    
    }

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		sensorManager.unregisterListener(this) ;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME)  ;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		doOnSensorChangedJob(event.sensor.getType(), event.values) ;
		
	}
	
	
	/**
	 * 具体感应器改变操作
	 * @param sensor 传感器
	 * @param values 传感器的值
	 */
	private void doOnSensorChangedJob(int sensor, float[] values) 
	{
	  if (sensor == Sensor.TYPE_ACCELEROMETER) 
	  {
		   long curTime = System.currentTimeMillis();
		   // 测100毫秒 速度
		   if ((curTime - lastUpdate) > 100) {
		    long diffTime = (curTime - lastUpdate);
		    x = values[SensorManager.DATA_X];
		    y = values[SensorManager.DATA_Y];
		    z = values[SensorManager.DATA_Z];
		    float speed = Math.abs(x + y - last_x - last_y - last_z)
		      / diffTime * 10000;
		    // Log.i("Throw", "Throw at speed " + speed);
		    if (speed > SHAKE_THRESHOLD) {
		     // 检测到摇晃后执行的代码
		      Toast.makeText(this, "检测到甩歌:x="+x+"y="+y+"z="+z, 3000).show() ;
		      if(!mediaPlayer.isPlaying())
		      {		    	 
		    	    Log.i("sundy", "start") ;
					mediaPlayer.start() ;  	  
		      }
		      else
		      {
		    	  Log.i("sundy", "pause") ;
		    	  mediaPlayer.pause();
		    	  
		      }
		    }
		    last_x = x;
		    last_y = y;
		    last_z = z;
		    lastUpdate = curTime;
		   }
		}
	}
	
	
	
	
}