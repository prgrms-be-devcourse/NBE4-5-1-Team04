package com.team4.project1.domain.customer.controller;


import com.team4.project1.domain.customer.dto.CustomerDto;
import com.team4.project1.domain.customer.entity.Customer;
import com.team4.project1.domain.customer.service.CustomerService;
import com.team4.project1.global.exception.CustomerNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerervice;
    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody CustomerDto customerDto) {
        Customer customer = customerervice.createCustomer(customerDto);
        return ResponseEntity.ok(customer);
    }

    @GetMapping
    public ResponseEntity<List<Customer>> getAllcustomer() {
        List<Customer> customer = customerervice.getAllcustomer();
        return ResponseEntity.ok(customer);
    }


    @GetMapping("/{id}")
    public ResponseEntity<CustomerDto> getCustomerById(@PathVariable Long id) {
        Optional<Customer> Opcustomer = customerervice.getcustomerById(id);

        return Opcustomer.map(customer -> ResponseEntity.ok(new CustomerDto(customer))).orElseThrow(() -> new CustomerNotFoundException(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerDto> updateCustomer(@PathVariable Long id, @RequestBody CustomerDto customerDto) {
        Customer updated = customerService.updateCustomer(id,customerDto);
        return ResponseEntity.ok(new CustomerDto(updated));
    }
}
