package com.vcf_web

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection


data class vcf(val id: Int, val contig: String, val left_boundary: Int,
               val right_boundary: Int, val nucleotide: String, val rs: Int)
object VCF_data : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val contig: Column<String> = varchar("contig", 55)
    val left_boundary: Column<Int> = integer("left_boundary")
    val right_boundary: Column<Int> = integer("right_boundary")
    val nucleotide: Column<String> = varchar("nucleotide", 1)
    val rs: Column<Int> = integer("rs")
    override val primaryKey = PrimaryKey(id)
}


fun main() {
    Database.connect("jdbc:mysql://localhost:3306/vcf", driver = "com.mysql.jdbc.Driver",
            user = "newuser", password = "password")
    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

    transaction {
        SchemaUtils.create(VCF_data)
        // Test data generation
        VCF_data.insert {
            it[contig] = "chr2"
            it[left_boundary] = 4
            it[right_boundary] = 2
            it[nucleotide] = "A"
            it[rs] = 42
        }
    }

}
