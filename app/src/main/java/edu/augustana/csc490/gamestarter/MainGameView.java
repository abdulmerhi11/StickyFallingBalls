// CannonView.java
// Displays and controls the Cannon Game
package edu.augustana.csc490.gamestarter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

public class MainGameView extends SurfaceView implements SurfaceHolder.Callback
{
    // All variables defined
    private static final String TAG = "GameStarter"; // for Log.w(TAG, ...)
    private GameThread gameThread; // runs the main game loop
    private Activity mainActivity; // keep a reference to the main Activity
    private boolean isGameOver = true;
    private double gravity = 10;
    private int screenWidth;
    private int screenHeight;
    private int x;
    private int y;
    private int score;
    private Ball myBall;
    private Paint myPaint;
    private Paint backgroundPaint;
    Random rand = new Random();
    private ArrayList<Ball> ballsDisplayed;


    public MainGameView(Context context, AttributeSet atts)
    {
        super(context, atts);
        mainActivity = (Activity) context;

        getHolder().addCallback(this);

        myPaint = new Paint();
        myPaint.setColor(randomColor());
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.GRAY);
    }

    //select a random color to the ball
    private int randomColor(){
        Random rColor = new Random();
        int colNum = rColor.nextInt(3);
        if (colNum == 0){
            return Color.YELLOW;
        } else if(colNum == 1){
            return Color.GREEN;
        } else if(colNum == 2){
            return Color.BLUE;
        } else {
            return Color.RED;
        }
    }

    // called when the size changes (and first time, when view is created)
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);

        screenWidth = w;
        screenHeight = h;

        startNewGame();
    }

    public void startNewGame()
    {
        myBall.x = myBall.radius + rand.nextInt(screenWidth + 1 - myBall.radius);
        myBall.y = -myBall.radius;

        if (isGameOver)
        {
            isGameOver = false;
            gameThread = new GameThread(getHolder());
            gameThread.start(); // start the main game loop going
        }
    }

    public void drawBalls(Canvas canvas){
        if (canvas !=null){
            canvas.drawRect(0,0, canvas.getWidth(), canvas.getHeight(),backgroundPaint);
            while (isGameOver == false){
                canvas.drawCircle(myBall.x,(float)myBall.y,myBall.radius, myPaint);
            }
        }
    }

    private void gameStep()
    {
        x++;
    }

    public void updateView(Canvas canvas)
    {
        if (canvas != null) {
            canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), backgroundPaint);
            canvas.drawCircle(x, y, 20, myPaint);
        }
    }

    // stop the game; may be called by the MainGameFragment onPause
    public void stopGame()
    {
        if (gameThread != null)
            gameThread.setRunning(false);
    }

    // release resources; may be called by MainGameFragment onDestroy
    public void releaseResources()
    {
        // release any resources (e.g. SoundPool stuff)
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    // called when the surface is destroyed
    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        // ensure that thread terminates properly
        boolean retry = true;
        gameThread.setRunning(false); // terminate gameThread

        while (retry)
        {
            try
            {
                gameThread.join(); // wait for gameThread to finish
                retry = false;
            }
            catch (InterruptedException e)
            {
                Log.e(TAG, "Thread interrupted", e);
            }
        }
    }

        @Override
    public boolean onTouchEvent(MotionEvent e)
    {
        if (e.getAction() == MotionEvent.ACTION_DOWN)
        {
            this.x = (int) e.getX();
            this.y = (int) e.getY();
        }

        return true;
    }

    // Thread subclass to run the main game loop
    private class GameThread extends Thread
    {
        private SurfaceHolder surfaceHolder; // for manipulating canvas
        private boolean threadIsRunning = true; // running by default

        // initializes the surface holder
        public GameThread(SurfaceHolder holder)
        {
            surfaceHolder = holder;
            setName("GameThread");
        }

        // changes running state
        public void setRunning(boolean running)
        {
            threadIsRunning = running;
        }

        @Override
        public void run()
        {
            Canvas canvas = null;

            while (threadIsRunning)
            {
                try
                {
                    // get Canvas for exclusive drawing from this thread
                    canvas = surfaceHolder.lockCanvas(null);

                    // lock the surfaceHolder for drawing
                    synchronized(surfaceHolder)
                    {
                        gameStep();         // update game state
                        updateView(canvas); // dr
                        // aw using the canvas
                    }
                    Thread.sleep(10); // if you want to slow down the action...
                } catch (InterruptedException ex) {
                    Log.e(TAG,ex.toString());
                }
                finally  // regardless if any errors happen...
                {
                    // make sure we unlock canvas so other threads can use it
                    if (canvas != null)
                        surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }
}