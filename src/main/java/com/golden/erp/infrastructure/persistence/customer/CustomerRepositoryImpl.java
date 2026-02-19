package com.golden.erp.infrastructure.persistence.customer;

import com.golden.erp.domain.customer.entity.Customer;
import com.golden.erp.domain.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CustomerRepositoryImpl implements CustomerRepository {

    private final CustomerJpaRepository jpaRepository;
    private final CustomerMapper mapper;

    @Override
    public Customer save(Customer customer) {
        CustomerJpaEntity entity = mapper.toJpaEntity(customer);
        CustomerJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Customer> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Page<Customer> findAll(String nome, String email, Pageable pageable) {
        return jpaRepository.findAllWithFilters(nome, email, pageable)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByCpf(String cpf) {
        return jpaRepository.existsByCpf(cpf);
    }

    @Override
    public boolean existsByEmailAndIdNot(String email, Long id) {
        return jpaRepository.existsByEmailAndIdNot(email, id);
    }

    @Override
    public boolean existsByCpfAndIdNot(String cpf, Long id) {
        return jpaRepository.existsByCpfAndIdNot(cpf, id);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }
}
