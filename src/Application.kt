package com.vcf_web

import com.fasterxml.jackson.databind.SerializationFeature
import freemarker.cache.ClassTemplateLoader
import htsjdk.tribble.readers.LineIteratorImpl
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.freemarker.FreeMarker
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.request.receiveParameters
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import htsjdk.tribble.readers.TabixReader
import htsjdk.tribble.readers.TabixIteratorLineReader

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }
    install(StatusPages) {
        exception<Throwable> { e ->
            call.respondText(e.localizedMessage, ContentType.Text.Plain, HttpStatusCode.InternalServerError)
        }
    }

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    routing {
        get("/") {
            call.respond(FreeMarkerContent("main_page.ftl", null))
        }

        post {
            val post = call.receiveParameters()

            if (
                    post["contig"].toString().isEmpty() ||
                    post["left_boundary"].toString().isEmpty() ||
                    post["right_boundary"].toString().isEmpty() ||
                    post["nucleotide"].toString().isEmpty()
            ) {
                call.respond(HttpStatusCode.BadRequest)
            }

            val tab_r = TabixReader(
                    "resources/data/42.tsv.gz",
                    "resources/data/42.tsv.gz.tbi"
            )
            var tab_r_iter =
                    TabixIteratorLineReader(tab_r.query(post["contig"].toString(),
                    post["left_boundary"]?.toInt()!!,
                    post["right_boundary"]?.toInt()!!))

            var res = mutableListOf<String>()

            LineIteratorImpl(tab_r_iter).forEach {
                if (it.split("\t").toList()[3] == post["nucleotide"]) {
                    res.add(it.split("\t").toList()[4])
                }
            }

            if (res.isEmpty()) {
                call.respond(HttpStatusCode.NotFound)
            }

            else {
                val rs = res
                call.respond(Response(rsID = rs))
            }
        }
    }
}

data class Response(val rsID: MutableList<String>)

