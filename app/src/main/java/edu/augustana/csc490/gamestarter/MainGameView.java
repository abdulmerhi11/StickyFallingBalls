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
import android.media.SoundPool;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

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
    private double speed;
    private int screenWidth;
    private int screenHeight;

    private int score = 0;
    private int highestScore;
    private Ball myBall;
    private int colorBallText = 0;

    Random rand = new Random();
    private ArrayList<Ball> ballsDisplayed;
    private ArrayList<Ball> ballsExploded;

    //Sounds
    private static final int Main_Game_ID = 0;
    private static final int Ball_Explode_ID = 1;
    private SoundPool soundPool;
    private SparseIntArray soundMap;
    private String scoreString = "";
   // private TextView scoreView;

    // paint variables
    private Paint textPaint;
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


        textPaint = new Paint();
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.DKGRAY);
        ballsDisplayed= new ArrayList<Ball>();
        ballsExploded = new ArrayList<Ball>();

       // scoreView = (TextView) mainActivity.findViewById(R.id.scorebox);


    }

    //select a random color to the ball
    private int randomColor(){
        Random rColor = new Random();
        int colNum = rColor.nextInt(10);
        if (colNum == 0 || colNum == 1 || colNum == 2 || colNum == 3){
            return Color.YELLOW;
        } else if(colNum == 4 || colNum ==5 || colNum == 6){
            return Color.GREEN;
        } else if(colNum == 7 || colNum == 8){
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

        textPaint.setTextSize(w / 10);
        textPaint.setAntiAlias(true);

        startNewGame();
    }

    // resets all the screen elements and start a new game
    public void startNewGame()
    {
        colorBallText = randomColor();
        myBall = new Ball();
        myBall.paint.setColor(colorBallText);
        textPaint.setColor(colorBallText);
        myBall.x = myBall.radius + rand.nextInt(screenWidth - 2*myBall.radius);
        myBall.y = - myBall.radius;
        ballsDisplayed.clear();

        speed = 30;

        score = 0;
        scoreString = ""+ score;


        if (isGameOver)
        {
            isGameOver = false;
            gameThread = new GameThread(getHolder());
            gameThread.start(); // start the main game loop going
        }
    }



    public boolean checkGameOver(){
        if(!myBall.isFalling && myBall.getBottomY() < 2*myBall.radius){
            return true;
        } else{
            return false;
        }
    }

    private void showGameOverDialog (){
        final DialogFragment gameResult = new DialogFragment() {

           public Dialog onCreateDialog(Bundle bundle){
               AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
               builder.setTitle("GAME OVER");

               builder.setMessage("Your Score: " +score+ "\nHighest Score: " +highestScore+ "\nBalls Shot: ");
               builder.setPositiveButton(R.string.reset_game, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       dialogIsDisplayed = false;
                       startNewGame();
                   }
               });

               return builder.create();
           }
        };
        mainActivity.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        dialogIsDisplayed = true;
                        gameResult.setCancelable(false);
                        gameResult.show(mainActivity.getFragmentManager(), "results");
                    }
                }
        );
    }


    public void drawBalls(Canvas canvas){

        if (canvas !=null){
//            if (speed > 55){
//                backgroundPaint.setColor(Color.BLACK);
//            } else  if (speed > 50){
//                backgroundPaint.setColor(Color.MAGENTA);
//            } else if (speed >= 45){
//                backgroundPaint.setColor(Color.DKGRAY);
//            } else if (speed >= 40){
//                backgroundPaint.setColor(Color.GRAY);
//            } else if (speed > 30){
//                backgroundPaint.setColor(Color.LTGRAY);
//            } else if (speed >= 25){
//                backgroundPaint.setColor(Color.TRANSPARENT);
//            } else if (speed < 25 ) {
//                backgroundPaint.setColor(Color.WHITE);
//            }
            canvas.drawRect(0,0, canvas.getWidth(), canvas.getHeight(),backgroundPaint);

            canvas.drawText(scoreString, 30, 50, textPaint);

            canvas.drawCircle((float)myBall.x,(float)myBall.y,myBall.radius, myBall.paint);

           for (int i =0; i < ballsDisplayed.size(); i++){
               Ball b = ballsDisplayed.get(i);
               canvas.drawCircle((float)b.x,(float)b.y,b.radius, b.paint);
            }
        }
    }

    private void gameStep()
    {
        if ((myBall.intersectsWith(ballsDisplayed) || myBall.hitsFloor(screenHeight))){
            if (myBall.isExploded){
                myBall.isFalling = true;
                myBall.isExploded = false;
            } else {
                if (myBall.hitsFloor(screenHeight)) { myBall.y = screenHeight - myBall.radius; }

                myBall.isFalling = false;
                if (checkGameOver()){
                    showGameOverDialog();
                } else {
                    ballsDisplayed.add(myBall);
                    colorBallText = randomColor();
                    myBall = new Ball();
                    myBall.paint.setColor(colorBallText);
                    myBall.x = myBall.radius + rand.nextInt(screenWidth - 2 * myBall.radius);
                    myBall.y = -myBall.radius;

                    speed = 2 * (10 + rand.nextInt(20));


                }
            }


        }

        if (myBall.isFalling){
            myBall.y = myBall.y + speed;
        }



    }

    public void explodeBall (int touchX, int touchY) {
        if (myBall.contains(touchX, touchY)) {
            score = score + myBall.colorPoints();
            colorBallText = randomColor();
            scoreString = ""+ score;
            //scoreView.setText(scoreString);
            if (score >highestScore){
                highestScore = score;
            }
            textPaint.setColor(myBall.paint.getColor());
            myBall = new Ball();
            myBall.paint.setColor(colorBallText);
            myBall.x = myBall.radius + rand.nextInt(screenWidth - 2 * myBall.radius);
            myBall.y = -myBall.radius;
            myBall.isExploded = true;

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
        if (!dialogIsDisplayed){
            gameThread = new GameThread(holder);
            gameThread.setRunning(true);
            gameThread.start();
        }
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


    //}
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        int action = e.getAction();
        int touchX = - 1000;
        int touchY = - 1000;

        if (action == MotionEvent.ACTION_DOWN)
        {
            touchX = (int) e.getX();
            touchY = (int) e.getY();
        }

        explodeBall(touchX, touchY);

        return false;
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
                    Thread.sleep(1); // if you want to slow down the action...
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