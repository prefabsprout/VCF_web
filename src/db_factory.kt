package com.vcf_web

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection



data class vcf(val id: Int, val contig: String, val left_boundary: Int,
               val right_boundary: Int, val nucleotide: String, val rs: String)
object VCF_data : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val contig: Column<String> = varchar("contig", 55)
    val left_boundary: Column<Int> = integer("left_boundary")
    val right_boundary: Column<Int> = integer("right_boundary")
    val nucleotide: Column<String> = varchar("nucleotide", 10000)
    val rs: Column<String> = text("rs")
    override val primaryKey = PrimaryKey(id)
}


fun main() {
    val config = HikariConfig().apply {
        jdbcUrl         = "jdbc:mysql://localhost:3306/vcf"
        driverClassName = "com.mysql.jdbc.Driver"
        username        = "newuser"
        password        = "password"
        maximumPoolSize = 10
    }
    val dataSource = HikariDataSource(config)
    Database.connect(dataSource)

    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
    transaction {
        SchemaUtils.create(VCF_data)
        }
}