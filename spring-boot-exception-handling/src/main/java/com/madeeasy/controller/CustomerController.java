package com.madeeasy.controller;

import com.madeeasy.entity.Customer;
import com.madeeasy.entity.SomeObject;
import com.madeeasy.service.CustomerService;
import jakarta.validation.constraints.Min;
import jakarta.validation.executable.ValidateOnExecution;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
@Validated
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        Customer createdCustomer = customerService.createCustomer(customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer);
    }

    /**
     * this logic i have written to throw MissingPathVariableException
     * one more example to throw {@link MissingPathVariableException} is given below
     *
     * @GetMapping(value = {"getId///", "/getId/{participantId}/{walletType}"})
     */
    @GetMapping(value = {"with/", "with/{customerId}"})
    public ResponseEntity<Customer> getCustomerById(@PathVariable @Min(1)
                                                    Long customerId) throws NoSuchMethodException, MissingPathVariableException {
        Customer customer = customerService.getCustomerById(customerId);
        return ResponseEntity.ok(customer);
    }

    @GetMapping(value = "with/request-param")
    public ResponseEntity<Customer> getCustomerByIdWithRequestParameter(@RequestParam Long customerId) {
        Customer customer = customerService.getCustomerByIdWithRequestParameter(customerId);
        return ResponseEntity.ok(customer);
    }

    @GetMapping("/get-all-customers")
    public ResponseEntity<List<Customer>> getAllCustomersd() {
        List<Customer> customer = customerService.getAllCustomers();
        return ResponseEntity.ok(customer);
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable @Min(1) Long customerId, @RequestBody Customer customer) {
        customer.setId(customerId);
        Customer updatedCustomer = customerService.updateCustomer(customer);
        return ResponseEntity.ok(updatedCustomer);
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long customerId) {
        customerService.deleteCustomer(customerId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/serialize")
    public ResponseEntity<Object> getExample() {
        // Assume there is an error while serializing the response object
        SomeObject object = new SomeObject();
        return ResponseEntity.ok(object);
    }

    @GetMapping("/example")
    public ResponseEntity<String> exampleMethod(@RequestParam("param") int paramValue) {
        // Method implementation
        return ResponseEntity.ok("message sent : " + paramValue);
    }

    @GetMapping("/example/{id}")
    public ResponseEntity<String> exampleMethod( @PathVariable("id") Long id) {
        // Method implementation
        return ResponseEntity.ok("message sent : " + id);
    }
}
