package com.assignment.repository

import com.assignment.repository.table.*
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction


interface UserRepository {
    suspend fun selectUser(email: String, password: String): User?
    suspend fun insertUser(email: String, password: String)
    suspend fun findUser(email: String): User?
    suspend fun verifyOTP(otp: String, email: String): OTP?
    suspend fun insertOTP(otp: String, email: String)
}

class UserRepositoryImpl : UserRepository {
    override suspend fun selectUser(email: String, password: String): User? =
        transaction {
            UserTable
                .select { UserTable.email.eq(email) and UserTable.password.eq(password) }
                .firstOrNull()?.toUser() ?: let { null }
        }


    override suspend fun insertUser(email: String, password: String) {
        transaction {
            val statement = UserTable.insert {
                it[UserTable.email] = email
                it[UserTable.password] = password
            }
            statement.resultedValues ?: throw IllegalStateException("Generated id is null.")
        }
    }

    override suspend fun findUser(email: String): User? =
        transaction {
            UserTable
                .select { UserTable.email.eq(email) }
                .firstOrNull()?.toUser() ?: let { null }
        }

    override suspend fun verifyOTP(otp: String, email: String): OTP? =
        transaction {
            OTPTable
                .select { OTPTable.email.eq(email) and OTPTable.otp.eq(otp) }
                .firstOrNull()?.toOTP()
                ?: let { null }
        }


    override suspend fun insertOTP(otp: String, email: String) {
        transaction {
            OTPTable.deleteWhere { OTPTable.email.eq(email) }
            val statement = OTPTable.insert {
                it[OTPTable.email] = email
                it[OTPTable.otp] = otp
            }
            statement.resultedValues ?: throw IllegalStateException("Generated id is null.")
        }
    }
}