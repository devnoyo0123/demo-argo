package com.example.kotlinspringkafkasample.repository

import com.example.kotlinspringkafkasample.entity.Message
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface MessageRepository : JpaRepository<Message, Long>