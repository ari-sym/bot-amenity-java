package com.symphony.profserv;

import com.symphony.bdk.core.activity.AbstractActivity;
import com.symphony.bdk.core.activity.ActivityRegistry;
import com.symphony.bdk.core.activity.model.ActivityInfo;
import com.symphony.bdk.core.activity.model.ActivityType;
import com.symphony.profserv.repositories.HelpCommandList;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Objects;
import java.util.stream.Collectors;

@SpringBootApplication
public class BotApplication {

    public BotApplication(HelpCommandList helpCommandList, ActivityRegistry activityRegistry) {
        // retrieve list of commands activity info
        var commands = activityRegistry.getActivityList().stream()
          .map(AbstractActivity::getInfo)
          .filter(Objects::nonNull)
          .filter(this::isCommand).collect(Collectors.toList());
        // update help command list
        helpCommandList.setCommandActivities(commands);
    }

    public static void main(String[] args) {
        SpringApplication.run(BotApplication.class, args);
    }

    /**
     * @param info an activity's basic info
     * @return whether the activity is a command or not
     */
    private boolean isCommand(ActivityInfo info) {
        return Objects.equals(info.type(), ActivityType.COMMAND);
    }

}