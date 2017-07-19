package cn.edu.xmu.yeahbuddy.domain;

import org.hibernate.annotations.CreationTimestamp;
import org.jetbrains.annotations.Contract;

import javax.persistence.*;
import java.sql.Time;
import java.util.Collection;
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
    @Column(name = "TokenTime", updatable = false, nullable = false)
    private Time time;

    @Column(name = "TokenRevoked", nullable = false)
    private boolean revoked;

    @OneToMany
    private Collection<Review> reviews;

    public Token() {
    }

    public Token(String tokenValue, Tutor tutor, Collection<Review> reviews) {
        this.tokenValue = tokenValue;
        this.tutor = tutor;
        this.reviews = reviews;
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
    public Time getTime() {
        return time;
    }

    @Contract(pure = true)
    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked() {
        this.revoked = true;
    }

    @Contract(pure = true)
    public Collection<Review> getReviews() {
        return reviews;
    }

    @Override
    public String toString() {
        return String.format("tokenValue:%s tutor:%s revoked:%b reviews:[%s]", tokenValue, tutor, revoked, reviews.stream().map(Object::toString).collect(Collectors.joining(", ")));
    }
}
