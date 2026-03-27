package com.example.quoson.mkryptos;

import android.content.Context;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.Cipher;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * DESCipher — Symmetric encryption using the DES (Data Encryption Standard) algorithm.
 *
 * DES uses a fixed 56-bit effective key length (stored as 8 bytes with parity bits).
 * It is considered cryptographically weak by modern standards; included here for
 * legacy compatibility and educational purposes only.
 *
 * Supports two modes of operation:
 *   1. String mode — encrypt/decrypt text messages ({@link #doCipher} / {@link #doDecoding})
 *   2. File mode   — encrypt binary files in 16 KB streaming chunks ({@link #doCipherFile})
 *
 * Key handling:
 *   The constructor expects the key as a Base64-encoded string (as produced by
 *   {@link #keyGenerator()}). The string is decoded to raw bytes and wrapped in a
 *   {@link SecretKeySpec}.
 *
 * Output encoding:
 *   Ciphertext is Base64-encoded so it can be safely displayed and shared as text.
 */
public class DESCipher {

    private Cipher myCipher;
    private Key myKey;                             // The DES secret key used for cipher operations
    private final String algorithm = "DES";        // Algorithm identifier for javax.crypto
    private final int LENGTH_PASSWORD = 8;         // DES requires exactly 8 bytes (56 usable bits)
    // Lookup table for converting bytes to their two-character uppercase hex representation
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    /**
     * Constructs a DESCipher instance with the provided Base64-encoded key.
     *
     * @param password A Base64-encoded DES key string (e.g. produced by {@link #keyGenerator()}).
     */
    public DESCipher( String password ) {
        try {
            // Decode the Base64 key string back to the raw 8-byte DES key material
            this.myKey = new SecretKeySpec( Base64.decode(password.getBytes(), Base64.DEFAULT), this.algorithm );
        }
        catch( Exception e ){
            e.printStackTrace();
        }
    }

    /**
     * Encrypts a plaintext string using DES.
     *
     * @param message The plaintext to encrypt.
     * @return A Base64-encoded ciphertext string, or the exception message on failure.
     */
    public String doCipher( String message ) {
        try {
            byte [] plainText = message.getBytes();
            byte [] cipherText;
            this.myCipher = Cipher.getInstance( this.algorithm );
            this.myCipher.init( Cipher.ENCRYPT_MODE, this.myKey );
            cipherText = this.myCipher.doFinal( plainText );
            // Encode the raw cipher bytes to Base64 for safe text display/transport
            return Base64.encodeToString( cipherText, Base64.DEFAULT );
        }
        catch( Exception e ){
            return e.getMessage();
        }
    }

    /**
     * Decrypts a Base64-encoded DES ciphertext back to plaintext.
     *
     * @param message The Base64-encoded ciphertext to decrypt.
     * @return The original plaintext string, or the exception message on failure.
     */
    public String doDecoding( String message) {
        try {
            // Decode the Base64 string back to raw cipher bytes before decryption
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

    /**
     * Normalizes a password string to exactly 8 characters (DES key length).
     * - Truncates if longer than 8 characters.
     * - Pads with '0' if shorter than 8 characters.
     *
     * Note: Currently unused in the primary flow (which uses pre-generated Base64 keys).
     *
     * @param password The raw password string.
     * @return An 8-character key string.
     */
    public String testKey( String password ) {

        int lengthKey = password.length();

        if( lengthKey == LENGTH_PASSWORD ) {
            return password;
        }
        else if( lengthKey > LENGTH_PASSWORD ) {
            return password.substring( 0, LENGTH_PASSWORD );
        }
        else {
            // Append '0' characters until the key reaches 8 characters
            int distance = LENGTH_PASSWORD - lengthKey;
            for( int i=0; i<distance; i++ ) {
                password = password.concat( "0" );
            }
            return password;
        }
    }

    /**
     * Generates a random DES key and returns it as a Base64-encoded string.
     *
     * @return Base64-encoded DES key string, or exception message on failure.
     */
    public static String keyGenerator() {
        try {
            KeyGenerator myKeyGen = KeyGenerator.getInstance( "DES" );
            SecretKey key = myKeyGen.generateKey(); // Generates a random 56-bit DES key
            String myKEyText = Base64.encodeToString(key.getEncoded(), Base64.DEFAULT);
            return myKEyText;
        }
        catch( Exception e ){
            return e.getMessage();
        }
    }

    /**
     * Encrypts a binary file using DES in streaming mode (16 KB chunks).
     *
     * Reads the source file chunk by chunk, encrypts each block with
     * {@link Cipher#update}, and writes output to a new timestamped temporary
     * file in the device's public Pictures directory. The final cipher block
     * (with padding) is flushed via {@link Cipher#doFinal()}.
     *
     * @param path     Absolute path to the source file to encrypt.
     * @param pahtName Prefix string used when creating the output temp file.
     * @return The "file:" URI string of the encrypted output file,
     *         or the exception message on failure.
     */
    public String doCipherFile( String path, String pahtName ) {
        try {
            this.myCipher = Cipher.getInstance( this.algorithm );
            this.myCipher.init( Cipher.ENCRYPT_MODE, this.myKey );

            // Create a timestamped output file in the public Pictures directory
            String mCurrentPhotoPath;
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            File image = File.createTempFile(
                    pahtName,   /* filename prefix */
                    ".jpg",     /* filename suffix  */
                    storageDir  /* target directory */
            );

            // Build a "file:" URI for ACTION_VIEW intents
            mCurrentPhotoPath = "file:" + image.getAbsolutePath();

            InputStream myFile    = new FileInputStream( path );
            OutputStream out_file = new FileOutputStream( image );

            // Process the file in 16 KB blocks to avoid loading the entire file into memory
            byte [] buffer = new byte[16384];
            byte [] block_cipher;
            int end_file = -1;
            int read_byte;
            read_byte = myFile.read( buffer );

            while( read_byte != end_file ) {
                Log.i("image", bytesToHex( buffer ) + "\n");
                // Encrypt the current chunk; update() may buffer internally
                block_cipher = this.myCipher.update( buffer, 0, read_byte );
                out_file.write( block_cipher );
                read_byte = myFile.read( buffer );
            }

            myFile.close();
            // Flush any remaining bytes (padding block) and write them to the output file
            block_cipher = this.myCipher.doFinal();
            out_file.write( block_cipher );
            out_file.close();

            return mCurrentPhotoPath;
        }
        catch ( Exception e ) {
            return e.getMessage();
        }
    }

    /**
     * Converts a byte array to its uppercase hexadecimal string representation.
     * Used for debug logging raw file bytes during file encryption.
     *
     * @param bytes The byte array to convert.
     * @return A hex string of length {@code bytes.length * 2}.
     */
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;                   // Treat byte as unsigned
            hexChars[j * 2]     = hexArray[v >>> 4];   // High nibble
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];  // Low nibble
        }
        return new String(hexChars);
    }
}
