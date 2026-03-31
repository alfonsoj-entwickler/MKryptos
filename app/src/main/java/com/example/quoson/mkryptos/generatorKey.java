package com.example.quoson.mkryptos;


import android.util.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * generatorKey — Simple AES key generation utility (standalone helper class).
 *
 * A lightweight two-step helper: call {@link #run()} to generate the key,
 * then call {@link #getKey()} to retrieve it as a Base64-encoded string.
 *
 * Usage:
 *   generatorKey gen = new generatorKey();
 *   if (gen.run()) {
 *       String base64Key = gen.getKey();
 *   }
 *
 * Note:
 *   The key size is hardcoded to 512 bits, which is non-standard for AES
 *   (standard sizes are 128, 192, and 256 bits). On devices without an
 *   unrestricted JCE policy file, this will throw an InvalidParameterException.
 *   Prefer {@link AESCipher#keyGenerator(String)} for portable, configurable
 *   key generation.
 */
public class generatorKey {

    // Raw bytes of the generated AES key, stored after a successful run()
    private byte [] myKey;

    /**
     * Generates a new AES key and stores the raw bytes internally.
     *
     * @return {@code true} if key generation succeeded;
     *         {@code false} if an exception occurred (e.g. unsupported 512-bit size).
     */
    public boolean run() {
        try {
            KeyGenerator keyG = KeyGenerator.getInstance( "AES" );
            keyG.init( 512 ); // Non-standard size; requires unrestricted JCE policy on some devices
            SecretKey mySecret = keyG.generateKey();
            this.myKey = mySecret.getEncoded(); // Store raw key bytes for later retrieval
            return true;
        }
        catch ( Exception e ) {
            return false;
        }
    }

    /**
     * Returns the generated key as a Base64-encoded string.
     * Must only be called after a successful {@link #run()} invocation.
     *
     * @return Base64-encoded AES key string.
     */
    public String getKey() {
        return Base64.encodeToString( this.myKey, Base64.DEFAULT );
    }
}
