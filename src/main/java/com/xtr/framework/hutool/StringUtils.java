package com.xtr.framework.hutool;

import java.util.*;

 class StringUtils extends org.apache.commons.lang3.StringUtils {
  private static final String NULLSTR = "";

  private static final char SEPARATOR = '_';

  public static <T> T nvl(T value, T defaultValue) {
    return (value != null) ? value : defaultValue;
  }

  public static boolean isEmpty(Collection<?> coll) {
    return (isNull(coll) || coll.isEmpty());
  }

  public static boolean isNotEmpty(Collection<?> coll) {
    return !isEmpty(coll);
  }

  public static boolean isEmpty(Object[] objects) {
    return (isNull(objects) || objects.length == 0);
  }

  public static boolean isNotEmpty(Object[] objects) {
    return !isEmpty(objects);
  }

  public static boolean isEmpty(Map<?, ?> map) {
    return (isNull(map) || map.isEmpty());
  }

  public static boolean isNotEmpty(Map<?, ?> map) {
    return !isEmpty(map);
  }

  public static boolean isEmpty(String str) {
    return (isNull(str) || "".equals(str.trim()));
  }

  public static boolean isNotEmpty(String str) {
    return !isEmpty(str);
  }

  public static boolean isNull(Object object) {
    return (object == null);
  }

  public static boolean isNotNull(Object object) {
    return !isNull(object);
  }

  public static boolean isArray(Object object) {
    return (isNotNull(object) && object.getClass().isArray());
  }

  public static String trim(String str) {
    return (str == null) ? "" : str.trim();
  }

  public static String[] longToString(Long[] array){
    if(array == null || array.length == 0) return null;
    String[] strArr = new String[array.length];
    for(int i=0;i<strArr.length;i++){
      strArr[i] = String.valueOf(array[i]);
    }
    return strArr;
  }
  public static String arrayToDbInString(String[] array){
    if(array == null || array.length == 0) return null;
    StringBuilder stringBuilder = new StringBuilder();
    for(int i=0;i<array.length;i++){
      stringBuilder.append(",'"+array[i]+"'");
    }
    return stringBuilder.substring(1);
  }

  public static Long[] stringtoLong(String str, String regex) {
    String strs[] = str.split(regex);
    Long array[] = new Long[strs.length];
    for (int i = 0; i < strs.length; i++) {
      array[i] = Long.parseLong(strs[i]);
    }
    return array;
  }

  public static String substring(String str, int start) {
    if (str == null)
      return "";
    if (start < 0)
      start = str.length() + start;
    if (start < 0)
      start = 0;
    if (start > str.length())
      return "";
    return str.substring(start);
  }

  public static String substring(String str, int start, int end) {
    if (str == null)
      return "";
    if (end < 0)
      end = str.length() + end;
    if (start < 0)
      start = str.length() + start;
    if (end > str.length())
      end = str.length();
    if (start > end)
      return "";
    if (start < 0)
      start = 0;
    if (end < 0)
      end = 0;
    return str.substring(start, end);
  }

  public static String format(String template, Object... params) {
    if (isEmpty(params) || isEmpty(template))
      return template;
    return StrFormatter.format(template, params);
  }

  public static final Set<String> str2Set(String str, String sep) {
    return new HashSet<>(str2List(str, sep, true, false));
  }

  public static final List<String> str2List(String str, String sep, boolean filterBlank, boolean trim) {
    List<String> list = new ArrayList<>();
    if (isEmpty(str))
      return list;
    if (filterBlank && isBlank(str))
      return list;
    String[] split = str.split(sep);
    for (String string : split) {
      if (!filterBlank || !isBlank(string)) {
        if (trim)
          string = string.trim();
        list.add(string);
      }
    }
    return list;
  }

  public static String toUnderScoreCase(String str) {
    if (str == null)
      return null;
    StringBuilder sb = new StringBuilder();
    boolean preCharIsUpperCase = true;
    boolean curreCharIsUpperCase = true;
    boolean nexteCharIsUpperCase = true;
    for (int i = 0; i < str.length(); i++) {
      char c = str.charAt(i);
      if (i > 0) {
        preCharIsUpperCase = Character.isUpperCase(str.charAt(i - 1));
      } else {
        preCharIsUpperCase = false;
      }
      curreCharIsUpperCase = Character.isUpperCase(c);
      if (i < str.length() - 1)
        nexteCharIsUpperCase = Character.isUpperCase(str.charAt(i + 1));
      if (preCharIsUpperCase && curreCharIsUpperCase && !nexteCharIsUpperCase) {
        sb.append('_');
      } else if (i != 0 && !preCharIsUpperCase && curreCharIsUpperCase) {
        sb.append('_');
      }
      sb.append(Character.toLowerCase(c));
    }
    return sb.toString();
  }

  public static boolean inStringIgnoreCase(String str, String... strs) {
    if (str != null && strs != null)
      for (String s : strs) {
        if (str.equalsIgnoreCase(trim(s)))
          return true;
      }
    return false;
  }

  public static String convertToCamelCase(String name) {
    StringBuilder result = new StringBuilder();
    if (name == null || name.isEmpty())
      return "";
    if (!name.contains("_"))
      return name.substring(0, 1).toUpperCase() + name.substring(1);
    String[] camels = name.split("_");
    for (String camel : camels) {
      if (!camel.isEmpty()) {
        result.append(camel.substring(0, 1).toUpperCase());
        result.append(camel.substring(1).toLowerCase());
      }
    }
    return result.toString();
  }

  public static String toCamelCase(String s) {
    if (s == null)
      return null;
    s = s.toLowerCase();
    StringBuilder sb = new StringBuilder(s.length());
    boolean upperCase = false;
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (c == '_') {
        upperCase = true;
      } else if (upperCase) {
        sb.append(Character.toUpperCase(c));
        upperCase = false;
      } else {
        sb.append(c);
      }
    }
    return sb.toString();
  }

  public static final String padl(final Number num, final int size)
  {
    return padl(num.toString(), size, '0');
  }

  /**
   * 字符串左补齐。如果原始字符串s长度大于size，则只保留最后size个字符。
   *
   * @param s 原始字符串
   * @param size 字符串指定长度
   * @param c 用于补齐的字符
   * @return 返回指定长度的字符串，由原字符串左补齐或截取得到。
   */
  public static final String padl(final String s, final int size, final char c)
  {
    final StringBuilder sb = new StringBuilder(size);
    if (s != null)
    {
      final int len = s.length();
      if (s.length() <= size)
      {
        for (int i = size - len; i > 0; i--)
        {
          sb.append(c);
        }
        sb.append(s);
      }
      else
      {
        return s.substring(len - size, len);
      }
    }
    else
    {
      for (int i = size; i > 0; i--)
      {
        sb.append(c);
      }
    }
    return sb.toString();
  }
}
