package com.seungmoo.spring_jpa;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@RunWith(SpringRunner.class)
@DataJpaTest
public class CommentRepositoryTest {
    @Autowired
    CommentRepository commentRepository;

    @Test
    public void crud1() {
        Comment comment = new Comment();
        comment.setComment("Hello Comment");
        commentRepository.save(comment);

        List<Comment> all = commentRepository.findAll();
        assertThat(all.size()).isEqualTo(1);

        Optional<Comment> byId = commentRepository.findById(100l);
        assertThat(byId).isEmpty(); // isEmpty --> Optional 체크용으로 씀
        Comment comment1 = byId.orElseThrow(IllegalArgumentException::new);
        log.debug(comment1.toString());

        /**
         * 중요 -> SpringDataJpa에서 Collection을 리턴할 때
         * Null을 리턴하지 않는다. 비어있는 콜렉션을 리턴한다.
         * 그러므로 단순 Null 체크를 하는 것은 의미가 없음
         */

        // save의 parameter에 @NonNull을 선언했음
        // IllegalArgumentException 발생
        commentRepository.save(null);
    }

    @Test
    public void crud2() throws ExecutionException, InterruptedException {
        createComment("spring", "spring data jpa");
        createComment("hibernate", "spring data jpa with hibernate");
        createComment("spring jpa", "jpa + spring data jpa with hibernate");

        PageRequest pageRequest = PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "id"));

        Page<Comment> comments = commentRepository.findByCommentContainsIgnoreCaseAndIdGreaterThan("Spring", 0l, pageRequest);
        assertThat(comments.getNumberOfElements()).isEqualTo(3);
        assertThat(comments).first().hasFieldOrPropertyWithValue("comment", "jpa + spring data jpa with hibernate");

        // Stream으로 받는 경우 try with resources 구문으로 닫아주도록 한다.
        try(Stream<Comment> commentStr = commentRepository.findByCommentContainsIgnoreCaseAndIdGreaterThan("Spring", 0l, pageRequest).stream()) {
            Comment firstComment = commentStr.findFirst().get();
            assertThat(firstComment.getComment()).isEqualTo("jpa + spring data jpa with hibernate");
        }

        // @Async는 백기선이 비추천함
        // Non-blocking Code
        Future<List<Comment>> future =
                commentRepository.findByCommentContainsIgnoreCase("jpa + spring data jpa with hibernate", pageRequest);

        // get()을 호출하면 결과가 나올때까지 기다린다.
        // get() 호출하게 되면 Blocking Code 이다. 결국 Blocking Code가 된다.
        future.get();

        comments.forEach(s -> log.info(s.toString()));

        // ListenableFuture : Non-blocking + Callback,  but 백기선이 비추천
        ListenableFuture<List<Comment>> listenableFuture = commentRepository.findByTitleContainsIgnoreCase("spring", pageRequest);

        listenableFuture.addCallback(new ListenableFutureCallback<List<Comment>>() {
            @Override
            public void onFailure(Throwable throwable) {
                System.out.println(throwable);
            }

            @Override
            public void onSuccess(List<Comment> comments) {
                log.info("======================");
                comments.forEach(s -> log.info("async comment : " + s.toString())); // 이 부분 출력안될 것임
                // 위에서 commentRepository.save를 통해 데이터를 3건을 넣어줬다(INSERT).
                // BUT 얘는 데이터를 찾아오지 못했음, main thread의 트랜잭션에서 발생된 데이터를 감지하지 못한다.
                // 그냥 트랜잭션 이전 상태의 데이터를 갖고 온다.
                // ---> 매우 불편 (Async는 비추천)
            }
        });

        /**
         * Async를 비추천하는 이유
         * @Async 쿼리 실행 로직을 스프링 TaskExecutor에 전달해서 다른 쓰레드에서 실행된다.
         * --> JPA에서 @Async 로직을 모르게 된다.
         * --> 스프링 트랜잭션의 기본은 Rollback이다. save를 해도 UPSERT가 아니라 Persistent State로 된다.
         * --> 위의 테스트 예제에서는 @Async 로직이 돌아가는지 모르기 때문에 아예 INSERT 로직을 호출하지도 않을 수 있다.
         * ----> 서로 다른 쓰레드에서 실행 --> @Async 쪽에서 main thread의 데이터들을 감지하지 못한다.
         * --> 성능이 좋아지지도 않는다. (DB 부하는 그대로임)
         *
         * --> 그리고 @Async로직이 실행되기도 전에 메인 쓰레드가 끝나버리는 등 여러 부작용이 존재한다.
         * 해결 방법
         * --> Thread.sleep을 하든 get()을 호출해서 blocking 통해 흐름을 맞춰줄 수 밖에 없다. --> 소스의 비효율
         * --> commentRepository.flush() --> flushing하면 바로 save로직이 DB로 실행된다. (JpaRepository를 써야함)
         */
    }

    private void createComment(String title, String content) {
        Comment comment = new Comment();
        comment.setTitle(title);
        comment.setComment(content);
        commentRepository.save(comment);
    }
}