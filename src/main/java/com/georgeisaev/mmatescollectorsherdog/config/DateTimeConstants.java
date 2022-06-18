package com.georgeisaev.mmatescollectorsherdog.config;

import lombok.experimental.UtilityClass;

import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import static java.time.format.DateTimeFormatter.ofPattern;

@UtilityClass
public class DateTimeConstants {

  public static final DateTimeFormatter DATE_FORMAT_YYYY_MM_DD = ofPattern("yyyy-MM-dd");
  public static final DateTimeFormatter DATE_FORMATTER_MMM_DD_YYYY_1 =
      ofPattern("MMM / dd / yyyy", Locale.US);
  public static final DateTimeFormatter DATE_FORMATTER_MMM_DD_YYYY_2 =
      ofPattern("MMM d, yyyy", Locale.US);

  public static Collection<DateTimeFormatter> dateTimeFormatters() {
    return List.of(
        DATE_FORMAT_YYYY_MM_DD, DATE_FORMATTER_MMM_DD_YYYY_1, DATE_FORMATTER_MMM_DD_YYYY_2);
  }
}
