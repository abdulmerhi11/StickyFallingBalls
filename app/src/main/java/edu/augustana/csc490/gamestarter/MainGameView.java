// CannonView.java
// Displays and controls the Cannon Game
package edu.augustana.csc490.gamestarter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.ArrayList;
import java.util.Random;

public class MainGameView extends SurfaceView implements SurfaceHolder.Callback
{


    private static final String TAG = "FallingBall"; // for Log.w(TAG, ...)
    private GameThread gameThread; // runs the main game loop
    private Activity mainActivity; // keep a reference to the main Activity
    private boolean dialogIsDisplayed = false;

    //constants


    // All variables defined
    private boolean isGameOver = true;
    private double gravity;
    private int screenWidth;
    private int screenHeight;

    private int score;
    private Ball myBall;

    Random rand = new Random();
    private ArrayList<Ball> ballsDisplayed;

    //Sounds
    private static final int Main_Game_ID = 0;
    private static final int Ball_Explode_ID = 1;
    private SoundPool soundPool;
    private SparseIntArray soundMap;

    // paint variables
    private Paint textPaint;
    private Paint myBallPaint;
    private Paint backgroundPaint;



    // public constructor
    public MainGameView(Context context, AttributeSet atts)
    {
        super(context, atts);
        mainActivity = (Activity) context;

        getHolder().addCallback(this);

//        soundPool = new SoundPool (1,AudioManager.STREAM_MUSIC, 0);
//        soundMap.put(Main_Game_ID, soundPool.load(context, R.raw.blocker_hit, 1));
//        soundMap.put(Ball_Explode_ID, soundPool.load(context, R.raw.cannon_fire, 1));

        myBallPaint = new Paint();
        myBallPaint.setColor(randomColor());
        textPaint = new Paint();
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.GRAY);

    }

    //select a random color to the ball
    private int randomColor(){
        Random rColor = new Random();
        int colNum = rColor.nextInt(4);
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

        textPaint.setTextSize(w / 40);
        textPaint.setAntiAlias(true);

        startNewGame();
    }

    // resets all the screen elements and start a new game
    public void startNewGame()
    {
        myBall = new Ball();
        myBall.radius = screenHeight / 20;

        myBall.x = myBall.radius + rand.nextInt(screenWidth - 2*myBall.radius);
        myBall.y = 2*myBall.radius;
        //myBall.y = -myBall.radius;

        if (isGameOver)
        {
            isGameOver = false;
            gameThread = new GameThread(getHolder());
            gameThread.start(); // start the main game loop going
        }
    }



    public void drawBalls(Canvas canvas){
        Log.w(TAG, "drawBalls() called");
        if (canvas !=null){
            canvas.drawRect(0,0, canvas.getWidth(), canvas.getHeight(),backgroundPaint);

            canvas.drawCircle(myBall.x,(float)myBall.y,myBall.radius, myBallPaint);

        }
    }

    private void gameStep()
    {
        myBall.y++;
        myBall.x++;

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
        int action = e.getAction();

        if (action == MotionEvent.ACTION_DOWN)
        {
            int touchX = (int) e.getX();
            int touchY = (int) e.getY();
        }
        //TODO: make a contain method in ball
       // if (myBall.isFalling && (this.touchX < myBall.touchX + ) && (this.touchY == myBall.touchY)){
            //explodeMyBall(e);
        //}

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
                        drawBalls(canvas); // dr
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