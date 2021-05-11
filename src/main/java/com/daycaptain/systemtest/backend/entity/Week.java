package com.daycaptain.systemtest.backend.entity;

import com.daycaptain.systemtest.backend.WeekDeserializer;

import javax.json.bind.annotation.JsonbTypeDeserializer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonbTypeDeserializer(WeekDeserializer.class)
public class Week {

    public List<Task> tasks = new ArrayList<>();

    public Map<LocalDate, Day> days = new HashMap<>();

}
