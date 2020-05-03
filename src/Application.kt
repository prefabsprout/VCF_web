package com.vcf_web

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import freemarker.cache.*
import io.ktor.freemarker.*
import kotlinx.coroutines.selects.select
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
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
                call.respondText(res.toString())
            }
            else if (res.isEmpty()){
                call.respondText("Oops! Looks like we're not found your annotation.")
            }
//            else if (post["contig"].isEmpty()!!) {
//                call.respondText("Error: You need to fill every form!")
//            }

        }

    }
}

