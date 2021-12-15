import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CompareExcelFile {
  public static void main(String[] args) {
    URL file1 = CompareExcelFile.class.getClassLoader().getResource("1.xlsx");
    URL file2 = CompareExcelFile.class.getClassLoader().getResource("2.xlsx");

    final Map<String, List<String>> file1ValueMap = toValueMap(file1);
    final Map<String, List<String>> file2ValueMap = toValueMap(file2);

    for (String key : file1ValueMap.keySet()) {
      final List<String> value1 = file1ValueMap.get(key);
      final List<String> value2 = file2ValueMap.get(key);
      if (!value1.equals(value2)) {
        System.out.println(key);
      }
    }
  }

  private static Map<String, List<String>> toValueMap(final URL file1) {
    Map<String, List<String>> file1ValueMap = new HashMap<>();

    try {
      final FileInputStream input = new FileInputStream(file1.getFile());
      @SuppressWarnings("resource")
      XSSFWorkbook book = new XSSFWorkbook(input);
      XSSFSheet sheet = book.getSheetAt(0);

      for (int i = 1; i <= sheet.getLastRowNum(); i++) {
        XSSFRow row = sheet.getRow(i);

        String key = row.getCell(0).toString() + row.getCell(1).toString();
        List<String> values = new ArrayList<>();

        final short lastCellNum = row.getLastCellNum();
        for (int j = 2; j < lastCellNum; j++) {
          values.add(row.getCell(j).toString());
        }
        file1ValueMap.put(key, values);
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return file1ValueMap;
  }
}
