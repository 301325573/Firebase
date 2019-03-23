package com.example.firebase;

import java.util.Date;

public class Task {
    String taskId;
    String taskName;
    String taskAssn;
    String school;

    boolean done;


    Date dueDate;


    public Task() {
    }

    public Task(String taskId, String taskName,
                String taskAssn, boolean done, Date dueDate) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.taskAssn = taskAssn;
        this.done = done;
        this.dueDate = dueDate;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskAssn() {
        return taskAssn;
    }

    public void setTaskAssn(String taskAssn) {
        this.taskAssn = taskAssn;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }


    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }


}
