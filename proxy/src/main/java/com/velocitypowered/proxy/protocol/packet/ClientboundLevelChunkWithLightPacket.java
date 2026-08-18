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

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.proxy.connection.MinecraftSessionHandler;
import com.velocitypowered.proxy.protocol.MinecraftPacket;
import com.velocitypowered.proxy.protocol.ProtocolUtils;
import com.velocitypowered.proxy.protocol.util.DeferredByteBufHolder;
import io.netty.buffer.ByteBuf;

/** A chunk-with-light packet whose payload is retained verbatim for zero-copy forwarding. */
public final class ClientboundLevelChunkWithLightPacket extends DeferredByteBufHolder
    implements MinecraftPacket {

  private int chunkX;
  private int chunkZ;

  public ClientboundLevelChunkWithLightPacket() {
    super(null);
  }

  @Override
  public void decode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion version) {
    int index = buf.readerIndex();
    this.chunkX = buf.getInt(index);
    this.chunkZ = buf.getInt(index + Integer.BYTES);
    this.replace(buf.readRetainedSlice(buf.readableBytes()));
  }

  @Override
  public void encode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion version) {
    buf.writeBytes(content());
  }

  @Override
  public boolean handle(MinecraftSessionHandler handler) {
    return handler.handle(this);
  }

  public int getChunkX() {
    return chunkX;
  }

  public int getChunkZ() {
    return chunkZ;
  }

  @Override
  public int decodeExpectedMinLength(ByteBuf buf, ProtocolUtils.Direction direction,
      ProtocolVersion version) {
    return Integer.BYTES * 2;
  }

  @Override
  public int encodeSizeHint(ProtocolUtils.Direction direction, ProtocolVersion version) {
    return content().readableBytes();
  }
}
