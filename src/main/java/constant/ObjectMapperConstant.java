package constant;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class ObjectMapperConstant {
  public static final java.time.format.DateTimeFormatter DateFormat = java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd");
  public static final java.time.format.DateTimeFormatter TimeFormat = java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
  public static final DateTimeFormatter TimeJodaFormat = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss");
  public static final DateTimeFormatter DateJodaFormat = DateTimeFormat.forPattern("yyyy/MM/dd");
}
