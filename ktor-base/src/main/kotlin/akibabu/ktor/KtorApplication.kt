package akibabu.ktor

import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.engine.addShutdownHook
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.stop
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.plugins.compression.Compression
import io.ktor.server.plugins.compression.deflate
import io.ktor.server.plugins.compression.gzip
import io.ktor.server.plugins.compression.minimumSize
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respondText
import kotlinx.serialization.json.Json
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.concurrent.TimeUnit

class KtorApplication(
    private val port: Int,
    private val preServerShutdownHook: (() -> Unit)? = null
) {

    val server: NettyApplicationEngine = embeddedServer(Netty, port) {
        install(StatusPages) {
            exception<Throwable> { call, cause ->
                call.respondText(text = cause.stackTraceToString(), status = HttpStatusCode.InternalServerError)
            }
        }
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                ignoreUnknownKeys = true
            })
        }
        install(Compression) {
            gzip {
                minimumSize(1024)
            }
            deflate {
                minimumSize(1024)
            }
        }
    }.apply {
        addShutdownHook {
            preServerShutdownHook?.invoke()
            logger.info("Shutting down Ktor server since JVM is shutting down")
            stop(0, 3, TimeUnit.SECONDS)
        }
    }

    fun start(wait: Boolean = true) {
        server.start()
        logger.info("Ktor server starting on port $port")
        server.start(wait)
    }

    companion object {
        private val logger: Logger = LogManager.getLogger()
    }
}