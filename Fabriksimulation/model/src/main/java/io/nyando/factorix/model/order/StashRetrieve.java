package io.nyando.factorix.model.order;

import java.util.Optional;

/**
 * Implements a repository interface for products.
 * Objects handling products can create new ones and request r/w access to or update existing products.
 */
public interface StashRetrieve {

    /**
     * Create a new product in the repository.
     * @param product Newly created product object.
     */
    void create(Product product);

    /**
     * Store an existing product back in the repository, making it available for other objects to retrieve.
     * @param product Product object to stash in the repository.
     */
    void stash(Product product);

    /**
     * Retrieve a product from the repository for modification, gets exclusive write access.
     * @param productID ID of product to retrieve from repository.
     * @return Optional containing either the product object with productID or empty.
     */
    Optional<Product> retrieve(String productID);

    /**
     * Get read access to a product from the repository.
     * @param productID ID of product to read from repository.
     * @return Product object with corresponding ID.
     */
    Product check(String productID);

}
