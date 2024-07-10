package net.rsprox.protocol.game.outgoing.decoder.codec.camera

import net.rsprot.buffer.JagByteBuf
import net.rsprot.protocol.ClientProt
import net.rsprot.protocol.message.codec.MessageDecoder
import net.rsprot.protocol.metadata.Consistent
import net.rsprot.protocol.tools.MessageDecodingTools
import net.rsprox.protocol.game.outgoing.decoder.prot.GameServerProt
import net.rsprox.protocol.game.outgoing.model.camera.CamMoveTo

@Consistent
public class CamMoveToDecoder : MessageDecoder<CamMoveTo> {
    override val prot: ClientProt = GameServerProt.CAM_MOVETO

    override fun decode(
        buffer: JagByteBuf,
        tools: MessageDecodingTools,
    ): CamMoveTo {
        val destinationXInBuildArea = buffer.g1()
        val destinationZInBuildArea = buffer.g1()
        val height = buffer.g2()
        val speed = buffer.g1()
        val acceleration = buffer.g1()
        return CamMoveTo(
            destinationXInBuildArea,
            destinationZInBuildArea,
            height,
            speed,
            acceleration,
        )
    }
}
