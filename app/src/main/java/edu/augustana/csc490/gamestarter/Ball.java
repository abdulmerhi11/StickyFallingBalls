package edu.augustana.csc490.gamestarter;

/**
 * Created by abdulmerhi11 on 4/1/2015.
 */

import android.graphics.Color;
import android.graphics.Paint;

import java.math.MathContext;

public class Ball {
    boolean isFalling = true;
    double x;
    int radius = 40;
    double y;
    Paint paint;
    boolean isExploded;

    //constructor

    //get the y-value of the very bottom point of a ball
    public double getBottomY () {
        return y + radius;
    }

    //sets points to each of the four different colors
    public int colorPoints(){
        if (paint.equals(Color.YELLOW)){
            return 10;
        } else if (paint.equals(Color.GREEN)){
           return 20;

        } else if (paint.equals(Color.BLUE)){
            return 30;
        } else {
            return 40;
        }
    }


    // checks if it intersects with another ball
    public boolean intersectsWith (Ball other){
        if (this.isFalling == true && Math.sqrt((other.x - this.x) * (other.x - this.x) + (other.y - this.y) * (other.y - this.y)) == (radius*2)){
            return false;
        } else {
            return true;
        }
    }

    //checks if it intersects with the floor
    public boolean hitsFloor(double canHeight){
        if(this.isFalling == true && getBottomY() >= (canHeight) ){
            return true;
        } else {
            return false;
        }
    }

    public void changeY (double gravity){
        if (isFalling == true){
            y = y + gravity/100;
        }
    }




   public boolean contains(int touchX,int touchY){
       double distance = Math.sqrt((touchX - x)*(touchX-x) + (touchY - y)*(touchY - y));
       if (distance < (double) radius){
           return true;
       } else {
           return false;
       }
   }



}

