package com.xtr.framework.queue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Configuration
public class MQConfig {
    @Value("${mq-config.thread-size}")
    private int threadSize;
    @Bean
    public MessageQueue messageQueue() {
        return new MessageQueue();
    }
    //@Autowired
    //private MyService service; 注入bean
    @Bean(destroyMethod="shutdown")
    public ExecutorService taskExecutor(MessageQueue queue) {
        ExecutorService executor = Executors.newFixedThreadPool(threadSize);
        for (int i = 0; i < threadSize; i++) {
            // executor.submit(new TaskConsumer(queue,service)); 把bean传给消费类,需要修改消费类的构造方法
            executor.submit(new TaskConsumer(queue));
        }
        return executor;
    }

}

