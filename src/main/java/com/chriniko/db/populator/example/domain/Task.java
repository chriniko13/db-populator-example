package com.chriniko.db.populator.example.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class Task implements Serializable {

    @Id
    private String id;

    private String name;
    private String dueDate;

    public Task() {
    }

    public Task(String id, String name, String dueDate) {
        this.id = id;
        this.name = name;
        this.dueDate = dueDate;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDueDate() {
        return dueDate;
    }
}
