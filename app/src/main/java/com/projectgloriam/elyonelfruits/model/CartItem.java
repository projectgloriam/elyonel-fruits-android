package com.projectgloriam.elyonelfruits.model;

import com.google.firebase.firestore.DocumentReference;

public class CartItem {
	private DocumentReference fruit;
    private Integer quantity;

    public CartItem() {}

    public CartItem(DocumentReference fruit, Integer quantity) {
        this.fruit = fruit;
        this.quantity = quantity;
    }

    public DocumentReference getFruit() {
        return fruit;
    }

    public Integer getQuantity() {
        return quantity;
    }

}
