package com.symphony.profserv.repositories;

import com.symphony.bdk.core.activity.model.ActivityInfo;
import lombok.Data;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Data
@Repository
public class HelpCommandList {

  private List<ActivityInfo> commandActivities = new ArrayList<>();

}
