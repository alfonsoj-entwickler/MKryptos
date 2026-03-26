package com.example.quoson.mkryptos;

import android.util.Log;

/**
 * PseudoRandomGenerator — Middle-square pseudo-random number generator.
 *
 * Implements the classic "middle-square" method originally devised by
 * John von Neumann (1946). Starting from a fixed seed, each iteration
 * squares the current value and extracts a middle window of digits as
 * the next number in the sequence.
 *
 * Algorithm (this implementation):
 *   1. Start with seed = 3187.
 *   2. Square the current step value: step = step * step.
 *   3. Convert to a string and extract characters at positions [1..3]
 *      (a 3-digit window offset by one from the left).
 *   4. Append the 3-digit slice to the output and use it as the next seed.
 *   5. Repeat for the requested count.
 *
 * IMPORTANT limitations:
 *   - The fixed seed means the sequence is deterministic and identical on every run.
 *   - This PRNG is NOT cryptographically secure and must NOT be used for key generation,
 *     IVs, nonces, or any security-sensitive purpose.
 *   - For cryptographic random numbers, use {@link java.security.SecureRandom}.
 *   - Integer overflow is likely at large step values; the string-extraction approach
 *     provides a pseudo-random window regardless.
 *
 * This class is used by NumbersActivity for educational / demonstration purposes only.
 */
public class PseudoRandomGenerator {

    /**
     * Generates a sequence of pseudo-random 3-digit numbers using the middle-square method.
     *
     * @param total The number of pseudo-random values to generate.
     * @return A comma-separated string of {@code total} 3-digit numbers
     *         (e.g. "473, 821, 036, ...").
     */
    public static String squareRandom(int total){
        String numbers = "";
        int semilla = 3187;       // Fixed initial seed — always produces the same sequence
        int step = semilla;
        String numberText = "";

        for( int i=0; i<total; i++ ) {
            step = step * step;                           // Square the current value
            numberText = Double.toString(step);           // Convert to string for digit extraction

            // Extract a 3-digit window starting at index 1 (skips the leading digit)
            numbers = numbers.concat(numberText.substring(1, 4) + ", ");
            Log.i( "numeros", numberText );

            // The extracted 3-digit slice becomes the seed for the next iteration
            step = Integer.parseInt( numberText.substring(1, 4));
        }

        return numbers;
    }
}
