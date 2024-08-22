package com.example.kotlinspringkafkasample.service

import com.example.kotlinspringkafkasample.entity.Message
import com.example.kotlinspringkafkasample.entity.MessageEvent
import com.example.kotlinspringkafkasample.model.MyMessage
import com.example.kotlinspringkafkasample.repository.MessageEventRepository
import com.example.kotlinspringkafkasample.repository.MessageRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify


class MessageServiceTest : BehaviorSpec({
    val messageRepository = mockk<MessageRepository>()
    val messageEventRepository = mockk<MessageEventRepository>()
    val messageService = MessageService(messageRepository, messageEventRepository)

    Given("a message service") {
        When("processing a valid message") {
            val message = MyMessage(content = "Test content")
            val savedMessage = Message(id = 1, content = "Test content")
            val savedEvent = MessageEvent(id = 1)

            every { messageRepository.save(any()) } returns savedMessage
            every { messageEventRepository.save(any()) } returns savedEvent

            Then("it should save the message and event successfully") {
                messageService.processMessage(message)

                verify(exactly = 1) { messageRepository.save(any()) }
                verify(exactly = 1) { messageEventRepository.save(any()) }
            }
        }
    }
})