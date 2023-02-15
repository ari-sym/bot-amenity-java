package com.symphony.profserv.api.commands.matchers;

import lombok.Data;

@Data
public class CommandIdObject {

  private String type;
  private String value;

  public boolean isUserId() {
    return "com.symphony.user.userId".equals(type);
  }
}
