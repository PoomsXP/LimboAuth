/*
 * Copyright (C) 2021 - 2025 Elytrium
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.elytrium.limboauth.handler;

import at.favre.lib.crypto.bcrypt.BCrypt;
import java.nio.charset.StandardCharsets;

final class PasswordVerifier {

  private static final int MAX_BCRYPT_PASSWORD_BYTES = 71;
  private static final BCrypt.Verifyer HASH_VERIFIER = BCrypt.verifyer();

  private PasswordVerifier() {
  }

  static boolean isBcryptCompatible(String password) {
    return password.getBytes(StandardCharsets.UTF_8).length <= MAX_BCRYPT_PASSWORD_BYTES;
  }

  static boolean verify(String password, String hash) {
    if (!isBcryptCompatible(password)) {
      return false;
    }

    try {
      return HASH_VERIFIER.verify(
          password.getBytes(StandardCharsets.UTF_8),
          hash.replace("BCRYPT$", "$2a$").getBytes(StandardCharsets.UTF_8)
      ).verified;
    } catch (IllegalArgumentException ignored) {
      // Invalid hashes are authentication failures, not connection errors.
      return false;
    }
  }
}
