import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import utils.ObjectMapperCreator;

public class CreateJsonByClass {
  public static void main(String[] args) throws Exception {
    Class classType = Integer.class;

    ObjectMapper objectMapper = ObjectMapperCreator.objectMapper();
    objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
    final Object o = classType.newInstance();
    System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o));
  }
}
