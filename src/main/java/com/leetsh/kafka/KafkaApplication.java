package com.leetsh.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class KafkaApplication {

    @Autowired private MessageListener listener;
    @Autowired private MessageProducer producer;

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = SpringApplication.run(KafkaApplication.class, args);

        MessageProducer producer = context.getBean(MessageProducer.class);
        MessageListener listener = context.getBean(MessageListener.class);

        producer.sendMessage("Hello, World!");
        listener.latch.await(10, TimeUnit.SECONDS);


        for (int i = 0; i < 5; i++) {
            producer.sendMessageToPartion("Hello To Partioned Topic!", i);
        }
        listener.partitionLatch.await(10, TimeUnit.SECONDS);


        producer.sendMessageToFiltered("Hello Baeldung!");
        producer.sendMessageToFiltered("Hello World!");
        listener.filterLatch.await(10, TimeUnit.SECONDS);


        producer.sendGreetingMessage(new Greeting("Greetings", "World!"));
        listener.greetingLatch.await(10, TimeUnit.SECONDS);

        context.close();
    }

    @Bean
    public MessageProducer messageProducer() {
        return new MessageProducer();
    }

    @Bean
    public MessageListener messageListener() {
        return new MessageListener();
    }
}
