import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class SQLStatement {
  public static void main(String[] args) {
    URL file1 = CompareExcelFile.class.getClassLoader().getResource("delete.xlsx");

    toValueMap(file1);
  }

  private static void toValueMap(final URL file1) {
    Map<String, List<String>> file1ValueMap = new HashMap<>();

    try {
      final FileInputStream input = new FileInputStream(file1.getFile());
      @SuppressWarnings("resource")
      XSSFWorkbook book = new XSSFWorkbook(input);
      XSSFSheet sheet = book.getSheetAt(0);
      Set<String> set = new HashSet<>();

      for (int i = 1; i <= sheet.getLastRowNum(); i++) {
        XSSFRow row = sheet.getRow(i);

        if (row.getCell(0) == null) {
          continue;
        }
        row.getCell(0).setCellType(CellType.STRING);
        String key = row.getCell(0).getStringCellValue();

        final XSSFCell cell = row.getCell(7);
        if (cell != null) {
          final String delete = cell.toString();
          if (delete.equals("V")) {
            set.add(key);
          }
          if (delete.length() > 5) {
            System.out.printf("update share_medicine s set s.approval_id = '%s' where s.id = '%s';\n", delete.trim(), key);
          }
        }
      }

      final String join = StringUtils.join(set, ",");

      System.out.printf("delete from share_medicine where id in ( %s )", join);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
