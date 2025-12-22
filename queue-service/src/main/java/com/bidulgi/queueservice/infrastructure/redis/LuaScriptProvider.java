package com.bidulgi.queueservice.infrastructure.redis;

import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Getter
@Component
public class LuaScriptProvider {

	private final RedisScript<String> enqueueScript;
	private final RedisScript<String> dequeueScript;

	public LuaScriptProvider() {
		this.enqueueScript = buildEnqueueScript();
		this.dequeueScript = buildDequeueScript();
	}

	/**
	 * 대기열 등록 스크립트
	 * 1. 사용자가 이미 활성 상태인지 확인
	 * 2. 활성 상태가 아니면 작업 큐의 최대 크기와 비교
	 * 3. 최대 크기 미만이면 작업 큐에 추가
	 * 4. 최대 크기 이상이면 대기열 추가
	 */
	private RedisScript<String> buildEnqueueScript() {
		String lua = """
			local activeKey = KEYS[1]
			local waitingKey = KEYS[2]
			local productsKey = KEYS[3]
			
			local userId = ARGV[1]
			local timestamp = tonumber(ARGV[2])
			local maxSize = tonumber(ARGV[3])
			local productId = ARGV[4]
			
			local value = productId .. ":" .. userId
			
			if redis.call('SISMEMBER', activeKey, value) == 1 then
				return 'ACTIVE'
			end
			
			local currentSize = tonumber(redis.call('SCARD', activeKey))
			if currentSize < maxSize then
				redis.call('SADD', activeKey, value)
				return 'ACTIVE'
			end
			
			local added = redis.call('ZADD', waitingKey, 'NX', timestamp, userId)
			if added == 1 then
			  local waitingCount = tonumber(redis.call('ZCARD', waitingKey))
			  if waitingCount == 1 then
			    redis.call('LPUSH', productsKey, productId)
			  end
			end
			
			return 'WAIT'
			""";

		DefaultRedisScript<String> script = new DefaultRedisScript<>();
		script.setScriptText(lua);
		script.setResultType(String.class);
		return script;
	}

	/**
	 * 대기열 제거 및 다음 사용자 활성 스크립트
	 * 1. 활성 대기열에서 사용자 제거
	 * 2. 대기 중인 사용자 확인
	 * 3. 대기 중인 사용자가 있으면 활성 대기열에 추가 및 반환
	 */
	private RedisScript<String> buildDequeueScript() {
		String lua = """
			local activeKey = KEYS[1]
			local productsKey = KEYS[2]
			
			local valueToRemove = ARGV[1]
			
			redis.call('SREM', activeKey, valueToRemove)
			
			local productsCount = tonumber(redis.call('LLEN', productsKey))
			if productsCount == 0 then
			  return nil
			end
			
			for i = 1, productsCount do
				local productId = redis.call('RPOPLPUSH', productsKey, productsKey)
			 	local waitingKey = "queue:waiting:" .. productId
			 	local waitingCount = tonumber(redis.call('ZCARD', waitingKey))
			
			    if waitingCount > 0 then
			        local users = redis.call('ZRANGE', waitingKey, 0, 0)
			        local userId = users[1]
			        redis.call('ZREM', waitingKey, userId)
			
			        if waitingCount == 1 then
			        	redis.call('LREM', productsKey, 0, productId)
			        end
			
			        local value = productId .. ":" .. userId
			        redis.call('SADD', activeKey, value)
			        local json = string.format('{"productId":"%s","userId":"%s"}', productId, userId)
			        return json
			    end
			end
			
			return nil
			""";

		DefaultRedisScript<String> script = new DefaultRedisScript<>();
		script.setScriptText(lua);
		script.setResultType(String.class);
		return script;
	}
}
