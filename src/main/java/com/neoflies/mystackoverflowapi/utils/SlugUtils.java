package com.neoflies.mystackoverflowapi.utils;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public class SlugUtils {
  private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
  private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

  public static String slugify(String message) {
    if (message == null) {
      return "";
    }

    String noWhiteSpace = WHITESPACE.matcher(message).replaceAll("-");
    String normalized = Normalizer.normalize(noWhiteSpace, Normalizer.Form.NFD);
    String slug = NON_LATIN.matcher(normalized).replaceAll("");
    return slug.toLowerCase(Locale.ENGLISH);
  }}
