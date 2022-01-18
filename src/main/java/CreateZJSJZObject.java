import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import com.google.common.base.CaseFormat;
import org.apache.commons.lang3.StringUtils;

public class CreateZJSJZObject {
  public static void main(String[] args) throws URISyntaxException, IOException {
    URL file1 = CompareExcelFile.class.getClassLoader().getResource("json.txt");
    List<String> text = Files.readAllLines(Paths.get(file1.toURI()));
    System.out.println("\n");

    for (String line : text) {
      if (StringUtils.isEmpty(line)) {
        break;
      }
      final String[] split = line.split("\t", 8);
      String fieldName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, split[1]);
      String fieldDescription = split[2];
      Boolean codeType = split.length > 6 && split[5].equals("Y");
      String type;
      if (codeType) {
        type = "Integer";
        fieldDescription += " (代碼)";
      } else {
        if (split[3].equals("字符型")) {
          type = "String";
        } else if (split[3].equals("日期时间型")) {
          type = "LocalDateTime";
        } else {
          type = "???";
        }
      }
      System.out.printf("private %s %s; // %s \n", type, fieldName, fieldDescription);
    }

    System.out.println("\n\n");
  }
}
