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
import android.util.Base64;
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

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class YourSignatureActivity extends ActionBarActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener{

    private EditText enterEditText, editKey, myResult, saveKey;
    private ImageButton buttonCipher, buttonDecoding;
    private Spinner select_algorithm;
    private Toast changeAlert;
    private String myAlgorithm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_signature);

        // action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setIcon(R.drawable.mini_file);

        // editText
        this.enterEditText = (EditText)findViewById( R.id.enterEditText );
        this.editKey = (EditText)findViewById( R.id.editKey );
        this.myResult = (EditText)findViewById( R.id.resultText );
        this.saveKey = (EditText)findViewById( R.id.SaveKeyText );

        // button
        this.buttonCipher = (ImageButton)findViewById( R.id.buttonCipher );
        this.buttonDecoding = (ImageButton)findViewById( R.id.buttonDecoding );
        this.buttonCipher.setOnClickListener( this );
        this.buttonDecoding.setOnClickListener( this );

        // spinner
        this.select_algorithm = (Spinner) findViewById( R.id.select_algorithm );
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.array_signature, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.select_algorithm.setAdapter(adapter);
        this.select_algorithm.setOnItemSelectedListener(this);

        // initial
        this.myAlgorithm = "";

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_your_signature, menu);
        return true;
    }

    @Override
    public void onClick( View v ) {

        switch( v.getId() ) {
            case ( R.id.buttonCipher ):
                if( this.enterEditText.getText().toString().length() > 0 || this.editKey.getText().toString().length() > 0 || this.myAlgorithm != "" ) {
                    this.buttonCipher.setBackgroundResource(R.drawable.border_on);
                    this.buttonCipher.setImageResource(R.drawable.icon_close_on);
                    this.buttonDecoding.setBackgroundResource(R.drawable.border_off);
                    this.buttonDecoding.setImageResource(R.drawable.icon_open_off);
                    try {
                        Signature sig = Signature.getInstance( this.myAlgorithm );
                        String myPassword = this.saveKey.getText().toString();
                        String [] piece = myPassword.split("###TAB###");

                        //Public key
                        PublicKey pKey = null;
                        byte[] binCpk = Base64.decode( piece[0], Base64.DEFAULT);
                        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(binCpk);
                        pKey = keyFactory.generatePublic(publicKeySpec);

                        //Private Key
                        PrivateKey privateKey = null;
                        byte[] binCpkpriv = Base64.decode( piece[1], Base64.DEFAULT);
                        KeyFactory keyFactorypriv = KeyFactory.getInstance("RSA");
                        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(binCpkpriv);
                        privateKey = keyFactorypriv.generatePrivate(privateKeySpec);
                        sig.initSign( privateKey );


                        Cipher myCipher = Cipher.getInstance("RSA");
                        myCipher.init(Cipher.ENCRYPT_MODE, pKey);
                        byte [] cipherBytes = myCipher.doFinal(this.enterEditText.getText().toString().getBytes());
                        String cipherText = Base64.encodeToString( cipherBytes, Base64.DEFAULT );
                        sig.update(this.enterEditText.getText().toString().getBytes());


                        this.myResult.setText(
                                "###BEGIN Message###\n" +
                                        cipherText +
                                "###END Message###\n" +
                                "###BEGIN Signature###\n" +
                                Base64.encodeToString( sig.sign() , Base64.DEFAULT ) +
                                "###END Signature###"
                        );

                    }
                    catch( Exception e ){
                        String [] arrayErrors = new String[2];
                        arrayErrors[0] = e.getMessage();
                        arrayErrors[1] = e.getLocalizedMessage();
                        this.myResult.setText("###ERROR###" + arrayErrors[0] + arrayErrors[1]);

                    }

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

                    try {
                        Signature sig = Signature.getInstance( this.myAlgorithm );
                        String myPassword = this.saveKey.getText().toString();
                        String [] piece = myPassword.split("###TAB###");

                        //Public key
                        PublicKey pKey = null;
                        byte[] binCpk = Base64.decode( piece[0], Base64.DEFAULT);
                        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(binCpk);
                        pKey = keyFactory.generatePublic(publicKeySpec);


                        //Private Key
                        PrivateKey privateKey = null;
                        byte[] binCpkpriv = Base64.decode( piece[1], Base64.DEFAULT);
                        KeyFactory keyFactorypriv = KeyFactory.getInstance("RSA");
                        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(binCpkpriv);
                        privateKey = keyFactorypriv.generatePrivate(privateKeySpec);


                        String arrayResult [] = this.enterEditText.getText().toString().split("###END Message###\n###BEGIN Signature###\n");
                        Cipher myCipher = Cipher.getInstance("RSA");
                        myCipher.init(Cipher.DECRYPT_MODE, privateKey);
                        byte [] cipherBytes = myCipher.doFinal(Base64.decode(arrayResult[0].substring("###BEGIN Message###\n".length()).getBytes(), Base64.DEFAULT));

                        sig.initVerify( pKey );
                        sig.update(cipherBytes);
                        boolean valid = sig.verify(Base64.decode(arrayResult[1].substring(0, arrayResult[1].length() - "###END Signature###".length()).getBytes(), Base64.DEFAULT ) );

                        this.myResult.setText(new String(cipherBytes) + "\nMessage ist " + valid );




                    }
                    catch( Exception e ){
                        String [] arrayErrors = new String[2];
                        arrayErrors[0] = e.getMessage();
                        arrayErrors[1] = e.getLocalizedMessage();
                        this.myResult.setText("###ERROR###" + arrayErrors[0] + arrayErrors[1]);

                    }
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

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)

        switch( parent.getItemAtPosition(pos).toString() ) {

            case "SHA512WithRSA":
                this.myAlgorithm = "SHA512WithRSA";
                this.myResult.setText( this.myAlgorithm );
                break;
            case "SHA384WithRSA":
                this.myAlgorithm = "SHA384WithRSA";
                this.myResult.setText( this.myAlgorithm );
                break;
            case "SHA256WithRSA":
                this.myAlgorithm = "SHA256WithRSA";
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
                loadYourkey( "RSA" );
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
            Log.i("base", "Error DB");
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
                EditText saveKey = (EditText)findViewById( R.id.SaveKeyText );

                // Databases
                DataKeys selectSQLKeys = new DataKeys( getApplication(), "DBKeys", null, 4 );
                SQLiteDatabase dbNew = selectSQLKeys.getWritableDatabase();

                Cursor c = dbNew.rawQuery("SELECT * FROM KeysCipher WHERE name = '" + mySelection[which] + "'", null);

                Log.i("base", "Your selection: " + mySelection[which]);

                if( c.moveToFirst() ) {
                    save.setText(c.getString(1));
                    saveKey.setText(c.getString(3));
                }

                dbNew.close();

            }
        });

        alert_select.show();

        return yourKey;
    }



}
