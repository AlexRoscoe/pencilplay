package com.example.noquarter;

import android.graphics.Canvas;

public class GameLoopThread extends Thread {
	private GameView view; 
	private boolean running = false; 
	// Used to keep the frames constant given powerful hardware. 
	static final long FPS = 30; 
	
	public GameLoopThread(GameView view) {
		this.view = view; 
	}
	
	public void setRunning(boolean running) {
		this.running = running;
	}
	
	@Override
	public void run() {
		long ticksPS = 1000 / FPS; 
		long startTime; 
		long sleepTime; 
		
		while(running) { 
			Canvas c = null; 
			startTime = System.currentTimeMillis();
			try { 
				c = view.getHolder().lockCanvas(); 
				synchronized (view.getHolder()) {
					view.update(); 
					if (!running) {
						break; 
					}
					view.draw(c); 
					if (!running) {
						break; 
					}
				}
			} finally { 
				if (c != null) { 
					view.getHolder().unlockCanvasAndPost(c);
				}
			}
			sleepTime = ticksPS - (System.currentTimeMillis() - startTime);
			try { 
				if (sleepTime > 0) {
					sleep(sleepTime);
				}
			}
			catch (Exception e) { 
				
			}
		}
	}
}
