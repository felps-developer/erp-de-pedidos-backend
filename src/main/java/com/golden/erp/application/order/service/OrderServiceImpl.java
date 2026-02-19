package com.golden.erp.application.order.service;

import com.golden.erp.application.order.dto.CreateOrderRequest;
import com.golden.erp.application.order.dto.OrderItemRequest;
import com.golden.erp.application.order.dto.OrderResponse;
import com.golden.erp.application.order.dto.OrderUsdResponse;
import com.golden.erp.application.order.port.ExchangeRatePort;
import com.golden.erp.domain.customer.entity.Customer;
import com.golden.erp.domain.customer.repository.CustomerRepository;
import com.golden.erp.domain.exception.DomainException;
import com.golden.erp.domain.exception.EntityNotFoundException;
import com.golden.erp.domain.order.entity.Order;
import com.golden.erp.domain.order.entity.OrderItem;
import com.golden.erp.domain.order.repository.OrderRepository;
import com.golden.erp.domain.order.valueobject.OrderStatus;
import com.golden.erp.domain.product.entity.Product;
import com.golden.erp.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final ExchangeRatePort exchangeRatePort;

    public OrderResponse create(CreateOrderRequest request) {
        Customer customer = customerRepository.findById(request.getClienteId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente", request.getClienteId()));

        List<OrderItem> orderItems = new ArrayList<>();
        List<Product> productsToUpdate = new ArrayList<>();

        for (OrderItemRequest itemReq : request.getItens()) {
            Product product = productRepository.findById(itemReq.getProdutoId())
                    .orElseThrow(() -> new EntityNotFoundException("Produto", itemReq.getProdutoId()));

            product.decreaseStock(itemReq.getQuantidade());
            productsToUpdate.add(product);

            OrderItem orderItem = OrderItem.builder()
                    .produtoId(product.getId())
                    .produtoNome(product.getNome())
                    .quantidade(itemReq.getQuantidade())
                    .precoUnitario(product.getPrecoBruto())
                    .desconto(itemReq.getDesconto())
                    .build();

            orderItems.add(orderItem);
        }

        for (Product product : productsToUpdate) {
            productRepository.save(product);
        }

        Order order = Order.builder()
                .clienteId(customer.getId())
                .clienteNome(customer.getNome())
                .status(OrderStatus.CREATED)
                .itens(orderItems)
                .build();

        Order saved = orderRepository.save(order);
        log.info("Pedido criado com id: {} para cliente: {}", saved.getId(), customer.getNome());
        return OrderResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public OrderResponse findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido", id));
        return OrderResponse.from(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> findAll(OrderStatus status, Long clienteId, Pageable pageable) {
        return orderRepository.findAll(status, clienteId, pageable)
                .map(OrderResponse::from);
    }

    public OrderResponse pay(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido", id));

        order.pay();

        Order saved = orderRepository.save(order);
        log.info("Pedido {} pago com sucesso", saved.getId());
        return OrderResponse.from(saved);
    }

    public OrderResponse cancel(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido", id));

        boolean shouldRestoreStock = order.canRestoreStock();

        order.cancel();

        if (shouldRestoreStock) {
            for (OrderItem item : order.getItens()) {
                Product product = productRepository.findById(item.getProdutoId())
                        .orElse(null);
                if (product != null) {
                    product.increaseStock(item.getQuantidade());
                    productRepository.save(product);
                }
            }
            log.info("Estoque devolvido para pedido {}", id);
        }

        Order saved = orderRepository.save(order);
        log.info("Pedido {} cancelado", saved.getId());
        return OrderResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public OrderUsdResponse getUsdTotal(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido", id));

        BigDecimal rate = exchangeRatePort.getBrlToUsdRate();
        if (rate == null) {
            throw new DomainException("Taxa de câmbio indisponível no momento");
        }

        BigDecimal totalBrl = order.getTotal();
        BigDecimal totalUsd = totalBrl.multiply(rate).setScale(2, RoundingMode.HALF_UP);

        return OrderUsdResponse.builder()
                .orderId(order.getId())
                .totalBrl(totalBrl)
                .totalUsd(totalUsd)
                .exchangeRate(rate)
                .build();
    }
}
