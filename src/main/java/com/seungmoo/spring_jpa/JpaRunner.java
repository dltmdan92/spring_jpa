package com.seungmoo.spring_jpa;

import org.hibernate.Session;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Component
@Transactional // JPA 트랜잭션 선언 (클래스, 메소드 단위 등으로 셋팅해주면 된다.)
public class JpaRunner implements ApplicationRunner {
    @PersistenceContext
    EntityManager entityManager; // EntityManager --> JPA의 핵심 클래스

    @Override
    public void run(ApplicationArguments args) throws Exception {
        User user = new User();
        user.setUsername("seungmoo");
        user.setPassword("jpa");

        Study study = new Study();
        study.setName("Spring Data JPA");
        //study.setOwner(user);

        // 양방향 관계라면 이렇게 양쪽에 관계 주입하는 것은 필수임.
        // 추가로 User 클래스에 아예 addStudy 메소드로 만들어 줄 수 있다.
        //user.getStudies().add(study);
        //study.setOwner(user);
        user.addStudy(study); // 위의 양방향 셋팅을 하나의 Method로 만들었다.

        Session session = entityManager.unwrap(Session.class);

        // JPA 관련 Operation의 하나의 Transaction 안에서 일어나야 한다.
        //entityManager.persist(account);

        // 이렇게 세션으로 저장할 수 있다.
        session.save(user);
        session.save(study);
    }
}
