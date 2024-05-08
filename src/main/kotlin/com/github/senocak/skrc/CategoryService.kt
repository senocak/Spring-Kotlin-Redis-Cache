package com.github.senocak.skrc

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class CategoryService(
    private val categoryRepository: CategoryRepository
) {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    /**
     * Create new category
     * @param categoryCreateRequestDto -- CategoryCreateRequestDto object to be created
     * @return -- Category entity that is created
     * @throws ServerException -- ServerException if category name or slug is already exist
     */
    @Caching(
        put = [
            CachePut(value = arrayOf(AppConstants.CACHE_CATEGORY), key = "#result.id"),
            CachePut(value = arrayOf(AppConstants.CACHE_CATEGORY), key = "#result.slug")
        ]
    )
    fun createCategory(categoryCreateRequestDto: CategoryCreateRequestDto): Category {
        var category: Category? = null
        try {
            category = findCategory(idOrSlug = AppConstants.toSlug(input = categoryCreateRequestDto.name))
        } catch (serverException: ServerException) {
            log.debug("Caught ServerException, Post not exist in db")
        }
        if (category != null) {
            val error = "Category ${category.name} already exist"
            log.error(error)
            throw ServerException(omaErrorMessageType = OmaErrorMessageType.BASIC_INVALID_INPUT,
                variables = arrayOf(error), statusCode = HttpStatus.CONFLICT)
        }
        return saveCategory(category = Category(name = categoryCreateRequestDto.name))
    }

    /**
     * @param nextPage -- next page variable to filter
     * @param maxNumber -- max page to retrieve from db
     * @return -- post objects that has retrieved by page
     */
    fun getAll(nextPage: Int, maxNumber: Int): Page<Category?>? =
        categoryRepository.findAll(PageRequest.of(nextPage, maxNumber))

    /**
     * @param idOrSlug -- slug or identifier of Category entity
     * @return -- Category entity that is retrieved from db
     * @throws ServerException -- if Category is not found
     */
    @Cacheable(value = [AppConstants.CACHE_CATEGORY], key = "#idOrSlug", unless = "#result == null")
    @Throws(ServerException::class)
    fun findCategory(idOrSlug: String): Category {
        val category = categoryRepository.findByIdOrSlug(idOrNameOrSlug = idOrSlug)
        if (!category.isPresent) {
            log.error("Category is not found: $idOrSlug")
            throw ServerException(omaErrorMessageType = OmaErrorMessageType.NOT_FOUND,
                variables = arrayOf("Category: $idOrSlug"), statusCode = HttpStatus.NOT_FOUND)
        }
        return category.get()
    }

    /**
     * @param category -- Category entity to be wanted to persist
     * @return -- Persisted Category entity
     */
    @Caching(
        evict = [CacheEvict(value = arrayOf(AppConstants.CACHE_CATEGORY), key = "#category.slug")],
        put = [
            CachePut(value = arrayOf(AppConstants.CACHE_CATEGORY), key = "#result.id"),
            CachePut(value = arrayOf(AppConstants.CACHE_CATEGORY), key = "#result.slug")
        ]
    )
    fun updateCategory(category: Category, categoryUpdateRequestDto: CategoryUpdateRequestDto): Category {
        val categoryName: String? = categoryUpdateRequestDto.name
        when {
            categoryName != null -> category.name = categoryName
        }
        return saveCategory(category = category)
    }

    /**
     * @param category -- Category entity to be wanted to delete
     */
    @Caching(
        evict = [
            CacheEvict(value = arrayOf(AppConstants.CACHE_CATEGORY), key = "#category.id"),
            CacheEvict(value = arrayOf(AppConstants.CACHE_CATEGORY), key = "#category.slug")
        ]
    )
    fun deleteCategory(category: Category) = categoryRepository.delete(category)

    /**
     * Update category
     * @param category -- Category entity to be wanted to update
     * @return -- Updated Category entity
     */
    private fun saveCategory(category: Category): Category = categoryRepository.save(category)
}