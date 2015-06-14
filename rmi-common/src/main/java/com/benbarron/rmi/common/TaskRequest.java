package com.benbarron.rmi.common;

import java.io.Serializable;

public class TaskRequest<T, C> implements Serializable {

    private final long taskId;
    private final Task<T, C> task;

    public TaskRequest(long taskId, Task<T, C> task) {
        this.taskId = taskId;
        this.task = task;
    }

    public long getTaskId() {
        return taskId;
    }

    public Task<T, C> getTask() {
        return task;
    }
}
