package com.vcf_web

import com.fasterxml.jackson.databind.SerializationFeature
import freemarker.cache.ClassTemplateLoader
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
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection


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

    Database.connect("jdbc:mysql://localhost:3306/vcf", driver = "com.mysql.jdbc.Driver",
        user = "newuser", password = "password")
    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

    routing {
        get("/") {
            call.respond(FreeMarkerContent("main_page.ftl", null))
        }

        post {
            val post = call.receiveParameters()
            var res = transaction {
                VCF_data
                    .slice(VCF_data.rs)
                    .select { (VCF_data.contig eq post["contig"].toString()) and
                            (VCF_data.left_boundary eq (post["left_boundary"]?.toInt() ?: 0)) and
                            (VCF_data.right_boundary eq (post["right_boundary"]?.toInt() ?: 0)) and
                            (VCF_data.nucleotide eq post["nucleotide"].toString());}
                    .map { it[VCF_data.rs]}
            }

            if (res.isNotEmpty()){
                call.respondText("Result of your request:" +
                        res.toString(),ContentType.Text.Html)
            }
            else if (res.isEmpty()){
                call.respondText("Oops! Looks like we're not found your annotation.",
                        ContentType.Text.Html)
            }
        }

    }
}
