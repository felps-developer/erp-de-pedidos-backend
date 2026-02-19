package com.golden.erp.domain.order.entity;

import com.golden.erp.domain.exception.InvalidOrderStateException;
import com.golden.erp.domain.order.valueobject.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    private Long id;
    private Long clienteId;
    private String clienteNome;
    @Builder.Default
    private OrderStatus status = OrderStatus.CREATED;
    @Builder.Default
    private List<OrderItem> itens = new ArrayList<>();
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    public BigDecimal getSubtotal() {
        return itens.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getDescontos() {
        return itens.stream()
                .map(OrderItem::getDescontoTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTotal() {
        return getSubtotal().subtract(getDescontos()).setScale(2, RoundingMode.HALF_UP);
    }

    public void pay() {
        if (this.status != OrderStatus.CREATED && this.status != OrderStatus.LATE) {
            throw new InvalidOrderStateException(this.status.name(), OrderStatus.PAID.name());
        }
        this.status = OrderStatus.PAID;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void cancel() {
        if (this.status == OrderStatus.PAID) {
            throw new InvalidOrderStateException(this.status.name(), OrderStatus.CANCELLED.name());
        }
        if (this.status == OrderStatus.CANCELLED) {
            throw new InvalidOrderStateException(this.status.name(), OrderStatus.CANCELLED.name());
        }
        this.status = OrderStatus.CANCELLED;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public boolean canRestoreStock() {
        return this.status != OrderStatus.PAID;
    }

    public void markAsLate() {
        if (this.status == OrderStatus.CREATED) {
            this.status = OrderStatus.LATE;
            this.dataAtualizacao = LocalDateTime.now();
        }
    }
}
