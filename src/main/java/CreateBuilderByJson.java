import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.CaseFormat;
import com.sun.javafx.binding.StringFormatter;
import javafx.util.Pair;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;
import utils.ObjectMapperCreator;

public class CreateBuilderByJson {
  private static boolean builderMode = false;
  private static boolean createExpression = true;
  private static boolean isTudiHandler = false;
  private static boolean createBasicBuilder = true;

  public static void main(String[] args) throws IOException, URISyntaxException, NoSuchFieldException, InstantiationException, IllegalAccessException {

    Class<?> type = Integer.class;
    ObjectMapper objectMapper = ObjectMapperCreator.objectMapper();

    if (createBasicBuilder) {
      createBasicBuilder(type);
    } else {
      if (createExpression) {
        toCreateExpression(type);
      } else {
        final Object o = toInitJson(type);
        System.out.println(objectMapper.writeValueAsString(o));
      }
    }
  }

  private static void createBasicBuilder(Class<?> type) {
    final String variableName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, type.getSimpleName());
    String output = type.getSimpleName() + " " + variableName + "=" + type.getSimpleName() + ".builder()\n";
    for (Field field : type.getDeclaredFields()) {
      output += StringFormatter.format(".%s()\n", field.getName()).getValue();
    }
    output += ".build();";
    System.out.println(output);
  }

  private static Object toInitJson(final Class<?> type) throws InstantiationException, IllegalAccessException {
    final Field[] declaredFields = type.getDeclaredFields();
    final Object o = type.newInstance();
    for (Field field : declaredFields) {
      if (field.getType() == BigDecimal.class) {
        field.set(o, BigDecimal.ONE);
      } else if (field.getType() == Integer.class) {
        field.set(o, 0);
      } else if (field.getType() == String.class) {
        field.set(o, "");
      } else {
//        field.set(o, toInitJson(field.getType()));
      }
    }

    return o;
  }

  private static void toCreateExpression(final Class<?> type) throws IOException, URISyntaxException, NoSuchFieldException {
    URL file1 = CompareExcelFile.class.getClassLoader().getResource("json.txt");
    String text = new String(Files.readAllBytes(Paths.get(file1.toURI())), StandardCharsets.UTF_8);

    try {
      final JSONArray jsonArray = new JSONArray(text);
      final String listName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, type.getSimpleName()) + "List";
      getListExpression(type, jsonArray, listName);
    } catch (JSONException e) {
      JSONObject obj = new JSONObject(text);
      getObject(obj, type, null);
    }

    System.out.println();
    System.out.println();
    System.out.println();
  }

  /*
   * Print builder expression of object and then return the variable naming
   * */
  private static String getObject(JSONObject obj, Class<?> type, Integer numAfterVariableName) throws NoSuchFieldException {
    String variableName = getFieldNameByJsonAttribute(type.getSimpleName(), CaseFormat.UPPER_CAMEL);
    if (numAfterVariableName != null) {
      variableName += numAfterVariableName;
    }

    Map<String, String> builderMap = new HashMap<>();

    for (String key : obj.keySet()) {
      final String fieldName = getFieldNameByJsonAttribute(key, CaseFormat.LOWER_UNDERSCORE);
      final Object value = obj.get(key);

      Object fieldValue;
      if (value instanceof JSONObject) {
        if (type.getDeclaredField(fieldName).getType() == Map.class) {
          final String mapExpression = getMapExpression((JSONObject) value, (ParameterizedType) type.getDeclaredField(fieldName).getGenericType(), fieldName);
          final Field field = getField(type, fieldName, key);
          builderMap.put(field.getName(), mapExpression);
        } else {
          final String innerObject = getObject((JSONObject) value, type.getDeclaredField(fieldName).getType(), null);
          final Field field = getField(type, fieldName, key);
          builderMap.put(field.getName(), innerObject);
        }
      } else if (value instanceof JSONArray) {
        JSONArray jsonArray = (JSONArray) value;
        final Class<?> subType = (Class<?>) ((ParameterizedTypeImpl) type.getDeclaredField(fieldName).getGenericType()).getActualTypeArguments()[0];
        final String listName = getFieldNameByJsonAttribute(fieldName, CaseFormat.UPPER_CAMEL);

        if (!jsonArray.isEmpty() && !(((JSONArray) value).get(0) instanceof JSONObject)) {
          getListExpression(subType, jsonArray, listName);
          final Field field = getField(type, fieldName, key);
          builderMap.put(field.getName(), listName);
          continue;
        }

        if (!jsonArray.isEmpty() || jsonArray != null) {
          getListExpression(subType, jsonArray, listName);

          final Field field = getField(type, fieldName, key);
          builderMap.put(field.getName(), listName);
        }
      } else {
        try {
          final Field field = getField(type, fieldName, key);
          final Class<?> fieldType = field.getType();
          fieldValue = getFieldValueString(value, fieldType);
          builderMap.put(field.getName(), fieldValue.toString());
        } catch (Exception e) {
          System.out.printf("ignore field=%s\n", fieldName);
        }
      }
    }

    String output;
    if (builderMode) {
      output = type.getSimpleName() + " " + variableName + "=" + type.getSimpleName() + ".builder()";
      for (Field field : type.getDeclaredFields()) {
        final String value = builderMap.get(field.getName());
        if (value != null) {
          output += StringFormatter.format(".%s(%s)", field.getName(), value).getValue();
        }
      }
      output += ".build();";
    } else {
      output = StringFormatter.format("%s %s = new %s(", type.getSimpleName(), variableName, type.getSimpleName()).getValue();
      for (Field field : type.getDeclaredFields()) {
        final String value = builderMap.get(field.getName());
        output += StringFormatter.format("%s,", value).getValue();
      }
      output = StringUtils.removeEnd(output, ",");
      output += ");";
    }
    System.out.println(output);
    return variableName;
  }

  private static String getFieldNameByJsonAttribute(final String key, final CaseFormat lowerUnderscore) {
    if (isTudiHandler) {
      return key;
    } else {
      return lowerUnderscore.to(CaseFormat.LOWER_CAMEL, key);
    }
  }

  /**
   * print
   * List<?> list = ArrayList<>();
   * list.add(element1);
   * list.add(element2);
   *
   * @param subType
   * @param jsonArray
   * @param listName
   * @throws NoSuchFieldException
   */
  private static void getListExpression(final Class<?> subType, final JSONArray jsonArray, final String listName) throws NoSuchFieldException {
    List<String> elementNames = new ArrayList<>();
    for (int i = 0; i < jsonArray.length(); i++) {
      if (jsonArray.get(i) instanceof JSONObject) {
        final String objectInList = getObject(jsonArray.getJSONObject(i), subType, i + 1);
        elementNames.add(objectInList);
      } else {
        final Object objectInList = getFieldValueString(jsonArray.get(i), subType);
        elementNames.add((String) objectInList);
      }
    }
    System.out.printf("List<%s> %s= new ArrayList<>();%n", subType.getSimpleName(), listName);
    elementNames.forEach(x -> System.out.printf("%s.add(%s);%n", listName, x));
  }

  private static Object getFieldValueString(final Object value, final Class<?> fieldType) {
    Object fieldValue;
    if (fieldType == String.class) {
      fieldValue = "\"" + value + "\"";
    } else if (fieldType == BigDecimal.class) {
      fieldValue = "BigDecimal.valueOf(" + value + ")";
    } else if (fieldType == LocalDate.class) {
      fieldValue = "CommonUtils.parseDate(\"" + value + "\")";
//          fieldValue = "CommonUtility.parseDate(\"" + value + "\")";
    } else if (fieldType == LocalTime.class) {
      fieldValue = "CommonUtils.parseTime(\"" + value + "\")";
//          fieldValue = "";
    } else if (fieldType == LocalDateTime.class) {
      fieldValue = "CommonUtils.parseTime(\"" + value + "\")";
//          fieldValue = "CommonUtility.parseTime(\"" + value + "\")";
    } else {
      fieldValue = value;
    }
    return fieldValue;
  }

  /**
   * print
   * Map<K,V> map = new HashMap<>();
   * map.put(k1, v1);
   * map.put(k1, v2);
   * map.put(k2, v3);
   *
   * @param obj       jsonObject of Map
   * @param type      ParameterizedType of the Map
   * @param fieldName
   * @return
   * @throws NoSuchFieldException
   */
  private static String getMapExpression(JSONObject obj, ParameterizedType type, final String fieldName) throws NoSuchFieldException {
    final Type[] actualTypeArguments = type.getActualTypeArguments();
    final Type K = actualTypeArguments[0];
    final Type V = actualTypeArguments[1];

    String VSimpleName = getValueTypeString(V);

    List<Pair<String, String>> mapPairList = new ArrayList<>();
    for (String key : obj.keySet()) {
      if (obj.get(key) instanceof JSONObject) {
        final String variableName = getObject(obj.getJSONObject(key), (Class<?>) V, null);
        final String keyName = getFieldValueString(key, (Class<?>) K).toString();
        mapPairList.add(new Pair<>(keyName, variableName));
      } else {
        final JSONArray jsonArray = obj.getJSONArray(key);
        final Class<?> listGenericType = (Class<?>) ((ParameterizedTypeImpl) V).getActualTypeArguments()[0];
        final String listName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, listGenericType.getSimpleName()) + "List";
        getListExpression(listGenericType, jsonArray, listName);
        final String keyName = getFieldValueString(key, (Class<?>) K).toString();
        mapPairList.add(new Pair<>(keyName, listName));
      }
    }
    System.out.printf("Map<%s,%s> %s = new HashMap<>();%n", ((Class) K).getSimpleName(), VSimpleName, fieldName);

    for (Pair<String, String> pair : mapPairList) {
      System.out.printf("%s.put(%s,%s);%n", fieldName, pair.getKey(), pair.getValue());
    }
    return fieldName;
  }

  private static String getValueTypeString(final Type V) {
    String VSimpleName = "";
    if (V instanceof ParameterizedType) {
      final Type[] actualTypeArguments1 = ((ParameterizedType) V).getActualTypeArguments();
      final String simpleName = ((ParameterizedTypeImpl) V).getRawType().getSimpleName();
      VSimpleName += simpleName + "<" + ((Class) actualTypeArguments1[0]).getSimpleName();
      for (Type g : Arrays.copyOfRange(actualTypeArguments1, 1, actualTypeArguments1.length)) {
        VSimpleName += "," + ((Class) g).getSimpleName();
      }
      VSimpleName += ">";
    } else {
      VSimpleName = ((Class) V).getSimpleName();
    }
    return VSimpleName;
  }

  private static Field getField(Class<?> type, String fieldName, String jsonFieldName) {
    try {
      final Field field = type.getDeclaredField(fieldName);
      return field;
    } catch (NoSuchFieldException e) {
      final Field[] fields = type.getDeclaredFields();
      for (Field field : fields) {
        final JsonProperty declaredAnnotation = field.getDeclaredAnnotation(JsonProperty.class);
        if (declaredAnnotation != null && declaredAnnotation.value().equals(jsonFieldName)) {
          return field;
        }
      }
    }
    return null;
  }
}
