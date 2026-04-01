package com.example.quoson.mkryptos;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

/**
 * MainActivity — Application entry point.
 *
 * Displays the main navigation screen, which consists of a grid of
 * ImageButtons. Each button launches a specific feature of the app:
 *   b1 → Text message encryption  (YourMessageActivity)
 *   b2 → Digital signatures       (YourSignatureActivity)
 *   b3 → Photo encryption         (PhotoActivity)
 *   b4 → Video encryption         (VideoActivity)
 *   b5 → Voice/audio encryption   (VoiceActivity)
 *   b6 → Stored key management    (YourKeysActivity)
 *   b7 → Key generation           (KeysActivity)
 *   b8 → Pseudo-random numbers    (NumbersActivity)
 *   b10 → About / company info    (AboutActivity)
 *
 * The ActionBar is intentionally hidden on this screen so the full
 * icon grid occupies the entire display.
 */
public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    // Navigation buttons — each maps to a distinct feature screen
    private ImageButton b1, b2, b3, b4, b5, b6, b7, b8, b9, b10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hide the ActionBar so the icon grid fills the whole screen
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        // Bind each ImageButton to its view ID and register this activity as the click listener
        this.b1 = (ImageButton) findViewById( R.id.optionB1 );
        this.b1.setOnClickListener( this );
        this.b2 = (ImageButton) findViewById( R.id.optionB2 );
        this.b2.setOnClickListener( this );
        this.b3 = (ImageButton) findViewById( R.id.optionB3 );
        this.b3.setOnClickListener( this );
        this.b4 = (ImageButton) findViewById( R.id.optionB4 );
        this.b4.setOnClickListener( this );
        this.b5 = (ImageButton) findViewById( R.id.optionB5 );
        this.b5.setOnClickListener( this );
        this.b6 = (ImageButton) findViewById( R.id.optionB6 );
        this.b6.setOnClickListener( this );
        this.b7 = (ImageButton) findViewById( R.id.optionB7 );
        this.b7.setOnClickListener( this );
        this.b8 = (ImageButton) findViewById( R.id.optionB8 );
        this.b8.setOnClickListener( this );
        // b9 is reserved / not yet implemented
        //this.b9 = (ImageButton) findViewById( R.id.optionB9 );
        this.b10 = (ImageButton) findViewById( R.id.optionB10 );
        this.b10.setOnClickListener( this );
    }

    /**
     * Handles all navigation button clicks using a switch on the view ID.
     * Creates an explicit Intent for the target Activity and starts it.
     */
    @Override
    public void onClick( View v ) {

        Intent nextActivity;

        switch( v.getId() ) {
            case ( R.id.optionB1 ):
                // Navigate to text message encryption / decryption screen
                nextActivity = new Intent( this, YourMessageActivity.class );
                startActivity( nextActivity );
                break;
            case ( R.id.optionB2 ):
                // Navigate to digital signature (sign / verify) screen
                nextActivity = new Intent( this, YourSignatureActivity.class );
                startActivity( nextActivity );
                break;
            case ( R.id.optionB3 ):
                // Navigate to photo encryption screen
                nextActivity = new Intent(this, PhotoActivity.class);
                startActivity(nextActivity);
                break;
            case ( R.id.optionB4 ):
                // Navigate to video encryption screen
                nextActivity = new Intent(this, VideoActivity.class);
                startActivity(nextActivity);
                break;
            case ( R.id.optionB5 ):
                // Navigate to voice / audio encryption screen
                nextActivity = new Intent(this, VoiceActivity.class);
                startActivity(nextActivity);
                break;
            case ( R.id.optionB6 ):
                // Navigate to stored key browser (reads from SQLite)
                nextActivity = new Intent(this, YourKeysActivity.class);
                startActivity(nextActivity);
                break;
            case ( R.id.optionB7 ):
                // Navigate to key generator screen
                nextActivity = new Intent(this, KeysActivity.class);
                startActivity(nextActivity);
                break;
            case ( R.id.optionB8 ):
                // Navigate to pseudo-random number generator
                nextActivity = new Intent(this, NumbersActivity.class);
                startActivity(nextActivity);
                break;
            case ( R.id.optionB10 ):
                // Navigate to the About / company information screen
                nextActivity = new Intent(this, AboutActivity.class);
                startActivity(nextActivity);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu (used by the overflow / action bar)
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Any options-menu selection falls through to the About screen
        int id = item.getItemId();

        Intent nextActivity;
        nextActivity = new Intent(this, AboutActivity.class);
        startActivity(nextActivity);

        return true;
    }
}
