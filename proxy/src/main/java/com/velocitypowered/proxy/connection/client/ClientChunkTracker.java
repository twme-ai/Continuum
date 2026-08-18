/*
 * Copyright (C) 2018-2026 Velocity Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.velocitypowered.proxy.connection.client;

import java.util.HashSet;
import java.util.Set;

/** Tracks the chunks currently present in one Minecraft client. */
final class ClientChunkTracker {

  private final Set<Long> loaded = new HashSet<>();
  private final Set<Long> retainedAtSwitch = new HashSet<>();
  private boolean waitingForFirstTransferBatch;

  boolean recordChunk(int x, int z) {
    long key = key(x, z);
    loaded.add(key);
    return retainedAtSwitch.remove(key);
  }

  void forgetChunk(int x, int z) {
    long key = key(x, z);
    loaded.remove(key);
    retainedAtSwitch.remove(key);
  }

  void beginTransferBatch() {
    retainedAtSwitch.clear();
    retainedAtSwitch.addAll(loaded);
    waitingForFirstTransferBatch = true;
  }

  boolean finishTransferBatch() {
    boolean firstBatch = waitingForFirstTransferBatch;
    waitingForFirstTransferBatch = false;
    return firstBatch;
  }

  void reset() {
    loaded.clear();
    retainedAtSwitch.clear();
    waitingForFirstTransferBatch = false;
  }

  int loadedCount() {
    return loaded.size();
  }

  private static long key(int x, int z) {
    return ((long) x << 32) ^ (z & 0xffffffffL);
  }
}
