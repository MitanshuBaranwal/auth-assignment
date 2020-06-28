package com.assignment

import com.assignment.repository.UserRepositoryImpl
import com.assignment.service.OTPWrite
import com.assignment.service.UserWrite
import com.assignment.service.sendEmail
import com.assignment.utils.generateOtp
import com.assignment.utils.hashPassword
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing
import org.jetbrains.exposed.sql.Database
import org.simplejavamail.api.mailer.config.TransportStrategy
import org.simplejavamail.config.ConfigLoader
import org.simplejavamail.mailer.MailerBuilder
import java.io.File

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)
val mailer by lazy {
    ConfigLoader.loadProperties(File("/Users/mitanshubaranwal/Downloads/auth-ktor-assignment/properties/mail.properties"), false)
    MailerBuilder
        .withSMTPServer("smtp.mailtrap.io", 587, "44c2af0f7c0a0e", "a583530be8fd84")
        .withTransportStrategy(TransportStrategy.SMTP_TLS)
        .withProperty("mail.smtp.sendpartial", true)
        .withDebugLogging(true)
        .async()
        // not enough? what about this:
        .buildMailer()
}

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    val repo = UserRepositoryImpl()
    val client = HttpClient(Apache) {
        engine { connectTimeout = 10 }
        install(DefaultHeaders)
        install(CallLogging)
        install(ContentNegotiation) {
            gson {
                setPrettyPrinting()

            }
        }
        Database.connect(
            "jdbc:mysql://localhost/userauth", driver = "com.mysql.jdbc.Driver",
            user = "root", password = "Rr123456@"
        )
        routing {
            route("api/signup") {
                post {
                    val writeUser = call.receive<UserWrite>()
                    repo.findUser(writeUser.email)?.let {
                        call.respondText(status = HttpStatusCode.Conflict, text = "User exist")
                    } ?: let {
                        try {
                                repo.insertUser(writeUser.email, hashPassword(writeUser.password), writeUser.username, writeUser.contact_number)
                                call.respondText(status = HttpStatusCode.OK, text = "Success")
                        } catch (exception: Exception) {
                            call.respond(HttpStatusCode.BadRequest)
                        }
                    }
                }
            }

            route("api/signin") {
                post {
                    val writeUser = call.receive<UserWrite>()
                    repo.selectUser(writeUser.email, hashPassword(writeUser.password))?.let {
                        val otp = generateOtp()
                        repo.insertOTP(otp, writeUser.email)
                        sendEmail(otp, writeUser.email)
                        call.respondText(status = HttpStatusCode.OK, text = "Success")
                    } ?: let {
                        call.respond(HttpStatusCode.BadRequest)
                    }
                }
            }

            route("api/verify") {
                post {
                    val writeOTP = call.receive<OTPWrite>()
                    repo.verifyOTP(writeOTP.otp, writeOTP.email)?.let {

                        call.respondText(status = HttpStatusCode.OK, text = "hi "+ writeOTP.email+ " Your login is Successful")
                    } ?: let {
                        call.respondText(status = HttpStatusCode.Forbidden, text = "Failed")
                    }
                }
            }
        }
    }
}




