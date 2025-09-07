package tsukoyachi.camelshop.common.entity;

import jakarta.persistence.*;
import tsukoyachi.camelshop.common.enums.OrderState;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
public class Order {
    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderState state;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    private List<OrderItem> cart;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Temporal(TemporalType.DATE)
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public OrderState getState() {
        return state;
    }

    public void setState(OrderState state) {
        this.state = state;
    }

    public List<OrderItem> getCart() {
        return cart;
    }

    public void setCart(List<OrderItem> cart) {
        this.cart = cart;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Order() {
    }

    public Order(String id, User user, OrderState state, BigDecimal totalAmount, Date createdAt) {
        this.id = id;
        this.user = user;
        this.state = state;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
    }

    public Order(String id, User user, OrderState state, List<OrderItem> cart, BigDecimal totalAmount, Date createdAt) {
        this.id = id;
        this.user = user;
        this.state = state;
        this.cart = cart;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
    }
}
