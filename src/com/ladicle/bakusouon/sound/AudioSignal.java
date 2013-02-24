package com.ladicle.bakusouon.sound;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class AudioSignal {
	// デフォルトサンプルレート
	private static int SAMPLE_RATE = 44100; // 44.1KH

	// 中華
	public static final double FREQUENCY3[] = { 110.00, 130.8, 146.8, 155.6,
			164.8, 196.0, 220.0, 261.6, 293.7, 311.1, 329.6, 392.0, 440.0,
			523.3, 587.3, 622.3, 659.3, 784.0, 880.0, 1046.5, 1174.7, 1244.5,
			1318.5, 1568.0, 1760.0 };

	// ホールトン
	public static final double FREQUENCY[] = { 261.6, 293.7, 329.6, 370.0,
			415.3, 466.2, 523.3, 587.3, 659.3, 740.0, 830.6, 932.3, 1046.5,
			1174.7, 1318.5, 1480.0, 1661.2, 1864.7, 2093.0 };

	// ピアノ
	public static final double FREQUENCY2[] = { 110.00, 116.54, 123.47, 130.81,
			138.59, 146.83, 155.56, 164.83, 155.56, 164.81, 174.61, 184.99,
			195.99, 207.65, 220.00, 233.08, 249.94, 261.62, 277.18, 293.66,
			311.12, 329.62, 349.22, 369.99, 391.99, 415.30, 440.00, 466.16,
			493.88, 523.25, 554.36, 587.32, 622.25, 659.25, 698.45, 739.98,
			783.99, 830.60, 880.00, 932.32, 987.76, 1046.50, 1108.73, 1174.65,
			1244.50, 1318.51, 1396.91, 1479.97, 1567.98, 1661.21, 1760.00,
			1864.65, 1975.53, 2093.00 };

	public static final int SOUND_NUM = FREQUENCY.length;

	// AudioTrack
	private AudioTrack audioTrack;
	// サンプルレート
	private int sampleRate;
	// バッファーサイズ
	private int bufferSize;

	// コンストラクタ
	public AudioSignal(int bufferSize) {
		this(SAMPLE_RATE, bufferSize);
	}

	@SuppressWarnings("deprecation")
	public AudioSignal(int sampleRate, int bufferSize) {
		this.sampleRate = sampleRate;
		this.bufferSize = bufferSize;
		this.audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
				this.sampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_DEFAULT, this.bufferSize,
				AudioTrack.MODE_STATIC);
	}

	// 矩形波作成
	public byte[] getSquareWave(double frequency) {
		byte[] b = new byte[this.bufferSize];
		for (int i = 0; i < b.length; i++) {
			double r = i / (this.sampleRate / frequency);
			b[i] = (byte) ((Math.round(r) % 2 == 0) ? 100 : -100);
		}
		return b;
	}

	// getter
	public AudioTrack getAudioTrack() {
		return audioTrack;
	}
}
