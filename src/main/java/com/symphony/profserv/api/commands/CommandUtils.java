package com.symphony.profserv.api.commands;

import com.symphony.bdk.core.activity.command.CommandContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Locale;

@Slf4j
public class CommandUtils {

  /**
   * @param context command context
   * @return the type of stream in upper case (IM or room)
   */
  public static String getStreamType(CommandContext context) {
    try {
      return context.getSourceEvent().getMessage().getStream().getStreamType().toUpperCase(Locale.ROOT);
    } catch (NullPointerException exception) {
      log.error("NPE - Could not find Stream Type: " + exception.getMessage());
      return "";
    }
  }

}
