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

package net.elytrium.limboauth.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.velocitypowered.api.util.GameProfile;
import com.velocitypowered.api.util.GameProfile.Property;
import com.velocitypowered.api.util.UuidUtils;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AuthListenerTest {

  private static final String USERNAME = "Player";
  private static final Property TEXTURES = new Property("textures", "value", "signature");

  @Test
  void preservesFloodgateProfileForOfflineConnections() {
    UUID floodgateUuid = new UUID(0, 1234);
    GameProfile floodgateProfile = new GameProfile(floodgateUuid, ".BedrockPlayer", List.of(TEXTURES));

    GameProfile result = ProfileSettings.apply(
        floodgateProfile, USERNAME, false, true, true, "cracked_", "premium_"
    );

    assertEquals(floodgateUuid, result.getId());
    assertEquals(".BedrockPlayer", result.getName());
    assertSame(TEXTURES, result.getProperties().get(0));
  }

  @Test
  void preservesFloodgateProfileForOnlineConnections() {
    UUID linkedUuid = UUID.randomUUID();
    GameProfile floodgateProfile = new GameProfile(linkedUuid, "LinkedPlayer", List.of(TEXTURES));

    GameProfile result = ProfileSettings.apply(
        floodgateProfile, USERNAME, true, true, true, "cracked_", "premium_"
    );

    assertEquals(linkedUuid, result.getId());
    assertEquals("LinkedPlayer", result.getName());
    assertSame(TEXTURES, result.getProperties().get(0));
  }

  @Test
  void appliesOfflineSettingsWithoutDiscardingProfileProperties() {
    UUID originalUuid = UUID.randomUUID();
    GameProfile profile = new GameProfile(originalUuid, USERNAME, List.of(TEXTURES));

    GameProfile result = ProfileSettings.apply(
        profile, USERNAME, false, false, true, "cracked_", "premium_"
    );

    assertEquals(UuidUtils.generateOfflinePlayerUuid(USERNAME), result.getId());
    assertEquals("cracked_" + USERNAME, result.getName());
    assertSame(TEXTURES, result.getProperties().get(0));
  }

  @Test
  void appliesOnlinePrefixToCurrentProfile() {
    UUID uuid = UUID.randomUUID();
    GameProfile profile = new GameProfile(uuid, "rewrittenPlayer", List.of(TEXTURES));

    GameProfile result = ProfileSettings.apply(
        profile, USERNAME, true, false, false, "cracked_", "premium_"
    );

    assertEquals(uuid, result.getId());
    assertEquals("premium_rewrittenPlayer", result.getName());
    assertSame(TEXTURES, result.getProperties().get(0));
  }

  @Test
  void parsesCanonicalSavedUuid() {
    UUID uuid = UUID.randomUUID();

    assertEquals(uuid, ProfileSettings.parseUuid(uuid.toString().toUpperCase(Locale.ROOT)));
  }

  @Test
  void rejectsMalformedSavedUuid() {
    assertNull(ProfileSettings.parseUuid(null));
    assertNull(ProfileSettings.parseUuid("not-a-uuid"));
    assertNull(ProfileSettings.parseUuid("1-1-1-1-1"));
    assertNull(ProfileSettings.parseUuid("00000000-0000-0000-0000-000000000000-extra"));
  }
}
