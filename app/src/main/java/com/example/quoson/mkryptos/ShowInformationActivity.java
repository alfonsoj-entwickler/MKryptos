package com.example.quoson.mkryptos;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ShowInformationActivity extends ActionBarActivity {

    private String myID, myPassword;
    DataKeys mySQLKeys;
    private ListKey mySelect;
    SQLiteDatabase db;
    TextView id, name, type, date, password, lenghtByte;
    EditText myEdit;
    private Toast changeAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_information);

        // get ID of Key
        Bundle extraInformation = getIntent().getExtras();
        if( extraInformation != null ) {
            this.myID = extraInformation.getString( "myID" );
        }

        // Databases
        this.mySQLKeys = new DataKeys( this, "DBKeys", null, 4 );
        db = this.mySQLKeys.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM KeysCipher WHERE id = '" + this.myID + "'", null);

        if( c.moveToFirst() ){
            do {
                this.mySelect = new ListKey( c.getString(0), c.getString(1), c.getString(2), c.getString(4) );
                this.myPassword = c.getString(3);
            }while( c.moveToNext() );
        }
        else {
            Log.i( "base", "Error DB" );
        }
        // database closed
        db.close();

        // TextView
        this.id = (TextView)findViewById( R.id.textID );
        this.id.setText( "ID Table: " + mySelect.getID() );
        this.name = (TextView)findViewById( R.id.textName );
        this.name.setText( "Name: " + mySelect.getName() );
        this.type = (TextView)findViewById( R.id.textType );
        this.type.setText( "Algorithm: " + mySelect.getAlgorithm() );
        this.date = (TextView)findViewById( R.id.textDate );
        this.date.setText( "Date: " + mySelect.getTime() );
        this.password = (TextView)findViewById( R.id.textKey );
        this.password.setText( "Your password" );
        this.myEdit = (EditText)findViewById( R.id.showKey );
        this.myEdit.setText( this.myPassword );
        this.lenghtByte = (TextView)findViewById( R.id.textLength );

        if( mySelect.getAlgorithm().compareTo( "RSA" ) == 0 || mySelect.getAlgorithm().compareTo("DSA") == 0 || mySelect.getAlgorithm().compareTo("EC") == 0 ){
            String [] piece = this.myPassword.split("###TAB###");
            this.lenghtByte.setText( "Lenght: " + ( Base64.decode(piece[0].getBytes(), Base64.DEFAULT).length + Base64.decode(piece[1].getBytes(), Base64.DEFAULT).length )+ " bytes" );
        }
        else {
            this.lenghtByte.setText( "Lenght: " + Base64.decode(this.myPassword.getBytes(), Base64.DEFAULT).length + " bytes" );
        }

        switch( mySelect.getAlgorithm() ) {
            case "DES":
                this.myEdit.setText( "-----BEGIN DES KEY-----\n" + this.myPassword + "-----END DES KEY-----" );
                break;

            case "TripleDES":
                this.myEdit.setText( "-----BEGIN TRIPLEDES KEY-----\n" + this.myPassword + "-----END TRIPLEDES KEY-----" );
                break;

            case "AES":
                this.myEdit.setText( "-----BEGIN AES KEY-----\n" + this.myPassword + "-----END AES KEY-----" );
                break;

            case "RC4":
                this.myEdit.setText( "-----BEGIN RC4 KEY-----\n" + this.myPassword + "-----END RC4 KEY-----" );
                break;

            case "BlowFish":
                this.myEdit.setText( "-----BEGIN BLOWFISH KEY-----\n" + this.myPassword + "-----END BLOWFISH KEY-----" );
                break;

            case "HMACMD5":
                this.myEdit.setText( "-----BEGIN HMACMD5 KEY-----\n" + this.myPassword + "-----END HMACMD5 KEY-----" );
                break;

            case "HMACSHA1":
                this.myEdit.setText( "-----BEGIN HMACSHA1 KEY-----\n" + this.myPassword + "-----END HMACSHA1 KEY-----" );
                break;

            case "HMACSHA256":
                this.myEdit.setText( "----BEGIN HMACSHA256 KEY----\n" + this.myPassword + "----END HMACSHA256 KEY----" );
                break;

            case "HMACSHA384":
                this.myEdit.setText( "---BEGIN HMACSHA384 KEY----\n" + this.myPassword + "----END HMACSHA384 KEY----" );
                break;

            case "HMACSHA512":
                this.myEdit.setText( "----BEGIN HMACSHA512 KEY----\n" + this.myPassword + "----END HMACSHA512 KEY----" );
                break;

            case "RSA":
                String [] pieceRSA = this.myPassword.split("###TAB###");
                this.myEdit.setText( "----BEGIN RSA PUBLIC KEY----\n"
                        + pieceRSA[0] +
                        "----END RSA PUBLIC KEY----\n"+
                        "----BEGIN RSA PRIVATE KEY----\n"
                        + pieceRSA[1] +
                        "----END RSA PRIVATE KEY----"
                );
                this.myPassword = pieceRSA[0]; //Public key
                break;

            case "DSA":
                String [] pieceDSA = this.myPassword.split("###TAB###");
                this.myEdit.setText( "----BEGIN DSA PUBLIC KEY----\n"
                         + pieceDSA[0] +
                         "----END DSA PUBLIC KEY----\n"+
                         "----BEGIN DSA PRIVATE KEY----\n"
                         + pieceDSA[1] +
                         "----END DSA PRIVATE KEY----"
                );
                this.myPassword = pieceDSA[0]; //Public key
                break;

            case "EC":
                String [] pieceEC = this.myPassword.split("###TAB###");
                this.myEdit.setText( "----BEGIN EC PUBLIC KEY----\n"
                                + pieceEC[0] +
                                "----END EC PUBLIC KEY----\n"+
                                "----BEGIN EC PRIVATE KEY----\n"
                                + pieceEC[1] +
                                "----END EC PRIVATE KEY----"
                );
                this.myPassword = pieceEC[0]; //Public key
                break;

            default:
                break;

        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_information, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        ClipboardManager clipboard;
        //noinspection SimplifiableIfStatement

        if ( id == R.id.action_copy ) {
            this.changeAlert = Toast.makeText(getApplicationContext(), R.string.menuOptionCopy, Toast.LENGTH_SHORT);
            this.changeAlert.show();
            clipboard = (ClipboardManager) getSystemService( Context.CLIPBOARD_SERVICE );
            ClipData clip = ClipData.newPlainText("simple text", this.myPassword );
            clipboard.setPrimaryClip( clip );
            return true;
        }
        else if(  id == R.id.action_send )  {
            this.changeAlert = Toast.makeText(getApplicationContext(), R.string.menuOptionSend, Toast.LENGTH_SHORT );
            this.changeAlert.show();
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra( Intent.EXTRA_TEXT, this.myEdit.getText().toString() );
            sendIntent.setType("text/plain");
            startActivity(sendIntent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
