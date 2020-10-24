package com.seungmoo.spring_jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.RepositoryDefinition;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Stream;

/**
 * Repository 인터페이스로 공개할 메소드를 직접 일일이 정의할 수 있다. (정의한 것만 사용)
 * @RepositoryDefinition - 특정 리포지토리 당
 */
/*
@RepositoryDefinition(domainClass = Comment.class, idClass = Long.class)
public interface CommentRepository {
    Comment save(Comment comment);

    List<Comment> findAll();
}
*/

/**
 * 또한 이렇게 MyRepository를 상속 받아서 사용할 수 있다.
 * MyRepository 에서 정의한 기능만!! 사용할 수 있다.
 *
 * 기능 더 쓰고 싶으면, JpaRepository, CrudRepository 등등 에서 소스 복붙하시면 댐
 *
 */
public interface CommentRepository extends MyRepository<Comment, Long> {

    // 스프링 데이터 Query 만들기

    // 1. 메소드 이름을 분석해서 SpringData JPA가 자동으로 쿼리를 만들어 준다.
    List<Comment> findByTitleContains(String keyword);

    // 2. 미리 정의된 쿼리(USE_DECLARED_QUERY)를 찾아서 사용하기
    @Query("SELECT c FROM Comment AS c") // 기본은 JPQL
    //@Query("SELECT c FROM Comment AS c", nativeQuery = true) // SQL 사용하기
    List<Comment> findByComment(String keyword);

    // 3. 위의 두 개를 MIX 한 것
    // 미리 정의한 쿼리(2번) 먼저 찾아보고 없으면 메소드 이름을 분석(1번)해서 쿼리를 만든다.
    // CREATE_IF_NOT_FOUND


    // Pageable을 통해 일정 범위 만큼 페이징 처리 가능하다.
    // But) return 타입을 그냥 List<>로 해버리면, Paging 처리는 못씀 (그냥 범위만)
    Page<Comment> findByComment(String keyword, Pageable pageable);

    // Sort 기능은 Pageable에 내장된 것을 쓰는거 추천함.

    // 예제
    List<Comment> findByTitleContainsAndCommentLikeOrderByIdAsc(String title, String comment, Pageable pageable);

    Page<Comment> findByCommentContainsIgnoreCaseAndIdGreaterThan(String keyword, Long num, Pageable pageable);

    // 쿼리 실행을 별도의 쓰레드(스프링 TaskExecutor)에 위임 (별도의 쓰레드에서 비동기 처리를 하기 위함!)
    // Future로 감싸서 non-blocking code화 할 수 있다. get() 메서드 호출하면 Blocking화 된다.
    @Async
    Future<List<Comment>> findByCommentContainsIgnoreCase(String comment, Pageable pageable);

    // Non-blocking Code, 그리고 Callback 선언할 수 있다.
    @Async
    ListenableFuture<List<Comment>> findByTitleContainsIgnoreCase(String comment, Pageable pageable);
}