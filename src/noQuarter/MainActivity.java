package com.example.noquarter;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends Activity implements OnClickListener {
	public final static String WIDTH = "com.example.noquarter.WIDTH";
	public final static String HEIGHT = "com.example.noquarter.HEIGHT";
	private int screenWidth, screenHeight; 
	private MediaPlayer player; 
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Turns off the title at the top of the screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Get the height and width of the screen
		if (Build.VERSION.SDK_INT >= 11) {
			Point size = new Point();
			try {
				this.getWindowManager().getDefaultDisplay().getRealSize(size);
				screenWidth = size.x;
				screenHeight = size.y;
			} catch (NoSuchMethodError e) {
				Log.i("error", "it can't work");
			}

		} else {
			DisplayMetrics metrics = new DisplayMetrics();
			this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
			screenWidth = metrics.widthPixels;
			screenHeight = metrics.heightPixels;
		}
		
		// Set up menu layout
		LinearLayout menuButtonLayout = new LinearLayout(this);
		menuButtonLayout.setBackgroundResource(R.drawable.menu_photo);
		menuButtonLayout.setGravity(Gravity.BOTTOM);
		LinearLayout.LayoutParams menuButtonParams = new LinearLayout.LayoutParams(screenWidth/2, LinearLayout.LayoutParams.WRAP_CONTENT);
		menuButtonLayout.setOrientation(LinearLayout.VERTICAL);
		menuButtonParams.setMargins(screenWidth/4, 0, 0, screenHeight/10);
		// Buttons
		/* START GAME BUTTON */
		Button startGameButton = new Button(this);
		startGameButton.setText("Start Game!");
		startGameButton.setWidth(screenWidth/2);
		startGameButton.setId(0);
		startGameButton.setOnClickListener(this);
		/* SETTINGS BUTTON */
		Button settingsButton = new Button(this);
		settingsButton.setText("Settings");
		settingsButton.setWidth(screenWidth/2);
		settingsButton.setId(1);
		settingsButton.setOnClickListener(this);
		/* CLOSE GAME BUTTON */
		Button closeGameButton = new Button(this);
		closeGameButton.setText("Close Game");
		closeGameButton.setWidth(screenWidth/2);
		closeGameButton.setId(2);
		closeGameButton.setOnClickListener(this);
		
		menuButtonLayout.addView(startGameButton, menuButtonParams);
		menuButtonLayout.addView(settingsButton, menuButtonParams);
		menuButtonLayout.addView(closeGameButton, menuButtonParams);
		setContentView(menuButtonLayout);
		
		AssetFileDescriptor afd = null;
		try {
			afd = getAssets().openFd("hes_a_pirate.mp3");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    player = new MediaPlayer();
	    try {
	    	player.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    try {
			player.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    player.start();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void startGame() {
		Intent startGame = new Intent(this, GameActivity.class);
		startGame.putExtra(WIDTH, screenWidth);
		startGame.putExtra(HEIGHT, screenHeight);
		startActivity(startGame);
	}
	
	public void openSettings() {
		// TODO: Add settings lolololol.
	}
	
	public void closeGame() {
		player.release();
		this.finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case 0:
				startGame();
				break;
			case 1:
				openSettings();
				break; 
			case 2: 
				closeGame(); 
				break; 
		}
		 
			
		
	}

}
