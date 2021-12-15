package utils;

import java.io.IOException;
import java.math.BigDecimal;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class BigDecimalSerializer extends JsonSerializer<BigDecimal> {
  @Override
  public void serialize(BigDecimal value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
    JsonProcessingException {
    // put your desired money style here
    jgen.writeNumber(value.setScale(4, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString());
  }
}