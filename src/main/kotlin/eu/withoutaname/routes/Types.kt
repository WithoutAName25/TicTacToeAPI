package eu.withoutaname.routes

import com.papsign.ktor.openapigen.APIException
import com.papsign.ktor.openapigen.annotations.parameters.HeaderParam
import com.papsign.ktor.openapigen.exceptions.OpenAPIRequiredFieldException
import io.ktor.http.*
import io.ktor.server.plugins.*

open class Response(val success: Boolean)

object Success : Response(true)

open class Error(val error: String) : Response(false)

data class SessionParameter(@HeaderParam("User session (obtained by setting the username)") val user_session: String)

class InvalidSessionException : Exception()

object InvalidSessionError : Error("Set username first!")

val invalidSessionException = APIException.apiException<InvalidSessionException, Error>(
    HttpStatusCode.Unauthorized,
    InvalidSessionError
) {
    InvalidSessionError
}

val missingFieldExeption = APIException.apiException<OpenAPIRequiredFieldException, Error>(
    HttpStatusCode.BadRequest,
    Error("The field xy is required")
) {
    Error(it.message)
}

object BadRequestError : Error("Bad Request!")

val badRequestException = APIException.apiException<BadRequestException, Error>(
    HttpStatusCode.BadRequest,
    BadRequestError
) {
    BadRequestError
}
