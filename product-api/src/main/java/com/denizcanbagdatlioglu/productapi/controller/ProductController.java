package com.denizcanbagdatlioglu.productapi.controller;

import com.denizcanbagdatlioglu.productapi.product.dto.ProductRequest;
import com.denizcanbagdatlioglu.productapi.product.dto.ProductResponse;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final Map<String, ProductResponse> products = new HashMap<>();

    @GetMapping
    public CollectionModel<EntityModel<ProductResponse>> list() {
        List<EntityModel<ProductResponse>> productList = products.values().stream().map(this::toEntityModel).toList();
        return CollectionModel.of(productList).add(linkTo(methodOn(ProductController.class).list()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<ProductResponse> get(@PathVariable String id) {
        ProductResponse productResponse = products.get(id);
        if(productResponse == null) {
            throw new NoSuchElementException("Product with id {" + id + "} not found!");
        }

        return toEntityModel(productResponse);
    }

    @PostMapping
    public ResponseEntity<EntityModel<ProductResponse>> create(@Valid @RequestBody ProductRequest productRequest) {
        String id = UUID.randomUUID().toString();
        ProductResponse productResponse = new ProductResponse(id, productRequest.name(), productRequest.price());
        products.put(id, productResponse);

        return ResponseEntity.created(linkTo(methodOn(ProductController.class).get(id)).toUri()).body(toEntityModel(productResponse));
    }

    @PutMapping("/{id}")
    public EntityModel<ProductResponse> update(@PathVariable String id, @Valid @RequestBody ProductRequest productRequest) {
        if(!products.containsKey(id)) {
            throw new NoSuchElementException("Product with id {" + id + "} not found!");
        }

        ProductResponse productResponse = new ProductResponse(id, productRequest.name(), productRequest.price());
        products.put(id, productResponse);
        return toEntityModel(productResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if(!products.containsKey(id)) {
            throw new NoSuchElementException("Product with id {" + id + "} not found!");
        }

        products.remove(id);
        return ResponseEntity.noContent().build();
    }

    private EntityModel<ProductResponse> toEntityModel(ProductResponse productResponse) {
        return EntityModel.of(productResponse)
                .add(linkTo(methodOn(ProductController.class).get(productResponse.id())).withSelfRel())
                .add(linkTo(methodOn(ProductController.class).delete(productResponse.id())).withRel("delete"))
                .add(linkTo(methodOn(ProductController.class).update(productResponse.id(), new ProductRequest("", 0.0))).withRel("update"));
    }

}
