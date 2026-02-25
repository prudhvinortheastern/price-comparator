package com.example.price_comparator.entity;

import jakarta.persistence.*;
        import java.time.LocalDateTime;

@Entity
@Table(name = "tracked_products",
        uniqueConstraints = @UniqueConstraint(
                name="uk_user_product",
                columnNames={"user_id","product_id"}
        )
)
public class TrackedProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false)
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne(optional=false)
    @JoinColumn(name="product_id")
    private Product product;

    private Double lastSeenPrice;

    @Column(nullable=false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; }
    public User getUser() { return user; }
    public Product getProduct() { return product; }
    public Double getLastSeenPrice() { return lastSeenPrice; }

    public void setUser(User user) { this.user = user; }
    public void setProduct(Product product) { this.product = product; }
    public void setLastSeenPrice(Double lastSeenPrice) { this.lastSeenPrice = lastSeenPrice; }
}