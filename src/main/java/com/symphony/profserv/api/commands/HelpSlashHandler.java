package com.symphony.profserv.api.commands;

import com.symphony.bdk.core.activity.ActivityMatcher;
import com.symphony.bdk.core.activity.command.CommandActivity;
import com.symphony.bdk.core.activity.command.CommandContext;
import com.symphony.bdk.core.activity.model.ActivityInfo;
import com.symphony.bdk.core.activity.model.ActivityType;
import com.symphony.bdk.core.activity.parsing.MatchingUserIdMentionToken;
import com.symphony.bdk.core.activity.parsing.SlashCommandPattern;
import com.symphony.bdk.core.service.datafeed.EventException;
import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.core.service.message.model.Message;
import com.symphony.bdk.template.api.Template;
import com.symphony.profserv.repositories.HelpCommandList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class HelpSlashHandler extends CommandActivity<CommandContext> {

  private final static String COMMAND_NAME = "/help";

  private final MessageService messageService;
  private final HelpCommandList helpCommandList;
  private final Template template;

  @Autowired
  public HelpSlashHandler(@NonNull MessageService messageService, @NonNull HelpCommandList helpCommandList) {
    this.messageService = messageService;
    this.helpCommandList = helpCommandList;
    this.template = messageService.templates().newTemplateFromClasspath("/templates/help.ftl");
  }

  @Override
  protected ActivityMatcher<CommandContext> matcher() throws EventException {
    return (context) -> {
      // standard slash command pattern
      var matchPattern = new SlashCommandPattern(COMMAND_NAME);
      // @ mention is required in Rooms but not IM
      if (!Objects.equals(CommandUtils.getStreamType(context),"IM")) {
        matchPattern.prependToken(new MatchingUserIdMentionToken(this::getBotUserId));
      }
      // check if the pattern match
      return matchPattern.getMatchResult(context.getSourceEvent().getMessage()).isMatching();
    };
  }

  @Override
  protected void onActivity(CommandContext context) throws EventException {
    this.messageService.send(context.getStreamId(), Message.builder().template(template, Map.of(
      "commands", this.helpCommandList.getCommandActivities()
    )).build());
  }

  @Override
  protected ActivityInfo info() {
    return new ActivityInfo().type(ActivityType.COMMAND)
      .name(COMMAND_NAME)
      .description("Prints all available commands (@mention required in rooms).");
  }
}
