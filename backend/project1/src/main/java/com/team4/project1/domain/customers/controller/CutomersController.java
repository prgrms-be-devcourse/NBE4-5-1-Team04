package com.team4.project1.domain.customers.controller;


import com.team4.project1.domain.customers.dto.CustomersDto;
import com.team4.project1.domain.customers.entity.Customers;
import com.team4.project1.domain.customers.service.CustomersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CutomersController {
    private final CustomersService customersService;

    @PostMapping
    public ResponseEntity<Customers> createCustomers(@RequestBody CustomersDto customersDto) {
        Customers customers = customersService.createCustomer(customersDto);
        return ResponseEntity.ok(customers);
    }

    @GetMapping
    public ResponseEntity<List<Customers>> getAllCustomers() {
        List<Customers> customers = customersService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }


    @GetMapping("/{id}")
    public ResponseEntity<CustomersDto> getCustomerById(@PathVariable Long id) {
        Optional<Customers> Customers = customersService.getCustomersById(id);

        return Customers.map(customers -> ResponseEntity.ok(new CustomersDto(customers))).orElse(ResponseEntity.notFound().build());
    }
}
