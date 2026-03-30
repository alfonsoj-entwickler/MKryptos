package com.example.quoson.mkryptos;

import android.util.Base64;
import android.util.Log;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * RSACipher — Asymmetric RSA key pair generator.
 *
 * Generates RSA public/private key pairs of configurable sizes using the
 * standard Java {@link KeyPairGenerator} API. Both keys are returned as
 * Base64-encoded strings (DER format) suitable for display, storage, and
 * clipboard operations.
 *
 * Output format:
 *   arrayKeys[0] — Base64-encoded public key  (X.509 SubjectPublicKeyInfo)
 *   arrayKeys[1] — Base64-encoded private key (PKCS#8 PrivateKeyInfo)
 *
 * Supported key sizes: 512, 768, 1024, 2048, 3072, 4096 bits.
 * (512-bit keys are cryptographically weak; for demo/testing only.)
 *
 * Note: Large key sizes (3072, 4096) can be slow on low-end devices.
 * Key generation should always run on a background thread — see
 * {@link ProgressKeys} for the AsyncTask wrapper.
 */
public class RSACipher {

    // Algorithm identifier used by KeyPairGenerator
    private final String algorithm = "RSA";

    /**
     * Generates an RSA key pair of a user-specified size.
     *
     * @param size Key size in bits as a string (e.g. "1024", "2048", "4096").
     * @return A two-element String array: [0] public key, [1] private key (both Base64).
     *         On failure, both elements contain error messages.
     */
    public static String [] keyGenerator( String size ) {
        try {
            String [] arrayKeys = new String[2];
            // Initialize the RSA generator with the requested bit length
            KeyPairGenerator myKeyGen = KeyPairGenerator.getInstance( "RSA" );
            int size_key = Integer.parseInt( size );
            myKeyGen.initialize( size_key );
            KeyPair pair = myKeyGen.generateKeyPair();
            PublicKey pub   = pair.getPublic();
            PrivateKey priv = pair.getPrivate();
            // Encode both keys to Base64 strings (DER encoding via getEncoded())
            String myKEyTextPrivada = Base64.encodeToString( priv.getEncoded(), Base64.DEFAULT );
            String myKEyTextPublica = Base64.encodeToString( pub.getEncoded(),  Base64.DEFAULT );
            arrayKeys[0] = myKEyTextPublica;  // Public key at index 0
            arrayKeys[1] = myKEyTextPrivada;  // Private key at index 1
            return arrayKeys;
        }
        catch( Exception e ){
            // Return error details in both slots so the caller can surface them
            String [] arrayErrors = new String[2];
            arrayErrors[0] = e.getMessage();
            arrayErrors[1] = e.getLocalizedMessage();
            return arrayErrors;
        }
    }

    /**
     * Generates a 4096-bit RSA key pair (maximum supported size).
     * Use this overload when the caller does not need to configure the key size.
     *
     * @return A two-element String array: [0] public key, [1] private key (both Base64).
     *         On failure, both elements contain error messages.
     */
    public static String [] keyGenerator() {
        try {
            String [] arrayKeys = new String[2];
            KeyPairGenerator myKeyGen = KeyPairGenerator.getInstance( "RSA" );
            myKeyGen.initialize(4096); // Default to the strongest supported key size
            KeyPair pair = myKeyGen.generateKeyPair();
            PublicKey pub   = pair.getPublic();
            PrivateKey priv = pair.getPrivate();
            String myKEyTextPrivada = Base64.encodeToString( priv.getEncoded(), Base64.DEFAULT );
            String myKEyTextPublica = Base64.encodeToString( pub.getEncoded(),  Base64.DEFAULT );
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
