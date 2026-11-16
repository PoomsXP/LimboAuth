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

import com.velocitypowered.api.util.GameProfile;
import com.velocitypowered.api.util.UuidUtils;
import java.util.UUID;

final class ProfileSettings {

  private ProfileSettings() {
  }

  static GameProfile apply(GameProfile profile, String username, boolean onlineMode,
      boolean floodgate, boolean forceOfflineUuid, String offlineModePrefix, String onlineModePrefix) {
    if (forceOfflineUuid && !floodgate) {
      profile = profile.withId(UuidUtils.generateOfflinePlayerUuid(username));
    }

    if (!onlineMode && !floodgate && !offlineModePrefix.isEmpty()) {
      profile = profile.withName(offlineModePrefix + profile.getName());
    }

    if (onlineMode && !floodgate && !onlineModePrefix.isEmpty()) {
      profile = profile.withName(onlineModePrefix + profile.getName());
    }

    return profile;
  }

  static UUID parseUuid(String value) {
    if (value == null) {
      return null;
    }

    try {
      UUID uuid = UUID.fromString(value);
      return uuid.toString().equalsIgnoreCase(value) ? uuid : null;
    } catch (IllegalArgumentException ignored) {
      return null;
    }
  }
}
