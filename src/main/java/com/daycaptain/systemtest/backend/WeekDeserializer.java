package com.daycaptain.systemtest.backend;

import com.daycaptain.systemtest.backend.entity.Day;
import com.daycaptain.systemtest.backend.entity.Week;
import com.daycaptain.systemtest.backend.entity.Task;

import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;

public class WeekDeserializer implements JsonbDeserializer<Week> {

    private static Type listType = new ArrayList<Task>() {
    }.getClass().getGenericSuperclass();

    @Override
    public Week deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        Week week = new Week();

        while (parser.hasNext()) {
            JsonParser.Event event = parser.next();
            if (event == JsonParser.Event.KEY_NAME && parser.getString().equals("tasks")) {
                week.tasks = ctx.deserialize(listType, parser);
            } else if (event == JsonParser.Event.KEY_NAME && Character.isDigit(parser.getString().charAt(0))) {
                week.days.put(LocalDate.parse(parser.getString()), ctx.deserialize(Day.class, parser));
            }
        }

        return week;
    }

}
