package com.team4.project1.domain.customer.controller;

import com.team4.project1.domain.customer.dto.CustomerDto;
import com.team4.project1.domain.customer.entity.Customer;
import com.team4.project1.domain.customer.service.CustomerService;
import com.team4.project1.global.dto.ResponseDto;
import com.team4.project1.global.exception.CustomerNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "ApiV1CustomerController", description = "회원 관련 API")
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
    ) {
    }

    @Operation(summary = "회원 가입")
    @PostMapping
    public ResponseEntity<ResponseDto<CustomerDto>> join(@RequestBody @Valid JoinReqBody reqBody) {
        CustomerDto existingCustomer = CustomerDto.from(customerService.findByUsername(reqBody.username()));

        if (existingCustomer != null) {
            throw new IllegalStateException("Username already exists");
        }

        Customer customer = customerService.join(
                reqBody.username(),
                reqBody.password(),
                reqBody.name(),
                reqBody.email()
        );

        CustomerDto customerDto = CustomerDto.from(customer);
        return ResponseEntity.ok(ResponseDto.ok(customerDto));
    }


    record LoginReqBody(
            @NotBlank String username,
            @NotBlank String password
    ) {
    }

    record LoginResBody(
            CustomerDto item,
            String apiKey
    ) {
    }

    @Operation(summary = "로그인", description = "로그인 성공 시 ApiKey와 DTO 반환")
    @PostMapping("/login")
    public ResponseEntity<LoginResBody> login(@RequestBody @Valid LoginReqBody reqBody) {
        CustomerDto customerDto = CustomerDto.from(customerService.findByUsername(reqBody.username()));
        if (customerDto == null) {
            new IllegalArgumentException("잘못된 아이디 입니다.");
        }

        Customer customer = customerService.getCustomerById(customerDto.getId());
        if (!customer.getPassword().equals(reqBody.password())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return ResponseEntity.ok(
                new LoginResBody(
                        CustomerDto.from(customer),
                        customer.getApiKey()
                )
        );
    }

    @Operation(summary = "전체 회원 정보 조회")
    @GetMapping
    public ResponseEntity<ResponseDto<List<Customer>>> getAllCustomers() {
        List<Customer> customer = customerService.getAllCustomers();
        return ResponseEntity.ok(ResponseDto.ok(customer));
    }

    @Operation(summary = "내 정보 조회")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<CustomerDto>> getCustomerById(@PathVariable Long id) {
        Customer customer = customerService.getCustomerById(id);

        return ResponseEntity.ok(ResponseDto.ok(CustomerDto.from(customer)));
    }

    @Operation(summary = "내 정보 수정")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto<CustomerDto>> updateCustomer(@PathVariable Long id, @RequestBody CustomerDto customerDto) {
        Customer updated = customerService.updateCustomer(id, customerDto);
        return ResponseEntity.ok(ResponseDto.ok(CustomerDto.from(updated)));
    }
}
