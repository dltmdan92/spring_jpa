package com.seungmoo.spring_jpa;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Address {
    
    @Column // 생략 가능, 컬럼 Customize하고 싶으면 사용
    private String street;

    private String city;
    
    private String state;

    private String zipCode;
}
