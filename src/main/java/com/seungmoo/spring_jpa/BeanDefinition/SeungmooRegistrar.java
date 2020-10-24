package com.seungmoo.spring_jpa.BeanDefinition;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Spring Data JPA의 JpaRepository가 수행하는 로직을
 * 직접 Programming 해보자
 *
 * @EnableJpaRepositories --> @JpaRepositoriesRegistrar
 * --> @RepositoryBeanDefinitionRegistrarSupport --> @ImportBeanDefinitionRegistrar(스프링 인터페이스를 차용한다.)
 * @ImportBeanDefinitionRegistrar (스프링 data JPA는 스프링 빈 definition 등록 interface를 사용한다.)
 */
public class SeungmooRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {
        // 아래 로직이 Spring Data JPA가 수행하는 로직임
        // 특정 도메인에 맞는 BeanDefinition을 모두 찾고 BeanDefition을 regist한다.

        // Seungmoo라는 도메인을 Bean으로 명시적으로 등록하지 않았음에도 불구하고
        // Spring Data JPA는 아래와 같은 로직을 수행하기 때문에 Seungmoo 도메인이 Bean으로 등록된다.
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(Seungmoo.class);
        beanDefinition.getPropertyValues().add("name", "seungmoo");

        registry.registerBeanDefinition("seungmoo", beanDefinition);
    }
}
