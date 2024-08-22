package com.example.kotlinspringkafkasample.controller

import com.example.kotlinspringkafkasample.model.MyMessage
import com.example.kotlinspringkafkasample.service.MessageService
import org.springframework.http.ResponseEntity
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/messages")
class MessageController(private val kafkaTemplate: KafkaTemplate<String, MyMessage>, val messageService: MessageService) {

    @PostMapping
    fun sendMessage(@RequestBody message: MyMessage): ResponseEntity<String> {
//        repeat(10) { index ->
//            val modifiedMessage = MyMessage(
//                id = message.id + index,
//                age = message.age + index,
//                name = "${message.name}_${index + 1}",
//                content = "${message.content} - Iteration ${index + 1}"
//            )
//            kafkaTemplate.send("test-topic", modifiedMessage)
//        }
        messageService.processMessage(message)
        return ResponseEntity.ok("10 messages sent to Kafka")
    }
}