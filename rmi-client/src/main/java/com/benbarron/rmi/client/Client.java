package com.benbarron.rmi.client;

import com.benbarron.flow.Closeable;
import com.benbarron.flow.Flow;
import com.benbarron.flow.Producer;
import com.benbarron.flow.SimpleFlow;
import com.benbarron.objectsocket.ObjectSocketChannel;
import com.benbarron.rmi.common.Task;
import com.benbarron.rmi.common.TaskRequest;
import com.benbarron.rmi.common.TaskResponse;
import com.benbarron.rmi.common.TaskResult;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

public class Client implements Closeable {

    private final ObjectSocketChannel objectSocketChannel;
    private final AtomicLong taskIdCounter = new AtomicLong(0);

    Client(ObjectSocketChannel objectSocketChannel) {
        this.objectSocketChannel = objectSocketChannel;
    }

    public <T, C> TaskResult<T> createTask(Task<T, C> task) {
        SimpleFlow<T> flow = new SimpleFlow<>();
        return new TaskResult<T>() {
            @Override
            public <R> Flow<R> operate(Function<Producer<R>, Producer<T>> operation) {
                return flow.operate(operation);
            }

            @Override
            public Closeable subscribe(Producer<T> producer) {
                Closeable subscription = flow.subscribe(producer);
                long taskId = taskIdCounter.getAndIncrement();
                TaskRequest<T, C> request = new TaskRequest<>(taskId, task);

                objectSocketChannel.messages()
                    .ofType(TaskResponse.class)
                    .filter(tr -> tr.getTaskId() == taskId)
                    .operate((TaskResponse tr, Producer<T> nextProducer) -> {
                        nextProducer.next((T) tr.getResponse());

                        if (tr.isLastMessage()) {
                            nextProducer.complete();
                        }
                    })
                    .subscribe(flow);

                objectSocketChannel.write(request);

                return subscription;
            }
        };
    }

    @Override
    public void close() {
        try {
            objectSocketChannel.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static Client connect(String address, int port) {
        try {
            AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open();
            socketChannel.connect(new InetSocketAddress(address, port)).get(30, TimeUnit.SECONDS);

            ObjectSocketChannel objectSocketChannel = new ObjectSocketChannel(socketChannel);
            objectSocketChannel.startReading();

            return new Client(objectSocketChannel);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
