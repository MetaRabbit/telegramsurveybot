package com.survey.telegramsurveybot.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.List;

// TODO rewrite to Redis Stream
public class RedisMessageEventStore implements IMessageCommandStore {

    private final Integer limitMaxLoadEvents = 200;

    @Autowired
    private RedisTemplate<String, Command> template;

    // inject the template as ListOperations
    @Resource(name="redisTemplate")
    private ListOperations<String, Command> listOps;

    @Override
    public void put(long chatId, Command command) {
        listOps.leftPush(Long.toString(chatId), command);
    }

    @Override
    public List<Command> get(long chatId) {
        return listOps.range(Long.toString(chatId), -1, limitMaxLoadEvents);
    }
}
