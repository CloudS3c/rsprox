package net.rsprox.proxy.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.michaelbull.logging.InlineLogger
import net.rsprox.proxy.binary.BinaryBlob
import net.rsprox.proxy.binary.StreamDirection
import net.rsprox.proxy.config.BINARY_PATH
import net.rsprox.proxy.huffman.HuffmanProvider
import net.rsprox.proxy.plugin.DecodingSession
import net.rsprox.proxy.plugin.PluginLoader
import net.rsprox.transcriber.MessageConsumer
import net.rsprox.transcriber.MessageConsumerContainer
import java.io.BufferedWriter
import java.nio.file.Path
import java.util.Locale
import kotlin.io.path.bufferedWriter
import kotlin.io.path.exists
import kotlin.io.path.nameWithoutExtension
import kotlin.time.measureTime

@Suppress("DuplicatedCode")
public class TranscribeCommand : CliktCommand(name = "tostring") {
    private val name by option("-name")

    override fun run() {
        Locale.setDefault(Locale.US)
        val pluginLoader = PluginLoader()
        HuffmanProvider.load()
        pluginLoader.loadTranscriberPlugins("osrs")
        val fileName = this.name
        if (fileName != null) {
            val binaryName = if (fileName.endsWith(".bin")) fileName else "$fileName.bin"
            val file = BINARY_PATH.resolve(binaryName)
            if (!file.exists()) {
                echo("Unable to locate file $fileName in $BINARY_PATH")
                return
            }
            val time =
                measureTime {
                    stdoutTranscribe(file, pluginLoader)
                }
            logger.debug { "$file took $time to transcribe." }
        } else {
            val fileTreeWalk =
                BINARY_PATH
                    .toFile()
                    .walkTopDown()
                    .filter { it.extension == "bin" }
            for (bin in fileTreeWalk) {
                val time =
                    measureTime {
                        stdoutTranscribe(bin.toPath(), pluginLoader)
                    }
                logger.debug { "${bin.name} took $time to transcribe." }
            }
        }
    }

    private fun stdoutTranscribe(
        binaryPath: Path,
        pluginLoader: PluginLoader,
    ) {
        val binary = BinaryBlob.decode(binaryPath)
        val latestPlugin = pluginLoader.getPlugin(binary.header.revision)
        val transcriberProvider = pluginLoader.getTranscriberProvider(binary.header.revision)
        val session = DecodingSession(binary, latestPlugin)
        val writer = binaryPath.parent.resolve(binaryPath.nameWithoutExtension + ".txt").bufferedWriter()
        val consumers = MessageConsumerContainer(listOf(createBufferedWriterConsumer(writer)))
        val runner = transcriberProvider.provide(consumers)

        writer.appendLine("------------------")
        writer.appendLine("Header information")
        writer.appendLine("version: ${binary.header.revision}.${binary.header.subRevision}")
        writer.appendLine("client type: ${binary.header.clientType}")
        writer.appendLine("platform type: ${binary.header.platformType}")
        writer.appendLine(
            "world: ${binary.header.worldId}, host: ${binary.header.worldHost}, " +
                "flags: ${binary.header.worldFlags}, location: ${binary.header.worldLocation}, " +
                "activity: ${binary.header.worldActivity}",
        )
        writer.appendLine("local player index: ${binary.header.localPlayerIndex}")
        writer.appendLine("-------------------")

        for ((direction, prot, packet) in session.sequence()) {
            try {
                when (direction) {
                    StreamDirection.CLIENT_TO_SERVER -> {
                        runner.onClientProt(prot, packet)
                    }
                    StreamDirection.SERVER_TO_CLIENT -> {
                        runner.onServerPacket(prot, packet)
                    }
                }
            } catch (t: NotImplementedError) {
                continue
            }
        }
        consumers.close()
    }

    private fun createBufferedWriterConsumer(writer: BufferedWriter): MessageConsumer {
        return object : MessageConsumer {
            override fun consume(message: List<String>) {
                for (line in message) {
                    writer.write(line)
                    writer.newLine()
                }
            }

            override fun close() {
                writer.flush()
                writer.close()
            }
        }
    }

    private companion object {
        private val logger = InlineLogger()
    }
}

public fun main(args: Array<String>) {
    TranscribeCommand().main(args)
}