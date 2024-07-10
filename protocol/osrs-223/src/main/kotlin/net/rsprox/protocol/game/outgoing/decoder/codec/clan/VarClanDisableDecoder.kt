package net.rsprox.protocol.game.outgoing.decoder.codec.clan

import net.rsprot.buffer.JagByteBuf
import net.rsprot.protocol.ClientProt
import net.rsprot.protocol.message.codec.MessageDecoder
import net.rsprot.protocol.metadata.Consistent
import net.rsprot.protocol.tools.MessageDecodingTools
import net.rsprox.protocol.game.outgoing.decoder.prot.GameServerProt
import net.rsprox.protocol.game.outgoing.model.clan.VarClanDisable

@Consistent
public class VarClanDisableDecoder : MessageDecoder<VarClanDisable> {
    override val prot: ClientProt = GameServerProt.VARCLAN_DISABLE

    override fun decode(
        buffer: JagByteBuf,
        tools: MessageDecodingTools,
    ): VarClanDisable {
        return VarClanDisable
    }
}
