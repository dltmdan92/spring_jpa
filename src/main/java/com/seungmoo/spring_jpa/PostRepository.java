package com.seungmoo.spring_jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * 본격적으로 Spring Data JPA를 사용해보자
 * Spring Data JPA의 핵심 인터페이스 : JpaRepository<Entity, Id>
 * 매직 인터페이스이며, @Repository가 없어도 빈으로 등록해줌
 */

/*
// 이러한 방식은 옛날 방식
// 각 CRUD들에 대한 코드를 직접 Repository 클래스에 만들어 줘야 했다. (번거로움)
@Repository
//@Transactional
public class PostRepository {
    @PersistenceContext
    EntityManager entityManager;

    public Post add(Post post) {
        entityManager.persist(post);
        return post;
    }

    public void delete(Post post) {
        entityManager.remove(post);
    }

    public List<Post> findAll() {
        return entityManager.createQuery("SELECT p FROM Post as p").getResultList();
    }
}
*/

/**
 * Spring Data JPA의 새로운 Generic Repository 생성 방식
 * @Repository가 없어도 빈으로 등록된다.
 *
 * 스프링 Data Common --> Repository(기능 X, Marker 용), CrudRepository(기능 정의됨), PagingAndSortingRepository
 *      NoRepositoryBean --> 이것은 실제 Repository가 아님을 명시
 * 스프링 Data JPA --> JpaRepository
 * 스프링 Data JPA는 스프링 Data Common를 차용하며, 여기에 부가 기능을 얹은 것임
 */
public interface PostRepository extends JpaRepository<Post, Long> {

    // 이렇게 Naming-Rule에 맞춰서 메소드 생성 가능
    Page<Post> findByTitleContains(String title, Pageable pageable);

    long countByTitleContains(String title);
}