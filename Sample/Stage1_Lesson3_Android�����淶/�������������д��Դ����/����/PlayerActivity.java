package station.mp3player;

import station.model.Mp3Info;
import station.mp3player.service.PlayerService;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class PlayerActivity extends Activity {
	ImageButton startButton = null;
	ImageButton pauseButton = null;
	ImageButton stopButton = null;
	MediaPlayer mediaPlayer = null;
	
	private TextView lrcTextView = null;
	Mp3Info mp3Info = null;
	// private Handler handler = new Handler();
	// private UpdateTimeCallback updateTimeCallback = null;
	// private ArrayList<Queue> queues = null;
	// private long begin = 0;
	// private long nextTimeMill = 0;
	// private long pauseTimeMills = 0;
	// private long currentTimeMill = 0;
	// private String message = null;
//	private boolean isPlaying = false;
	private IntentFilter intentFilter = null;
	private BroadcastReceiver receiver = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player);

		Intent intent = getIntent();
		mp3Info = (Mp3Info) intent.getSerializableExtra("mp3Info");

		startButton = (ImageButton) findViewById(R.id.begin);
		pauseButton = (ImageButton) findViewById(R.id.pause);
		stopButton = (ImageButton) findViewById(R.id.stop);

		startButton.setOnClickListener(new startButtonListener());
		pauseButton.setOnClickListener(new pauseButtonListener());
		stopButton.setOnClickListener(new stopButtonListener());
		lrcTextView = (TextView) findViewById(R.id.textView1);
	}

	/**
	 * 当activity处于不可见的时候就会调用改方法
	 */
	@Override
	protected void onPause() {
		super.onPause();
		// 解除广播接收器
		unregisterReceiver(receiver);
	}

	/**
	 * 当activity与用户获得焦点的时候调用改方法
	 */
	@Override
	protected void onResume() {
		super.onResume();
		// 重新注册广播接收器
		receiver = new LrcMessageBroadcastReceiver();
		registerReceiver(receiver, getIntentFilter());
	}

	/**
	 * 过滤器：只要接受制定好action的广播
	 * @return
	 */
	private IntentFilter getIntentFilter() {
		if (intentFilter == null) {
			intentFilter = new IntentFilter();
			intentFilter.addAction(AppConstant.LRC_MESSAGE_ACTION);
		}
		return intentFilter;
	}

	/**
	 * 广播接收器主要接受Service发送的广播，并且更新歌词(UI)
	 * @author luhan
	 *
	 */
	class LrcMessageBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 从Intent中取出歌词信息，然后更新TextView
			String lrcMessage = intent.getStringExtra("lrcMessage");
			lrcTextView.setText(lrcMessage);
		}

	}

/*	*//**
	 * 根据歌词文件的名字，来读取歌词文件当中的信息
	 * 
	 * @param lrcName
	 *//*
	private void prepareLrc(String lrcName) {
		try {
			InputStream inputStream = new FileInputStream(
					Environment.getExternalStorageDirectory() + File.separator
							+ "mp3" + File.separator + mp3Info.getLrcname());
			LrcProcessor lrcProcessor = new LrcProcessor();
			// 分析歌词文件后返回队列list
			queues = lrcProcessor.process(inputStream);
			// 创建一个UpdateTimeCallback对象
			updateTimeCallback = new UpdateTimeCallback(queues);
			// 初始化操作
			begin = 0;
			currentTimeMill = 0;
			nextTimeMill = 0;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	class UpdateTimeCallback implements Runnable {
		Queue times, messages = null;

		public UpdateTimeCallback(ArrayList<Queue> queues) {
			// 从ArrayList当中取出相应的对象对象
			times = queues.get(0);
			messages = queues.get(1);
		}

		@Override
		public void run() {
			// 计算偏移量，也就是说从开始播放MP3到现在为止，共消耗了多少时间，以毫秒为单位
			long offset = System.currentTimeMillis() - begin;
			if (currentTimeMill == 0) {
				nextTimeMill = (Long) times.poll();
				message = (String) messages.poll();
			}
			if (offset >= nextTimeMill) {
				lrcTextView.setText(message);
				message = (String) messages.poll();
				nextTimeMill = (Long) times.poll();
			}
			currentTimeMill = currentTimeMill + 10;
			handler.postDelayed(updateTimeCallback, 10);
		}

	}*/

	class startButtonListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			Intent intent = new Intent(PlayerActivity.this, PlayerService.class);
			// 因为播放MP3需要一个实体类对象(Mp3Info)来获得音乐名以及存放路径
			intent.putExtra("mp3Info", mp3Info);
			intent.putExtra("MSG", AppConstant.PlayerMsg.PLAY_MSG);

			// // 根据名称读取歌词文件
			// prepareLrc(mp3Info.getLrcname());
			startService(intent);
			// 将begin的值置为当前时间(毫秒)
			// begin = System.currentTimeMillis();
			// // 5毫秒后执行updateTimeCallback线程
			// handler.postDelayed(updateTimeCallback, 5);
			// isPlaying = true;
		}
	}

	class pauseButtonListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			Intent intent = new Intent(PlayerActivity.this, PlayerService.class);
			intent.putExtra("MSG", AppConstant.PlayerMsg.PAUSE_MSG);
			startService(intent);
			/*
			 * if (isPlaying) { handler.removeCallbacks(updateTimeCallback); //
			 * 暂停时当前时间 pauseTimeMills = System.currentTimeMillis(); } else {
			 * handler.postDelayed(updateTimeCallback, 5); // 再次开始时间 begin =
			 * begin + System.currentTimeMillis() - pauseTimeMills; } isPlaying
			 * = isPlaying ? false : true;
			 */
		}

	}

	class stopButtonListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			Intent intent = new Intent(PlayerActivity.this, PlayerService.class);
			intent.putExtra("MSG", AppConstant.PlayerMsg.STOP_MSG);
			// 通知Service停止播放MP3
			startService(intent);
			// handler.removeCallbacks(updateTimeCallback);
		}

	}

}
