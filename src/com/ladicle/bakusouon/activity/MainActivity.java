package com.ladicle.bakusouon.activity;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import jp.co.olympus.meg40.BluetoothNotEnabledException;
import jp.co.olympus.meg40.BluetoothNotFoundException;
import jp.co.olympus.meg40.Meg;
import jp.co.olympus.meg40.MegGraphics;
import jp.co.olympus.meg40.MegListener;
import jp.co.olympus.meg40.MegStatus;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.ladicle.bakusouon.MySurfaceView;
import com.ladicle.bakusouon.R;
import com.ladicle.bakusouon.meg.DeviceListActivity;
import com.ladicle.bakusouon.obj.MoonParam;
import com.ladicle.bakusouon.obj.MoonPoint;
import com.ladicle.bakusouon.sound.AudioSignal;
import com.ladicle.util.Log;

public class MainActivity extends Activity implements SensorEventListener,
		MegListener {
	private static final Log log = new Log("MainActivity");

	// Meg
	private Meg mMeg; // MEGへのコマンド送信を行うインスタンス
	private MegGraphics mMegGraphics; // グラフィック描画用
	private static final int REQUEST_CONNECT_DEVICE = 1; // MEGへの接続要求
	private static final int REQUEST_ENABLE_BT = 2;
	private static final int SENSOR_STOP = 3; // センサー値表示のアクティビティが閉じられた
	public static final int WHITE = 0xffffffff;
	public static final int BLACK = 0xff000000;

	// センサー
	private static final int sensorLimit = 7;
	private SensorManager manager;
	private Boolean xBool = false;
	private Boolean yBool = false;

	// 月データ
	private MoonParam param;
	private MoonPoint[][] moonPoint;
	private double maxHeight, minHeight;
	private int centerX, centerY;
	private int nowX, nowY;

	// 音データ
	private AudioTrack track;
	private byte[][] sound;

	// UI
	private MySurfaceView surfaceView;
	private ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// 月のデータ取得
		param = (MoonParam) getIntent().getSerializableExtra("MoonParam");
		if (param == null) {
			log.e("param is null");
			finish();
		}
		System.out.println(param.getURL());

		centerX = Math.round(param.getX() / 2);
		centerY = Math.round(param.getY() / 2);
		nowX = centerX;
		nowY = centerY;
		moonPoint = new MoonPoint[param.getY()][param.getX()];
		
		surfaceView = new MySurfaceView(this, param.getX(), param.getY());
		LinearLayout root = (LinearLayout) findViewById(R.id.root);
		root.addView(surfaceView);

		dialog = ProgressDialog.show(this, "DataLoading", "Make sound data.");
		new LoadMusicData().execute();
	}

	@Override
	protected void onStop() {
		super.onStop();
		log.i("onStop");
		stopGame();
	}
	
	private void stopGame() {
		// Listenerの登録解除
		if (manager != null)
			manager.unregisterListener(this);
		// 再生停止
		if (track.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
			track.stop();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		log.i("onDestroy");

		track.release();
		disConnect();
		finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
		log.i("onResume");

		// Listenerの登録
		if (manager != null) {
			registSensor();
		}
	}

	private void registSensor() {
		List<Sensor> sensors = manager.getSensorList(Sensor.TYPE_ACCELEROMETER);
		if (sensors.size() > 0) {
			Sensor s = sensors.get(0);
			manager.registerListener(this, s, SensorManager.SENSOR_DELAY_UI);
		}
	}

	private class LoadMusicData extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			// AudioTrackクラスの作成と初期化
			AudioSignal audioSignal = new AudioSignal(44100);
			track = audioSignal.getAudioTrack();

			// 音声データ作成
			sound = new byte[AudioSignal.SOUND_NUM][];
			for (int i = 0; i < AudioSignal.SOUND_NUM; ++i) {
				sound[i] = audioSignal.getSquareWave(AudioSignal.FREQUENCY[i]);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			dialog.setMessage("Get moon data.");
			new GetMoonData().execute(param);
		}
	}

	private class GetMoonData extends AsyncTask<MoonParam, Void, Boolean> {
		@Override
		protected Boolean doInBackground(MoonParam... params) {
			MoonParam param = params[0];
			try {
				Document document = new SAXBuilder().build(param.getURL());
				Element root = document.getRootElement();

				// 最大値、最小値の取得
				Element summary = root.getChild("summary");
				maxHeight = Double.parseDouble(summary.getChildText("max"));
				minHeight = Double.parseDouble(summary.getChildText("min"));
				double diff = (maxHeight - minHeight) / AudioSignal.SOUND_NUM;
				log.d("max:" + maxHeight + ", min:" + minHeight + ", length:"
						+ AudioSignal.SOUND_NUM);

				// 配列取得
				int iX = 0, iY = 0;
				List<Element> points = root.getChild("points").getChildren();
				for (Element elem : points) {
					double lat = Double.parseDouble(elem.getChildText("lat"));
					double lng = Double.parseDouble(elem.getChildText("lng"));
					double dHeight = Double.parseDouble(elem
							.getChildText("height"));

					// 正規化
					int height = (int) Math.round((dHeight - minHeight) / diff) - 1;
					if (height < 0) {
						height = 0;
					}
					log.d("point[" + iY + "][" + iX + "] = (" + lat + ", "
							+ lng + "); heightIndex=" + height);

					moonPoint[iY][iX++] = new MoonPoint(lat, lng, height);
					if (iX == param.getX()) {
						iX = 0;
						++iY;
					}
				}

			} catch (JDOMException e) {
				log.e(e.getMessage());
				return false;
			} catch (IOException e) {
				log.e(e.getMessage());
				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			dialog.setMessage("Connect to MEG.");
			connect();
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (sensorLimit < event.values[0]) {
			if (xBool) {
				if (++nowX >= param.getX()) { // 右へ移動
					nowX = centerX;
				}
				surfaceView.draw(nowX, nowY, 0);
				showMessage("→");
				playSound();
			}
			xBool = false;
		} else if (event.values[0] < -sensorLimit) {
			if (xBool) {
				if (--nowX < 0) { // 左へ移動
					nowX = centerX;
				}
				surfaceView.draw(nowX, nowY, 0);
				showMessage("←");
				playSound();
			}
			xBool = false;
		} else {
			xBool = true;
		}

		if (sensorLimit < event.values[1]) {
			if (yBool) {
				if (--nowY < 0) { // 後へ移動
					nowY = centerY;
				}
				surfaceView.draw(nowX, nowY, 0);
				showMessage("↓");
				playSound();
			}
			yBool = false;
		} else if (event.values[1] < -sensorLimit) {
			if (yBool) {
				if (++nowY >= param.getY()) { // 前へ移動
					nowY = centerY;
				}
				surfaceView.draw(nowX, nowY, 0);
				showMessage("↑");
				playSound();
			}
			yBool = false;
		} else {
			yBool = true;
		}

		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			String str = "X軸:" + event.values[0] + "\nY軸:" + event.values[1]
					+ "\nZ軸:" + event.values[2] + "\nx移動値:" + nowX + "\ny移動値:"
					+ nowY;
			log.d(str);
		}

		if (nowX == 0 && nowY == 0) {
			stopGame();
			showMessage(param.getName() + "へ到着〜");
			new Thread(new CallPhone()).start();
		}
	}

	private class CallPhone implements Runnable {
		@Override
		public void run() {
			TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
			String line1Number = telephonyManager.getLine1Number();
			URL url;
			try {
				// TODO : URL変わるかも!
				MoonPoint point = moonPoint[0][0];
				url = new URL(
						"http://www25006ue.sakura.ne.jp:4567/twilio?number="
								+ line1Number + "&lat=" + point.getLat()
								+ "&lng=" + point.getLng());
				System.out.println(url.toString());
				HttpURLConnection http = (HttpURLConnection) url
						.openConnection();
				http.setRequestMethod("GET");
				http.connect();
				BufferedInputStream bis = new BufferedInputStream(
						http.getInputStream());
				int data;
				while ((data = bis.read()) != -1)
					System.out.write(data);
				http.disconnect();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void playSound() {
		if (track.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
			track.stop();
			track.reloadStaticData();
		}
		int index = moonPoint[nowY][nowX].getHeight();
		track.write(sound[index], 0, sound[index].length);
		track.play();
	}

	private boolean connect() {
		if (mMeg == null) {
			try {
				mMeg = Meg.getInstance();
				mMeg.registerMegListener(MainActivity.this);

				// MEGのグラフィックス機能を使うクラスの生成
				mMegGraphics = new MegGraphics(mMeg);
			} catch (BluetoothNotFoundException e) {
				log.e("Bluetoothが見つかりません");
				return false;
			} catch (BluetoothNotEnabledException e) {
				log.e("Bluetoothが無効です");
				return false;
			}
		}

		// mMegは非null
		if (!mMeg.isConnected()) {
			Intent serverIntent = new Intent(this, DeviceListActivity.class);
			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
		}
		return true;
	}

	private void disConnect() {
		if (mMeg != null) {
			mMeg.disconnect();
		}
	}

	private boolean megCheck() {
		if (mMeg == null || !mMeg.isConnected()) {
			log.e("Megと未接続");
			return false;
		}
		return true;
	}

	private void showMessage(String message) {
		if (!megCheck())
			return;
		mMegGraphics.begin();
		mMegGraphics.clearScreen();
		mMegGraphics.setFontColor(WHITE);
		mMegGraphics.setFontSize(200);
		mMegGraphics.setClearColor(BLACK);
		mMegGraphics.drawString(20, 0, message);
		mMegGraphics.end();
	}

	// 他のアクティビティから結果を受信したときのコールバック
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE:
			if (resultCode == Activity.RESULT_OK) {
				String address = data.getExtras().getString(
						DeviceListActivity.EXTRA_DEVICE_ADDRESS);
				mMeg.connect(address);
			}
			break;

		case REQUEST_ENABLE_BT:
			if (resultCode == Activity.RESULT_OK) {
				// TODO
			} else {
				// TODO
			}
			break;

		case SENSOR_STOP:
			log.i("センサーActivityが閉じました");
			break;
		}

		// 開始
		manager = (SensorManager) getSystemService(SENSOR_SERVICE);
		registSensor();
		playSound();
		dialog.dismiss();
		surfaceView.draw(centerX, centerY, 0);
	}

	@Override
	public void onMegAccelChanged(int arg0, int arg1, int arg2) {
	}

	@Override
	public void onMegConnected() {
		showMessage("月面探索スタート!");
	}

	@Override
	public void onMegConnectionFailed() {
	}

	@Override
	public void onMegDeleteImage(int arg0) {
	}

	@Override
	public void onMegDirectionChanged(int arg0, int arg1) {
	}

	@Override
	public void onMegDisconnected() {
	}

	@Override
	public void onMegGraphicsCommandEnd(int arg0) {
	}

	@Override
	public void onMegGraphicsCommandStart(int arg0) {
	}

	@Override
	public void onMegKeyPush(int arg0, int arg1) {
	}

	@Override
	public void onMegSetContext(int arg0) {
	}

	@Override
	public void onMegSleep() {
	}

	@Override
	public void onMegStatusChanged(MegStatus arg0) {
	}

	@Override
	public void onMegVoltageLow() {
	}
}
