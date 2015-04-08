package edu.augustana.csc490.gamestarter;

/**
 * Created by abdulmerhi11 on 4/1/2015.
 */

import android.graphics.Color;

import java.math.MathContext;

public class Ball {
    boolean isFalling = true;
    int points;
    int x;
    int radius;
    double y;
    Color color;



    //get the y-value of the very bottom point of a ball
    public double getBottomY () {
        return y + radius;
    }

    //sets points to each of the four different colors
    public void colorPoints(){
        if (color.equals(Color.YELLOW)){
            points = 10;
        } else if (color.equals(Color.GREEN)){
            points = 20;

        } else if (color.equals(Color.BLUE)){
            points = 30;
        } else {
            points = 40;
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
    public boolean intersectsWith (double canWidth){
        if(this.isFalling == true && getBottomY() == canWidth){
            return false;
        } else {
            return true;
        }
    }

    public void changeY (double gravity){
        if (isFalling == true){
            y = y + gravity/100;
        }
    }



}

