package com.bidulgi.queueservice.infrastructure.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import com.bidulgi.queueservice.domain.event.TokenExpiredEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TokenExpirationEventListener extends KeyExpirationEventMessageListener {

    private static final String TOKEN_KEY_PREFIX = "token:";

    private final ApplicationEventPublisher eventPublisher;

	public TokenExpirationEventListener(
		RedisMessageListenerContainer listenerContainer,
		ApplicationEventPublisher eventPublisher
	) {
		super(listenerContainer);
		this.eventPublisher = eventPublisher;
	}

	@Override
	public void onMessage(Message message, byte[] pattern) {
		String expiredKey = new String(message.getBody());

		if (!expiredKey.startsWith(TOKEN_KEY_PREFIX)) {
			return;
		}

		// 토큰 만료 이벤트 발행
		TokenKeyInfo keyInfo = parseTokenKey(expiredKey);
		log.info("토큰 만료 감지: userId={}, productId={}", keyInfo.userId(), keyInfo.productId());
		eventPublisher.publishEvent(new TokenExpiredEvent(keyInfo.userId(), keyInfo.productId()));
	}

    private TokenKeyInfo parseTokenKey(String expiredKey) {
        String[] parts = expiredKey.substring(TOKEN_KEY_PREFIX.length()).split(":");
        if (parts.length != 2) {
			// TODO 커스텀 예외 처리
            throw new IllegalArgumentException("Invalid token key format: " + expiredKey);
        }
        return new TokenKeyInfo(parts[0], parts[1]);
    }

    private record TokenKeyInfo(String productId, String userId) {}
}
