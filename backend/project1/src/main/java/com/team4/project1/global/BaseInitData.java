package com.team4.project1.global;

import com.team4.project1.domain.item.service.ItemService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BaseInitData {

    private final ItemService itemService;

    @Bean
    public ApplicationRunner applicationRunner() {
        return args -> itemInit();
    }

    @Transactional
    public void itemInit(){

        if (itemService.count() > 0) {
            return;
        }

        itemService.addItem("스타벅스커피",48000);
        itemService.addItem("믹스커피",1000);
        itemService.addItem("공유커피",2500);
        itemService.addItem("컴포즈커피",38000);

    }
}
