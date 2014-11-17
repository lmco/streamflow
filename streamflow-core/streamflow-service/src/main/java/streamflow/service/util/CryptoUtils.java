/**
 * Copyright 2014 Lockheed Martin Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package streamflow.service.util;

import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha512Hash;

public class CryptoUtils {

    public static final int DEFAULT_HASH_ITERATIONS = 2048;

    /**
     * Generates a salt for use with password salting
     *
     * @return randomly generated salt in Base64 format
     */
    public static String generateSalt() {
        // Generate a random salt for a password (password salt is Base64)
        RandomNumberGenerator rng = new SecureRandomNumberGenerator();
        return rng.nextBytes().toBase64();
    }

    /**
     * Hashes the provided password using Sha512Hash algorithms using the given
     * salt and default number of iterations.
     *
     * @param password actual password to be hashed
     * @param passwordSalt salt used when hashing the password
     * @return hashed password in Base64 format
     */
    public static String hashPassword(String password, String passwordSalt) {
        return hashPassword(password, passwordSalt, DEFAULT_HASH_ITERATIONS);
    }

    /**
     * Hashes the provided password using Sha512Hash algorithms using the given
     * salt and specified number of iterations.
     *
     * @param password actual password to be hashed
     * @param passwordSalt salt used when hashing the password
     * @param iterations number of iterations to use when hashing the password
     * @return hashed password in Base64 format
     */
    public static String hashPassword(String password, String passwordSalt, int iterations) {
        return new Sha512Hash(password, passwordSalt, iterations).toBase64();
    }
}
