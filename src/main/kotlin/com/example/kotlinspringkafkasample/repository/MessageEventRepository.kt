package com.example.kotlinspringkafkasample.repository

import com.example.kotlinspringkafkasample.entity.Message
import com.example.kotlinspringkafkasample.entity.MessageEvent
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface MessageEventRepository : JpaRepository<MessageEvent, Long>