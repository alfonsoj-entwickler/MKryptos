package com.example.quoson.mkryptos;

import android.util.Base64;
import android.util.Log;

import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.ECField;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AESCipher — Symmetric encryption using the AES algorithm.
 *
 * This class wraps the javax.crypto AES implementation and exposes
 * simple encrypt / decrypt methods that accept and return Base64-encoded
 * strings, making the output safe for display and clipboard operations.
 *
 * Key handling:
 *   The constructor expects the key as a Base64-encoded string
 *   (as produced by {@link #keyGenerator()}). The string is decoded
 *   back to raw bytes and wrapped in a {@link SecretKeySpec}.
 *
 * Supported key sizes (via {@link #keyGenerator(String)}):
 *   128, 192, or 256 bits. Note: 512-bit AES is not part of the
 *   standard; devices without an unlimited-strength JCE policy will
 *   reject sizes above 128 bits unless the policy is installed.
 */
public class AESCipher {

    private PBEKeySpec mypbeKeySpec;
    private PBEParameterSpec myPbeParamSpec;
    private SecretKeyFactory myKeyFac;
    private SecretKey myPbeKey;
    private Key myKey;                          // The AES secret key used for cipher operations
    private Cipher myCipher;
    private KeyGenerator myKeyGen;
    private final int count = 1024;             // Iteration count (reserved for PBE use)
    private final String algorithm = "AES";     // Algorithm identifier passed to javax.crypto
    private final int LENGTH_PASSWORD = 32;     // Expected raw-byte length for a 256-bit key (unused in current flow)
    private final int LENGTH_PASSWORD_BITS = 256; // Target key length in bits

    /**
     * Constructs an AESCipher instance ready for encrypt/decrypt operations.
     *
     * @param password A Base64-encoded AES key string (e.g. produced by keyGenerator()).
     *                 The string is decoded to bytes and used directly as the key material.
     */
    public AESCipher( String password ) {
        try {
            // Decode the Base64 key string back to raw bytes and build a SecretKeySpec
            this.myKey = new SecretKeySpec( Base64.decode(password.getBytes(), Base64.DEFAULT), this.algorithm );
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * Encrypts a plaintext message using AES in ECB mode (default provider mode).
     *
     * @param message The plaintext string to encrypt.
     * @return A Base64-encoded ciphertext string, or the exception message on failure.
     */
    public String doCipher( String message ) {

        byte [] plaintext = message.getBytes();
        byte cipherText [];
        String cipherMessage = "";

        try {
            this.myCipher = Cipher.getInstance(this.algorithm);
            // Initialize the cipher in encryption mode with the loaded AES key
            this.myCipher.init(Cipher.ENCRYPT_MODE, this.myKey);
            cipherText = this.myCipher.doFinal(plaintext);
            // Encode the raw ciphertext bytes to a Base64 string for safe transport/display
            cipherMessage = Base64.encodeToString(cipherText, Base64.DEFAULT);
            return cipherMessage;
        }
        catch ( Exception e) {
            return e.getMessage();
        }
    }

    /**
     * Decrypts a Base64-encoded ciphertext string back to plaintext.
     *
     * @param message The Base64-encoded ciphertext to decrypt.
     * @return The original plaintext string, or the exception message on failure.
     */
    public String doDecoding( String message ) {
        try {
            // Decode the Base64 string back to raw cipher bytes
            byte [] cipherText = Base64.decode(message.getBytes(), Base64.DEFAULT);
            byte [] plainText;
            this.myCipher = Cipher.getInstance( this.algorithm );
            // Initialize the cipher in decryption mode with the same AES key
            this.myCipher.init( Cipher.DECRYPT_MODE, this.myKey );
            plainText = this.myCipher.doFinal(cipherText);
            return new String( plainText );
        }
        catch( Exception e ){
            return e.getMessage();
        }
    }

    /**
     * Utility method to normalize a password string to exactly 32 characters (256 bits).
     * - If shorter than 32 chars: pads with '0' characters on the right.
     * - If longer than 32 chars: truncates to 32 characters.
     * - If exactly 32 chars: returns unchanged.
     *
     * Note: This method is currently unused in the main encrypt/decrypt flow,
     * which instead relies on pre-generated Base64 keys.
     *
     * @param password The raw password string.
     * @return A 32-character normalized key string.
     */
    public String testKey( String password ) {

        int lengthKey = password.length();

        if( lengthKey == LENGTH_PASSWORD ) {
            return password;
        }
        else if( lengthKey >  LENGTH_PASSWORD ) {
            // Truncate to the required 32-character length
            return password.substring( 0, LENGTH_PASSWORD );
        }
        else {
            // Pad with '0' characters until the key is 32 characters long
            int distance = LENGTH_PASSWORD - lengthKey;
            for( int i=0; i<distance; i++ ) {
                password = password.concat( "0" );
            }
            return password;
        }
    }

    /**
     * Generates a random 256-bit AES key and returns it as a Base64 string.
     * This overload always uses 256-bit key size.
     *
     * @return Base64-encoded AES-256 key string, or exception message on failure.
     */
    public static String keyGenerator() {
        try {
            KeyGenerator myKeyGen = KeyGenerator.getInstance( "AES" );
            myKeyGen.init( 256 ); // Always generate a 256-bit key
            SecretKey key = myKeyGen.generateKey();
            // Encode the raw key bytes to Base64 for display and storage
            String myKEyText = Base64.encodeToString(key.getEncoded(), Base64.DEFAULT);
            return myKEyText;
        }
        catch( Exception e ){
            return e.getMessage();
        }
    }

    /**
     * Generates a random AES key of a user-specified size.
     *
     * @param size Key size as a string (e.g. "128", "192", "256").
     *             Note: "512" is non-standard and requires an unrestricted JCE policy.
     * @return Base64-encoded AES key string of the requested size, or exception message on failure.
     */
    public static String keyGenerator( String size ) {
        try {
            KeyGenerator myKeyGen = KeyGenerator.getInstance( "AES" );
            int size_key = Integer.parseInt( size );
            myKeyGen.init( size_key );
            SecretKey key = myKeyGen.generateKey();
            String myKEyText = Base64.encodeToString(key.getEncoded(), Base64.DEFAULT);
            return myKEyText;
        }
        catch( Exception e ){
            return e.getMessage();
        }
    }
}
