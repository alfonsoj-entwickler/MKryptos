package com.example.quoson.mkryptos;


import android.util.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class HMACCipher {

    public static String keyGenerator( String version ) {
        try {
            KeyGenerator myKeyGen;
            switch ( version ) {
                case "HMACMD5":
                    myKeyGen = KeyGenerator.getInstance( "HMACMD5" );
                    break;

                case "HMACSHA1":
                    myKeyGen = KeyGenerator.getInstance( "HMACSHA1" );
                    break;

                case "HMACSHA256":
                    myKeyGen = KeyGenerator.getInstance( "HMACSHA256" );
                    break;

                case "HMACSHA384":
                    myKeyGen = KeyGenerator.getInstance( "HMACSHA384" );
                    break;

                case "HMACSHA512":
                    myKeyGen = KeyGenerator.getInstance( "HMACSHA512" );
                    break;

                default:
                    myKeyGen = KeyGenerator.getInstance( "HMACMD5" );
                    break;
            }

            SecretKey key = myKeyGen.generateKey();
            String myKEyText = Base64.encodeToString(key.getEncoded(), Base64.DEFAULT);
            return myKEyText;
        }
        catch( Exception e ){
            return e.getMessage();
        }

    }
}
