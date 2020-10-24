package com.seungmoo.spring_jpa;

import com.seungmoo.spring_jpa.BeanDefinition.Seungmoo;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Slf4j
@Component
// JPA 관련 Operation의 하나의 Transaction 안에서 일어나야 한다.
// JPA 트랜잭션 선언 (클래스, 메소드 단위 등으로 셋팅해주면 된다.)
@Transactional
public class JpaRunner implements ApplicationRunner {
    @PersistenceContext
    EntityManager entityManager; // EntityManager --> JPA의 핵심 클래스

    @Autowired
    PostRepository postRepository; // Spring Data JPA 에서 새롭게 사용가능한 Repository (DAO)

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    Seungmoo seungmoo;

    @Autowired
    CommentRepository commentRepository;

    //@Override
    public void run1(ApplicationArguments args) throws Exception {
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

        // Hibernate의 Session API를 통해 DB 통신
        // JPA의 구현체 Hibernate를 사용한다.
        Session session = entityManager.unwrap(Session.class);

        // EntityManager를 통해 DB 통신
        //entityManager.persist(account);

        // 이렇게 세션으로 저장할 수 있다.
        // Session.save(), load(), get()를 하게 되면,
        // Transient(JPA가 관리 X) 상태에서 --> Persistent(JPA가 관리 O) 상태로 변경된다.
        // Persistent Context 상태에서 엔티티 정보가 "1차 캐시" 된다.
        session.save(user); // update, insert 모두 save
        session.save(study);

        // 여기서 Entity 데이터 Load 해도 SELECT 쿼리를 실행하지 않는다.
        // 위에서 Cache 된 데이터를 바로 리턴한다. (DB까지 굳이 가지 않는다.)
        User seungmoo = session.load(User.class, user.getId()); // seungmoo 객체는 Persistent Context에 들어간다.(JPA가 관리)
        seungmoo.setUsername("seungmoo2"); // Persistent Context의 객체를 업데이트 하면 --> UPDATE 문이 실행된다!!
        seungmoo.setUsername("seungmoo3");
        seungmoo.setUsername("seungmoo4");
        // Dirty Checking(Persistent Context) --> JPA에서 객체의 변경사항을 계속 감지
        // Write Behind(Persistent Context) --> 불필요한 UPDATE문이 실행되지 않는다!
        seungmoo.setUsername("seungmoo");
        log.info("============================");
        log.info(seungmoo.getUsername());

        /**
         * session.save --> session.load를 했으니, 당연히 INSERT 쿼리, 그리고 SELECT 쿼리가 실행될까??
         * JPA에서는 다르다.
         * 먼저 SELECT 로직(쿼리 X)이 실행되고 Cache된 데이터를 리턴한 후에,
         * Transaction이 끝나는 시점에 INSERT 쿼리가 실행되었다.
         * SELECT 쿼리를 위한 Connection을 생략함.
         * --> JPA의 성능 효율화 장점
         *
         * Persistent 상태가 끝나고 Detached 상태로 갈 때 --> Transaction이 끝났을 때
         * 더 이상 JPA에서의 Cache, Dirty Checking, Write Behind가 발생하지 않는다.
         *
         * 자세한 내용은 word 파일 참고!!
         */
    }

    //@Override
    public void run2(ApplicationArguments args) throws Exception {
        Post post = new Post();
        post.setTitle("Spring Data JPA 언제 보나...");

        Comment comment = new Comment();
        comment.setComment("빨리 보고 싶어요");
        post.addComment(comment);

        Comment comment1 = new Comment();
        comment1.setComment("곧 보여드릴게요.");
        post.addComment(comment1);

        Session session = entityManager.unwrap(Session.class);
        session.save(post);
        // 이렇게 post만 session.save 할 경우, comment 데이터는 저장되지 않는다.
        // comment, comment1 객체는 persistent 상태가 아님
        // 상태 변화를 전파시키는 옵션 Cascade를 통해 연관관계가 있는 객체(comment, comment1)도 persistent 상태로 넘긴다. CascadeType.PERSIST


        Session session1 = entityManager.unwrap(Session.class);
        Post post1 = session1.get(Post.class, 1l); // Persistent 상태
        // session.delete 하면 Persistent --> Removed 상태로 넘어감
        // CascadeType.REMOVE 선언을 통해 연관된 엔티티(Comment) 데이터도 Remove 상태로 변환된다.(session.delete 실행)
        session.delete(post1);
    }

    //@Override
    public void run3(ApplicationArguments args) throws Exception {
        /*
        Post post = new Post();
        post.setTitle("Spring Data JPA 언제 보나...");

        Comment comment = new Comment();
        comment.setComment("빨리 보고 싶어요");
        post.addComment(comment);

        Comment comment1 = new Comment();
        comment1.setComment("곧 보여드릴게요.");
        post.addComment(comment1);

        Session session = entityManager.unwrap(Session.class);
        session.save(post);
        */
        Session session = entityManager.unwrap(Session.class);
        Post post1 = session.get(Post.class, 1l); // @OneToMany Lazy 연동이라서 Comment 조회 안함
        Comment comment1 = session.get(Comment.class, 1l); // @ManyToOne Eager 연동이라서 Comment 조회

        post1.getComments().forEach(c -> { // 여기서 연관 객체가 Lazy Loading 된다. 이게 더 효율적임.
            log.info("==========");
            log.info(c.getComment());
        });

        /**
         * JPA Fetch - 연관 관계의 엔티티를 어떻게 가져올 것이냐?? 지금(Eager) or 나중에(Lazy)
         * @OneToMany : 기본 Lazy 연동, SQL SELECT 문에서 연관 테이블 안 긁어옴
         * @ManyToOne : 기본 Eager 연동, SQL SELECT 문에서 연관 테이블 긁어옴
         */
    }

    //@Override
    public void run4(ApplicationArguments args) throws Exception {
        // JPQL(HQL)을 통해서 쿼리를 만들 수 있다.
        // SQL과의 가장 큰 차이점 --> TABLE이 아닌 Entity 명을 기반으로 작성!
        // 단점 Type Safe 하지 않음 --> Criteria 를 사용해서 Type-safe 코드 작성 가능함(word 참고)
        TypedQuery<Post> query = entityManager.createQuery("SELECT p FROM Post AS p", Post.class);
        List<Post> posts = query.getResultList();
        posts.forEach(c -> log.info(c.toString())); // toString()에서 연관 객체(Comment) 출력하면 Lazy 연동안됨!! (Eager 연동으로 수행)

        // 네이티브 query (SQL)을 만들 수도 있다.
        List<Post> posts1 = entityManager.createNativeQuery("SELECT * FROM Post", Post.class).getResultList();
        posts1.forEach(c -> log.info(c.toString()));
    }

    //@Override
    public void run5(ApplicationArguments args) throws Exception {
        postRepository.findAll().forEach(s -> log.info(s.toString()));
        log.info(seungmoo.getName());
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Post post = new Post();
        post.setTitle("Spring Data JPA 언제 보나...");

        Comment comment = new Comment();
        comment.setComment("빨리 보고 싶어요");
        post.addComment(comment);

        Comment comment1 = new Comment();
        comment1.setComment("곧 보여드릴게요.");
        post.addComment(comment1);

        Session session = entityManager.unwrap(Session.class);
        session.save(post);

        commentRepository.findById(1l);
        commentRepository.findByComment("빨리 보고 싶어요");
        commentRepository.findByTitleContainsAndCommentLikeOrderByIdAsc("Spring Data JPA 언제 보나...", "빨리", PageRequest.of(0, 10));
    }
}
