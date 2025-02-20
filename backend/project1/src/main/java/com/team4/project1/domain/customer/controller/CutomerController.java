package com.team4.project1.domain.customer.controller;


import com.team4.project1.domain.customer.dto.CustomerDto;
import com.team4.project1.domain.customer.entity.Customer;
import com.team4.project1.domain.customer.service.CustomerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
public class CutomerController {

    private final CustomerService customerervice;

    record JoinReqBody(
            @NotBlank String username,
            @NotBlank String password,
            @NotBlank String name,
            @NotBlank String email
    ) {
    }

    @PostMapping
    public ResponseEntity<CustomerDto> join(@RequestBody @Valid JoinReqBody reqBody) {

        customerervice.findByUsername(reqBody.username())
                .ifPresent(existingCustomer -> {
                    throw new IllegalStateException("Username already exists");
                });

        Customer customer = customerervice.join(reqBody.username(), reqBody.password(), reqBody.name(), reqBody.email());
        CustomerDto customerDto = new CustomerDto(customer);

        return ResponseEntity.ok(customerDto);
    }

//    @GetMapping
//    public ResponseEntity<List<Customer>> getAllcustomer() {
//        List<Customer> customer = customerervice.getAllcustomer();
//        return ResponseEntity.ok(customer);
//    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDto> getCustomerById(@PathVariable Long id) {
        Optional<Customer> Opcustomer = customerervice.getcustomerById(id);

        return Opcustomer.map(
                customer -> ResponseEntity.ok(
                        new CustomerDto(customer))).orElse(ResponseEntity.notFound().build()
        );
    }

}
