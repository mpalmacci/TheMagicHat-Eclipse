package com.magichat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class MagicHatActivity extends Activity implements View.OnClickListener {
	protected Button bDelete, bPrefs, bPlayers, bAddPlayer, bCardSearch;

	protected TextView tvTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

		setContentView(R.layout.main_view);

		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.main_title_bar);

		initialize();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bPrefs:
			Intent playGamePrefs = new Intent("com.magichat.MAGICHATPREFS");
			startActivity(playGamePrefs);
			break;
		case R.id.bPlayers:
			Intent openPlayersActivity = new Intent(
					"com.magichat.players.PLAYERSMAIN");
			startActivity(openPlayersActivity);
			break;
		case R.id.bAddPlayer:
			Bundle emptyPlayerBundle = new Bundle();
			emptyPlayerBundle.putInt("playerId", 0);
			
			Intent openPlayerViewActivity = new Intent(
					"com.magichat.players.PLAYERVIEW");
			openPlayerViewActivity.putExtras(emptyPlayerBundle);
			startActivity(openPlayerViewActivity);
			break;
		case R.id.bCardSearch:
			Intent openCardSearchActivity = new Intent(
					"com.magichat.cards.CARDSEARCH");
			startActivity(openCardSearchActivity);
			break;
		default:
			break;
		}
	}

	private void initialize() {
		bDelete = (Button) findViewById(R.id.bDelete);
		bPrefs = (Button) findViewById(R.id.bPrefs);
		bPlayers = (Button) findViewById(R.id.bPlayers);
		bAddPlayer = (Button) findViewById(R.id.bAddPlayer);
		bCardSearch = (Button) findViewById(R.id.bCardSearch);
		tvTitle = (TextView) findViewById(R.id.tvTitle);

		bPrefs.setOnClickListener(this);
		bCardSearch.setOnClickListener(this);
		bPlayers.setOnClickListener(this);
		bAddPlayer.setOnClickListener(this);
	}
}
