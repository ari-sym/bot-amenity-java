package com.symphony.profserv.api.commands.matchers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.symphony.bdk.core.activity.ActivityMatcher;
import com.symphony.bdk.core.activity.command.CommandContext;
import com.symphony.bdk.core.activity.parsing.Arguments;
import com.symphony.bdk.core.service.datafeed.EventException;
import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.core.service.message.model.Message;
import com.symphony.bdk.gen.api.model.StreamType;
import com.symphony.bdk.gen.api.model.V4Message;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class CommandActivityMatcher implements ActivityMatcher<CommandContext> {

  public static final String CMD_OPTION_KEY = "options";
  public static final String CMD_MENTIONS_KEY = "mentions";

  private final MessageService messageService;

  private final String commandName;
  private Set<String> authorizedUsersIds;
  private Set<StreamType.TypeEnum> authorizedStreams;

  public CommandActivityMatcher(MessageService messageService, String commandName) {
    this.messageService = messageService;
    this.commandName = commandName;
  }

  public CommandActivityMatcher authorizedUsersIds(Set<String> authorizedUsersIds) {
    this.authorizedUsersIds = authorizedUsersIds;
    return this;
  }

  public CommandActivityMatcher authorizedStreams(Set<StreamType.TypeEnum> authorizedStreams) {
    this.authorizedStreams = authorizedStreams;
    return this;
  }

  @Override
  public boolean matches(CommandContext context) throws EventException {
    // get message
    var message = context.getSourceEvent().getMessage();
    if (message == null) {
      return false;
    }

    // retrieve text from message
    if (Strings.isBlank(message.getMessage())) {
      return false;
    }

    // check if the pattern match
    var text = message.getMessage().replaceAll("<[^>]*>", "");
    if (text.startsWith(commandName)) {
      log.debug("Received command {}.", commandName);

      // check if the user is authorized
      if (!checkUser(context)) return false;

      // check if the room type is accepted
      if (!checkStream(message)) return false;

      // get arguments then return true
      var commandObjectMapType = new TypeToken<Map<String, CommandDataObject>>() {}.getType();
      Map<String, CommandDataObject> data = new Gson().fromJson(message.getData(), commandObjectMapType);
      var mentions = data != null ?
        data.values().stream().filter(CommandDataObject::isMention)
          .map(CommandDataObject::getId).flatMap(List::stream)
          .filter(CommandIdObject::isUserId)
          .map(CommandIdObject::getValue)
          .collect(Collectors.joining(","))
        : "";
      context.setArguments(new Arguments(Map.of(
        CMD_OPTION_KEY, text.replace(commandName, "").strip(),
        CMD_MENTIONS_KEY, mentions
      )));
      return true;
    }

    return false;
  }

  private boolean checkStream(V4Message message) {
    if (authorizedStreams != null) {
      if (message.getStream() == null || message.getStream().getStreamType() == null ||
        authorizedStreams.stream().map(StreamType.TypeEnum::getValue).noneMatch(message.getStream().getStreamType()::equals)) {
        log.debug("Unauthorized stream type. Only {} allowed.", authorizedStreams.toString());
        return false;
      }
    }
    return true;
  }

  private boolean checkUser(CommandContext context) {
    if (authorizedUsersIds != null) {
      var user = context.getInitiator().getUser();
      if (user == null || user.getUserId() == null) {
        log.error("Couldn't find user id.");
        return false;
      } else if (!authorizedUsersIds.contains(user.getUserId().toString())) {
        log.debug("Unauthorized user: " + user.getUserId().toString());
        messageService.send(context.getStreamId(), Message.builder().content("User unauthorized").build());
        return false;
      }
    }
    return true;
  }
}
