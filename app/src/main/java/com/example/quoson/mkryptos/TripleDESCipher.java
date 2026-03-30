package com.example.quoson.mkryptos;


import android.util.Base64;

import java.security.Key;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class TripleDESCipher {

    private Key myKey;
    private Cipher myCipher;
    private final String algorithm = "DESEDE";
    private final int LENGTH_PASSWORD = 24;

    public TripleDESCipher( String password ) {
        try {
            //password = testKey( password );
            this.myKey = new SecretKeySpec(  Base64.decode(password.getBytes(), Base64.DEFAULT), algorithm );
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }

    }

    public String doCipher( String message ) {
        try {
            byte [] plainText =  message.getBytes();
            byte [] cipherText;
            this.myCipher = Cipher.getInstance( this.algorithm );
            this.myCipher.init( Cipher.ENCRYPT_MODE, this.myKey );
            cipherText = this.myCipher.doFinal( plainText );

            return Base64.encodeToString( cipherText, Base64.DEFAULT );
        }
        catch ( Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }

    }

    public String doDecoding( String message ) {
        try {
            byte [] cipherText = Base64.decode(message.getBytes(), Base64.DEFAULT);
            byte [] plainText;
            this.myCipher = Cipher.getInstance( this.algorithm );
            this.myCipher.init( Cipher.DECRYPT_MODE, this.myKey );
            plainText = this.myCipher.doFinal( cipherText );
            return new String( plainText );
        }
        catch( Exception e ){
            return e.getMessage();
        }
    }

    public String testKey( String password ) {

        int lengthKey = password.length();

        if( lengthKey == LENGTH_PASSWORD ) {
            return password;
        }
        else if( lengthKey >  LENGTH_PASSWORD ) {
            return password.substring( 0, LENGTH_PASSWORD );
        }
        else {
            int distance = LENGTH_PASSWORD - lengthKey;
            for( int i=0; i<distance; i++ ) {
                password = password.concat( "0" );
            }

            return password;
        }
    }

    public static String keyGenerator() {
        try {
            KeyGenerator myKeyGen = KeyGenerator.getInstance( "DESEDE" );
            //myKeyGen.init(LENGTH_PASSWORD_BITS);
            SecretKey key = myKeyGen.generateKey();
            String myKEyText = Base64.encodeToString(key.getEncoded(), Base64.DEFAULT);
            return myKEyText;
        }
        catch( Exception e ){
            return e.getMessage();
        }

    }


}
