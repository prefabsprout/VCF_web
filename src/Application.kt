package com.vcf_web

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import freemarker.cache.*
import io.ktor.freemarker.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }

    routing {

        get("/") {
            call.respond(FreeMarkerContent("main_page.ftl", null))
        }

        post {
            val post = call.receiveParameters()
            if (post["contig"] == "42") {
                call.respondText("Don't panic!")
            } else {
                call.respondText("Ok, you can panic just a little bit.")
            }
        }

    }
}