package com.example.quoson.mkryptos;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

public class NumbersActivity extends ActionBarActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private EditText myResult;
    private ImageButton buttonCipher;
    private Spinner select_algorithm;
    private Toast changeAlert;
    private String myAlgorithm;
    private DataKeys mySQLKeys;
    private String temporalKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_numbers);

        // action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setIcon(R.drawable.mini_main);

        // editText
        this.myResult = (EditText)findViewById( R.id.resultText );

        // button
        this.buttonCipher = (ImageButton)findViewById( R.id.buttonCipher );
        this.buttonCipher.setOnClickListener( this );

        // spinner
        this.select_algorithm = (Spinner) findViewById( R.id.select_random );
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.array_randoms, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.select_algorithm.setAdapter(adapter);
        this.select_algorithm.setOnItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_numbers, menu);
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

    @Override
    public void onClick( View v ) {

        switch( v.getId() ) {
            case ( R.id.buttonCipher ):
                this.buttonCipher.setBackgroundResource(R.drawable.border_on);
                this.buttonCipher.setImageResource(R.drawable.icon_close_on);
                this.myResult.setText( PseudoRandomGenerator.squareRandom(10) );

                break;

            default:
                break;
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)

        switch( parent.getItemAtPosition(pos).toString() ) {

            case "DES":
                this.myAlgorithm = "DES";
                this.myResult.setText( this.myAlgorithm );
                break;
            case "TripleDES":
                this.myAlgorithm = "TripleDES";
                this.myResult.setText( this.myAlgorithm );
                break;
            case "AES":
                this.myAlgorithm = "AES";
                this.myResult.setText( this.myAlgorithm );
                break;
            case "BlowFish":
                this.myAlgorithm = "BlowFish";
                this.myResult.setText( this.myAlgorithm );
                break;
            case "RC4":
                this.myAlgorithm = "RC4";
                this.myResult.setText( this.myAlgorithm );
                break;
            case "RSA":
                this.myAlgorithm = "RSA";
                this.myResult.setText( this.myAlgorithm );
                break;
            case "DSA":
                this.myAlgorithm = "DSA";
                this.myResult.setText( this.myAlgorithm );
                break;
            default:
                this.myAlgorithm = "Select your cipher";
                break;
        }

    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
}
