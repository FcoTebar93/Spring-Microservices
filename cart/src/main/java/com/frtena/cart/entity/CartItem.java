package com.frtena.cart.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="cartitem")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable=false)
    private String title;

    @Column(nullable=false)
    private double price;

    @Column(nullable=false)
    private String description;

    @Column(nullable=false)
    private String category;

    @Column(nullable=false)
    private String image;

    @JoinColumn()
    private ShoppingCart shoppingCart;

}
