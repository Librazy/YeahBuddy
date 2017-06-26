package cn.edu.xmu.yeahbuddy.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Time;

@Entity
public class Stage {

    @Id
    @Column(name = "StageId", updatable = false, nullable = false)
    private int id;

    @Column(name = "StageStart", nullable = false)
    private Time start;

    @Column(name = "StageEnd", nullable = false)
    private Time end;

    @Column(name = "StageDescription")
    private String description;

    @Column(name = "StageTitle", nullable = false)
    private String title;

    public Stage() {
    }

    public Stage(int id, Time start, Time end) {
        this.id = id;
        this.start = start;
        this.end = end;
    }

    public int getId() {
        return id;
    }

    public Time getStart() {
        return start;
    }

    public void setStart(Time start) {
        this.start = start;
    }

    public Time getEnd() {
        return end;
    }

    public void setEnd(Time end) {
        this.end = end;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
