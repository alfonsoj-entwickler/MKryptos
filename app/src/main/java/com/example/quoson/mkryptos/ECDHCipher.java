package com.example.quoson.mkryptos;

import android.util.Base64;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.ECGenParameterSpec;

/**
 * ECDHCipher — Elliptic Curve key pair generator (ECDH / EC).
 *
 * Generates EC public/private key pairs using standard NIST named curves.
 * The generated keys are suitable for Elliptic Curve Diffie-Hellman (ECDH)
 * key agreement and ECDSA digital signatures.
 *
 * Supported curves (passed as the {@code size} parameter):
 *   "secp224r1" — NIST P-224 (224-bit key)
 *   "secp256r1" — NIST P-256 / prime256v1 (256-bit key)
 *   "secp384r1" — NIST P-384 (384-bit key)
 *   "secp521r1" — NIST P-521 (521-bit key) ← default for unknown values
 *
 * Output format:
 *   arrayKeys[0] — Base64-encoded public key  (X.509 SubjectPublicKeyInfo)
 *   arrayKeys[1] — Base64-encoded private key (PKCS#8 PrivateKeyInfo)
 */
public class ECDHCipher {

    /**
     * Generates an EC key pair for the specified named curve.
     *
     * @param size The NIST curve name: "secp224r1", "secp256r1", "secp384r1", or "secp521r1".
     *             Unrecognized values default to secp521r1 (strongest).
     * @return A two-element String array: [0] public key, [1] private key (both Base64).
     *         On failure, both elements contain error messages.
     */
    public static String [] keyGenerator( String size ) {
        try {
            String [] arrayKeys = new String[2];
            // Obtain an EC key pair generator from the default security provider
            KeyPairGenerator myKeyGen = KeyPairGenerator.getInstance( "EC" );

            // Select the named curve parameters based on the requested key size
            ECGenParameterSpec ecParamSpec = null;
            switch ( size ) {
                case "secp224r1":
                    ecParamSpec = new ECGenParameterSpec( "secp224r1" ); // NIST P-224 (224 bits)
                    break;
                case "secp256r1":
                    ecParamSpec = new ECGenParameterSpec( "secp256r1" ); // NIST P-256 (256 bits)
                    break;
                case "secp384r1":
                    ecParamSpec = new ECGenParameterSpec( "secp384r1" ); // NIST P-384 (384 bits)
                    break;
                case "secp521r1":
                    ecParamSpec = new ECGenParameterSpec( "secp521r1" ); // NIST P-521 (521 bits)
                    break;
                default:
                    // Fall back to the strongest supported curve for unrecognized input
                    ecParamSpec = new ECGenParameterSpec( "secp521r1" );
                    break;
            }

            // Initialize the generator with the chosen curve and generate the key pair
            myKeyGen.initialize( ecParamSpec );
            KeyPair pair = myKeyGen.generateKeyPair();
            PublicKey  pub  = pair.getPublic();
            PrivateKey priv = pair.getPrivate();

            // Encode keys to Base64 (DER-encoded X.509 / PKCS#8 respectively)
            String myKEyTextPrivada = Base64.encodeToString(priv.getEncoded(), Base64.DEFAULT);
            String myKEyTextPublica = Base64.encodeToString(pub.getEncoded(),  Base64.DEFAULT);
            arrayKeys[0] = myKEyTextPublica;  // Public key at index 0
            arrayKeys[1] = myKEyTextPrivada;  // Private key at index 1
            return arrayKeys;
        }
        catch( Exception e ){
            // Return error details in both slots for caller visibility
            String [] arrayErrors = new String[2];
            arrayErrors[0] = e.getMessage();
            arrayErrors[1] = e.getLocalizedMessage();
            return arrayErrors;
        }
    }
}
