package edu.augustana.csc490.gamestarter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class FirstPage extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);

        Button newGame = (Button) findViewById(R.id.newGameB);
        newGame.setOnClickListener(NGClickHandler);

        Button highestScore = (Button) findViewById(R.id.highestScoreB);
        highestScore.setOnClickListener(HSClickHandler);
    }

    View.OnClickListener NGClickHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(FirstPage.this, MainActivity.class);
            startActivity(intent);
        }
    };


    View.OnClickListener HSClickHandler = new View.OnClickListener() {
    @Override
        public void onClick(View v) {
        Intent intent = new Intent(FirstPage.this, HighestScore.class);
        startActivity(intent);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_first_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
