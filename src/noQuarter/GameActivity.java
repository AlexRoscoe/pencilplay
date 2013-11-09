package com.example.noquarter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GameActivity extends Activity {
	private int screenWidth, screenHeight; 
	private Button moveLeftButton, moveRightButton, fireLeftButton, fireRightButton; 
	private FrameLayout game;

	@SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Get screen dimensions from calling activity
		Intent menu = getIntent();
		screenWidth = menu.getIntExtra(MainActivity.WIDTH, 0);
		screenHeight = menu.getIntExtra(MainActivity.HEIGHT, 0); 
		// Turns off the title at the top of the screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        // Move commands setup
        LinearLayout moveButtonLayout = new LinearLayout(this);
        LinearLayout.LayoutParams moveButtonParams = new LinearLayout.LayoutParams(screenWidth/5, LinearLayout.LayoutParams.WRAP_CONTENT);
        moveButtonParams.setMargins(0, screenHeight - 2*screenHeight/5, screenWidth - 2*screenWidth/5, 0);
        // Buttons 
        moveLeftButton = new Button(this);
        moveLeftButton.setText("Left ");
        moveLeftButton.setWidth(screenWidth/5); 
        moveLeftButton.setId(3);
//        moveLeftButton.setAlpha(0.5f); BREAKING GAME??
        moveRightButton = new Button(this); 
        moveRightButton.setText("Right");
        moveRightButton.setWidth(screenWidth/5); 
        moveRightButton.setId(4);
        moveButtonLayout.addView(moveLeftButton, moveButtonParams);    
        moveButtonLayout.addView(moveRightButton, moveButtonParams);
        
        // Fire commands setup
        LinearLayout fireButtonLayout = new LinearLayout(this);
        LinearLayout.LayoutParams fireButtonParams = new LinearLayout.LayoutParams(screenWidth/5, LinearLayout.LayoutParams.WRAP_CONTENT);
        fireButtonParams.setMargins(0, screenHeight - screenHeight/5, screenWidth - 2*screenWidth/5, 0);
        // Buttons
        fireLeftButton = new Button(this);
        fireLeftButton.setId(1); 
        fireLeftButton.setText("Fire!");
        fireLeftButton.setWidth(screenWidth/5);
        fireRightButton = new Button(this); 
        fireRightButton.setId(2); 
        fireRightButton.setText("Fire!");
        fireRightButton.setWidth(screenWidth/5); 
        fireButtonLayout.addView(fireLeftButton, fireButtonParams);
        fireButtonLayout.addView(fireRightButton, fireButtonParams);
        
        game = new FrameLayout(this);
        GameView gameView = new GameView(this, this ,screenWidth, screenHeight, moveLeftButton, moveRightButton);
        // Set Listeners
        moveLeftButton.setOnClickListener(gameView);
        moveRightButton.setOnClickListener(gameView);
        fireLeftButton.setOnClickListener(gameView);
        fireRightButton.setOnClickListener(gameView);
        
        game.addView(gameView);
        game.addView(moveButtonLayout);
        game.addView(fireButtonLayout);
        
		setContentView(game);
	}
	
	public void endGame() {
		finish();
	}
	
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
