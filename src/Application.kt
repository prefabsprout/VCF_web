package com.vcf_web

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import freemarker.cache.*
import io.ktor.freemarker.*
import kotlinx.coroutines.selects.select
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*
import java.sql.Connection

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }

    Database.connect("jdbc:mysql://localhost:3306/users", driver = "com.mysql.jdbc.Driver",
            user = "newuser", password = "password")
    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE


    var res = transaction {
        VCF_data.selectAll().map { vcf(it[VCF_data.id], it[VCF_data.contig],
                it[VCF_data.left_boundary], it[VCF_data.right_boundary], it[VCF_data.nucleotide],
                it[VCF_data.rs]) }
    }
    



    routing {
        get("/") {
            call.respond(FreeMarkerContent("main_page.ftl", null))
        }

        post {
            val post = call.receiveParameters()
            if (post["contig"] == "42") {
                call.respondText(res.toString())
            } else {
                call.respondText("Ok, you can panic just a little bit.")
            }
        }

    }
}

