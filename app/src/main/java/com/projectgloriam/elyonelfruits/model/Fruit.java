/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 package com.projectgloriam.elyonelfruits.model;

import com.google.firebase.firestore.IgnoreExtraProperties;

/**
 * Fruit POJO.
 */
@IgnoreExtraProperties
public class Fruit {

    public static final String FIELD_ASCENDING = "ascending";
    public static final String FIELD_DESCENDING = "descending";
    public static final String FIELD_PRICE = "price";

    private String name;
    private String category;
    private String photo;
    private int price;
    private String description;

    public Fruit() {}

    public Fruit(String name, String category, String photo,
                 int price, String description) {
        this.name = name;
        this.category = category;
        this.photo = photo;
        this.price = price;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
