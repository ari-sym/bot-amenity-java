package com.symphony.profserv.api.commands.matchers;

import lombok.Data;

import java.util.List;

@Data
public class CommandDataObject {

  private List<CommandIdObject> id;
  private String type;

  public boolean isMention() {
    return "com.symphony.user.mention".equals(type);
  }
}
