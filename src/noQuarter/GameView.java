package com.example.noquarter;

import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class GameView extends SurfaceView implements OnClickListener {
	private SurfaceHolder holder;
	private GameLoopThread gameLoopThread;
	private Random r = new Random();
	private Paint blackColor, greenColor, textPaint;
	private Ship playerShip;
	private Land land;
	private int screenWidth, screenHeight;
	private static int shipSpeed;
	private final int HEALTH_BAR_BORDER = 4;
	private Bitmap healthText, startText;
	private MediaPlayer m;
	private boolean gameOver = false;
	private boolean gameWon = false;
	private int generateDockedShipCounter;
	private int generateCargoCounter; 
	private TextView metersRemaining;
	private Button left, right; 
	private boolean movingLeft, movingRight;
	// Enemies and dodgeables 
	private ArrayList<DockedShip> enemies = new ArrayList<DockedShip>();
	private ArrayList<Cargo> incomingCargo = new ArrayList<Cargo>();
	// Score Measuring Variables
	private int numShipsGenerated, numShipsDisabled; 
	private int numCargoGenerated, numCargoDestroyed, numCargoHit; 
	private GameActivity game; 
	private Vibrator v;
	private boolean gameStarted; 
	private int landLength = 4000;
	
	
	public GameView(Context context, GameActivity game, int screenWidth, int screenHeight, Button left, Button right) {
		super(context);
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.left = left; 
		this.right = right; 
		initialize();
	}

	public void initialize() {
		gameLoopThread = new GameLoopThread(this);
		holder = getHolder();
		// Initialize playerShip.
		startText = BitmapFactory.decodeResource(getResources(), R.drawable.backstory);
		startText = Bitmap.createScaledBitmap(startText, screenWidth, screenHeight, true);
		int startingHealth = screenWidth - 4 * screenWidth / (15) - HEALTH_BAR_BORDER - (4 * screenWidth / (15) + HEALTH_BAR_BORDER);
		playerShip = new Ship(screenWidth, screenHeight, startingHealth);
		playerShip.setShipImage(BitmapFactory.decodeResource(getResources(), R.drawable.ship));
		playerShip.setWakeImage(BitmapFactory.decodeResource(getResources(), R.drawable.wake1));
		playerShip.setWakeImage2(BitmapFactory.decodeResource(getResources(), R.drawable.wake2));
		playerShip.setWakeImage3(BitmapFactory.decodeResource(getResources(), R.drawable.wake3));
		playerShip.setCannonFireImage(BitmapFactory.decodeResource(getResources(), R.drawable.cannon_fire_left_animation),
								      BitmapFactory.decodeResource(getResources(),R.drawable.cannon_fire_right_animation));
		v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
		shipSpeed = screenHeight / 100;
		healthText = BitmapFactory.decodeResource(getResources(), R.drawable.health);
		CannonBall.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.cannonball));
		Cargo.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.cargo), screenWidth, screenHeight);
		DockedShip.setImages(screenWidth, screenHeight, 
				BitmapFactory.decodeResource(getResources(), R.drawable.docked_left_ship), 
				BitmapFactory.decodeResource(getResources(), R.drawable.docked_right_ship),
				BitmapFactory.decodeResource(getResources(), R.drawable.ship_left_damage),
				BitmapFactory.decodeResource(getResources(), R.drawable.ship_right_damage));
		CannonBall.setSpeed(shipSpeed);
		land = new Land(screenWidth, screenHeight, shipSpeed, landLength);
		// We create this anonymous callback class to handle the screen's events
		// and call our methods accordingly
		holder.addCallback(new Callback() {
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				gameLoopThread.setRunning(false);
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				gameLoopThread.setRunning(true);
				gameLoopThread.start();
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				// TODO Auto-generated method stub
			}
		});
		greenColor = new Paint();
		greenColor.setAlpha(255);
		greenColor.setColor(Color.GREEN);
		blackColor = new Paint();
		blackColor.setAlpha(255);
		blackColor.setColor(Color.BLACK);

		textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		textPaint.setColor(Color.WHITE);
		textPaint.setTypeface(Typeface.DEFAULT);
		float textHeight = screenHeight/30; 
		if (textHeight == 0) {
			textHeight = textPaint.getTextSize();
		} else {
			textPaint.setTextSize(textHeight);
		}
	}

	public void update() {
		if (!gameStarted) {
			this.setClickable(true);
			if (this.isPressed()) {
				gameStarted = true; 
			}
		}
		else {
			if (!gameOver) {
				// Update Position
				land.update();
				// Check left/right movement
				if (left.isPressed()) {
					playerShip.moveLeft();
				}
				if (right.isPressed()) {
					playerShip.moveRight();
				}
				// Update playerShip
				playerShip.update();
				// Generate or progress to generating a dockedShip
				if (generateDockedShipCounter == 0) {
					generateDockedShip();
					generateDockedShipCounter = r.nextInt(900) + 100;
				} else {
					generateDockedShipCounter--;
				}
				// Update and Eliminate all docked ships that are offscreen. 
				ArrayList<DockedShip> offScreen = new ArrayList<DockedShip>(5);
				for (DockedShip enemy : enemies) {
					enemy.update(shipSpeed);
					if (enemy.getTop() >= screenHeight) {
						offScreen.add(enemy);
					}
				}
				for (DockedShip enemy : offScreen) {
					if (enemy.hasBeenHit()) { numShipsDisabled++; }
					enemies.remove(enemy);
				}
				// Generate or progress to generating incoming cargo. 
				if (generateCargoCounter == 0) {
					generateCargo();
					generateCargoCounter = r.nextInt(100);
				}
				else {
					generateCargoCounter--;
				}
				// Update and Eliminate all cargo that has gone off-screen
				ArrayList<Cargo> offScreenCargo = new ArrayList<Cargo>(5);
				for (Cargo c : incomingCargo) {
					c.update(); 
					if (c.getTop() >= screenHeight) {
						offScreenCargo.add(c);
					}
				}
				for (Cargo c : offScreenCargo) {
					incomingCargo.remove(c);
				}
				detectCollisions();
				if (playerShip.getHealth() <= 0) {
					gameOver = true;
					this.setClickable(true);
				}
				if (land.getMapRemaining() <= 0) {
					gameWon = true;
					gameOver = true;
					this.setClickable(true);
				}
			}
			// GAME OVER
			else {
				for (DockedShip enemy : enemies) {
					if (enemy.hasBeenHit()) {
						numShipsDisabled++;
					}
				}
				// Victory.
				if (gameWon) {
					
				}
				// Destroyed.
				else {
	
				}
				if (this.isPressed()) {
					if (game != null) {
						game.endGame();
					}
					else {
						((Activity) getContext()).finish();
					}
				}
			}
		}
	}

	public void detectCollisions() {
		// detect if ship has hit land.
		// Left collision possible
		boolean playerCollision = false;
		int pointHit = 0;
		if (playerShip.getLeftSide() <= land.getLimit()) {
			int lowerBound = playerShip.getCollidableSideLowerBound();
			int upperBound = playerShip.getCollidableSideUpperBound();
			int leftSideOfObject = playerShip.getLeftSide();
			int landHitLocation = land.checkLeftCollision(lowerBound, upperBound, leftSideOfObject);
			// Collision detected
			if (landHitLocation != -1) {
				playerCollision = true;
				if (landHitLocation > pointHit) {
					pointHit = landHitLocation;
				}
			}
			if (playerCollision) {
				playerShip.handleLeftCollison(pointHit);
				v.vibrate(200);
			}
		}
		// Right collision possible
		else if (playerShip.getRightSide() >= screenWidth - land.getLimit()) {
			pointHit = screenWidth;
			int lowerBound = playerShip.getCollidableSideLowerBound();
			int upperBound = playerShip.getCollidableSideUpperBound();
			int rightSideOfObject = playerShip.getRightSide();
			int landHitLocation = land.checkRightCollision(lowerBound, upperBound, rightSideOfObject);
			// Collision detected
			if (landHitLocation != screenWidth) {
				playerCollision = true;
				if (landHitLocation < pointHit) {
					pointHit = landHitLocation;
				}
			}
			if (playerCollision) {
				playerShip.handleRightCollison(pointHit);
				v.vibrate(200);
			}
		}
		if (!playerCollision) {
			playerShip.clearCollisions();
		}
		// See if player has collided with docked ships. 
		for (DockedShip enemy : enemies) {
			if (Rect.intersects(playerShip.getBoundingBox(), enemy.getBoundingBox())) {
				int location = 0;
				if (enemy.isLeft()) {
					location = enemy.getRightSide();
				}
				else {
					location = enemy.getLeftSide() - playerShip.getWidth(); 
				}
				playerShip.collideWithDockedShip(location);
				v.vibrate(200);
			}
		}
		// See if player has hit cargos
		ArrayList<Cargo> hitCargo = new ArrayList<Cargo>(5);
		for (Cargo c : incomingCargo) {
			if (Rect.intersects(playerShip.getBoundingBox(), c.getBoundingBox())) {
				hitCargo.add(c);
				playerShip.collideWithCargo();
				v.vibrate(200);
				numCargoHit++;
			}
		}


		// detect if ship's shots have hit land.
		ArrayList<CannonBall> shotsFired = playerShip.getShotsFired();
		ArrayList<CannonBall> shotsHit = new ArrayList<CannonBall>();
		for (CannonBall cb : shotsFired) {
			boolean hit = false; 
			// Traveling left
			if (cb.isTravellingLeft()) {
				// Hit.
				if (land.get_L_at(cb.getHitPosition().y) >= cb.getHitPosition().x) {
					int lowerBound = cb.getTop();
					int upperBound = cb.getBottom();
					int maxDepth = cb.getWidth();
					land.shotLeft(lowerBound, upperBound, maxDepth);
					shotsHit.add(cb);
					hit = true; 
					continue;
				}
				// Could hit Docked Ship. 
				for (DockedShip enemy : enemies) {
					if (enemy.isLeft() && (enemy.getCollidableSideLowerBound() <= cb.getHitPosition().y && enemy.getCollidableSideUpperBound() >= cb.getHitPosition().y)) {
						// Hit DOCKED_SHIP
						if (cb.getHitPosition().x <= enemy.getRightSide()) {
							enemy.shot(cb.getHitPosition().y);
							shotsHit.add(cb);
							hit = true; 
							break;
						}
					}
				}
			}
			// Traveling right
			else {
				// Could hit docked ship.
				for (DockedShip enemy : enemies) {
					if (!enemy.isLeft() && (enemy.getCollidableSideLowerBound() <= cb.getHitPosition().y && enemy.getCollidableSideUpperBound() >= cb.getHitPosition().y)) {
						// Hit DOCKED_SHIP
						if (cb.getHitPosition().x >= enemy.getLeftSide()) {
							enemy.shot(cb.getHitPosition().y);
							shotsHit.add(cb);
							hit = true; 
							break;
						}
					}
				}
				// Hit LAND.
				if (!hit && screenWidth - land.get_R_at(cb.getHitPosition().y) <= cb.getHitPosition().x) {
					int lowerBound = cb.getTop();
					int upperBound = cb.getBottom();
					int maxDepth = cb.getWidth();
					land.shotRight(lowerBound, upperBound, maxDepth);
					shotsHit.add(cb);
					hit = true; 
					continue;
				}
			}
			// See if it hit cargo
			if (!hit) {
				for (Cargo c : incomingCargo) {
					if (Rect.intersects(c.getBoundingBox(), cb.getBoundingBox())) {
						shotsHit.add(cb);
						hitCargo.add(c);
						hit = true; 
						numCargoDestroyed++;
						playerShip.replenishHealth();
					}
				}
			}
		}
		// Remove Cargo killed or collided with
		for (Cargo c : hitCargo) {
			incomingCargo.remove(c);
		}
		// Remove balls that collided with world.
		for (CannonBall cb : shotsHit) {
			playerShip.removeShot(cb);
		}
	} 

	public void generateDockedShip() {
		int test = r.nextInt(10);
		DockedShip enemy = null;

		if (test < 5) {
			enemy = new DockedShip(land.get_R_DockLocation(), 100);
		} else {
			enemy = new DockedShip(land.get_L_DockLocation(), 100);
		}
		enemies.add(enemy);
		numShipsGenerated++;
	}
	
	
	
	
	public void generateCargo() {
		int leftLimit = land.get_L_at(0); 
		int rightLimit = screenWidth - land.get_R_at(0) - Cargo.getWidth();
		int xPos = r.nextInt(rightLimit - leftLimit);
		xPos += leftLimit; 
		Cargo newCargo = new Cargo(xPos, 0, land.getVelocity());
		incomingCargo.add(newCargo);
		numCargoGenerated++;
	}

	public void drawHealthBar(Canvas canvas) {
		// Draw the black background
		int top = healthText.getHeight() + HEALTH_BAR_BORDER;
		int bottom = top + screenHeight / 20;
		for (int i = top; i < bottom; i++) {
			canvas.drawLine(4 * screenWidth / (15), i, screenWidth - 4 * screenWidth / (15), i, blackColor);
		}
		// Draw over the black with the current amount of health
		for (int i = top + HEALTH_BAR_BORDER; i < bottom - HEALTH_BAR_BORDER; i++) {
			int test = playerShip.getHealthDeduction();
			canvas.drawLine(4 * screenWidth / (15) + HEALTH_BAR_BORDER, i, screenWidth - 4 * screenWidth / (15) - HEALTH_BAR_BORDER - (playerShip.getHealthDeduction()), i, greenColor);
		}
	}

	
	public void playCannonShot() {
		m = new MediaPlayer();
		try {
			if (m.isPlaying()) {
				m.stop();
				m.release();
				m = new MediaPlayer();
			}
			AssetFileDescriptor afd = getContext().getAssets().openFd("cannonshot.mp3");
			m.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(),
					afd.getLength());
			m.prepare();
			m.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void draw(Canvas canvas) {
		if (canvas != null) {
			if (!gameStarted) {
				canvas.drawBitmap(startText, 0, 0, null);
			}
			else {
				if (!gameOver) {
					canvas.drawColor(Color.BLUE);
					land.draw(canvas);
					playerShip.draw(canvas);
		
					for (DockedShip enemy : enemies) {
						enemy.draw(canvas);
					}
					for (Cargo c : incomingCargo) {
						c.draw(canvas);
					}
					canvas.drawBitmap(healthText, screenWidth / 2 - healthText.getWidth() / 2, 0, null);
					drawHealthBar(canvas);
					// Draw the label text
					int bottomOfHealthBar = healthText.getHeight() + HEALTH_BAR_BORDER + (screenHeight / 20);
					Rect bounds = new Rect();
					String text = "" + land.getMapRemaining() + "m Remaining";
					textPaint.getTextBounds(text, 0, text.length(), bounds);
					canvas.drawText(text, screenWidth/2 - bounds.right/2, 6*bottomOfHealthBar/4, textPaint);
				}
				else {
					canvas.drawColor(Color.BLACK);
					Rect bounds = new Rect();
					String text = "";
					if (gameWon) {
						textPaint.setColor(Color.GREEN);
						text = "You Won!";
						textPaint.getTextBounds(text, 0, text.length(), bounds);
						canvas.drawText(text, screenWidth/2 - bounds.right/2, screenWidth/5, textPaint);
					}
					else {
						textPaint.setColor(Color.WHITE);
						text = "You Were Destroyed...";
						textPaint.getTextBounds(text, 0, text.length(), bounds);
						canvas.drawText(text, screenWidth/2 - bounds.right/2, screenWidth/5, textPaint);
					}
					text = "Docked Ships Killed: " + numShipsDisabled + "/" + numShipsGenerated;
					textPaint.getTextBounds(text, 0, text.length(), bounds);
					canvas.drawText(text, screenWidth/2 - bounds.right/2, 3 * screenWidth/5, textPaint);
					text = "Cargo Destroyed: " + numCargoDestroyed + "/" + numCargoGenerated;
					textPaint.getTextBounds(text, 0, text.length(), bounds);
					canvas.drawText(text, screenWidth/2 - bounds.right/2, 4 * screenWidth/5, textPaint);
					text = "Cargo Dodged: " + (numCargoGenerated-numCargoHit) + "/" + numCargoGenerated;
					textPaint.getTextBounds(text, 0, text.length(), bounds);
					canvas.drawText(text, screenWidth/2 - bounds.right/2, 5 * screenWidth/5, textPaint);	
				}
			}
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case 1:
			if (!playerShip.isFiringLeft() && !gameOver) {
				playerShip.fireLeft();
				v.vibrate(100);
				playCannonShot();
			}
			break;
		case 2:
			if (!playerShip.isFiringRight() && !gameOver) {
				playerShip.fireRight();
				v.vibrate(100);
				playCannonShot();
			}
			break;
		case 3: 
			playerShip.shiftLeft(); 
			break; 
		case 4: 
			playerShip.shiftRight();
			break;
		}
	}
}
