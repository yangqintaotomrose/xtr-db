package com.xtr.framework.queue;

import java.util.concurrent.ConcurrentLinkedDeque;

public class MessageQueue {
    private final ConcurrentLinkedDeque<Task> deque = new ConcurrentLinkedDeque<>();
    private final Object lock = new Object();

    public void submitTask(Task task) {
        synchronized (lock) {
            if (task.getPrioritize()==1) {
                deque.offerFirst(task); //如果标记优先处理,头部插入任务
            } else {
                deque.offerLast(task);//尾插
            }
            lock.notifyAll(); // 通知等待的消费者线程
        }
    }

    public Task takeTask() {
        synchronized (lock) {
            return deque.pollFirst();
        }
    }

    public Object getLock() {
        return this.lock;
    }
}

