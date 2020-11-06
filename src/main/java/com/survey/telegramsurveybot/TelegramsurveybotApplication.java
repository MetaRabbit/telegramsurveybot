package com.survey.telegramsurveybot;

import com.survey.telegramsurveybot.chat.Command;
import com.survey.telegramsurveybot.chat.DefaultMessageSender;
import com.survey.telegramsurveybot.chat.IMessageCommandStore;
import com.survey.telegramsurveybot.chat.RedisMessageEventStore;
import com.survey.telegramsurveybot.survey.ISurveyRepository;
import com.survey.telegramsurveybot.survey.ResponseService;
import com.survey.telegramsurveybot.telegram.BotAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;

@SpringBootApplication
public class TelegramsurveybotApplication {

	private static final Logger log = LoggerFactory.getLogger(TelegramsurveybotApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(TelegramsurveybotApplication.class, args);
	}

	@Bean
	public IMessageCommandStore messageCommandStore() {
		return new RedisMessageEventStore();
	}

	@Bean
	public ResponseService responseService(ISurveyRepository repository,
										   IMessageCommandStore messageCommandStore) {
		return new ResponseService(new DefaultMessageSender(), messageCommandStore, repository);
	}

	@Bean
	public BotAdapter telegramBotAdapter(ResponseService responseService,
												 @Value("${tg.token}") String token,
												 @Value("${tg.username}") String username,
												 @Value("${tg.admin}") Integer adminId) {
		// TODO move adminId to redis conf
		return new BotAdapter(responseService, token, username, adminId);
	}

	@Bean
	public CommandLineRunner setupBotCommands(BotAdapter telegramBotAdapter, ResponseService responseService) {
		return (args) -> {
			// TODO move enabling of commands by redis conf
			telegramBotAdapter.addCommand(ResponseService.StartCommandName, responseService::replyToStart);
			telegramBotAdapter.addCommand(ResponseService.NewCommandName, responseService::tryProcessNewSurvey);
			telegramBotAdapter.addCommand(ResponseService.SaveCommandName, responseService::saveNewSurvey);
			telegramBotAdapter.addCommand(ResponseService.RunPassingSurveyCommandName, responseService::tryProcessNewPassingSurvey);
		};
	}

	@Bean
	public CommandLineRunner registerBot(BotAdapter telegramBotAdapter) {
		return (args) -> {
			// Initializes dependencies necessary for the base bot - Guice
			ApiContextInitializer.init();

			TelegramBotsApi botsApi = new TelegramBotsApi();

			try {
				botsApi.registerBot(telegramBotAdapter);
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
	}

	// create beans for redis setup

	@Bean
	public LettuceConnectionFactory lettuceConnectionFactory() {
		return new LettuceConnectionFactory(new RedisStandaloneConfiguration());
	}

	@Bean
	public RedisTemplate<String, Command> redisTemplate() {
		RedisTemplate<String, Command> template = new RedisTemplate<>();
		template.setConnectionFactory(lettuceConnectionFactory());
		return template;
	}
}
