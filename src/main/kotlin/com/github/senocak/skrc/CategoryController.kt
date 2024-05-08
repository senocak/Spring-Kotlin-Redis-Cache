package com.github.senocak.skrc

import com.github.senocak.skrc.AppConstants.DEFAULT_PAGE_NUMBER
import com.github.senocak.skrc.AppConstants.DEFAULT_PAGE_SIZE
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping(CategoryController.URL)
class CategoryController(
    private val categoryService: CategoryService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @Validated @RequestBody categoryCreateRequestDto: CategoryCreateRequestDto,
        resultOfValidation: BindingResult
    ): CategoryWrapperDto {
        validate(resultOfValidation = resultOfValidation)
        return categoryService.createCategory(categoryCreateRequestDto = categoryCreateRequestDto)
            .run { this.convertEntityToDto() }
            .run { CategoryWrapperDto(categoryDto = this) }
    }

    @GetMapping
    fun getAll(
        @RequestParam(value = "next", defaultValue = DEFAULT_PAGE_NUMBER) @Min(0) @Max(99) nextPage: Int,
        @RequestParam(value = "max", defaultValue = DEFAULT_PAGE_SIZE) @Min(0) @Max(99) maxNumber: Int,
    ): CategoriesDto {
        val categories = categoryService.getAll(nextPage = nextPage, maxNumber = maxNumber)
        val dtos: List<CategoryDto> = categories!!.content.map { category: Category? -> category!!.convertEntityToDto() }
        return CategoriesDto(categoryDtoList = dtos, total = categories.totalElements,
            next = if (categories.hasNext()) (nextPage + 1).toLong() else 0)
    }

    @GetMapping("/{slug}")
    fun getSingle(@PathVariable slug: String): CategoryWrapperDto =
        CategoryWrapperDto(categoryDto = categoryService.findCategory(idOrSlug = slug).convertEntityToDto())

    @PatchMapping("/{slug}")
    fun update(
        @PathVariable slug: String,
        @Validated @RequestBody categoryUpdateRequestDto: CategoryUpdateRequestDto,
        resultOfValidation: BindingResult
    ): CategoryWrapperDto {
        validate(resultOfValidation = resultOfValidation)
        val category = categoryService.findCategory(idOrSlug = AppConstants.toSlug(input = slug))
        val savedCategory = categoryService.updateCategory(category = category, categoryUpdateRequestDto = categoryUpdateRequestDto)
        return CategoryWrapperDto(categoryDto = savedCategory.convertEntityToDto())
    }

    @DeleteMapping("/{slug}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable slug: String) =
        categoryService.findCategory(idOrSlug = AppConstants.toSlug(input = slug))
            .run { categoryService.deleteCategory(category = this) }

    companion object {
        const val URL = "/api/v1/categories"
    }

    fun validate(resultOfValidation: BindingResult) {
        if (resultOfValidation.hasErrors()) {
            throw ServerException(omaErrorMessageType = OmaErrorMessageType.JSON_SCHEMA_VALIDATOR,
                variables = resultOfValidation.fieldErrors.stream()
                    .map { fieldError: FieldError? -> "${fieldError?.field}: ${fieldError?.defaultMessage}" }
                    .toList().toTypedArray(),
                statusCode = HttpStatus.BAD_REQUEST)
        }
    }
}