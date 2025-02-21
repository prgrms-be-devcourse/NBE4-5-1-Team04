package com.team4.project1.domain.customer.controller;

import com.team4.project1.domain.customer.dto.CustomerDto;
import com.team4.project1.domain.customer.entity.Customer;
import com.team4.project1.domain.customer.service.CustomerService;
import com.team4.project1.global.dto.ResponseDto;
import com.team4.project1.global.exception.CustomerNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
public class ApiV1CustomerController {

    private final CustomerService customerService;

    record JoinReqBody(
            @NotBlank String username,
            @NotBlank String password,
            @NotBlank String name,
            @NotBlank String email
    ) {}

    @PostMapping
    public ResponseEntity<ResponseDto<CustomerDto>> join(@RequestBody @Valid JoinReqBody reqBody) {
        customerService.findByUsername(reqBody.username())
                .ifPresent(existingCustomer -> {
                    throw new IllegalStateException("Username already exists");
                });

        Customer customer = customerService.join(
                reqBody.username(),
                reqBody.password(),
                reqBody.name(),
                reqBody.email()
        );

        CustomerDto customerDto = new CustomerDto(customer);

        return ResponseEntity.ok(ResponseDto.ok(customerDto));
    }

    record LoginReqBody(
            @NotBlank String username,
            @NotBlank String password
    ) {}

    record LoginResBody(
            CustomerDto item,
            String apiKey
    ) {}

    @PostMapping("/login")
    public ResponseEntity<LoginResBody> login(@RequestBody @Valid LoginReqBody reqBody) {
        Customer customer = customerService.findByUsername(reqBody.username()).orElseThrow(
                () -> new IllegalArgumentException("잘못된 아이디 입니다.")
        );

        if (!customer.getPassword().equals(reqBody.password())) {
            throw new IllegalArgumentException ("비밀번호가 일치하지 않습니다.");
        }

        return ResponseEntity.ok(
                new LoginResBody(
                        new CustomerDto(customer),
                        customer.getApiKey()
                )
        );
    }

    @GetMapping
    public ResponseEntity<ResponseDto<List<Customer>>> getAllCustomers() {
        List<Customer> customer = customerService.getAllCustomers();
        return ResponseEntity.ok(ResponseDto.ok(customer));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<CustomerDto>> getCustomerById(@PathVariable Long id) {
        Optional<Customer> opCustomer = customerService.getCustomerById(id);

        return opCustomer
                .map(customer -> ResponseEntity.ok(ResponseDto.ok(new CustomerDto(customer))))
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto<CustomerDto>> updateCustomer(@PathVariable Long id, @RequestBody CustomerDto customerDto) {
        Customer updated = customerService.updateCustomer(id, customerDto);
        return ResponseEntity.ok(ResponseDto.ok(new CustomerDto(updated)));
    }
}
