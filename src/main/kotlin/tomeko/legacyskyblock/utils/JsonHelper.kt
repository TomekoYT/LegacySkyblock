package tomeko.legacyskyblock.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.time.Duration

object JsonHelper {
    val GSON: Gson = GsonBuilder().create()
    private val HTTP: HttpClient by lazy { HttpClient.newHttpClient() }

    private val DEFAULT_TIMEOUT = Duration.ofSeconds(30)
    private const val USER_AGENT = Constants.MOD_ARCHIVES_NAME

    fun getString(url: String): String? {
        return getStringResponse(url)?.body()
    }

    fun downloadAndCacheJson(url: String, target: Path): String? {
        val text = getString(url) ?: return null
        writeString(target, text)
        return text
    }

    private fun getStringResponse(url: String): HttpResponse<String>? {
        return try {
            val request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", USER_AGENT)
                .timeout(DEFAULT_TIMEOUT)
                .GET()
                .build()

            val response = HTTP.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8))
            if (response.statusCode() !in 200..299) null else response
        } catch (_: Exception) {
            null
        }
    }

    fun writeString(target: Path, data: String) {
        ensureParentFolderExists(target)

        val tempPath = target.resolveSibling("${target.fileName}.tmp")
        Files.writeString(tempPath, data, StandardCharsets.UTF_8)

        moveAtomically(tempPath, target)
    }

    private fun moveAtomically(source: Path, target: Path) {
        try {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)
        } catch (_: AtomicMoveNotSupportedException) {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING)
        }
    }

    private fun ensureParentFolderExists(filePath: Path) {
        val parent = filePath.parent
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent)
        }
    }

    fun tryParseJson(text: String): JsonElement? {
        return try {
            JsonParser.parseString(text)
        } catch (_: JsonSyntaxException) {
            null
        }
    }

    fun getOrDownloadJson(url: String, target: Path): JsonElement? {
        if (Files.exists(target)) {
            try {
                Files.newBufferedReader(target).use { reader ->
                    return JsonParser.parseReader(reader)
                }
            } catch (_: Exception) {
            }
        }

        val text = downloadAndCacheJson(url, target) ?: return null
        return tryParseJson(text)
    }
}
