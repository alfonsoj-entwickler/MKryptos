package com.example.quoson.mkryptos;

import android.content.ClipboardManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import java.text.DateFormat;
import java.util.Date;


/**
 * KeysActivity — Cryptographic key generation and saving screen.
 *
 * Allows the user to:
 *   1. Select an algorithm (and optionally a key size) from a Spinner.
 *   2. Trigger key generation via {@link ProgressKeys} (runs on a background thread).
 *   3. Assign a human-readable name to the generated key.
 *   4. Save the named key to the SQLite database via {@link DataKeys}.
 *
 * UI fields:
 *   myResult     — Displays the generated key in a PEM-style formatted block.
 *   myID         — Text field for the user to type a name/identifier for the key.
 *   myProgress   — Hidden field holding the raw Base64 key value(s) for DB insertion.
 *   buttonCipher — Triggers background key generation via ProgressKeys.
 *   buttonSave   — Reads the generated key from myProgress and inserts it into SQLite.
 *
 * Key delimiter convention for asymmetric keys (RSA, DSA, EC):
 *   The public and private keys are joined with "###TAB###" in myProgress,
 *   and split again by {@link #dofinalMessage} before saving to the database.
 */
public class KeysActivity extends ActionBarActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private EditText myResult, myID, myProgress;
    private ImageButton buttonCipher, buttonSave;
    private Spinner select_algorithm, select_size;
    private Toast changeAlert;
    // myAlgorithm: selected algorithm name; temporalKey: key material ready for DB insert
    // identification: user-provided key name; size_key: selected key size string
    private String myAlgorithm, temporalKey, identification, size_key;
    private DataKeys mySQLKeys;  // SQLite helper opened once in onCreate


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keys);

        // action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setIcon(R.drawable.mini_key);

        // editText
        this.myResult = (EditText)findViewById( R.id.resultText );
        this.myID = (EditText)findViewById( R.id.editID );
        this.myProgress = (EditText)findViewById( R.id.progressText );

        // button
        this.buttonCipher = (ImageButton)findViewById( R.id.buttonCipher );
        this.buttonCipher.setOnClickListener( this );
        this.buttonSave = (ImageButton)findViewById( R.id.buttonSave );
        this.buttonSave.setOnClickListener( this );

        // spinner
        this.select_algorithm = (Spinner) findViewById( R.id.select_algorithm );
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.array_keys, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.select_algorithm.setAdapter(adapter);
        this.select_algorithm.setOnItemSelectedListener(this);

        this.select_size = (Spinner) findViewById( R.id.select_size );
        this.select_size.setOnItemSelectedListener(this);

        // initial
        this.myAlgorithm = "";

        // Databases
        this.mySQLKeys = new DataKeys( this, "DBKeys", null, 4 );
    }

    @Override
    public void onClick( View v ) {

        switch( v.getId() ) {
            case ( R.id.buttonCipher ):
                if( this.myAlgorithm.compareTo( "Select your cipher" ) == 0 ) {
                    this.changeAlert = Toast.makeText(getApplicationContext(), this.myAlgorithm, Toast.LENGTH_SHORT);
                    this.changeAlert.show();
                }
                else {
                    this.buttonCipher.setBackgroundResource(R.drawable.border_on);
                    new ProgressKeys( this, this.myAlgorithm, this.size_key, this.myResult, this.myProgress).execute();
                    //this.myID.setFocusable( true );
                    this.myID.setFocusableInTouchMode( true );
                }

                break;
            case ( R.id.buttonSave ):
                if( this.myAlgorithm.compareTo( "Select your cipher" ) == 0 || this.myID.getText().toString().compareTo( "" ) == 0 ) {
                    this.changeAlert = Toast.makeText(getApplicationContext(), R.string.menuConsole5, Toast.LENGTH_SHORT);
                    this.changeAlert.show();
                }
                else {
                    ClipboardManager clipboard;
                    SQLiteDatabase db;
                    dofinalMessage( this.myAlgorithm );
                    this.identification = this.myID.getText().toString();
                    String dateKey = DateFormat.getDateTimeInstance().format(new Date());
                    db = this.mySQLKeys.getWritableDatabase();

                    // the name is free?
                    Cursor c = db.rawQuery("SELECT * FROM KeysCipher WHERE name = '" + this.identification + "'", null);

                    if( c.moveToFirst() ){
                        this.changeAlert = Toast.makeText(getApplicationContext(), R.string.menuOptionName, Toast.LENGTH_SHORT);
                        this.changeAlert.show();
                    }
                    else {
                        // save data Database
                        // CREATE TABLE KeysCipher( id INTEGER PRIMARY KEY AUTOINCREMENT; type TEXT, yourKey TEXT )
                        db.execSQL("INSERT INTO KeysCipher( name, type, yourKey, myDate )" +
                                "VALUES ('" + this.identification + "', '" + this.myAlgorithm + "', '" + this.temporalKey + "', '" + dateKey + "' )");

                        this.changeAlert = Toast.makeText(getApplicationContext(), R.string.menuOptionSave, Toast.LENGTH_SHORT);
                        this.changeAlert.show();

                        this.buttonSave.setBackgroundResource(R.drawable.border_on);
                    }

                    db.close();
                }

                break;

            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_keys, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        ClipboardManager clipboard;
        SQLiteDatabase db;

        switch( item.getItemId() ) {
            case ( R.id.action_save ):
                this.identification = this.myID.getText().toString();
                String dateKey = DateFormat.getDateTimeInstance().format(new Date());
                db = this.mySQLKeys.getWritableDatabase();
                // id INTEGER PRIMARY KEY AUTOINCREMENT; type TEXT, yourKey TEXT
                if( db != null ) {
                    // CREATE TABLE KeysCipher( id INTEGER PRIMARY KEY AUTOINCREMENT; type TEXT, yourKey TEXT )
                    db.execSQL( "INSERT INTO KeysCipher( name, type, yourKey, myDate )" +
                    "VALUES ('" +this.identification+ "', '"+ this.myAlgorithm +"', '"+ this.temporalKey +"', '"+ dateKey +"' )");
                    db.close();
                    this.changeAlert = Toast.makeText(getApplicationContext(), R.string.menuOptionSave, Toast.LENGTH_SHORT);
                    this.changeAlert.show();
                }
                return true;

            case ( R.id.action_load ):

                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        this.buttonCipher.setBackgroundResource( R.drawable.border_off );
        this.buttonSave.setBackgroundResource( R.drawable.border_off );
        ArrayAdapter<CharSequence> adapterSize = null;
        switch( parent.getItemAtPosition(pos).toString() ) {
            case "DES":
                this.myAlgorithm = "DES";
                this.myResult.setText( this.myAlgorithm );
                break;
            case "TripleDES":
                this.myAlgorithm = "TripleDES";
                this.myResult.setText( this.myAlgorithm );
                break;
            case "AES 128":
                this.myAlgorithm = "AES";
                this.size_key = "128";
                this.myResult.setText(this.myAlgorithm);
                break;
            case "AES 192":
                this.myAlgorithm = "AES";
                this.size_key = "192";
                this.myResult.setText(this.myAlgorithm);
                break;
            case "AES 256":
                this.myAlgorithm = "AES";
                this.size_key = "256";
                this.myResult.setText(this.myAlgorithm);
                break;
            case "AES 512":
                this.myAlgorithm = "AES";
                this.size_key = "512";
                this.myResult.setText(this.myAlgorithm);
                break;
            case "BlowFish":
                this.myAlgorithm = "BlowFish";
                this.myResult.setText( this.myAlgorithm );
                break;
            case "RC4":
                this.myAlgorithm = "RC4";
                this.myResult.setText( this.myAlgorithm );
                break;
            case "RSA 1024":
                this.size_key = "1024";
                this.myAlgorithm = "RSA";
                this.myResult.setText(this.myAlgorithm);
                break;
            case "RSA 2048":
                this.size_key = "2048";
                this.myAlgorithm = "RSA";
                this.myResult.setText(this.myAlgorithm);
                break;
            case "RSA 3072":
                this.size_key = "3072";
                this.myAlgorithm = "RSA";
                this.myResult.setText(this.myAlgorithm);
                break;
            case "RSA 4096":
                this.size_key = "4096";
                /*adapterSize = ArrayAdapter.createFromResource(this, R.array.size_rsa, android.R.layout.simple_spinner_item);
                adapterSize.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                this.select_size.setAdapter( adapterSize );*/
                this.myAlgorithm = "RSA";
                this.myResult.setText(this.myAlgorithm);
                break;
            case "DSA":
                this.myAlgorithm = "DSA";
                this.myResult.setText( this.myAlgorithm );
                break;
            case "EC 224":
                this.myAlgorithm = "EC";
                this.size_key = "secp224r1";
                this.myResult.setText( this.myAlgorithm );
                break;
            case "EC 256":
                this.myAlgorithm = "EC";
                this.size_key = "secp256r1";
                this.myResult.setText( this.myAlgorithm );
                break;
            case "EC 384":
                this.myAlgorithm = "EC";
                this.size_key = "secp384r1";
                this.myResult.setText( this.myAlgorithm );
                break;
            case "EC 512":
                this.myAlgorithm = "EC";
                this.size_key = "secp521r1";
                this.myResult.setText( this.myAlgorithm );
                break;
            case "HMACMD5":
                this.myAlgorithm = "HMACMD5";
                this.myResult.setText( this.myAlgorithm );
                break;
            case "HMACSHA1":
                this.myAlgorithm = "HMACSHA1";
                this.myResult.setText( this.myAlgorithm );
                break;
            case "HMACSHA256":
                this.myAlgorithm = "HMACSHA256";
                this.myResult.setText( this.myAlgorithm );
                break;
            case "HMACSHA384":
                this.myAlgorithm = "HMACSHA384";
                this.myResult.setText( this.myAlgorithm );
                break;
            case "HMACSHA512":
                this.myAlgorithm = "HMACSHA512";
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

    /**
     * Reads the generated key from the hidden progress field and stores it in
     * {@link #temporalKey}, ready to be inserted into SQLite by the Save button handler.
     *
     * For asymmetric algorithms (RSA, DSA, EC), the progress field contains
     * public and private keys joined by "###TAB###". This method splits them
     * and reassembles the string so the DB record stores both in one column.
     *
     * @param wichCipher The algorithm type string (e.g. "AES", "RSA", "EC").
     */
    public void dofinalMessage( String wichCipher ) {

        switch( wichCipher ) {

            case "DES":
                temporalKey = this.myProgress.getText().toString(); // Single symmetric key
                this.myProgress.setText("");
                break;

            case "TripleDES":
                temporalKey = this.myProgress.getText().toString();
                this.myProgress.setText("");
                break;

            case "AES":
                temporalKey = this.myProgress.getText().toString();
                this.myProgress.setText("");
                break;

            case "BlowFish":
                temporalKey = this.myProgress.getText().toString();
                this.myProgress.setText("");
                break;

            case "RC4":
                temporalKey = this.myProgress.getText().toString();
                this.myProgress.setText("");
                break;

            case "HMACMD5":
                temporalKey = this.myProgress.getText().toString();
                this.myProgress.setText("");
                break;

            case "HMACSHA1":
                temporalKey = this.myProgress.getText().toString();
                this.myProgress.setText("");
                break;

            case "HMACSHA256":
                temporalKey = this.myProgress.getText().toString();
                this.myProgress.setText("");
                break;

            case "HMACSHA384":
                temporalKey = this.myProgress.getText().toString();
                this.myProgress.setText("");
                break;

            case "HMACSHA512":
                temporalKey = this.myProgress.getText().toString();
                this.myProgress.setText("");
                break;

            case "RSA":
                String [] RSAKeys;
                RSAKeys = this.myProgress.getText().toString().split("###TAB###");
                temporalKey = RSAKeys[0] + "###TAB###" + RSAKeys[1];
                this.myProgress.setText("");
                break;

            case "DSA":
                String [] DSAKeys;
                DSAKeys = this.myProgress.getText().toString().split("###TAB###");
                temporalKey = DSAKeys[0] + "###TAB###" + DSAKeys[1];
                this.myProgress.setText("");
                break;

            case "EC":
                String [] ECDHKeys;
                ECDHKeys = this.myProgress.getText().toString().split("###TAB###");
                temporalKey = ECDHKeys[0] + "###TAB###" + ECDHKeys[1];
                this.myProgress.setText("");
                break;

            case "Select your cipher":
                this.changeAlert = Toast.makeText(getApplicationContext(), R.string.menuConsole5, Toast.LENGTH_SHORT);
                this.changeAlert.show();
                this.myResult.setText("");
                this.myProgress.setText("");
                break;

            default:
                this.changeAlert = Toast.makeText(getApplicationContext(), R.string.menuConsole5, Toast.LENGTH_SHORT);
                this.changeAlert.show();
                break;
        }
    }


}
