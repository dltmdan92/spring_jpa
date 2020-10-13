package com.seungmoo.spring_jpa;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

// 모든 Annotation은 javax.persistence에서 불러온다.

// DB에 Account 테이블과 매핑이 된다는 것을 명시
@Entity(name = "users") // Entity의 이름, 객체 범주에서 부르는 name
// @Entity 객체에서 자동으로 생략가능함
@Table // Table 이름은 Entity 이름에 동일하게 default 셋팅, 릴레이션의 범주에서 부르는 name (테이블의 이름은 SQL에서 쓰임)
@Setter @Getter
public class User {

    @Id // DB PK에 매핑, ID는 primitive Type, reference Type 모두 지원한다.
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq") // 자동 생성되는 값, strategy 설정 가능
    private Long id;

    @Column(nullable = false, unique = true) // 생략 가능, columnDefinition : 특정 컬럼에 대해 SQL로 설정 가능(varchar255  등등)
    private String username;

    @Column // 생략 가능
    private String password;

    // JPA 2.2 부터 LocalDateTime, joda Java8 timeAPI 등이 적용가능할 것임. 아직 ㄴㄴ
    @Temporal(TemporalType.TIMESTAMP)
    private Date reg_date = new Date();

    private String yes;

    // 기본적으로 멤버변수는 자동으로 @Column이 붙는다. BUT 추가하고 싶지 않을 경우??
    @Transient // 스키마에 반영하고 싶지 않을 경우
    private String no;

    @Embedded // 해당 Value Type 객체를 Entity의 속성으로 사용할 때 붙여준다. 필수
    @AttributeOverrides({
            // Address Entity의 street 속성을 home_street이라는 이름으로 릴레이션의 속성으로 셋팅한다.
            @AttributeOverride(name = "street", column = @Column(name = "home_street"))
    })
    private Address homeAddress;

    // Collection으로 1대다를 매핑해보자
    @OneToMany(mappedBy = "owner") // OneToMany는 Join Table이 추가로 하나 생성된다. (users_studies)
    private Set<Study> studies = new HashSet<>();

    /**
     * 만약 Users와 Study 엔티티에서 각각 OneToMany, ManyToOne을 만들면 양방향 관계가 아닌
     * 두 개의 단방향 관계가 생성된다.
     *
     * 그러므로 양방향 관계를 만들 때는
     * @OneToMany(mappedBy = "owner") 이렇게 관계를 mapping 되는 관계를 정의해줘야 Study에서 관계를 또 안만든다.
     *
     * 또한 users_studies 관계 테이블은 별도로 drop 해줘야 한다. (Entity 테이블은 drop 처리 됨)
     */

    // 아래와 같은 메소드를 Convenient Method라고 한다.
    // User, Study 엔티티의 양방향 관계 셋팅 메소드
    public void addStudy(Study study) {
        this.getStudies().add(study);
        study.setOwner(this);
    }

    // 관계 제거 메소드
    public void removeStudy(Study study) {
        this.getStudies().remove(study);
        study.setOwner(null);
    }

}
