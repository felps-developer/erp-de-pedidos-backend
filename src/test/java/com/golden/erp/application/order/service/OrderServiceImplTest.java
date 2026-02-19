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
import com.golden.erp.domain.exception.InsufficientStockException;
import com.golden.erp.domain.exception.InvalidOrderStateException;
import com.golden.erp.domain.order.entity.Order;
import com.golden.erp.domain.order.entity.OrderItem;
import com.golden.erp.domain.order.repository.OrderRepository;
import com.golden.erp.domain.order.valueobject.OrderStatus;
import com.golden.erp.domain.product.entity.Product;
import com.golden.erp.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ExchangeRatePort exchangeRatePort;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Customer sampleCustomer;
    private Product sampleProduct;
    private Order sampleOrder;

    @BeforeEach
    void setUp() {
        sampleCustomer = Customer.builder()
                .id(1L)
                .nome("João Silva")
                .email("joao@email.com")
                .cpf("12345678901")
                .build();

        sampleProduct = Product.builder()
                .id(1L)
                .sku("SKU-001")
                .nome("Camiseta")
                .precoBruto(new BigDecimal("49.90"))
                .estoque(100)
                .estoqueMinimo(10)
                .ativo(true)
                .build();

        OrderItem item = OrderItem.builder()
                .id(1L)
                .produtoId(1L)
                .produtoNome("Camiseta")
                .quantidade(2)
                .precoUnitario(new BigDecimal("49.90"))
                .desconto(new BigDecimal("5.00"))
                .build();

        sampleOrder = Order.builder()
                .id(1L)
                .clienteId(1L)
                .clienteNome("João Silva")
                .status(OrderStatus.CREATED)
                .itens(new ArrayList<>(List.of(item)))
                .dataCriacao(LocalDateTime.now())
                .dataAtualizacao(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("Criar Pedido")
    class CreateOrder {

        @Test
        @DisplayName("Deve criar pedido com sucesso")
        void shouldCreateOrder() {
            CreateOrderRequest request = CreateOrderRequest.builder()
                    .clienteId(1L)
                    .itens(List.of(OrderItemRequest.builder()
                            .produtoId(1L)
                            .quantidade(2)
                            .desconto(new BigDecimal("5.00"))
                            .build()))
                    .build();

            when(customerRepository.findById(1L)).thenReturn(Optional.of(sampleCustomer));
            when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
            when(productRepository.save(any(Product.class))).thenReturn(sampleProduct);
            when(orderRepository.save(any(Order.class))).thenReturn(sampleOrder);

            OrderResponse response = orderService.create(request);

            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getClienteNome()).isEqualTo("João Silva");
            assertThat(response.getStatus()).isEqualTo("CREATED");
            verify(productRepository).save(any(Product.class));
            verify(orderRepository).save(any(Order.class));
        }

        @Test
        @DisplayName("Deve rejeitar pedido quando cliente não existe")
        void shouldThrowWhenCustomerNotFound() {
            CreateOrderRequest request = CreateOrderRequest.builder()
                    .clienteId(99L)
                    .itens(List.of(OrderItemRequest.builder()
                            .produtoId(1L)
                            .quantidade(1)
                            .build()))
                    .build();

            when(customerRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.create(request))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("Deve rejeitar pedido quando produto não existe")
        void shouldThrowWhenProductNotFound() {
            CreateOrderRequest request = CreateOrderRequest.builder()
                    .clienteId(1L)
                    .itens(List.of(OrderItemRequest.builder()
                            .produtoId(99L)
                            .quantidade(1)
                            .build()))
                    .build();

            when(customerRepository.findById(1L)).thenReturn(Optional.of(sampleCustomer));
            when(productRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.create(request))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("Deve rejeitar pedido quando estoque é insuficiente (422)")
        void shouldThrowWhenInsufficientStock() {
            Product lowStockProduct = Product.builder()
                    .id(1L)
                    .sku("SKU-001")
                    .nome("Camiseta")
                    .precoBruto(new BigDecimal("49.90"))
                    .estoque(1)
                    .estoqueMinimo(10)
                    .ativo(true)
                    .build();

            CreateOrderRequest request = CreateOrderRequest.builder()
                    .clienteId(1L)
                    .itens(List.of(OrderItemRequest.builder()
                            .produtoId(1L)
                            .quantidade(5)
                            .build()))
                    .build();

            when(customerRepository.findById(1L)).thenReturn(Optional.of(sampleCustomer));
            when(productRepository.findById(1L)).thenReturn(Optional.of(lowStockProduct));

            assertThatThrownBy(() -> orderService.create(request))
                    .isInstanceOf(InsufficientStockException.class);
        }

        @Test
        @DisplayName("Deve baixar estoque ao criar pedido")
        void shouldDecreaseStockOnCreate() {
            CreateOrderRequest request = CreateOrderRequest.builder()
                    .clienteId(1L)
                    .itens(List.of(OrderItemRequest.builder()
                            .produtoId(1L)
                            .quantidade(3)
                            .build()))
                    .build();

            when(customerRepository.findById(1L)).thenReturn(Optional.of(sampleCustomer));
            when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
            when(productRepository.save(any(Product.class))).thenReturn(sampleProduct);
            when(orderRepository.save(any(Order.class))).thenReturn(sampleOrder);

            orderService.create(request);

            assertThat(sampleProduct.getEstoque()).isEqualTo(97);
            verify(productRepository).save(sampleProduct);
        }
    }

    @Nested
    @DisplayName("Buscar Pedido")
    class FindOrder {

        @Test
        @DisplayName("Deve retornar pedido por ID")
        void shouldFindById() {
            when(orderRepository.findById(1L)).thenReturn(Optional.of(sampleOrder));

            OrderResponse response = orderService.findById(1L);

            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getStatus()).isEqualTo("CREATED");
        }

        @Test
        @DisplayName("Deve lançar exceção quando pedido não encontrado")
        void shouldThrowWhenNotFound() {
            when(orderRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.findById(99L))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("Deve retornar pedidos paginados por status")
        void shouldFindAllByStatus() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Order> page = new PageImpl<>(List.of(sampleOrder));
            when(orderRepository.findAll(OrderStatus.CREATED, null, pageable)).thenReturn(page);

            Page<OrderResponse> result = orderService.findAll(OrderStatus.CREATED, null, pageable);

            assertThat(result.getContent()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Pagar Pedido")
    class PayOrder {

        @Test
        @DisplayName("Deve pagar pedido CREATED com sucesso")
        void shouldPayCreatedOrder() {
            when(orderRepository.findById(1L)).thenReturn(Optional.of(sampleOrder));
            when(orderRepository.save(any(Order.class))).thenReturn(sampleOrder);

            OrderResponse response = orderService.pay(1L);

            assertThat(sampleOrder.getStatus()).isEqualTo(OrderStatus.PAID);
            verify(orderRepository).save(any(Order.class));
        }

        @Test
        @DisplayName("Deve rejeitar pagamento de pedido CANCELLED")
        void shouldThrowWhenPayingCancelledOrder() {
            sampleOrder.setStatus(OrderStatus.CANCELLED);
            when(orderRepository.findById(1L)).thenReturn(Optional.of(sampleOrder));

            assertThatThrownBy(() -> orderService.pay(1L))
                    .isInstanceOf(InvalidOrderStateException.class);
        }
    }

    @Nested
    @DisplayName("Cancelar Pedido")
    class CancelOrder {

        @Test
        @DisplayName("Deve cancelar pedido CREATED e devolver estoque")
        void shouldCancelAndRestoreStock() {
            when(orderRepository.findById(1L)).thenReturn(Optional.of(sampleOrder));
            when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
            when(productRepository.save(any(Product.class))).thenReturn(sampleProduct);
            when(orderRepository.save(any(Order.class))).thenReturn(sampleOrder);

            orderService.cancel(1L);

            assertThat(sampleOrder.getStatus()).isEqualTo(OrderStatus.CANCELLED);
            verify(productRepository).save(any(Product.class));
        }

        @Test
        @DisplayName("Deve rejeitar cancelamento de pedido PAID")
        void shouldThrowWhenCancellingPaidOrder() {
            sampleOrder.setStatus(OrderStatus.PAID);
            when(orderRepository.findById(1L)).thenReturn(Optional.of(sampleOrder));

            assertThatThrownBy(() -> orderService.cancel(1L))
                    .isInstanceOf(InvalidOrderStateException.class);
        }

        @Test
        @DisplayName("Deve rejeitar cancelamento de pedido já CANCELLED")
        void shouldThrowWhenCancellingAlreadyCancelled() {
            sampleOrder.setStatus(OrderStatus.CANCELLED);
            when(orderRepository.findById(1L)).thenReturn(Optional.of(sampleOrder));

            assertThatThrownBy(() -> orderService.cancel(1L))
                    .isInstanceOf(InvalidOrderStateException.class);
        }
    }

    @Nested
    @DisplayName("Câmbio USD")
    class UsdTotal {

        @Test
        @DisplayName("Deve retornar total em USD")
        void shouldReturnUsdTotal() {
            when(orderRepository.findById(1L)).thenReturn(Optional.of(sampleOrder));
            when(exchangeRatePort.getBrlToUsdRate()).thenReturn(new BigDecimal("0.200000"));

            OrderUsdResponse response = orderService.getUsdTotal(1L);

            assertThat(response.getOrderId()).isEqualTo(1L);
            assertThat(response.getExchangeRate()).isEqualByComparingTo(new BigDecimal("0.200000"));
            assertThat(response.getTotalUsd()).isNotNull();
        }

        @Test
        @DisplayName("Deve lançar exceção quando taxa indisponível")
        void shouldThrowWhenRateUnavailable() {
            when(orderRepository.findById(1L)).thenReturn(Optional.of(sampleOrder));
            when(exchangeRatePort.getBrlToUsdRate()).thenReturn(null);

            assertThatThrownBy(() -> orderService.getUsdTotal(1L))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("câmbio");
        }
    }

    @Nested
    @DisplayName("Cálculos de Totais")
    class TotalCalculations {

        @Test
        @DisplayName("Deve calcular subtotal, descontos e total corretamente")
        void shouldCalculateTotalsCorrectly() {
            OrderItem item = OrderItem.builder()
                    .produtoId(1L)
                    .produtoNome("Camiseta")
                    .quantidade(3)
                    .precoUnitario(new BigDecimal("100.00"))
                    .desconto(new BigDecimal("10.00"))
                    .build();

            assertThat(item.getSubtotal()).isEqualByComparingTo(new BigDecimal("300.00"));
            assertThat(item.getDescontoTotal()).isEqualByComparingTo(new BigDecimal("30.00"));
            assertThat(item.getTotal()).isEqualByComparingTo(new BigDecimal("270.00"));
        }

        @Test
        @DisplayName("Deve calcular totais do pedido corretamente")
        void shouldCalculateOrderTotalsCorrectly() {
            OrderItem item1 = OrderItem.builder()
                    .produtoId(1L)
                    .produtoNome("Produto A")
                    .quantidade(2)
                    .precoUnitario(new BigDecimal("50.00"))
                    .desconto(new BigDecimal("5.00"))
                    .build();

            OrderItem item2 = OrderItem.builder()
                    .produtoId(2L)
                    .produtoNome("Produto B")
                    .quantidade(1)
                    .precoUnitario(new BigDecimal("30.00"))
                    .desconto(null)
                    .build();

            Order order = Order.builder()
                    .itens(new ArrayList<>(List.of(item1, item2)))
                    .build();

            assertThat(order.getSubtotal()).isEqualByComparingTo(new BigDecimal("130.00"));
            assertThat(order.getDescontos()).isEqualByComparingTo(new BigDecimal("10.00"));
            assertThat(order.getTotal()).isEqualByComparingTo(new BigDecimal("120.00"));
        }

        @Test
        @DisplayName("Deve usar scale 2 com HALF_UP para casas decimais")
        void shouldUseCorrectScale() {
            OrderItem item = OrderItem.builder()
                    .produtoId(1L)
                    .produtoNome("Item")
                    .quantidade(3)
                    .precoUnitario(new BigDecimal("33.33"))
                    .desconto(new BigDecimal("1.11"))
                    .build();

            assertThat(item.getSubtotal().scale()).isEqualTo(2);
            assertThat(item.getDescontoTotal().scale()).isEqualTo(2);
            assertThat(item.getTotal().scale()).isEqualTo(2);
        }
    }
}
