package edu.augustana.csc490.gamestarter;

/**
 * Created by abdulmerhi11 on 4/1/2015.
 */

import android.graphics.Color;
import android.graphics.Paint;
import java.math.MathContext;
import java.util.ArrayList;

public class Ball {
    boolean isFalling = true;
    double x;
    int radius = 40;
    double y;
    Paint paint = new Paint();
    boolean isExploded = false;

    //constructor

    //get the y-value of the very bottom point of a ball
    public double getBottomY () {
        return y + radius;
    }

    //sets points to each of the four different colors
    public int colorPoints(){

        if (paint.getColor() == Color.YELLOW){
            return 10;
        } else if (paint.getColor() == Color.GREEN){
           return 20;

        } else if (paint.getColor() == Color.BLUE){
            return 30;
        } else {
            return 40;
        }
    }


    // checks if it intersects with another ball
    public boolean intersectsWith (ArrayList<Ball> ballsDisplayed){
        boolean check = false;
        for (int i = 0; i < ballsDisplayed.size(); i++) {
            if (Math.sqrt((ballsDisplayed.get(i).x - this.x) * (ballsDisplayed.get(i).x - this.x) + (ballsDisplayed.get(i).y - this.y) * (ballsDisplayed.get(i).y - this.y)) <= (radius*2)) {
                check = true;
                break;
            }
        }
            return check;

    }

    //checks if it intersects with the floor
    public boolean hitsFloor(double canHeight){
        if(this.isFalling == true && getBottomY() >= (canHeight) ){
            return true;
        } else {
            return false;
        }
    }

//    public void changeY (double gravity){
//        if (isFalling == true){
//            y = y + gravity/100;
//        }
//    }




   public boolean contains(int touchX,int touchY){
       double distance = Math.sqrt((touchX - x)*(touchX-x) + (touchY - y)*(touchY - y));
       if (distance < (double) radius){
           return true;
       } else {
           return false;
       }
   }



}

