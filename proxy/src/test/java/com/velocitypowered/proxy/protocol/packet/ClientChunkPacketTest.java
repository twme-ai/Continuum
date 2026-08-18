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

package com.velocitypowered.proxy.protocol.packet;

import static com.velocitypowered.proxy.protocol.ProtocolUtils.Direction.CLIENTBOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.proxy.protocol.StateRegistry;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Test;

class ClientChunkPacketTest {

  @Test
  void minecraft262MappingsMatchMojangPacketReport() {
    var registry = StateRegistry.PLAY.getProtocolRegistry(CLIENTBOUND,
        ProtocolVersion.MINECRAFT_26_2);
    assertEquals(ClientboundChunkBatchFinishedPacket.class,
        registry.createPacket(0x0B).getClass());
    assertEquals(ClientboundForgetLevelChunkPacket.class,
        registry.createPacket(0x25).getClass());
    assertEquals(ClientboundLevelChunkWithLightPacket.class,
        registry.createPacket(0x2D).getClass());
    assertEquals(ClientboundSetPassengersPacket.class,
        registry.createPacket(0x6B).getClass());
    assertEquals(UpdateRecipesPacket.class, registry.createPacket(0x85).getClass());
  }

  @Test
  void chunkPacketRetainsCoordinatesAndPayload() {
    ByteBuf input = Unpooled.buffer().writeInt(-123).writeInt(456).writeLong(0x1020304050607080L);
    byte[] expected = ByteBufUtil.getBytes(input);
    ClientboundLevelChunkWithLightPacket packet = new ClientboundLevelChunkWithLightPacket();

    try {
      packet.decode(input, CLIENTBOUND, ProtocolVersion.MINECRAFT_26_2);
      assertEquals(-123, packet.getChunkX());
      assertEquals(456, packet.getChunkZ());

      ByteBuf encoded = Unpooled.buffer();
      try {
        packet.encode(encoded, CLIENTBOUND, ProtocolVersion.MINECRAFT_26_2);
        assertEquals(ByteBufUtil.hexDump(expected), ByteBufUtil.hexDump(encoded));
      } finally {
        encoded.release();
      }
    } finally {
      packet.release();
      input.release();
    }
  }

  @Test
  void forgetChunkUsesProtocolZThenXOrder() {
    ByteBuf input = Unpooled.buffer().writeInt(99).writeInt(-55);
    ClientboundForgetLevelChunkPacket packet = new ClientboundForgetLevelChunkPacket();

    try {
      packet.decode(input, CLIENTBOUND, ProtocolVersion.MINECRAFT_26_2);
      assertEquals(-55, packet.getChunkX());
      assertEquals(99, packet.getChunkZ());

      ByteBuf encoded = Unpooled.buffer();
      try {
        packet.encode(encoded, CLIENTBOUND, ProtocolVersion.MINECRAFT_26_2);
        assertEquals(99, encoded.readInt());
        assertEquals(-55, encoded.readInt());
      } finally {
        encoded.release();
      }
    } finally {
      input.release();
    }
  }
}
