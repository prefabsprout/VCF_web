package com.vcf_web

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.sql.Connection
import org.jetbrains.exposed.sql.transactions.transaction



data class vcf(val id: Int, val contig: String, val left_boundary: Int,
               val right_boundary: Int, val nucleotide: String, val rs: String)
object VCF_data : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val contig: Column<String> = varchar("contig", 55)
    val left_boundary: Column<Int> = integer("left_boundary")
    val right_boundary: Column<Int> = integer("right_boundary")
    val nucleotide: Column<String> = varchar("nucleotide", 10000)
    val rs: Column<String> = varchar("rs", 40)
    override val primaryKey = PrimaryKey(id)
}


fun main() {
    Database.connect("jdbc:mysql://localhost:3306/vcf", driver = "com.mysql.jdbc.Driver",
            user = "newuser", password = "password")
    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
    transaction {
        SchemaUtils.create(VCF_data)
        }
//    var res = transaction {
//        VCF_data
//                .slice(VCF_data.rs)
//                .select { (VCF_data.contig eq "chr42" and
//                        (VCF_data.left_boundary eq 9578804) and
//                        (VCF_data.right_boundary eq 9578807) and
//                        (VCF_data.nucleotide eq "A"));
//                }
//                .map { it[VCF_data.rs]}
//    }
//    println(res)
}