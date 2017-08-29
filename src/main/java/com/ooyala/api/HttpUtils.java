package com.ooyala.api;

import java.net.URLEncoder;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;

public class HttpUtils {
  /**
   * The API charset to encode bytes
   */
  public static final String API_CHARSET = "ISO-8859-1";
  public static final String ENCODING = "US-ASCII";


  /**
   * Concatenates the key-values of parameters using a separator in between
   *
   * @param parameters HashMap with the key-value elements to be concatenated
   * @param separator  The separator (a char) which is added between hash elements
   * @return the concatenated string
   */
  public static String concatenateParams(Map<String, String> parameters, String separator) {
    Vector<String> keys = new Vector<String>(parameters.keySet());
    Collections.sort(keys);

    String string = "";
    for (Enumeration<String> e = keys.elements(); e.hasMoreElements(); ) {
      String key = e.nextElement();
      String value = parameters.get(key);
      if (!string.isEmpty()) {
        string += separator;
      }
      string += key + "=" + value;
    }
    return string;
  }

  /**
   * Encodes a String to be URI friendly.
   *
   * @param input The String to encode.
   * @return The encoded String.
   * @throws java.io.UnsupportedEncodingException if the encoding as US-ASCII is not supported.
   */
  public static String encodeURI(String input) throws java.io.UnsupportedEncodingException {
    return URLEncoder.encode(input, ENCODING);
  }
}
