package com.bidulgi.queueservice.infrastructure.redis;

import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Getter
@Component
public class LuaScriptProvider {

	private final RedisScript<String> enqueueScript;

	public LuaScriptProvider() {
		this.enqueueScript = buildEnqueueScript();
	}

	private RedisScript<String> buildEnqueueScript() {
		String lua = """
			local userId = ARGV[1]
			local timestamp = tonumber(ARGV[2])
			local maxSize = tonumber(ARGV[3])
			
			if redis.call('SISMEMBER', KEYS[1], userId) == 1 then
				return 'ACTIVE'
			end
			
			local score = redis.call('ZSCORE', KEYS[2], userId)
			if score then
				return 'WAIT'
			end
			
			local currentSize = tonumber(redis.call('SCARD', KEYS[1]))
			if currentSize < maxSize then
				redis.call('SADD', KEYS[1], userId)
				return 'ACTIVE'
			end
			
			redis.call('ZADD', KEYS[2], timestamp, userId)
			return 'WAIT'
			""";

		DefaultRedisScript<String> script = new DefaultRedisScript<>();
		script.setScriptText(lua);
		script.setResultType(String.class);
		return script;
	}
}
