# Mini-ERP de Pedidos - Desafio Golden

API REST de um mini-ERP para gerenciamento de clientes, produtos e pedidos.

## Arquitetura

O projeto segue **Clean Architecture** com **Domain-Driven Design (DDD)**, organizando o código em camadas com responsabilidades bem definidas e fluxo de dependência de fora para dentro.

### Por que Clean Architecture + DDD?

- **Independência de framework**: o domínio não conhece Spring, JPA ou qualquer biblioteca externa. Isso torna a lógica de negócio testável e portátil.
- **Facilidade de teste**: serviços de aplicação dependem de interfaces (ports), não de implementações concretas. Mocks são triviais.
- **Manutenibilidade**: cada camada tem uma responsabilidade clara. Mudanças em infraestrutura (trocar banco, trocar API de CEP) não afetam a lógica de negócio.
- **Organização por domínio**: código agrupado por contexto de negócio (Customer, Product, Order), não por tipo técnico.

### Estrutura de Camadas

```
src/main/java/com/golden/erp/
├── domain/                      # Núcleo - Entidades, Value Objects, Repository Interfaces
│   ├── customer/
│   ├── product/
│   ├── order/
│   └── exception/
├── application/                 # Casos de Uso - Services, DTOs, Ports
│   ├── customer/
│   ├── product/
│   ├── order/
│   └── scheduler/
├── infrastructure/              # Adapters - JPA, Feign, Configs
│   ├── persistence/
│   └── client/
└── presentation/                # API REST - Controllers, Exception Handler
    ├── controller/
    ├── handler/
    └── dto/
```

| Camada | Depende de | Responsabilidade |
|--------|-----------|-----------------|
| **Domain** | Nada | Entidades puras, regras de negócio, interfaces de repositório |
| **Application** | Domain | Orquestração de use cases, DTOs, ports para integrações |
| **Infrastructure** | Application, Domain | Implementações de repositórios (JPA), clientes HTTP (Feign) |
| **Presentation** | Application | Controllers REST, tratamento de exceções, validação |

## Tecnologias

- **Java 21** + **Spring Boot 3.4**
- **Spring Data JPA** + **PostgreSQL 16**
- **Liquibase** para migrações de banco
- **Spring Cloud OpenFeign** para integrações externas (ViaCEP, câmbio)
- **Bean Validation** (Hibernate Validator)
- **Lombok**
- **JUnit 5** + **Mockito** + **AssertJ** para testes
- **JaCoCo** para cobertura de testes
- **Docker** + **Docker Compose**

## Como Executar

### Pré-requisitos

- Docker e Docker Compose instalados

### Execução

```bash
docker compose up --build
```

A aplicação estará disponível em `http://localhost:8080`.

### Executar Testes

```bash
./mvnw test
```

## Endpoints da API

### Clientes (`/api/customers`)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/customers` | Criar cliente |
| GET | `/api/customers/{id}` | Buscar por ID |
| GET | `/api/customers?nome=&email=` | Listar com filtros e paginação |
| PUT | `/api/customers/{id}` | Atualizar cliente |
| DELETE | `/api/customers/{id}` | Remover cliente |

### Produtos (`/api/products`)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/products` | Criar produto |
| GET | `/api/products/{id}` | Buscar por ID |
| GET | `/api/products?ativo=true` | Listar com filtros e paginação |
| PUT | `/api/products/{id}` | Atualizar produto |
| DELETE | `/api/products/{id}` | Remover produto |

### Pedidos (`/api/orders`)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/orders` | Criar pedido |
| GET | `/api/orders/{id}` | Buscar por ID |
| GET | `/api/orders?status=&clienteId=` | Listar com filtros e paginação |
| PATCH | `/api/orders/{id}/pay` | Pagar pedido |
| PATCH | `/api/orders/{id}/cancel` | Cancelar pedido |
| GET | `/api/orders/{id}/usd-total` | Total em USD (opcional) |

## Exemplos de Requisições

### Criar Cliente

```json
POST /api/customers
{
  "nome": "João Silva",
  "email": "joao@email.com",
  "cpf": "123.456.789-09",
  "endereco": {
    "cep": "01001000",
    "numero": "100"
  }
}
```

O sistema enriquece automaticamente logradouro, bairro, cidade e UF via ViaCEP.

### Criar Produto

```json
POST /api/products
{
  "sku": "CAM-001",
  "nome": "Camiseta Básica",
  "precoBruto": 49.90,
  "estoque": 100,
  "estoqueMinimo": 10
}
```

### Criar Pedido

```json
POST /api/orders
{
  "clienteId": 1,
  "itens": [
    {
      "produtoId": 1,
      "quantidade": 2,
      "desconto": 5.00
    }
  ]
}
```

## Tarefas Agendadas

| Tarefa | Frequência | Descrição |
|--------|-----------|-----------|
| Pedidos Atrasados | A cada hora | Pedidos CREATED com +48h viram LATE |
| Estoque Baixo | Diário às 03:00 | Registra em log produtos com estoque < mínimo |

## Decisões Técnicas

- **BigDecimal com scale(2, HALF_UP)**: garante confiabilidade nas casas decimais para cálculos financeiros.
- **Entidades JPA separadas das entidades de domínio**: o domínio não tem anotações JPA, mantendo-se puro. Mappers fazem a conversão.
- **Ports & Adapters**: `AddressLookupPort` e `ExchangeRatePort` desacoplam integrações externas da lógica de negócio.
- **Liquibase**: migrações versionadas e reproduzíveis para o banco de dados.
- **Multi-stage Dockerfile**: imagem final leve usando JRE Alpine.
