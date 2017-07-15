package cn.edu.xmu.yeahbuddy.domain;

import org.jetbrains.annotations.Contract;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity
public class Stage {

    @Id
    @Column(name = "StageId", updatable = false, nullable = false)
    private int id;

    @Column(name = "StageStart", nullable = false)
    private Timestamp start;

    @Column(name = "StageEnd", nullable = false)
    private Timestamp end;

    @Column(name = "StageDescription")
    private String description;

    @Column(name = "StageTitle")
    private String title;

    public Stage() {
    }

    public Stage(int id, Timestamp start, Timestamp end) {
        this.id = id;
        this.start = start;
        this.end = end;
    }

    @Contract(pure = true)
    public int getId() {
        return id;
    }

    @Contract(pure = true)
    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    @Contract(pure = true)
    public Timestamp getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }

    @Contract(pure = true)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Contract(pure = true)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
