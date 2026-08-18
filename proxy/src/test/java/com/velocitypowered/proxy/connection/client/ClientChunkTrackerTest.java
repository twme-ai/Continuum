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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ClientChunkTrackerTest {

  @Test
  void filtersOnlyPreviouslyLoadedChunksDuringTransferBatch() {
    ClientChunkTracker tracker = new ClientChunkTracker();

    assertFalse(tracker.recordChunk(4, -7));
    assertFalse(tracker.recordChunk(-4, 7));
    assertEquals(2, tracker.loadedCount());

    tracker.beginTransferBatch();
    assertTrue(tracker.recordChunk(4, -7));
    assertFalse(tracker.recordChunk(8, 9));
    assertEquals(3, tracker.loadedCount());

    assertTrue(tracker.finishTransferBatch());
    assertFalse(tracker.recordChunk(4, -7));
    assertTrue(tracker.recordChunk(-4, 7));
    assertFalse(tracker.recordChunk(-4, 7));
    assertFalse(tracker.finishTransferBatch());
  }

  @Test
  void forgottenChunkCanBeSentAgain() {
    ClientChunkTracker tracker = new ClientChunkTracker();
    tracker.recordChunk(Integer.MIN_VALUE, Integer.MAX_VALUE);
    tracker.forgetChunk(Integer.MIN_VALUE, Integer.MAX_VALUE);
    tracker.beginTransferBatch();

    assertFalse(tracker.recordChunk(Integer.MIN_VALUE, Integer.MAX_VALUE));
  }

  @Test
  void resetClearsChunksAndTransferState() {
    ClientChunkTracker tracker = new ClientChunkTracker();
    tracker.recordChunk(1, 2);
    tracker.beginTransferBatch();
    tracker.reset();

    assertEquals(0, tracker.loadedCount());
    assertFalse(tracker.recordChunk(1, 2));
    assertFalse(tracker.finishTransferBatch());
  }
}
