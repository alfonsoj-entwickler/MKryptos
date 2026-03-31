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

public class RC4Cipher {

    private Cipher myCipher;
    private Key myKey;
    private final String algorithm = "RC4";

    public RC4Cipher( String password ) {
        try {
            this.myKey = new SecretKeySpec( Base64.decode(password.getBytes(), Base64.DEFAULT), this.algorithm );
        }
        catch( Exception e ){
            e.printStackTrace();

        }

    }

    public String doCipher( String message ) {
        try {
            byte [] plainText = message.getBytes();
            byte [] cipherText;
            this.myCipher = Cipher.getInstance( this.algorithm );
            this.myCipher.init( Cipher.ENCRYPT_MODE, this.myKey );
            cipherText = this.myCipher.doFinal( plainText );
            return Base64.encodeToString(cipherText, Base64.DEFAULT);
        }
        catch( Exception e ){
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

    public static String keyGenerator() {
        try {
            KeyGenerator myKeyGen = KeyGenerator.getInstance( "RC4" );
            SecretKey key = myKeyGen.generateKey();
            String myKEyText = Base64.encodeToString(key.getEncoded(), Base64.DEFAULT);
            return myKEyText;
        }
        catch( Exception e ){
            return e.getMessage();
        }

    }

}


