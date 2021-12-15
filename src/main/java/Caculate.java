import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class Caculate {
  public static void main(String[] args) {
    String packageSpecification = "0.375g*960粒";

//    System.out.println(countDigits(packageSpecification));
    replcae();
  }

  static void replcae() {
    final String s = "山慈菇(冰球子)wwwww".replaceAll("(\\(.*\\))", "");
    System.out.println(s);
  }

  static String countDigits(String stringToSearch) {
    Pattern digitRegex = Pattern.compile("\\d+(\\.\\d+)?");
    Matcher matcher = digitRegex.matcher(stringToSearch);

    List<String> integerList = new ArrayList<>();

    while (matcher.find()) {
      final String digitalString = matcher.group();
      if (digitalString.contains(".")) {
        final BigDecimal bigDecimal = BigDecimal.valueOf(Double.valueOf(digitalString)).multiply(BigDecimal.valueOf(1000));
        final BigDecimal bigDecimal1 = bigDecimal.setScale(0);
        integerList.add(bigDecimal1.toPlainString());
      } else {
        integerList.add(digitalString);
      }
    }

    final String join = StringUtils.join(integerList, " ");
    return join;
  }
}
