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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.junit.jupiter.api.Test;

class PasswordVerifierTest {

  @Test
  void verifiesValidPassword() {
    String hash = BCrypt.withDefaults().hashToString(4, "correct-password".toCharArray());

    assertTrue(PasswordVerifier.verify("correct-password", hash));
    assertFalse(PasswordVerifier.verify("wrong-password", hash));
  }

  @Test
  void rejectsOverlongPasswordWithoutThrowing() {
    String hash = BCrypt.withDefaults().hashToString(4, "correct-password".toCharArray());

    assertFalse(PasswordVerifier.verify("x".repeat(72), hash));
    assertFalse(PasswordVerifier.verify("é".repeat(36), hash));
  }

  @Test
  void rejectsMalformedHashWithoutThrowing() {
    assertFalse(PasswordVerifier.verify("password", "not-a-bcrypt-hash"));
  }
}
