package com.example.kotlinspringkafkasample.consumer

import com.example.kotlinspringkafkasample.model.MyMessage
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicInteger

@Component
class MyConsumer(val retryCount: AtomicInteger = AtomicInteger(0)) {

    @KafkaListener(topics = ["test-topic"], groupId = "test-group", containerFactory = "kafkaListenerContainerFactory")
    fun consume(message: ConsumerRecord<String, MyMessage>) {
        println("Consumed message: ${message.value()}")
    }

    @KafkaListener(topics = ["test-topic"], groupId = "test-group-batch", containerFactory = "batchKafkaListenerContainerFactory")
    fun consumeBatch(messages: List<ConsumerRecord<String, MyMessage>>, acknowledgment: Acknowledgment) {
        println("Consumed batch of messages: ${messages.size}")
        println("(Retry count: ${retryCount.get()})")
        messages.forEachIndexed { index, record ->
            val message = record.value()
            println("Message ${index + 1}:")
            println("  Topic: ${record.topic()}")
            println("  Partition: ${record.partition()}")
            println("  Offset: ${record.offset()}")
            println("  Key: ${record.key()}")
            println("  Value:")
            println("    ID: ${message.id}")
            println("    Age: ${message.age}")
            println("    Name: ${message.name}")
            println("    Content: ${message.content}")
            println("-----------------------------")
        }
        println("Batch processing completed")
        retryCount.incrementAndGet()
        throw RuntimeException("Intentional exception")
    }
}