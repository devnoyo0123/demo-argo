package com.example.kotlinspringkafkasample.service

import com.example.kotlinspringkafkasample.entity.Message
import com.example.kotlinspringkafkasample.entity.MessageEvent
import com.example.kotlinspringkafkasample.model.MyMessage
import com.example.kotlinspringkafkasample.repository.MessageEventRepository
import com.example.kotlinspringkafkasample.repository.MessageRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MessageService(
    val messageRepository: MessageRepository,
    val messageEventRepository: MessageEventRepository
) {

    @Transactional
    fun processMessage(message: MyMessage) {

        // 데이터베이스에 메시지 저장
        val savedMessage = messageRepository.save(Message(content = message.content))
        println("Saved message to database: ${savedMessage.id}")

        val savedEvent = messageEventRepository.save(MessageEvent())
        println("Saved message to database: ${savedEvent.id}")

//        // 예외 상황을 시뮬레이션하려면 아래 줄의 주석을 해제하세요
//         throw RuntimeException("Simulated error")
    }
}