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
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final EmailService emailService;

    public Customer join(String username, String password, String name, String email) {
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

    public long count() {
        return customerRepository.count();
    }

    public Optional<Customer> findByUsername(String username) {
        return customerRepository.findByUsername(username);
    }

    public Optional<Customer> findByApiKey(String apiKey) {
        return customerRepository.findByApiKey(apiKey);
    }

    public Optional<Customer> getCustomerById(Long id) {
        return Optional.ofNullable(customerRepository.findById(id)).orElseThrow(() -> new CustomerNotFoundException(id));
    }

    public List<Customer> getAllCustomers() {

        return customerRepository.findAll();
    }

    public Customer updateCustomer(Long id, CustomerDto customerDto) {
        Customer customer = customerRepository.findById(id).orElseThrow(() -> new CustomerNotFoundException(id));
        customer.setName(customerDto.getName());
        customer.setEmail(customerDto.getEmail());
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
