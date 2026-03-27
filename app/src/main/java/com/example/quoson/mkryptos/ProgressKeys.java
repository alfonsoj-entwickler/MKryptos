package com.example.quoson.mkryptos;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.EditText;
import android.widget.Toast;


/**
 * ProgressKeys — Background AsyncTask for cryptographic key generation.
 *
 * Key generation (especially for RSA at 4096 bits or EC operations) can be
 * computationally expensive and would block the UI thread if run synchronously.
 * This AsyncTask offloads the generation to a background thread and shows a
 * {@link ProgressDialog} while the user waits.
 *
 * Lifecycle:
 *   onPreExecute()   — Shows the "Generate key…" progress dialog on the UI thread.
 *   doInBackground() — Calls the appropriate static keyGenerator() method off the UI thread.
 *   onPostExecute()  — Dismisses the dialog and writes the result into the UI EditText fields.
 *
 * Key storage convention for asymmetric algorithms (RSA, DSA, EC):
 *   random[0] = public key  (Base64)
 *   random[1] = private key (Base64)
 *   Both are concatenated with "###TAB###" delimiter in the hidden progress field
 *   so that KeysActivity can split and save them to SQLite as a single record.
 *
 * For symmetric algorithms (DES, AES, Blowfish, RC4, HMAC variants):
 *   random[0] = the single secret key (Base64)
 */
public class ProgressKeys extends AsyncTask<String, Void, Boolean> {

    private ProgressDialog dialog;    // Spinner shown while key generation runs in the background
    private Context context;
    private String myAlgorithm, name, size; // Algorithm name and requested key size
    private String [] random = new String[2]; // Holds the generated key(s): [0]=public/symmetric, [1]=private
    private EditText myResult, progress;      // UI fields to receive the formatted key output


    /**
     * Constructs a ProgressKeys task for the given algorithm and size.
     *
     * @param c    Android Context (used for dialog and toast display).
     * @param a    Algorithm name string (e.g. "AES", "RSA", "EC").
     * @param s    Key size string (e.g. "256" for AES, "2048" for RSA, "secp256r1" for EC).
     * @param out  EditText that will display the formatted PEM-style key block.
     * @param outS EditText that stores the raw Base64 key value (used later for saving to DB).
     */
    public ProgressKeys( Context c, String a, String s, EditText out, EditText outS ) {
        this.context = c;
        this.myAlgorithm = a;
        this.size = s;
        this.dialog = new ProgressDialog( c );
        this.myResult = out;
        this.progress = outS;
    }

    /** Shows the progress spinner on the UI thread before background work begins. */
    protected void onPreExecute() {
        this.dialog.setMessage( "Generate key ..." );
        this.dialog.show();
    }

    /**
     * Called on the UI thread after doInBackground() completes.
     * Dismisses the progress dialog and updates the EditText fields with the
     * generated key wrapped in PEM-style header/footer lines.
     *
     * @param sucess {@code true} if key generation succeeded; {@code false} on failure.
     */
    @Override
    protected void onPostExecute( final Boolean sucess ){

        if( this.dialog.isShowing() ) {
            this.dialog.dismiss();
        }

        if( sucess ) {

            switch( this.myAlgorithm ) {

                case "DES":
                    this.myResult.setText("-----BEGIN DES KEY-----\n" +  random[0] + "-----END DES KEY-----" );
                    this.progress.setText( random[0] );
                    break;

                case "TripleDES":
                    this.myResult.setText( "-----BEGIN TRIPLEDES KEY-----\n" + random[0] + "-----END TRIPLEDES KEY-----");
                    this.progress.setText( random[0] );
                    break;

                case "AES":
                    this.myResult.setText( "-----BEGIN AES KEY-----\n" + random[0] + "-----END AES KEY-----");
                    this.progress.setText( random[0] );
                    break;

                case "BlowFish":
                    this.myResult.setText( "-----BEGIN BLOWFISH KEY-----\n" + random[0]  + "-----END BLOWFISH KEY-----");
                    this.progress.setText( random[0] );
                    break;

                case "RC4":
                    this.myResult.setText( "-----BEGIN RC4 KEY-----\n" + random[0] + "-----END RC4 KEY-----");
                    this.progress.setText( random[0] );
                    break;

                case "HMACMD5":
                    this.myResult.setText( "-----BEGIN HMACMD5 KEY-----\n" + random[0] + "-----END HMACMD5 KEY-----");
                    this.progress.setText( random[0] );
                    break;

                case "HMACSHA1":
                    this.myResult.setText( "-----BEGIN HMACSHA1 KEY-----\n" + random[0] + "-----END HMACSHA1 KEY-----");
                    this.progress.setText( random[0] );
                    break;

                case "HMACSHA256":
                    this.myResult.setText( "----BEGIN HMACSHA256 KEY----\n" + random[0] + "----END HMACSHA256 KEY----");
                    this.progress.setText( random[0] );
                    break;

                case "HMACSHA384":
                    this.myResult.setText( "----BEGIN HMACSHA384 KEY----\n" + random[0] + "----END HMACSHA384 KEY----");
                    this.progress.setText( random[0] );
                    break;

                case "HMACSHA512":
                    this.myResult.setText( "----BEGIN HMACSHA512 KEY----\n" + random[0] + "----END HMACSHA512 KEY----");
                    this.progress.setText( random[0] );
                    break;

                case "RSA":
                    this.myResult.setText(
                            "----BEGIN RSA PUBLIC KEY----\n"+
                                    random[0] +
                                    "----END RSA PUBLIC KEY----\n"+
                                    "----BEGIN RSA PRIVATE KEY----\n"+
                                    random[1] +
                                    "----END RSA PRIVATE KEY----"
                    );
                    this.progress.setText( random[0] + "###TAB###" + random[1] );
                    break;

                case "DSA":
                    this.myResult.setText(
                            "----BEGIN DSA PUBLIC KEY----\n"+
                                    random[0] +
                                    "----END DSA PUBLIC KEY----\n"+
                                    "----BEGIN DSA PRIVATE KEY----\n"+
                                    random[1] +
                                    "----END DSA PRIVATE KEY----"
                    );
                    this.progress.setText( random[0] + "###TAB###" + random[1] );
                    break;

                case "EC":
                    this.myResult.setText(
                            "----BEGIN EC PUBLIC KEY----\n"+
                                    random[0] +
                                    "----END EC PUBLIC KEY----\n"+
                                    "----BEGIN EC PRIVATE KEY----\n"+
                                    random[1] +
                                    "----END EC PRIVATE KEY----"
                    );
                    this.progress.setText( random[0] + "###TAB###" + random[1] );
                    break;

                case "Select your cipher":
                    this.myResult.setText( R.string.menuConsole5 );
                    break;

                default:
                    this.myResult.setText( R.string.menuConsole5 );
                    break;

            }

        }
        else {
            Toast.makeText( this.context, R.string.KeyMessage3, Toast.LENGTH_LONG ).show();
        }

    }

    /**
     * Performs key generation off the UI thread.
     * Dispatches to the correct static keyGenerator() method based on {@link #myAlgorithm}.
     *
     * @param args Unused (required by AsyncTask signature).
     * @return {@code true} if the key was generated successfully; {@code false} otherwise.
     */
    protected Boolean doInBackground( final String...args ) {

        switch( this.myAlgorithm ) {

            case "DES":
                random[0] = DESCipher.keyGenerator();
                return true;

            case "TripleDES":
                random[0] = TripleDESCipher.keyGenerator();
                return true;

            case "AES":
                random[0] = AESCipher.keyGenerator( this.size );
                return true;

            case "BlowFish":
                random[0] = BlowfishCipher.keyGenerator();
                return true;

            case "RC4":
                random[0] = RC4Cipher.keyGenerator();
                return true;

            case "RSA":
                random = RSACipher.keyGenerator( this.size );
                return true;

            case "DSA":
                random = DSACipher.keyGenerator();
                return true;

            case "EC":
                random = ECDHCipher.keyGenerator( this.size );
                return true;

            case "HMACMD5":
                random[0] = HMACCipher.keyGenerator( "HMACMD5" );
                return true;

            case "HMACSHA1":
                random[0] = HMACCipher.keyGenerator( "HMACSHA1" );
                return true;

            case "HMACSHA256":
                random[0] = HMACCipher.keyGenerator( "HMACSHA256" );
                return true;

            case "HMACSHA384":
                random[0] = HMACCipher.keyGenerator( "HMACSHA384" );
                return true;

            case "HMACSHA512":
                random[0] = HMACCipher.keyGenerator( "HMACSHA512" );
                return true;

            case "Select your cipher":
                return false;

            default:
                return false;
        }

    }
}
