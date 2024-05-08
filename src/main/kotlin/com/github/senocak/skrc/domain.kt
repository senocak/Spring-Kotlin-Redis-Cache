package com.github.senocak.skrc

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Lob
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.PrePersist
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.io.Serializable
import java.util.Date
import java.util.Optional
import org.hibernate.annotations.GenericGenerator
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@MappedSuperclass
open class BaseDomain(
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    var id: String? = null,
    @Column var createdAt: Date = Date(),
    @Column var updatedAt: Date = Date()
): Serializable

@Entity
@Table(name = "categories", uniqueConstraints = [UniqueConstraint(columnNames = ["slug"])])
class Category(
    @Column(nullable = false) var name: String? = null,
    @Column(nullable = false) var slug: String? = null,
): BaseDomain() {
    @PrePersist
    fun prePersist() {
        slug = AppConstants.toSlug(name!!)
    }
}

@Repository
interface CategoryRepository: CrudRepository<Category, String>, PagingAndSortingRepository<Category, String>, JpaSpecificationExecutor<Category> {
    @Query(value = "SELECT p FROM Category p WHERE p.id = :idOrSlug or p.slug = :idOrSlug")
    fun findByIdOrSlug(@Param("idOrSlug") idOrNameOrSlug: String): Optional<Category?>
}