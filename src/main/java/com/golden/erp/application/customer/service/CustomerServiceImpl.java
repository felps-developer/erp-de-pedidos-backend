package com.golden.erp.application.customer.service;

import com.golden.erp.application.customer.dto.AddressRequest;
import com.golden.erp.application.customer.dto.CreateCustomerRequest;
import com.golden.erp.application.customer.dto.CustomerResponse;
import com.golden.erp.application.customer.dto.UpdateCustomerRequest;
import com.golden.erp.application.customer.port.AddressLookupPort;
import com.golden.erp.domain.customer.entity.Customer;
import com.golden.erp.domain.customer.repository.CustomerRepository;
import com.golden.erp.domain.customer.valueobject.Address;
import com.golden.erp.domain.exception.DuplicateFieldException;
import com.golden.erp.domain.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl {

    private final CustomerRepository customerRepository;
    private final AddressLookupPort addressLookupPort;

    public CustomerResponse create(CreateCustomerRequest request) {
        validateUniqueFields(request.getEmail(), request.getCpf(), null);

        Address address = buildAddress(request.getEndereco());
        address = enrichAddressIfNeeded(address);

        Customer customer = Customer.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .cpf(request.getCpf())
                .endereco(address)
                .build();

        Customer saved = customerRepository.save(customer);
        log.info("Cliente criado com id: {}", saved.getId());
        return CustomerResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public CustomerResponse findById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente", id));
        return CustomerResponse.from(customer);
    }

    @Transactional(readOnly = true)
    public Page<CustomerResponse> findAll(String nome, String email, Pageable pageable) {
        return customerRepository.findAll(nome, email, pageable)
                .map(CustomerResponse::from);
    }

    public CustomerResponse update(Long id, UpdateCustomerRequest request) {
        Customer existing = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente", id));

        validateUniqueFields(request.getEmail(), request.getCpf(), id);

        Address address = buildAddress(request.getEndereco());
        address = enrichAddressIfNeeded(address);

        existing.setNome(request.getNome());
        existing.setEmail(request.getEmail());
        existing.setCpf(request.getCpf());
        existing.setEndereco(address);

        Customer saved = customerRepository.save(existing);
        log.info("Cliente atualizado com id: {}", saved.getId());
        return CustomerResponse.from(saved);
    }

    public void delete(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new EntityNotFoundException("Cliente", id);
        }
        customerRepository.deleteById(id);
        log.info("Cliente removido com id: {}", id);
    }

    private void validateUniqueFields(String email, String cpf, Long excludeId) {
        if (excludeId == null) {
            if (customerRepository.existsByEmail(email)) {
                throw new DuplicateFieldException("email", email);
            }
            if (customerRepository.existsByCpf(cpf)) {
                throw new DuplicateFieldException("cpf", cpf);
            }
        } else {
            if (customerRepository.existsByEmailAndIdNot(email, excludeId)) {
                throw new DuplicateFieldException("email", email);
            }
            if (customerRepository.existsByCpfAndIdNot(cpf, excludeId)) {
                throw new DuplicateFieldException("cpf", cpf);
            }
        }
    }

    private Address buildAddress(AddressRequest request) {
        if (request == null) return null;

        return Address.builder()
                .logradouro(request.getLogradouro())
                .numero(request.getNumero())
                .complemento(request.getComplemento())
                .bairro(request.getBairro())
                .cidade(request.getCidade())
                .uf(request.getUf())
                .cep(request.getCep())
                .build();
    }

    private Address enrichAddressIfNeeded(Address address) {
        if (address == null || !address.needsEnrichment()) {
            return address;
        }

        Address viaCepAddress = addressLookupPort.lookup(address.getCep());
        log.info("Endereço enriquecido via CEP {}", address.getCep());
        return address.enrichWith(
                viaCepAddress.getLogradouro(),
                viaCepAddress.getBairro(),
                viaCepAddress.getCidade(),
                viaCepAddress.getUf()
        );
    }
}
