package com.benbarron.rmi.common;

import java.io.Serializable;

public class TaskResponse<T> implements Serializable {

    private final long taskId;
    private final boolean isLastMessage;
    private final T response;

    public TaskResponse(long taskId, boolean isLastMessage, T response) {
        this.taskId = taskId;
        this.isLastMessage = isLastMessage;
        this.response = response;
    }

    public long getTaskId() {
        return taskId;
    }

    public boolean isLastMessage() {
        return isLastMessage;
    }

    public T getResponse() {
        return response;
    }
}
