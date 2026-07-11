package br.com.loginService.controller;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class RedisTestController {
    private final StringRedisTemplate redisTemplate;

    public RedisTestController(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/redis-ping")
    public String ping() {
        redisTemplate.opsForValue().set("ping", "pong");
        return redisTemplate.opsForValue().get("ping");   // deve devolver "pong"
    }
}
