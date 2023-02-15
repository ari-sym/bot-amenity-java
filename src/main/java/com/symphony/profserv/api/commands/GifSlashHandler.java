package com.symphony.profserv.api.commands;

import com.symphony.bdk.core.activity.ActivityMatcher;
import com.symphony.bdk.core.activity.command.CommandActivity;
import com.symphony.bdk.core.activity.command.CommandContext;
import com.symphony.bdk.core.activity.model.ActivityInfo;
import com.symphony.bdk.core.activity.model.ActivityType;
import com.symphony.bdk.core.service.datafeed.EventException;
import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.core.service.message.model.Message;
import com.symphony.bdk.template.api.Template;
import com.symphony.profserv.api.commands.matchers.CommandActivityMatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GifSlashHandler extends CommandActivity<CommandContext> {

  private final static String COMMAND_NAME = "/gif";

  private final MessageService messageService;
  private final Template template;

  public GifSlashHandler(MessageService messageService) {
    this.messageService = messageService;
    this.template = messageService.templates().newTemplateFromClasspath("/templates/gif.ftl");
  }

  @Override
  protected ActivityMatcher<CommandContext> matcher() throws EventException {
    return new CommandActivityMatcher(messageService, COMMAND_NAME);
  }

  @Override
  protected void onActivity(CommandContext context) throws EventException {
    this.messageService.send(context.getStreamId(), Message.builder().template(this.template).build());
  }

  @Override
  protected ActivityInfo info() {
    return new ActivityInfo().type(ActivityType.COMMAND)
      .name(COMMAND_NAME)
      .description("Send gif template");
  }
}
