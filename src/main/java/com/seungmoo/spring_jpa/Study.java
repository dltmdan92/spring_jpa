package com.seungmoo.spring_jpa;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Setter @Getter
public class Study {

    @Id @GeneratedValue
    private Long id;

    private String name;

    // User 엔티티와의 관계, 1대다
    @ManyToOne // owner 가 1임
    private User owner; // Study 엔티티에 User 엔티티의 Foreign Key가 등록된다.

}
