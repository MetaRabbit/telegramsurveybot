package com.survey.telegramsurveybot.telegram;

import com.survey.telegramsurveybot.chat.Action;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.util.AbilityExtension;

import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;

public class BotCommand implements AbilityExtension {
    private final String name;
    private final Action action;

    public BotCommand(String name, Action action) {
        this.name = name;
        this.action = action;
    }

    public Ability ability() {
        return Ability.builder()
                .name(name)
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx -> action.Execute(ctx.chatId(), ctx.update().getMessage().getText()))
                .build();
    }
}
