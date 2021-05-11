package com.daycaptain.systemtest.backend;

import com.daycaptain.systemtest.backend.entity.Day;

import javax.json.JsonObject;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.Map;

public class DayDateDeserializer implements JsonbDeserializer<Map<LocalDate, Day>> {

    @Override
    public Map<LocalDate, Day> deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        JsonObject object = parser.getObject();
        System.out.println("object = " + object);
        return Map.of();
    }

}
