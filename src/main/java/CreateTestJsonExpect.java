import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class CreateTestJsonExpect {
    public static void main(String[] args) throws Exception {
        URL file1 = CompareExcelFile.class.getClassLoader().getResource("json.txt");
        final FileInputStream input = new FileInputStream(file1.getFile());

        String jsonTxt = IOUtils.toString(input, "UTF-8");
        System.out.println(jsonTxt);
        String base;
        Object json;
        try {
            json = new JSONObject(jsonTxt);
            base = "$";
        } catch (JSONException e) {
            json = new JSONArray(jsonTxt);
            base = "$.";
        }
        List<String> expects = new ArrayList<>();
        printValue(json, base, expects);
        Collections.reverse(expects);
        expects.forEach(x -> System.out.print(x));
    }

    public static void printValue(Object o, String jsonPath, final List<String> expects) {
        if (o instanceof JSONArray) {
            JSONArray jsonarray = (JSONArray) o;
            if (jsonarray.length() != 0) {
                for (int i = 0; i < jsonarray.length(); i++) {
                    String newJsonPath = jsonPath + "[" + i + "]";
                    printValue(jsonarray.get(i), newJsonPath, expects);
                }
            } else {
                expects.add(String.format(".andExpect(jsonPath(\"%s.length()\").value(%s))\n", jsonPath, 0));
            }
        } else if (o instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) o;
            for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
                final String key = it.next();
                final Object childObject = jsonObject.get(key);
                String newJsonPath = jsonPath + "." + key;
                printValue(childObject, newJsonPath, expects);
            }
        } else {
            if (o instanceof Integer || o instanceof Double || o instanceof Boolean || o instanceof BigDecimal) {
                expects.add(String.format(".andExpect(jsonPath(\"%s\").value(%s))\n", jsonPath, o));
            } else if (o == JSONObject.NULL) {
                expects.add(String.format(".andExpect(jsonPath(\"%s\").value(nullValue()))\n", jsonPath));
            } else {
                expects.add(String.format(".andExpect(jsonPath(\"%s\").value(\"%s\"))\n", jsonPath, o));
            }
        }
    }
}
