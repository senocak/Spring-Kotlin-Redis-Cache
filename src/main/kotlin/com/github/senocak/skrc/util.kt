package com.github.senocak.skrc

import java.text.Normalizer
import java.util.Date
import java.util.regex.Pattern

object AppConstants {
    const val DEFAULT_PAGE_NUMBER = "0"
    const val DEFAULT_PAGE_SIZE = "10"
    const val CACHE_CATEGORY = "category"

    /**
     * @param input -- string variable to make it sluggable
     * @return -- sluggable string variable
     */
    fun toSlug(input: String): String {
        val nonLatin: Pattern = Pattern.compile("[^\\w-]")
        val whiteSpace: Pattern = Pattern.compile("[\\s]")
        val noWhiteSpace: String = whiteSpace.matcher(input).replaceAll("-")
        val normalized: String = Normalizer.normalize(noWhiteSpace, Normalizer.Form.NFD)
        return nonLatin.matcher(normalized).replaceAll("")
    }
}


enum class OmaErrorMessageType(val messageId: String, val text: String) {
    BASIC_INVALID_INPUT("SVC0001", "Invalid input value for message part %1"),
    GENERIC_SERVICE_ERROR("SVC0002", "The following service error occurred: %1. Error code is %2"),
    DETAILED_INVALID_INPUT("SVC0003", "Invalid input value for %1 %2: %3"),
    EXTRA_INPUT_NOT_ALLOWED("SVC0004", "Input %1 %2 not permitted in request"),
    MANDATORY_INPUT_MISSING("SVC0005", "Mandatory input %1 %2 is missing from request"),
    UNAUTHORIZED("SVC0006", "UnAuthorized Endpoint"),
    JSON_SCHEMA_VALIDATOR("SVC0007", "Schema failed."),
    NOT_FOUND("SVC0008", "Entry is not found")
}

/**
 * @return -- CategoryDto object
 */
fun Category.convertEntityToDto(): CategoryDto {
    val categoryDto = CategoryDto()
    categoryDto.resourceId = this.id
    categoryDto.slug = this.slug
    categoryDto.name = this.name
    return categoryDto
}


/**
 * @param date -- Date object to convert to long timestamp
 * @return -- converted timestamp object that is long type
 */
private fun convertDateToLong(date: Date): Long = date.time / 1000
