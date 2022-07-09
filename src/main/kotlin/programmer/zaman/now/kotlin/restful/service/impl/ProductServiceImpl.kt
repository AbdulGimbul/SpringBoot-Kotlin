package programmer.zaman.now.kotlin.restful.service.impl

import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import programmer.zaman.now.kotlin.restful.entity.Product
import programmer.zaman.now.kotlin.restful.error.NotFoundException
import programmer.zaman.now.kotlin.restful.model.CreateProductRequest
import programmer.zaman.now.kotlin.restful.model.ListProductRequest
import programmer.zaman.now.kotlin.restful.model.ProductResponse
import programmer.zaman.now.kotlin.restful.model.UpdateProductRequest
import programmer.zaman.now.kotlin.restful.repository.ProductRepository
import programmer.zaman.now.kotlin.restful.service.ProductService
import programmer.zaman.now.kotlin.restful.validation.ValidationUtil
import java.util.*
import java.util.stream.Collectors

@Service
class ProductServiceImpl(
    val productRepository: ProductRepository,
    val validationUtil: ValidationUtil
    ) : ProductService {

    override fun create(createProductRequest: CreateProductRequest): ProductResponse {
        validationUtil.validate(createProductRequest)

        val product = Product(
            id = createProductRequest.id!!,
            name = createProductRequest.name!!,
            price = createProductRequest.price!!,
            quantity = createProductRequest.quantity!!,
            createdAt = Date(),
            updatedAt = null
        )

        productRepository.save(product)

        return convertProductToProductResponse(product)
    }

    override fun get(id: String): ProductResponse {
        val product = findByIdOrThrowNotFound(id)
            return convertProductToProductResponse(product)

    }

    override fun update(id: String, updateProductRequest: UpdateProductRequest): ProductResponse {
        val product = findByIdOrThrowNotFound(id)
            validationUtil.validate(updateProductRequest)

            product.apply {
                name = updateProductRequest.name!!
                price = updateProductRequest.price!!
                quantity = updateProductRequest.quantity!!
                updatedAt = Date()
            }

            productRepository.save(product)

            return convertProductToProductResponse(product)
    }

    override fun delete(id: String) {
        val product = findByIdOrThrowNotFound(id)
        productRepository.delete(product)
    }

    override fun list(listProductRequest: ListProductRequest): List<ProductResponse> {
        val page = productRepository.findAll(PageRequest.of(listProductRequest.page, listProductRequest.size))
        val products: kotlin.collections.List<Product> = page.get().collect(Collectors.toList())
        return products.map { convertProductToProductResponse(it) }
    }

    private fun findByIdOrThrowNotFound(id: String) : Product {
        val product = productRepository.findByIdOrNull(id)
        if (product == null){
            throw NotFoundException()
        } else {
            return product
        }
    }

    private fun convertProductToProductResponse(product: Product) : ProductResponse {
        return ProductResponse(
            id = product.id,
            name = product.name,
            price = product.price,
            quantity = product.quantity,
            createdAt = product.createdAt,
            updatedAt = product.updatedAt
        )
    }


}