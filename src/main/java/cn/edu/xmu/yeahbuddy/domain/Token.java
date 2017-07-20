package cn.edu.xmu.yeahbuddy.domain;

import org.hibernate.annotations.CreationTimestamp;
import org.jetbrains.annotations.Contract;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;


//TODO: 每个阶段每个导师是否至多一个token？
@Entity
public class Token {

    @Id
    @Column(name = "TokenValue", updatable = false, nullable = false)
    private String tokenValue;

    @ManyToOne
    @JoinColumn(name = "TokenTutorId", updatable = false, nullable = false)
    private Tutor tutor;

    @CreationTimestamp
    @Column(name = "TokenStartTime", updatable = false, nullable = false)
    private Timestamp start;

    @Column(name = "TokenEndTime", nullable = false)
    private Timestamp end;

    @OneToMany(fetch = FetchType.EAGER)
    private Set<Review> reviews;

    public Token() {
    }

    public Token(String tokenValue, Tutor tutor, Set<Review> reviews, Timestamp end) {
        this.tokenValue = tokenValue;
        this.tutor = tutor;
        this.reviews = reviews;
        this.end = end;
    }

    @Contract(pure = true)
    public String getTokenValue() {
        return tokenValue;
    }

    @Contract(pure = true)
    public Tutor getTutor() {
        return tutor;
    }

    @Contract(pure = true)
    public int getTutorId() {
        return getTutor().getId();
    }

    @Contract(pure = true)
    public Timestamp getStart() {
        return start;
    }

    @Contract(pure = true)
    public boolean isRevoked() {
        return end.before(Timestamp.from(Instant.now()));
    }

    public void setRevoked() {
        setEnd(Timestamp.from(Instant.now()));
    }

    @Contract(pure = true)
    public Set<Review> getReviews() {
        return reviews;
    }

    @Contract(pure = true)
    public Timestamp getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return String.format("tokenValue:%s tutor:%s reviews:[%s]", tokenValue, tutor, reviews.stream().map(Object::toString).collect(Collectors.joining(", ")));
    }
}
