package com.example.quoson.mkryptos;

import android.util.Base64;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class ModernCipher {

    private Key myKey;
    private Cipher myCipher;
    private String algorithm;

    public ModernCipher( String password, String algorithm ) {
        try {
            this.myKey = new SecretKeySpec( password.getBytes(), algorithm );
            this.algorithm = algorithm;
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

            return Base64.encodeToString(cipherText, Base64.DEFAULT);
        }
        catch ( Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }

    }

    public String doDecoding( String message ) {
        try {
            byte [] cipherText = Base64.encode( message.getBytes(), Base64.DEFAULT );
            byte [] plainText;
            this.myCipher = Cipher.getInstance( this.algorithm );
            this.myCipher.init( Cipher.DECRYPT_MODE, this.myKey );
            plainText = this.myCipher.doFinal( cipherText );
            return new String( plainText );
        }
        catch ( Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }

    }
}
