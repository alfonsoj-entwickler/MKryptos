package com.example.quoson.mkryptos;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;


/**
 * YourMessageActivity — Text message encryption and decryption screen.
 *
 * Allows the user to encrypt or decrypt a text message using one of five
 * supported symmetric algorithms: DES, TripleDES, AES, BlowFish, RC4.
 *
 * UI overview:
 *   - enterEditText  : Input field for the plaintext (to encrypt) or ciphertext (to decrypt)
 *   - editKey        : Input field for the Base64-encoded symmetric key
 *   - select_algorithm : Spinner to choose the cipher algorithm
 *   - buttonCipher   : Triggers encryption of the input text
 *   - buttonDecoding : Triggers decryption of the input text
 *   - myResult       : Read-only field displaying the operation output
 *
 * Options menu actions:
 *   Copy   — copies the result text to the clipboard
 *   Paste  — pastes clipboard content into the input field
 *   Load   — opens a dialog to load a saved key from SQLite for the selected algorithm
 *   Example — fills in a demo message and demo key for quick testing
 *   Clean  — resets all input fields and the spinner
 *   Send   — shares the result via Android's ACTION_SEND intent (e.g. SMS, email)
 */
public class YourMessageActivity extends ActionBarActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private EditText enterEditText, editKey, myResult;
    private ImageButton buttonCipher, buttonDecoding;
    private Spinner select_algorithm;
    private Toast changeAlert;
    // Cipher instances — created on demand when the user triggers encrypt/decrypt
    private DESCipher myDESCipher;
    private AESCipher myAESCipher;
    private BlowfishCipher myBlowfishCipher;
    private TripleDESCipher myTripleDESCipher;
    private RC4Cipher myRC4Cipher;
    private String myAlgorithm;  // Tracks the currently selected algorithm name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_message);

        // action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setIcon(R.drawable.mini_msm);

        // editText
        this.enterEditText = (EditText)findViewById( R.id.enterEditText );
        this.editKey = (EditText)findViewById( R.id.editKey );
        this.myResult = (EditText)findViewById( R.id.resultText );

        // button
        this.buttonCipher = (ImageButton)findViewById( R.id.buttonCipher );
        this.buttonDecoding = (ImageButton)findViewById( R.id.buttonDecoding );
        this.buttonCipher.setOnClickListener( this );
        this.buttonDecoding.setOnClickListener( this );

        // spinner
        this.select_algorithm = (Spinner) findViewById( R.id.select_algorithm );
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.array_algorithm, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.select_algorithm.setAdapter(adapter);
        this.select_algorithm.setOnItemSelectedListener(this);

        // initial
        this.myAlgorithm = "";

    }

    @Override
    public void onClick( View v ) {

        switch( v.getId() ) {
            case ( R.id.buttonCipher ):
                if( this.enterEditText.getText().toString().length() > 0 || this.editKey.getText().toString().length() > 0 ) {
                    this.buttonCipher.setBackgroundResource(R.drawable.border_on);
                    this.buttonCipher.setImageResource(R.drawable.icon_close_on);
                    this.buttonDecoding.setBackgroundResource(R.drawable.border_off);
                    this.buttonDecoding.setImageResource(R.drawable.icon_open_off);
                    dofinalMessage(this.myAlgorithm, this.enterEditText.getText().toString(), this.editKey.getText().toString(), true, false);
                }
                else {
                    this.changeAlert = Toast.makeText( getApplicationContext(), R.string.menuConsole6, Toast.LENGTH_SHORT );
                    this.changeAlert.show();
                }
                break;
            case ( R.id.buttonDecoding ):
                if( this.enterEditText.getText().toString().length() > 0 || this.editKey.getText().toString().length() > 0 ) {
                    this.buttonDecoding.setBackgroundResource(R.drawable.border_on);
                    this.buttonDecoding.setImageResource(R.drawable.icon_open_on);
                    this.buttonCipher.setBackgroundResource(R.drawable.border_off);
                    this.buttonCipher.setImageResource(R.drawable.icon_close_off);
                    dofinalMessage(this.myAlgorithm, this.enterEditText.getText().toString(), this.editKey.getText().toString(), false, true);
                }
                else {
                    this.changeAlert = Toast.makeText( getApplicationContext(), R.string.menuConsole6, Toast.LENGTH_SHORT );
                    this.changeAlert.show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_your_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        ClipboardManager clipboard;

        switch( item.getItemId() ) {
            case ( R.id.action_copy ):
                this.changeAlert = Toast.makeText(getApplicationContext(), R.string.menuOptionCopy, Toast.LENGTH_SHORT);
                this.changeAlert.show();
                clipboard = (ClipboardManager) getSystemService( Context.CLIPBOARD_SERVICE );
                ClipData clip = ClipData.newPlainText("simple text", this.myResult.getText().toString());
                clipboard.setPrimaryClip( clip );
                return true;

            case ( R.id.action_paste ):
                this.changeAlert = Toast.makeText( getApplicationContext(), R.string.menuOptionPaste, Toast.LENGTH_SHORT );
                this.changeAlert.show();
                clipboard = (ClipboardManager) getSystemService( Context.CLIPBOARD_SERVICE );
                ClipData.Item paste = clipboard.getPrimaryClip().getItemAt(0);
                this.enterEditText.setText(paste.getText());
                return true;

            case ( R.id.action_load ):
                if( this.myAlgorithm == "" || this.myAlgorithm == "Select your cipher" ) {
                    this.changeAlert = Toast.makeText( getApplicationContext(), R.string.menuConsole6, Toast.LENGTH_SHORT );
                    this.changeAlert.show();
                }else {
                    loadYourkey( this.myAlgorithm );
                }


                return true;

            case ( R.id.action_example ):
                this.changeAlert = Toast.makeText(getApplicationContext(), R.string.menuOptionExample, Toast.LENGTH_SHORT );
                this.changeAlert.show();
                this.enterEditText.setText( R.string.exampleText );
                this.editKey.setText( R.string.exampleKey );
                return true;

            case ( R.id.action_clean ):
                this.changeAlert = Toast.makeText(getApplicationContext(), R.string.menuOptionClean, Toast.LENGTH_SHORT );
                this.changeAlert.show();
                this.enterEditText.setText("");
                this.editKey.setText( "" );
                this.myResult.setText( "" );
                this.select_algorithm.setSelection(0);
                return true;

            case ( R.id.action_send ):
                this.changeAlert = Toast.makeText(getApplicationContext(), R.string.menuOptionSend, Toast.LENGTH_SHORT );
                this.changeAlert.show();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra( Intent.EXTRA_TEXT, this.myResult.getText().toString() );
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                return true;
        }

        return super.onOptionsItemSelected(item);
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
            default:
                this.myAlgorithm = "";
                break;
        }

    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    /**
     * Dispatches the encrypt or decrypt operation to the appropriate cipher class.
     *
     * @param wichCipher The algorithm name (e.g. "AES", "DES", "RC4").
     * @param message    The input text — plaintext for encryption, Base64 ciphertext for decryption.
     * @param password   The Base64-encoded symmetric key string.
     * @param cipher     {@code true} to perform encryption.
     * @param decoding   {@code true} to perform decryption.
     */
    public void dofinalMessage( String wichCipher, String message, String password, boolean cipher, boolean decoding ) {

        switch( wichCipher ) {

            case "DES":
                myDESCipher = new DESCipher( password );
                if( cipher ){
                    this.myResult.setText( myDESCipher.doCipher( message ) );
                }
                else if( decoding ){
                    this.myResult.setText( myDESCipher.doDecoding(message) );
                }
                break;

            case "TripleDES":
                myTripleDESCipher = new TripleDESCipher( password );
                if( cipher ){
                    this.myResult.setText( myTripleDESCipher.doCipher( message ) );
                }
                else if( decoding ){
                    this.myResult.setText( myTripleDESCipher.doDecoding(message) );
                }
                break;
            case "AES":
                myAESCipher = new AESCipher( password );
                if( cipher ){
                    this.myResult.setText( myAESCipher.doCipher( message ) );
                }
                else if( decoding ){
                    this.myResult.setText( myAESCipher.doDecoding(message) );
                }
                break;

            case "BlowFish":
                myBlowfishCipher = new BlowfishCipher( password );
                if( cipher ){
                    this.myResult.setText( myBlowfishCipher.doCipher( message ) );
                }
                else if( decoding ){
                    this.myResult.setText( myBlowfishCipher.doDecoding(message) );
                }
                break;

            case "RC4":
                myRC4Cipher = new RC4Cipher( password );
                if( cipher ){
                    this.myResult.setText( myRC4Cipher.doCipher( message ) );
                }
                else if( decoding ){
                    this.myResult.setText( myRC4Cipher.doDecoding(message) );
                }
                break;

            case "Select your cipher":
                this.changeAlert = Toast.makeText(getApplicationContext(), R.string.menuConsole5, Toast.LENGTH_SHORT);
                this.changeAlert.show();
                break;

            default:
                this.changeAlert = Toast.makeText(getApplicationContext(), R.string.menuConsole5, Toast.LENGTH_SHORT);
                this.changeAlert.show();
                break;
        }
    }

    /**
     * Loads stored keys of the specified algorithm type from SQLite and displays
     * them in a selection dialog. When the user picks a key, its value is pasted
     * into the editKey field.
     *
     * @param myType The algorithm type used to filter keys (e.g. "AES", "DES").
     * @return An empty string (return value unused; result is applied directly to the UI).
     */
    public String loadYourkey( String myType ) {
        String yourKey = "";
        ListKey [] myListKey;
        String [] showName;
        DataKeys mySQLKeys;
        SQLiteDatabase db;

        // Databases
        mySQLKeys = new DataKeys( this, "DBKeys", null, 4 );
        db = mySQLKeys.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM KeysCipher WHERE type = '" + myType + "'", null);
        int cuantos =0;

        if( c.moveToFirst() ){
            do {
                cuantos++;
            }while( c.moveToNext() );
        }
        else {
            Log.i( "base", "Error DB" );
        }

        //myListKey = new ListKey[cuantos];
        showName = new String[cuantos];
        int index = 0;

        if( c.moveToFirst() ){
            do {
                //myListKey[index] = new ListKey( c.getString(0), c.getString(1), c.getString(2), c.getString(4) );
                showName[index] = c.getString(1);
                index++;
            }while( c.moveToNext() );
        }
        else {
            Log.i( "base", "Error DB" );
        }

        // database closed
        db.close();

        final String [] mySelection = showName;

        AlertDialog.Builder alert_select = new AlertDialog.Builder( this );
        alert_select.setTitle( R.string.menuOptionSelectKey );
        alert_select.setItems(showName, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText save = (EditText)findViewById( R.id.editKey );

                // Databases
                DataKeys selectSQLKeys = new DataKeys( getApplication(), "DBKeys", null, 4 );
                SQLiteDatabase dbNew = selectSQLKeys.getWritableDatabase();

                Cursor c = dbNew.rawQuery("SELECT * FROM KeysCipher WHERE name = '" + mySelection[which] + "'", null);

                Log.i("base", "Your selection: " + mySelection[which]);

                if( c.moveToFirst() ) {
                    save.setText(c.getString(3));
                }

                dbNew.close();

            }
        });

        alert_select.show();

        return yourKey;
    }
}
