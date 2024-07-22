package com.version1.kafka_tests.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.version1.kafka_tests.Kafka.DataTypeKafka;
import com.version1.kafka_tests.Kafka.KafkaConsumerService;
import com.version1.kafka_tests.Kafka.KafkaProducerService;
import org.springframework.ui.Model;



@Controller
public class KafkaController {

    private final KafkaProducerService kafkaProducerService;
    private final KafkaConsumerService kafkaConsumerService;
    private final RestTemplate restTemplate;

    public KafkaController(KafkaProducerService kafkaProducerService,RestTemplate restTemplate,KafkaConsumerService kafkaConsumerService) {
        this.kafkaProducerService = kafkaProducerService;
        this.kafkaConsumerService = kafkaConsumerService;
        this.restTemplate = restTemplate;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<DataTypeKafka> messages = kafkaConsumerService.getMessages();
        System.out.println(messages.size());
        model.addAttribute("messages", messages);
        model.addAttribute("averageAge", kafkaConsumerService.getAverageAge());
        model.addAttribute("genderMode", kafkaConsumerService.getGenderMode());
        
        return "home";
    }



    @PostMapping("/kafka/{users}")
    public String sendMessageToKafka(@PathVariable(value = "users") Long users) {
        for (int i = 0; i < users; i++) {
            String url = "https://randomuser.me/api/?results=1";
            try {
                HashMap response = restTemplate.getForObject(url, HashMap.class);
               
                if (response != null) {
                    Map<String, Object> userData = ((List<Map<String, Object>>) response.get("results")).get(0);
                    Map<String, Object> transformedData = new HashMap<>();
                    transformedData.put("gender", userData.get("gender"));
                    Map<String, String> name = (Map<String, String>) userData.get("name");
                    transformedData.put("firstName", name.get("first"));
                    transformedData.put("lastName", name.get("last"));
                    Map<String, Object> location = (Map<String, Object>) userData.get("location");
                    transformedData.put("city", location.get("city"));
                    transformedData.put("country", location.get("country"));
                    transformedData.put("postcode", location.get("postcode"));
                    transformedData.put("email", userData.get("email"));
                    Map<String, Object> dob = (Map<String, Object>) userData.get("dob");
                    transformedData.put("age", dob.get("age"));
                    System.out.println(transformedData);
                    kafkaProducerService.sendMessage("users_data", transformedData);
                    System.out.println("Data Send");
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error while processing user data: " + e.getMessage());
                
            }
        }
    return "redirect:/?send=send data";
    }

    

}