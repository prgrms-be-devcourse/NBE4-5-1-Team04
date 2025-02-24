package com.team4.project1.domain.customer.service;

import com.team4.project1.domain.customer.dto.CustomerDto;
import com.team4.project1.domain.customer.entity.Customer;
import com.team4.project1.domain.customer.repository.CustomerRepository;
import com.team4.project1.global.exception.CustomerNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
/**
 * 고객 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 * 이 클래스는 고객의 가입, 조회, 수정, 삭제 등의 기능을 제공합니다.
 */
 @Service
@Transactional
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    /**
     * 신규 고객을 등록합니다.
     *
     * @param username 고객의 사용자 이름
     * @param password 고객의 비밀번호
     * @param name 고객의 이름
     * @param email 고객의 이메일 주소
     * @return 저장된 고객 엔티티
     */
    public Customer join(String username, String password, String name, String email) {
        Customer customer = Customer.builder()
                .username(username)
                .password(password)
                .apiKey(username)
                .name(name)
                .email(email)
                .build();

        return customerRepository.save(customer);
    }
/**
 * 전체 고객 수를 조회합니다.
 * @return 고객의 총 수
 */
 public long count() {
        return customerRepository.count();
    }
/**
 * 사용자 이름을 기준으로 고객을 조회합니다.
 * @param username 고객의 사용자 이름
 * @return 해당 사용자 이름을 가진 고객을 Optional로 반환
 */
 public Optional<Customer> findByUsername(String username) {
        return customerRepository.findByUsername(username);
    }
    /**
     * API 키를 기준으로 고객을 조회합니다.
     *
     * @param apiKey 고객의 API 키
     * @return 해당 API 키를 가진 고객을 Optional로 반환
     */
    public Optional<Customer> findByApiKey(String apiKey) {
        return customerRepository.findByApiKey(apiKey);
    }

    /**
     * 고객 ID를 기준으로 고객을 조회합니다.
     *
     * @param id 고객의 고유 ID
     * @return 해당 ID를 가진 고객을 Optional로 반환하며, 없으면 예외를 발생시킴
     * @throws CustomerNotFoundException 고객을 찾을 수 없을 때 발생
     */
    public Optional<Customer> getCustomerById(Long id) {
        return Optional.ofNullable(customerRepository.findById(id))
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }
    /**
     * 모든 고객을 조회합니다.
     *
     * @return 전체 고객 목록
     */
    public List<Customer> getAllCustomers() {

        return customerRepository.findAll();
    }
    /**
     * 고객 정보를 업데이트합니다.
     *
     * @param id 고객의 고유 ID
     * @param customerDto 업데이트된 고객 정보
     * @return 업데이트된 고객 엔티티
     * @throws CustomerNotFoundException 고객을 찾을 수 없을 때 발생
     */
    public Customer updateCustomer(Long id, CustomerDto customerDto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
        customer.setName(customerDto.getName());
        customer.setEmail(customerDto.getEmail());
        return customer;
    }
}
