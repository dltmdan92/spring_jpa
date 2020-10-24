package com.seungmoo.spring_jpa;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

// 이렇게 NamedQuery를 활용해서 미리 준비할 수 있음
@NamedQueries({
        @NamedQuery(name = "select_post", query = "SELECT p FROM Post AS p WHERE p.id = 1")
})
@Entity
@Getter
@Setter
public class Post {

    @Id @GeneratedValue
    private Long id;

    private String title;

    @OneToMany(mappedBy = "post", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private Set<Comment> comments = new HashSet<>();

    public void addComment(Comment comment) {
        this.getComments().add(comment);
        comment.setPost(this);
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", comments=" + comments +
                '}';
    }
}
