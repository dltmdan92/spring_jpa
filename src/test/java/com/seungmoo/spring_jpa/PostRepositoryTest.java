package com.seungmoo.spring_jpa;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * test scope DB는 H2로 설정했음
 */
@RunWith(SpringRunner.class)
@DataJpaTest // Data 관련 Bean(Repository)만 Test 하도록 slicing Test 수행 (Spring data JPA의 기능)
public class PostRepositoryTest {
    @Autowired
    PostRepository postRepository;

    @Test
    public void crudRepository() {
        // Given
        Post post = new Post();
        post.setTitle("hello spring boot common");
        assertThat(post.getId()).isNull();

        // When
        // 참고로 save 실행해도 INSERT 수행이 안됨 --> @DataJpaTest에 @Transactional 있음
        // Transaction 이며, Test의 경우 Rollback 된다. (Spring framework 설정)
        // JPA에서는 어차피 Rollback될 것이므로 INSERT 쿼리를 호출안한다.
        Post newPost = postRepository.save(post);
        // 굳이 Rollback 하고 싶지 않으면 @Rollback(false) 선언해준다.

        // Then
        assertThat(newPost.getId()).isNotNull();

        // When
        List<Post> posts = postRepository.findAll();
        assertThat(posts.size()).isEqualTo(1);
        assertThat(posts).contains(newPost);

        // When
        // findAll 의 경우 Pageable parameter를 쓴다.
        Page<Post> page = postRepository.findAll(PageRequest.of(0, 10));
        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getNumber()).isEqualTo(0); // 현재 페이지의 Number
        assertThat(page.getSize()).isEqualTo(10); // 페이지의 size
        assertThat(page.getNumberOfElements()).isEqualTo(1);

        // When
        postRepository.findByTitleContains("spring", PageRequest.of(0, 10));
        // Then
        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getSize()).isEqualTo(10);
        assertThat(page.getNumberOfElements()).isEqualTo(1);

        // When
        long spring = postRepository.countByTitleContains("spring");
        // Then
        assertThat(spring).isEqualTo(1);
    }
}