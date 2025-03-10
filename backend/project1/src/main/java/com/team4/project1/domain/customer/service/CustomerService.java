package com.team4.project1.domain.customer.service;

import com.team4.project1.domain.customer.dto.CustomerDto;
import com.team4.project1.domain.customer.entity.Customer;
import com.team4.project1.domain.customer.repository.CustomerRepository;
import com.team4.project1.global.exception.CustomerNotFoundException;
import com.team4.project1.global.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

/**
 * {@Link Customer}엔티티에 대한 비즈니스 로직을 처리하는 서비스 클래스입니다.
 * 고객 가입, 조회, 업데이트 등의 기능을 제공합니다.
 *
 */
@Service
@Transactional
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final EmailService emailService;

    /**
     * 새로운 고객을 가입시키는 메서드입니다.
     * @param username 고객의 사용자 이름
     * @param password 고객의 비밀번호
     * @param name 고객의 이름
     * @param email 고객의 이메일
     * @return 새로 생성된 {@Link Customer} 객체
     */
    public Customer join(String username, String password,
                         String name, String email) {
        String apiKey = generateTrimmedBase64StringFromUuid(UUID.randomUUID());
        // 1) Customer 엔티티 생성
        Customer customer = Customer.builder().username(username).password(password).apiKey(apiKey)
                .name(name).email(email).build();

        // 2) DB에 저장
        Customer savedCustomer = customerRepository.save(customer);

        // 3) 이메일 발송
        String subject = "회원가입을 환영합니다!";
        String text = String.format("안녕하세요, %s 님!\n" + "서비스를 이용해주셔서 감사합니다.\n" + "회원가입이 성공적으로 완료되었습니다.", savedCustomer.getName());
        emailService.sendSimpleEmail(savedCustomer.getEmail(), subject, text);

        return savedCustomer;
    }

    /**
     * 전체 고객 수를 반환하는 메서드입니다.
     * @return DB에 저장된 고객의 총 수를 반환합니다.
     */
    public long count() {
        return customerRepository.count();
    }

    /**
     * 사용자 이름을 통해 고객을 조회하는 메서드입니다.
     * @param username 조회할 고객의 사용자 이름
     * @return {@Link Customer}객체, 해당 사용자 이름을 가진 고객이 있으면 반환합니다.
     */
    public Customer findByUsername(String username) {
        return customerRepository.findByUsername(username).orElseThrow(() -> new CustomerNotFoundException(username));
    }
  
    /**
     * API 키를 통해 고객을 조회하는 메서드입니다.
     * @param apiKey 조회할 고객의 API키
     * @return {@Link Customer} 객체, 해당 API 키를 가진 고객이 있으면 반환합니다.
     */
    public Customer findByApiKey(String apiKey) {
        return customerRepository.findByApiKey(apiKey).orElseThrow(() -> new CustomerNotFoundException(apiKey));

    }
  
    /**
     * 고객 ID를 통해 고객의 정보를 조회하는 메서드입니다.
     * @param id 조회할 고객의 ID
     * @return  {@Link Customer} 객체, 해당 ID를 가진 고객이 있으면 반환합니다.
     * @throws CustomerNotFoundException 고객이 존재하지 않는 경우에 예외 발생합니다.
     */
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id).orElseThrow(() -> new CustomerNotFoundException(id));
    }

    public Customer getCustomerByIdWithNewApiKey(Long id) {
        Customer customer = customerRepository.findById(id).orElseThrow(() -> new CustomerNotFoundException(id));
        customer.setApiKey(generateTrimmedBase64StringFromUuid(UUID.randomUUID()));
        return customer;
    }

    /**
     * 모든 고객 정보를 반환하는 메서드입니다.
     * @return 모든 고객을 담고 있는 {@Link List<Customer>}객체를 반환합니다.
     */
    public List<CustomerDto> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(CustomerDto::from)
                .toList();
    }

    /**
     * 고객의 정보를 업데이트하는 메서드입니다.
     * @param id 고객의 ID
     * @param customerDto 업데이트할 고객의 정보 DTO
     * @return 업데이트된 {@Link Customer} 객체를 반환합니다.
     * @throws CustomerNotFoundException 고객이 존재하지 않는 경우에 예외 발생합니다.
     */
    public Customer updateCustomer(Long id, CustomerDto customerDto) {
        Customer customer = customerRepository.findById(id).orElseThrow(() -> new CustomerNotFoundException(id));
        customer.setName(customerDto.getName());
        customer.setEmail(customerDto.getEmail());
        customer.setPassword(customerDto.getPassword());
        return customer;
    }

    /**
     * UUIDv4를 22글자 길이의 base64 인코딩된 문자열로 변환한다. UUIDv4가 아닌 UUID가 주어진 경우, 빈 문자열을 반환한다.
     *
     * @param uuid 변환하고자 하는 UUIDv4가 담긴 UUID 오브젝트
     * @return 주어진 UUIDv4에 해당하는 22글자 길이의 base64 인코딩 문자열 또는 빈 문자열
     */
    private String generateTrimmedBase64StringFromUuid(UUID uuid) {
        if (uuid.version() != 4) {
            return "";
        }
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getLeastSignificantBits());
        bb.putLong(uuid.getMostSignificantBits());
        return Base64.getEncoder().encodeToString(bb.array()).substring(0, 22);
    }
}
