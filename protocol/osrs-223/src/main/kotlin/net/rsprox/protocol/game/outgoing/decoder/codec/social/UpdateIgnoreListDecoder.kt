package net.rsprox.protocol.game.outgoing.decoder.codec.social

import net.rsprot.buffer.JagByteBuf
import net.rsprot.protocol.ClientProt
import net.rsprot.protocol.message.codec.MessageDecoder
import net.rsprot.protocol.metadata.Consistent
import net.rsprot.protocol.tools.MessageDecodingTools
import net.rsprox.protocol.game.outgoing.decoder.prot.GameServerProt
import net.rsprox.protocol.game.outgoing.model.social.UpdateIgnoreList

@Consistent
public class UpdateIgnoreListDecoder : MessageDecoder<UpdateIgnoreList> {
    override val prot: ClientProt = GameServerProt.UPDATE_IGNORELIST

    override fun decode(
        buffer: JagByteBuf,
        tools: MessageDecodingTools,
    ): UpdateIgnoreList {
        return UpdateIgnoreList(
            buildList {
                while (buffer.isReadable) {
                    val type = buffer.g1()
                    val name = buffer.gjstr()
                    if (type == 4) {
                        add(
                            UpdateIgnoreList.RemovedIgnoredEntry(
                                name,
                            ),
                        )
                        continue
                    }
                    val added = type and 0x1 != 0
                    val previousName = buffer.gjstr()
                    val note = buffer.gjstr()
                    add(
                        UpdateIgnoreList.AddedIgnoredEntry(
                            name,
                            previousName,
                            note,
                            added,
                        ),
                    )
                }
            },
        )
    }
}
