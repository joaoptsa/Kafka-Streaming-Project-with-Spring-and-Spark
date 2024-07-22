package com.version1.kafka_tests.Kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

@Service
public class KafkaConsumerService {

    private final List<DataTypeKafka> messages = Collections.synchronizedList(new LinkedList<>());
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public KafkaConsumerService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @KafkaListener(topics = "users_select", groupId = "my-group")
    public void listen(DataTypeKafka message) {
        System.out.println("->>>>>>>>>>>>>>>>>>>>>>><Received Message: " + message);
        messages.add(message);
        messagingTemplate.convertAndSend("/topic/users_select", message);

    }

    
    public List<DataTypeKafka> getMessages() {
        return messages;
    }

    public double getAverageAge() {
        return messages.stream().mapToInt(DataTypeKafka::getAge).average().orElse(0.0);
    }

    public String getGenderMode() {
        Map<String, Long> genderCounts = messages.stream()
                .collect(Collectors.groupingBy(DataTypeKafka::getGender, Collectors.counting()));
        return genderCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");
    }


}