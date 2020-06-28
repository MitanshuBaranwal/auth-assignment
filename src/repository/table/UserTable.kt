package com.assignment.repository.table

import com.assignment.repository.User
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.jodatime.datetime
import org.joda.time.DateTime

object UserTable : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val username: Column<String> = varchar("username", 255)
    val email: Column<String> = varchar("email", 100).uniqueIndex()
    val password: Column<String> = varchar("password", 255)
    val contact_number: Column<String> = varchar("contact_number", 15)
    override val primaryKey = PrimaryKey(id)
}

object OTPTable : Table("OTP") {
    val email = varchar("email", 100)
    val otp = varchar("otp", 100)
    val createdAt = datetime("creation_time").default(DateTime())
    val updatedAt = datetime("modification_time").default(DateTime())
    override val primaryKey = PrimaryKey(OTPTable.email)
    const val foreignKey = "email"
}

fun ResultRow.toUser() = User(
    id = this[UserTable.id],
    email = this[UserTable.email],
    password = this[UserTable.password],
    username = this[UserTable.username],
    contact_number = this[UserTable.contact_number]
)

fun ResultRow.toOTP() = OTP(
    otp = this[OTPTable.otp],
    email = this[OTPTable.email],
    updatedAt = this[OTPTable.updatedAt].toLocalDateTime()
)