package utils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;

public class CustomLocalDateDeserializer extends LocalDateDeserializer {
  public CustomLocalDateDeserializer(DateTimeFormatter formatter) {
    super(formatter);
  }

  public CustomLocalDateDeserializer(LocalDateDeserializer base, DateTimeFormatter dtf) {
    super(base, dtf);
  }

  protected CustomLocalDateDeserializer(LocalDateDeserializer base, JsonFormat.Shape shape) {
    super(base, shape);
  }

  @Override
  protected CustomLocalDateDeserializer withDateFormat(DateTimeFormatter dtf) {
    return new CustomLocalDateDeserializer(this, dtf);
  }

  protected LocalDateDeserializer withShape(JsonFormat.Shape shape) {
    return new CustomLocalDateDeserializer(this, shape);
  }

  @Override
  public LocalDate deserialize(JsonParser parser, DeserializationContext context) throws IOException {
    LocalDate deserialize;
    try {
      deserialize = super.deserialize(parser, context);
    } catch (Exception e) {
      deserialize = null;
    }
    return deserialize;
  }
}
