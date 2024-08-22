package com.example.kotlinspringkafkasample.config

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.*
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer
import org.springframework.kafka.listener.DefaultErrorHandler
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer
import org.springframework.util.backoff.BackOff
import org.springframework.util.backoff.ExponentialBackOff

@Configuration
class KafkaConsumerConfig(
    val kafkaProperties: KafkaProperties
) {

    @Bean
    fun consumerFactory(): ConsumerFactory<String, Any> {
        val props: Map<String, Any> = mapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaProperties.bootstrapServers,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to JsonDeserializer::class.java,
            ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to false, // 수동 커밋
            JsonDeserializer.TRUSTED_PACKAGES to "*"
        )
        return DefaultKafkaConsumerFactory(props)
    }

    @Bean
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        return ConcurrentKafkaListenerContainerFactory<String, String>().apply {
            consumerFactory = consumerFactory()
            setConcurrency(3) // 단, 이는 토픽의 파티션 수에 따라 제한됩니다. 파티션 수보다 많은 concurrency는 의미가 없습니다.
            containerProperties.pollTimeout = 3000
        }
    }

    @Bean
    fun batchKafkaListenerContainerFactory(
        kafkaTemplate: KafkaTemplate<String, Any>
    ): ConcurrentKafkaListenerContainerFactory<String, String> {
        return ConcurrentKafkaListenerContainerFactory<String, String>().apply {
            consumerFactory = consumerFactory()
            isBatchListener = true
            setConcurrency(2)
            containerProperties.pollTimeout = 5000
            setContainerCustomizer {
                it.containerProperties.ackMode = ContainerProperties.AckMode.MANUAL
            }
            val errorHandler = DefaultErrorHandler(DeadLetterPublishingRecoverer(kafkaTemplate))
            setCommonErrorHandler(errorHandler)
        }
    }

    private fun generateBackOff(): BackOff {
        return ExponentialBackOff().apply {
            initialInterval = 1000 // 초기 재시도 간격
            maxInterval = 10000 // 최대 재시도 간격
            multiplier = 2.0 // 재시도 간격 배수
            maxElapsedTime = 60000 // 최대 재시도 시간
        }
    }
}

@Configuration
class KafkaProducerConfig<V>(
    private val kafkaProperties: KafkaProperties
) {
    @Bean
    fun producerFactory(): ProducerFactory<String, V> {
        val configProps: Map<String, Any> = mapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaProperties.bootstrapServers,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to JsonSerializer::class.java,
            ProducerConfig.ACKS_CONFIG to "-1",
            ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG to "true",
            ProducerConfig.TRANSACTIONAL_ID_CONFIG to "tx-",  // 트랜잭션 ID 접두사 추가
        )
        return DefaultKafkaProducerFactory(configProps)
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, V> {
        return KafkaTemplate(producerFactory())
    }
}