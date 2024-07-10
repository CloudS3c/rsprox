package net.rsprox.protocol.game.outgoing.decoder.codec.interfaces

import net.rsprot.buffer.JagByteBuf
import net.rsprot.protocol.ClientProt
import net.rsprot.protocol.message.codec.MessageDecoder
import net.rsprot.protocol.tools.MessageDecodingTools
import net.rsprot.protocol.util.gCombinedIdAlt3
import net.rsprox.protocol.game.outgoing.decoder.prot.GameServerProt
import net.rsprox.protocol.game.outgoing.model.interfaces.IfSetModel

public class IfSetModelDecoder : MessageDecoder<IfSetModel> {
    override val prot: ClientProt = GameServerProt.IF_SETMODEL

    override fun decode(
        buffer: JagByteBuf,
        tools: MessageDecodingTools,
    ): IfSetModel {
        val combinedId = buffer.gCombinedIdAlt3()
        val model = buffer.g2Alt2()
        return IfSetModel(
            combinedId.interfaceId,
            combinedId.componentId,
            model,
        )
    }
}
