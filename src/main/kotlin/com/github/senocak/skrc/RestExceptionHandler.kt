package com.github.senocak.skrc

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.TypeMismatchException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MissingPathVariableException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.NoHandlerFoundException
import java.lang.reflect.UndeclaredThrowableException
import java.security.InvalidParameterException

@RestControllerAdvice
class RestExceptionHandler {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    @ExceptionHandler(
        InvalidParameterException::class,
        TypeMismatchException::class,
        MissingPathVariableException::class,
        HttpMessageNotReadableException::class,
        MissingServletRequestParameterException::class,
        UndeclaredThrowableException::class
    )
    fun handleBadRequestException(ex: Exception): ResponseEntity<ExceptionDto> =
        generateResponseEntity(httpStatus = HttpStatus.BAD_REQUEST,
            omaErrorMessageType = OmaErrorMessageType.BASIC_INVALID_INPUT, variables = arrayOf(ex.message))

    @ExceptionHandler(
        com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException::class
    )
    fun handleUnAuthorized(ex: Exception): ResponseEntity<ExceptionDto> =
        generateResponseEntity(httpStatus = HttpStatus.UNAUTHORIZED,
            omaErrorMessageType = OmaErrorMessageType.UNAUTHORIZED, variables = arrayOf(ex.message))

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleHttpRequestMethodNotSupported(ex: HttpRequestMethodNotSupportedException): ResponseEntity<ExceptionDto> =
        generateResponseEntity(httpStatus = HttpStatus.METHOD_NOT_ALLOWED,
            omaErrorMessageType = OmaErrorMessageType.EXTRA_INPUT_NOT_ALLOWED, variables = arrayOf(ex.message))

    @ExceptionHandler(HttpMediaTypeNotSupportedException::class)
    fun handleHttpMediaTypeNotSupported(ex: HttpMediaTypeNotSupportedException): ResponseEntity<ExceptionDto> =
        generateResponseEntity(httpStatus = HttpStatus.UNSUPPORTED_MEDIA_TYPE,
            omaErrorMessageType = OmaErrorMessageType.BASIC_INVALID_INPUT, variables = arrayOf(ex.message))

    @ExceptionHandler(NoHandlerFoundException::class)
    fun handleNoHandlerFoundException(ex: Exception): ResponseEntity<ExceptionDto> =
        generateResponseEntity(httpStatus = HttpStatus.NOT_FOUND,
            omaErrorMessageType = OmaErrorMessageType.NOT_FOUND, variables = arrayOf(ex.message))

    @ExceptionHandler(ServerException::class)
    fun handleServerException(ex: ServerException): ResponseEntity<ExceptionDto> =
        generateResponseEntity(httpStatus = ex.statusCode,
            omaErrorMessageType = ex.omaErrorMessageType, variables = ex.variables)

    @ExceptionHandler(Exception::class)
    fun handleGeneralException(ex: Exception): ResponseEntity<ExceptionDto> =
        generateResponseEntity(httpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
            omaErrorMessageType = OmaErrorMessageType.GENERIC_SERVICE_ERROR, variables = arrayOf(ex.message))

    /**
     * @param httpStatus -- returned code
     * @return -- returned body
     */
    private fun generateResponseEntity(
        httpStatus: HttpStatus,
        omaErrorMessageType: OmaErrorMessageType,
        variables: Array<String?>
    ): ResponseEntity<ExceptionDto> {
        log.error("Exception is handled. HttpStatus: $httpStatus, OmaErrorMessageType: $omaErrorMessageType, variables: $variables")
        val exceptionDto = ExceptionDto()
            .also {
                it.statusCode = httpStatus.value()
                it.error = ExceptionDto.OmaErrorMessageTypeDto(
                    id = omaErrorMessageType.messageId,
                    text = omaErrorMessageType.text
                )
                it.variables = variables
            }
        return ResponseEntity.status(httpStatus).body(exceptionDto)
    }
}
