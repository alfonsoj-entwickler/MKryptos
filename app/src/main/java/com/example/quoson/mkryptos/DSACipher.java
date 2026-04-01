package com.example.quoson.mkryptos;


import android.util.Base64;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

public class DSACipher {

    public static String [] keyGenerator() {
        try {
            String [] arrayKeys = new String[2];
            KeyPairGenerator myKeyGen = KeyPairGenerator.getInstance( "DSA" );
            //myKeyGen.init(LENGTH_PASSWORD_BITS);
            KeyPair pair = myKeyGen.generateKeyPair();
            PublicKey pub = pair.getPublic();
            PrivateKey priv = pair.getPrivate();
            String myKEyTextPrivada = Base64.encodeToString( priv.getEncoded(), Base64.DEFAULT );
            String myKEyTextPublica = Base64.encodeToString( pub.getEncoded() , Base64.DEFAULT );
            arrayKeys[0] = myKEyTextPublica;
            arrayKeys[1] = myKEyTextPrivada;
            return arrayKeys;
        }
        catch( Exception e ){
            String [] arrayErrors = new String[2];
            arrayErrors[0] = e.getMessage();
            arrayErrors[1] = e.getLocalizedMessage();
            return arrayErrors;
        }

    }
}
