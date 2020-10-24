package com.seungmoo.spring_jpa;

import com.seungmoo.spring_jpa.BeanDefinition.SeungmooRegistrar;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
//@EnableJpaRepositories // SPRING BOOT 프로젝트에서는 생략가능, 일반 SPRING에서는 @Config class에서 선언 필수!
@EnableJpaRepositories(queryLookupStrategy = QueryLookupStrategy.Key.CREATE_IF_NOT_FOUND) // 쿼리 만드는 전략 선언하기
@EnableAsync // @Async를 사용하기 위해 선언, BUT 비추천함 (코드가 불편해진다.)
@Import(SeungmooRegistrar.class)
public class SpringJpaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringJpaApplication.class, args);
    }

}
