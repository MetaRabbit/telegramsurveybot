package com.survey.telegramsurveybot.telegram;

import com.survey.telegramsurveybot.chat.Action;
import com.survey.telegramsurveybot.survey.ResponseService;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.ReplyFlow;

import static org.telegram.abilitybots.api.util.AbilityUtils.getChatId;

public class BotAdapter extends AbilityBot {

    private ResponseService responseService;

    private final Integer adminId;

    @Override
    public int creatorId() {
        return adminId;
    }

    public BotAdapter(ResponseService responseService, String token, String username, Integer adminId) {
        super(token, username);

        this.adminId = adminId;
        responseService.setMessageSender(new MessageSender(sender()));
        this.responseService = responseService;
    }

    public void addCommand(String name, Action action) {
        this.addExtension(new BotCommand(name, action));
    }

    public ReplyFlow directionFlow() {
        ReplyFlow readQuestion = ReplyFlow.builder(db)
                .onlyIf(upd -> responseService.canReadNewQuestion(getChatId(upd)) && !upd.getMessage().isCommand())
                .action(upd -> {
                    responseService.readNewQuestion(getChatId(upd), upd.getMessage().getText());
                })
                .build();

        ReplyFlow readDescription = ReplyFlow.builder(db)
                .onlyIf(upd -> responseService.canReadDescription(getChatId(upd)))
                .action(upd -> {
                    responseService.readDescription(getChatId(upd), upd.getMessage().getText());
                })
                .build();

        ReplyFlow readTitle = ReplyFlow.builder(db)
                .onlyIf(upd -> responseService.canReadTitle(getChatId(upd)))
                .action(upd -> {
                    responseService.readTitle(getChatId(upd), upd.getMessage().getText());
                }
                )
                .build();

        return ReplyFlow.builder(db)
                .onlyIf(upd -> responseService.waitingInput(getChatId(upd), upd.getMessage()))
                .next(readTitle)
                .next(readDescription)
                .next(readQuestion)
                .build();
    }
}