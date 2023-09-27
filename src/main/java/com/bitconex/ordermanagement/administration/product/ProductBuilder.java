package com.bitconex.ordermanagement.administration.product;

import java.util.Date;

public class ProductBuilder {
        private Long id;
        private String name;
        private Double price;
        private Date validFrom;
        private Date validTo;
        private int quantity;
        private boolean active;

        public ProductBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public ProductBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public ProductBuilder withPrice(Double price) {
            this.price = price;
            return this;
        }

        public ProductBuilder withValidFrom(Date validFrom) {
            this.validFrom = validFrom;
            return this;
        }

        public ProductBuilder withValidTo(Date validTo) {
            this.validTo = validTo;
            return this;
        }

        public ProductBuilder withQuantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public ProductBuilder isActive(boolean active) {
            this.active = active;
            return this;
        }

        public Product build() {
            Product product = new Product();
            product.setId(id);
            product.setName(name);
            product.setPrice(price);
            product.setValidFrom(validFrom);
            product.setValidTo(validTo);
            product.setQuantity(quantity);
            product.setActive(active);
            return product;
        }
    }

