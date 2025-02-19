package com.team4.project1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team4.project1.domain.customers.repository.CustomersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class customersTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // 각 테스트 시작 전, DB 비우기 (Customers 테이블)
        customersRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /api/v1/customers - 새로운 고객 생성")
    void testCreateCustomer() throws Exception {
        // given
        // 고객 생성 요청에 필요한 JSON (id는 서버에서 자동 생성)
        String requestBody = """
            {
                "id": 1,
                "name": "홍길동",
                "email": "hong@example.com"
            }
        """;

        // when & then
        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name").value("홍길동"))
                .andExpect(jsonPath("$.email").value("hong@example.com"));

        // DB에 잘 들어갔는지 확인 (선택 사항)
        long count = customersRepository.count();
        // 실제로는 테스트 코드에서 assert 로 검증
        System.out.println("생성된 고객 레코드 수: " + count);
    }

}
