package utils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import constant.ObjectMapperConstant;

public class ObjectMapperCreator {
  public static ObjectMapper objectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
    objectMapper.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.setPropertyNamingStrategy(new PropertyNamingStrategies.SnakeCaseStrategy());
    objectMapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    setModules(objectMapper);
    return objectMapper;
  }

  private static void setModules(ObjectMapper objectMapper) {
    SimpleModule simpleModule = new SimpleModule();
    simpleModule.addSerializer(BigDecimal.class, new BigDecimalSerializer());

    objectMapper.registerModule(simpleModule);
    objectMapper.registerModule(getJavaTimeModule());
  }

  private static JavaTimeModule getJavaTimeModule() {
    final JavaTimeModule javaTimeModule = new JavaTimeModule();
    javaTimeModule.addDeserializer(LocalDateTime.class, new CustomLocalDateTimeDeserializer(ObjectMapperConstant.TimeFormat));
    javaTimeModule.addDeserializer(LocalDate.class, new CustomLocalDateDeserializer(ObjectMapperConstant.DateFormat));
    javaTimeModule.addSerializer(new LocalDateSerializer(ObjectMapperConstant.DateFormat));
    javaTimeModule.addSerializer(new LocalDateTimeSerializer(ObjectMapperConstant.TimeFormat));
    return javaTimeModule;
  }
}
