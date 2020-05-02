package com.vcf_web

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import freemarker.cache.*
import io.ktor.freemarker.*
import kotlinx.coroutines.selects.select
import org.jetbrains.exposed.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

data class vcf(val name:String, val left_boundary: Int,
               val right_boundary: Int, val nucleotide: Char, val rs: Int)
object VCF_data : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val contig: Column<String> = varchar("contig", 55)
    val left_boundary: Column<Int> = integer("left_boundary")
    val right_boundary: Column<Int> = integer("right_boundary")
    val nucleotide: Column<Char> = char("nucleotide")
    val rs: Column<Int> = integer("rs")
    override val primaryKey = PrimaryKey(id)
}


@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }

    Database.connect("jdbc:mysql://localhost:3306/users", driver = "com.mysql.jdbc.Driver",
        user = "newuser", password = "")

    transaction {
        SchemaUtils.create(VCF_data)

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