import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPOutputStream;

public class StringCompress {

  public static void main(String[] args) throws UnsupportedEncodingException {
    final String str = "{\"record_json\":\"{\\\"record_id\\\":836354,\\\"record_type\\\":0,\\\"diagnoses\\\":[{\\\"id\\\":32514,\\\"ins_id\\\":\\\"M48.006\\\",\\\"name\\\":\\\"颈腰综合征\\\",\\\"pinyin\\\":\\\"JYZHZ\\\"}],\\\"special_diagnoses\\\":[],\\\"history\\\":\\\"患者有颈腰病史，否认有传染病史。14天无发热咳嗽，腹泻病史,无疫区疫病过往接触史。新冠疫苗无异常反应。\\\\n\\\\n\\\",\\\"allergy\\\":\\\"无\\\",\\\"chief_complaint\\\":\\\"颈腰部酸胀疼痛不适半月多\\\",\\\"current_history\\\":\\\"半月前，无明显诱因下出现颈腰部酸胀疼痛不适，活动后不能缓解，随即来我门诊就诊。\\\",\\\"body_feature\\\":\\\"{\\\\\\\"pre\\\\\\\":[],\\\\\\\"cur\\\\\\\":[{\\\\\\\"name\\\\\\\":\\\\\\\"半月前，无明显诱因下出现颈腰部酸胀疼痛不适，活动后不能缓解，随即来我门诊就诊。\\\\\\\",\\\\\\\"property\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"time\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"status\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"trend\\\\\\\":\\\\\\\"\\\\\\\"}]}\\\",\\\"tongue_pulse\\\":\\\"舌红苔白\\\",\\\"lab\\\":\\\"\\\",\\\"pattern_treatment\\\":\\\"活血化瘀 可做推拿）治疗\\\",\\\"medicines\\\":[],\\\"herbs\\\":[],\\\"materials\\\":[],\\\"out_medicines\\\":[],\\\"out_herbs\\\":[],\\\"medicine_remarks\\\":[],\\\"physical_exams\\\":[{\\\"exam_id\\\":4944,\\\"name\\\":\\\"血压(收缩压)\\\",\\\"data_type\\\":1,\\\"data_unit\\\":\\\"mmHg\\\",\\\"value\\\":\\\"120\\\"},{\\\"exam_id\\\":4946,\\\"name\\\":\\\"血压(舒张压)\\\",\\\"data_type\\\":1,\\\"data_unit\\\":\\\"mmHg\\\",\\\"value\\\":\\\"75\\\"},{\\\"exam_id\\\":5047,\\\"name\\\":\\\"体温（度）\\\",\\\"data_type\\\":1,\\\"data_unit\\\":\\\"\\\",\\\"value\\\":\\\"36.2\\\"},{\\\"exam_id\\\":5049,\\\"name\\\":\\\"其他\\\",\\\"data_type\\\":2,\\\"data_unit\\\":\\\"\\\",\\\"value\\\":\\\"\\\"}],\\\"treatments\\\":[{\\\"treatment_id\\\":136886,\\\"count\\\":1,\\\"remark\\\":\\\"\\\",\\\"price\\\":97,\\\"materials\\\":[],\\\"medicines\\\":[]},{\\\"treatment_id\\\":136896,\\\"count\\\":1,\\\"remark\\\":\\\"\\\",\\\"price\\\":97,\\\"materials\\\":[],\\\"medicines\\\":[]}],\\\"mn_exam\\\":null,\\\"referral\\\":3,\\\"remark\\\":\\\"\\\",\\\"track_status\\\":0,\\\"track_time\\\":\\\"\\\",\\\"outbreak_date\\\":\\\"NaN/NaN/NaN\\\",\\\"public_remark\\\":\\\"\\\",\\\"cook_type\\\":0,\\\"out_cook_type\\\":0}\",\"REMOTE_IP\":\"10.120.19.10\",\"page\":\"Doctor\",\"op\":\"updatePatientRecord\"}";
    final String compress = compress(str);

    final byte[] utf8Bytes1 = str.getBytes("UTF-8");

    final byte[] utf8Bytes = compress.getBytes("UTF-8");

    System.out.println(utf8Bytes1.length);
    System.out.println(utf8Bytes.length);
  }

  public static String compress(String str) {
    if (str == null || str.length() == 0) {
      return str;
    }
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    GZIPOutputStream gzip = null;
    try {
      gzip = new GZIPOutputStream(out);
      gzip.write(str.getBytes());
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (gzip != null) {
        try {
          gzip.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return new sun.misc.BASE64Encoder().encode(out.toByteArray());
  }
}
