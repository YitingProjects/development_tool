package utils;

import java.io.IOException;
import java.util.Properties;

public class PropertiesReader {
  Properties prop;

  public PropertiesReader() {
    prop = new Properties();
    try {
      //load a properties file from class path, inside static method
      prop.load(PropertiesReader.class.getClassLoader().getResourceAsStream("config.properties"));
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public String getProperty(String name) {
    return prop.getProperty(name);
  }
}
