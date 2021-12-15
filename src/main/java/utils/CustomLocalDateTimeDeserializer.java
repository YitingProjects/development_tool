package utils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

public class CustomLocalDateTimeDeserializer extends LocalDateTimeDeserializer {
  public CustomLocalDateTimeDeserializer(DateTimeFormatter formatter) {
    super(formatter);
  }

  /**
   * Since 2.10
   */
  protected CustomLocalDateTimeDeserializer(LocalDateTimeDeserializer base, Boolean leniency) {
    super(base, leniency);
  }

  @Override
  protected LocalDateTimeDeserializer withDateFormat(DateTimeFormatter formatter) {
    return new LocalDateTimeDeserializer(formatter);
  }

  @Override
  protected LocalDateTimeDeserializer withLeniency(Boolean leniency) {
    return new CustomLocalDateTimeDeserializer(this, leniency);
  }

  @Override
  protected LocalDateTimeDeserializer withShape(JsonFormat.Shape shape) {
    return this;
  }

  @Override
  public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
    LocalDateTime deserialize;
    try {
      deserialize = super.deserialize(parser, context);
    } catch (Exception e) {
      deserialize = null;
    }
    return deserialize;
  }
}
