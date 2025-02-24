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

/**
 * 고객 관련 API를 처리하는 컨트롤러입니다.
 * 회원가입, 로그인, 고객 정보 조회 및 수정 기능을 제공합니다
 * */
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


/**
 * 회원가입 요청을 처리하는 메서드입니다.
 * 이미 존재하는 사용자 이름으로 가입을 시도하면 예외가 발생합니다.
 * */
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

        CustomerDto customerDto = CustomerDto.from(customer);

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


/**
 * 로그인 요청을 처리하는 메서드입니다.
 * 사용자가 입력한 아이디와 비밀번호가 일치하는지 확인 후, 로그인된 고객의 정보와 API 키를 반환합니다.
 * */
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
                        CustomerDto.from(customer),
                        customer.getApiKey()
                )
        );
    }
/**
 * 모든 고객의 정보를 조회하는 메서드입니다.
 */
 @GetMapping
    public ResponseEntity<ResponseDto<List<Customer>>> getAllCustomers() {
        List<Customer> customer = customerService.getAllCustomers();
        return ResponseEntity.ok(ResponseDto.ok(customer));
    }
/**
 * 특정 고객 ID에 해당하는 고객 정보를 조회하는 메서드입니다.
 * 고객이 존재하지 않을 경우 예외를 발생시킵니다.
 */
 @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<CustomerDto>> getCustomerById(@PathVariable Long id) {
        Optional<Customer> opCustomer = customerService.getCustomerById(id);

        return opCustomer
                .map(customer -> ResponseEntity.ok(ResponseDto.ok(CustomerDto.from(customer))))
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }
/**
 * 고객 정보를 수정하는 메서드입니다.
 * 주어진 ID에 해당하는 고객 정보를 업데이트합니다.
 */
 @PutMapping("/{id}")
    public ResponseEntity<ResponseDto<CustomerDto>> updateCustomer(@PathVariable Long id, @RequestBody CustomerDto customerDto) {
        Customer updated = customerService.updateCustomer(id, customerDto);
        return ResponseEntity.ok(ResponseDto.ok(CustomerDto.from(updated)));
    }
}
