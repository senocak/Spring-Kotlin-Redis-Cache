package com.github.senocak.skrc

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import jakarta.validation.constraints.Size
import org.springframework.http.HttpStatus

@JsonIgnoreProperties(ignoreUnknown = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
abstract class BaseDto

open class RestException(msg: String, t: Throwable? = null): Exception(msg, t)
class ServerException(
    var omaErrorMessageType: OmaErrorMessageType,
    var variables: Array<String?>,
    var statusCode: HttpStatus
): RestException("OmaErrorMessageType: $omaErrorMessageType, variables: $variables, statusCode: $statusCode")

@JsonPropertyOrder("resourceId", "name", "slug", "image")
class CategoryDto : BaseDto() {
    var resourceId: String? = null
    var name: String? = null
    var slug: String? = null
    var image: String? = null
}

@JsonPropertyOrder("category", "next", "total")
class CategoriesDto(
    @JsonProperty("category")
    var categoryDtoList: List<CategoryDto>,
    var total: Long = 0,
    var next: Long = 0,
)

class CategoryWrapperDto(
    @JsonProperty("category")
    var categoryDto: CategoryDto? = null
) : BaseDto()

data class CategoryCreateRequestDto(
    @field:Size(min = 3, max = 30)
    @JsonProperty("name")
    val name: String
): BaseDto()

data class CategoryUpdateRequestDto(
    @field:Size(min = 3, max = 30)
    var name: String? = null,
): BaseDto()

@JsonPropertyOrder("statusCode", "error", "variables")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("exception")
class ExceptionDto : BaseDto() {
    var statusCode = 200
    var error: OmaErrorMessageTypeDto? = null
    var variables: Array<String?> = arrayOf(String())

    @JsonPropertyOrder("id", "text")
    class OmaErrorMessageTypeDto(val id: String? = null, val text: String? = null)
}