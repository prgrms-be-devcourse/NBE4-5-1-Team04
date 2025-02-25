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



/**
 * 고객 관련 API를 처리하는 컨트롤러입니다.
 * 회원가입, 로그인, 고객 정보 조회 ,수정 기능을 제공을하고 있습니다.
 */
@Tag(name = "ApiV1CustomerController", description = "회원 관련 API")
@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
public class ApiV1CustomerController {

    private final CustomerService customerService;

    /**
     * 회원가입 요청을 위한 DTO(Data Transfer Object)입니다.
     * @param username
     * @param password
     * @param name
     * @param email
     */
    record JoinReqBody(
            @NotBlank String username,
            @NotBlank String password,
            @NotBlank String name,
            @NotBlank String email
    ) {
    }

    /**
     *  회원가입을 처리하는 메서드입니다.
     * @param reqBody 회원가입 요청 데이터를 나타낸다
     * @return 생성된 고객 정보를 return합니다.
     */
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

    /**
     * 로그인 요청을 위한 DTO(Data Transfer Object)입니다.
     * @param username
     * @param password
     */
    record LoginReqBody(
            @NotBlank String username,
            @NotBlank String password
    ) {
    }

    /**
     * 로그인 응답을 위한 DTo(Data Transfer Object)입니다.
     * @param item
     * @param apiKey
     */
    record LoginResBody(
            CustomerDto item,
            String apiKey
    ) {
    }

    /**
     * 로그인 요청을 처리하는 메서드입니다.
     * 사용자가 입력한 username과 password가 일치하는 확인후.
     * 로그인 된 고객 정보와 API 키를 반환합니다.
     * @param reqBody
     * @return
     */
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

    /**
     * 모든 고객의 정보를 조회하는 메서드입니다.
     * @return 고객 목록을 반환합니다.
     */
    @Operation(summary = "전체 회원 정보 조회")
    @GetMapping
    public ResponseEntity<ResponseDto<List<Customer>>> getAllCustomers() {
        List<Customer> customer = customerService.getAllCustomers();
        return ResponseEntity.ok(ResponseDto.ok(customer));
    }

    /**
     *
     * @param id 조회할 고객의 ID
     * @return 특정 고객의 정보를 반환합니다.
     * @throws CustomerNotFoundException 고객을 찾을 수 없는 경우,
     * "해당 고객을 찾을 수 없습니다. (ID: id) 예외처리합니다.
     */
    @Operation(summary = "내 정보 조회")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<CustomerDto>> getCustomerById(@PathVariable Long id) {
        Customer customer = customerService.getCustomerById(id);

        return ResponseEntity.ok(ResponseDto.ok(CustomerDto.from(customer)));
    }

    /**
     * 고객 정보를 수정하는 메서드입니다.
     * 주어진 ID에 해당하는 고객 정보를 업데이트 합니다
     * 수정된 고객 정보는 CustomerDto 형태로 반환됩니다.
     *
     * @param id 고객 ID
     * @param customerDto 업데이트할 고객 정보가 담긴 DTO
     * @return 수정된 고객 정보가 담긴 ResponseDto 객체,
     * 반환된 응답의 본문에는 업데이트된 고객 정보가 포함된 `CustomerDto`가 포함됩니다.
     */
    @Operation(summary = "내 정보 수정")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto<CustomerDto>> updateCustomer(@PathVariable Long id, @RequestBody CustomerDto customerDto) {
        Customer updated = customerService.updateCustomer(id, customerDto);
        return ResponseEntity.ok(ResponseDto.ok(CustomerDto.from(updated)));
    }
}
