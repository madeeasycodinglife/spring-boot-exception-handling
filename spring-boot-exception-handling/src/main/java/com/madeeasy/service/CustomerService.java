package com.madeeasy.service;

import com.madeeasy.controller.CustomerController;
import com.madeeasy.entity.Customer;
import com.madeeasy.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MissingPathVariableException;

import java.util.List;

@Service
@Transactional(isolation = Isolation.READ_COMMITTED,
        propagation = Propagation.REQUIRED,
        readOnly = true)
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED,
            propagation = Propagation.REQUIRED,
            readOnly = false)
    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public Customer getCustomerById(Long customerId) throws MissingPathVariableException, NoSuchMethodException {
        if (String.valueOf(customerId).isBlank()){
            MethodParameter methodParameter = MethodParameter
                    .forExecutable(CustomerService.class.getMethod("getCustomerById", new Class[0]),//Long.class or new Class[0] { new Class[0] ==  Class<?>... parameterTypes}
                    0); // here 0 is parameter index i.e. it will go like 0,1,2,3.... and so on.
            throw new MissingPathVariableException("customerRepository", methodParameter);
        }
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found: " + customerId));
        //throw new ErrorResponseException("Customer not found", HttpStatus.NOT_FOUND);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED,
            propagation = Propagation.REQUIRED,
            readOnly = false)
    public Customer updateCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public void deleteCustomer(Long customerId) {
        customerRepository.deleteById(customerId);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerByIdWithRequestParameter(Long customerId) {
        return customerRepository.findById(customerId).get();
    }
}
