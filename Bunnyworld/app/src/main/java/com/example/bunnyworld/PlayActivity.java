package com.example.bunnyworld;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class PlayActivity extends AppCompatActivity {

    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_play);

        Shape.context = this;
        db = openOrCreateDatabase("GamesDB", MODE_PRIVATE, null);




        String gameName;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                gameName = null;
            } else {
                gameName = extras.getString("STRING_I_NEED");
            }
        } else {
            gameName = (String) savedInstanceState.getSerializable("STRING_I_NEED");
        }


//        Game.main();




        // restore
        if (gameName.equals("bw")) {
            Game.main();
        } else {
            Game.loadGame(db, gameName);
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.main, menu);

        //menu.add(1, "Save");
        menu.add(1, 1, 1, "save");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                Game.saveGame(db);
                Toast.makeText(this, "Save", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
