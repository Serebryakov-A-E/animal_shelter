package me.serebryakov.animal_shelter.telegrammBotConfig;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TelegramBotConfiguration {
    @Value("${telegramm.bot.token}")
    private String token;

    @Bean
    public TelegramBot telegramBot() {
        TelegramBot telegramBot = new TelegramBot(token);
        return telegramBot;
    }
}
