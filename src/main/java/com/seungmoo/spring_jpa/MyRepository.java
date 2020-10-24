package com.seungmoo.spring_jpa;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@NoRepositoryBean // Repository가 아님을 선언
public interface MyRepository<T, Id extends Serializable> extends Repository<T, Id> {

    // 스프링 프레임워크 5.0 부터 지원하는 Null 애노테이션을 지원한다.
    // @NonNull, @NonNullApi, @Nullable 등등
    // 런타임 체크
    <E extends T> E save(@NonNull E entity);

    @Nullable
    List<T> findAll();

    @NonNull
    Long count();

    // Null 처리하기
    <E extends T> Optional<E> findById(Id Id);
}
