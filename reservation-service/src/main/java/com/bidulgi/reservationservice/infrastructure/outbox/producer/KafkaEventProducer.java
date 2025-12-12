package com.bidulgi.reservationservice.infrastructure.outbox.producer;

import java.nio.charset.StandardCharsets;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.bidulgi.common.globalException.custom.KafkaEventSendException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KafkaEventProducer {
	private final KafkaTemplate<String, String> kafkaTemplate;

	public void sendEvent(String topic, String eventId, String eventType, String key, String payload) {
		try {
			ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, payload);
			record.headers().add("eventId", eventId.getBytes(StandardCharsets.UTF_8));
			record.headers().add("eventType", eventType.getBytes(StandardCharsets.UTF_8));

			kafkaTemplate.send(record).get();
		} catch (Exception e) {
			throw new KafkaEventSendException("Kafka send failed: " + e.getMessage());
		}
	}

}
