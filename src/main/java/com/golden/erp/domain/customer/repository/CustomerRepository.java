package com.golden.erp.domain.customer.repository;

import com.golden.erp.domain.customer.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CustomerRepository {

    Customer save(Customer customer);

    Optional<Customer> findById(Long id);

    Page<Customer> findAll(String nome, String email, Pageable pageable);

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByCpfAndIdNot(String cpf, Long id);

    void deleteById(Long id);

    boolean existsById(Long id);
}
