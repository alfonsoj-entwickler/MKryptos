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

public class BlowfishCipher {
    private Key myKey;
    private final String algorithm = "Blowfish";

    public BlowfishCipher( String password ) {
        try {
            this.myKey = new SecretKeySpec(  Base64.decode(password.getBytes(), Base64.DEFAULT), this.algorithm );
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }

    }

    public String doCipher( String message ) {
        try {
            byte [] plainText = message.getBytes();
            byte [] cipherText;

            Cipher myCipher = Cipher.getInstance( this.algorithm );
            myCipher.init( Cipher.ENCRYPT_MODE, this.myKey );
            cipherText = myCipher.doFinal( plainText );
            return Base64.encodeToString( cipherText, Base64.DEFAULT );

        }
        catch ( Exception e) {

            return e.getMessage();
        }

    }

    public String doDecoding( String message ) {

        try {
            byte [] cipherText = Base64.decode(message.getBytes(), Base64.DEFAULT);
            byte [] plainText;

            Cipher myCipher = Cipher.getInstance( this.algorithm );
            myCipher.init( Cipher.DECRYPT_MODE, this.myKey );
            plainText = myCipher.doFinal( cipherText );

            return new String( plainText );
        }
        catch ( Exception e) {
            e.printStackTrace();
            return "Error Fatal";
        }

    }

    public static String keyGenerator() {
        try {
            KeyGenerator myKeyGen = KeyGenerator.getInstance( "Blowfish" );
            SecretKey key = myKeyGen.generateKey();
            String myKEyText = Base64.encodeToString(key.getEncoded(), Base64.DEFAULT);
            return myKEyText;
        }
        catch( Exception e ){
            return e.getMessage();
        }

    }


}

