<h2>Kafka-Streaming-Project-with-Spring-and-Spark </h2>

This project demonstrates a real-time data processing pipeline using Apache Kafka, Apache Spark Structured Streaming, and Spring Boot. The primary goal is to read user data from a Kafka topic using Spring Boot, process the data with Spark, and then send the processed data back to Kafka. Finally, the data is consumed by a Spring Boot application.


<h3>Project Structure</h3>

Spring Boot Producer: Reads user data and sends it to a Kafka topic.<br>
Kafka Setup: Assumes a Kafka setup with multiple brokers (kafka1, kafka2, kafka3) and topics (users_data, aggregated_data, users_select).<br>
Spark Structured Streaming: Processes data from Kafka, performs filtering, and sends the results back to Kafka.<br>
Spring Boot Consumer: Consumes the data from Kafka for further processing.<br>

<h3>Instructions</h3>

1. **Start Spring Boot**:
   - Ensure both the Spring Boot Producer and Consumer applications are running.

2. **Start the Docker Containers**:
   - Use the command: `docker-compose up -d`.

3. **Restart Containers if Necessary**:
   - If you encounter any issues, you might need to restart the containers using: `docker-compose restart`.

4. **Submit Spark Job**:
   - Make sure your Spark job is correctly set up to read from Kafka and process the data.

This setup will enable you to process and analyze real-time user data using Kafka and Spark, with Spring Boot applications handling data production and consumption.

