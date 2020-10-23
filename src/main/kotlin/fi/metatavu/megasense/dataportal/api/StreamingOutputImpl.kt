package fi.metatavu.megasense.dataportal.api

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import javax.ws.rs.core.StreamingOutput


/**
 * Implementation of JAX-RS StreamingOutput
 */
class StreamingOutputImpl(private val inputStream: InputStream) : StreamingOutput {
    @Throws(IOException::class)
    override fun write(output: OutputStream) {
        val buffer = ByteArray(1024 * 100)
        var bytesRead: Int
        while (inputStream.read(buffer, 0, buffer.size).also { bytesRead = it } != -1) {
            output.write(buffer, 0, bytesRead)
            output.flush()
        }
        output.flush()
    }
}