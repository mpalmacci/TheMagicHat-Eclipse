package com.magichat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Splash extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		Thread timer = new Thread() {
			public void run() {
				try {
					sleep(300);
				} catch (InterruptedException iE) {
					iE.printStackTrace();
				} finally {
						Intent openMagicHatMain = new Intent(
								"com.magichat.MAGICHATMAIN");
						startActivity(openMagicHatMain);
				}
			}
		};
		timer.start();
	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}

}
