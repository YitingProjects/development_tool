import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.comments.LineComment;
import com.google.common.base.CaseFormat;
import com.sun.javafx.binding.StringFormatter;
import utils.PropertiesReader;

public class CreateMdByObject {
  public static void main(String[] args) throws URISyntaxException {
//    String filePath = "com/tudihis/ushis/dto/clinic/ClinicSettingDto.java";
    String filePath = "ybserver/src/main/java/cn/tudihis/ybserver/dto/zjsjz/output/ZJSJZ1901OutputNode.java";

    PropertiesReader propertiesReader = new PropertiesReader();
    final String projectRoot = propertiesReader.getProperty("java-project-root3");
    final File file = new File(projectRoot + "/" + filePath);
    try {
      final CompilationUnit parse = StaticJavaParser.parse(file);
      final TypeDeclaration<?> type = parse.getType(0);
      final List<FieldDeclaration> fields = type.getFields();

      final String className = type.getName().asString();
      System.out.println("__" + className + "__");

      for (FieldDeclaration field : fields) {
        String typeName = field.getElementType().asString();
        final String variableName = field.getVariables().get(0).getNameAsString();
        String camelVariableName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, variableName);
        final String comment = field.getComment().orElse(
          field.getVariable(0).getComment().orElse(new LineComment(""))
        ).getContent().trim();

        if (typeName.startsWith("List")) {
          typeName = toArrayName(typeName) + "[]";
        }

        System.out.println(StringFormatter.format("* %s %s: %s", typeName, camelVariableName, comment).getValue());
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    System.out.println();
    System.out.println();
    System.out.println();
  }

  private static String toArrayName(String name) {
    final Pattern compile = Pattern.compile("List<(.*)>");
    final Matcher matcher = compile.matcher(name);
    matcher.find();
    final String type = matcher.group(1);
    return type;
  }
}
