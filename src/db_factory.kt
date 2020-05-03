package com.vcf_web

import com.vcf_web.VCF_data.autoIncrement
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection


data class vcf(val id: Int, val contig: String, val left_boundary: Int,
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


fun main() {
    Database.connect("jdbc:mysql://localhost:3306/vcf", driver = "com.mysql.jdbc.Driver",
            user = "newuser", password = "password")
    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

    transaction {
        SchemaUtils.create(VCF_data)
    }
}