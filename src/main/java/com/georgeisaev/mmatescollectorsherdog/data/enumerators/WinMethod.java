package com.georgeisaev.mmatescollectorsherdog.data.enumerators;

import java.util.NoSuchElementException;

public enum WinMethod {
  KO,
  TKO,
  DECISION,
  SUBMISSION,
  TECHNICAL_SUBMISSION,
  NC,
  OTHER;

  public static WinMethod winMethod(final String methodAndDetails) {
    final String[] methodDetails = methodAndDetails.split("[()]");
    if (methodDetails.length == 0) {
      throw new NoSuchElementException("Cannot define win method from " + methodAndDetails);
    }
    final String methodUpperCase = methodDetails[0].toUpperCase().trim();
    return switch (methodUpperCase) {
      case "KO", "KNOCKOUT", "K.O." -> KO;
      case "TKO", "TECHNICAL KNOCKOUT", "T.K.O." -> TKO;
      case "DECISION", "DEC" -> DECISION;
      case "SUBMISSION", "SUB" -> SUBMISSION;
      case "TECHNICAL SUBMISSION" -> TECHNICAL_SUBMISSION;
      case "NO CONTEST", "NC", "N/C" -> NC;
      default -> OTHER;
    };
  }

  public static String winMethodDetails(final String methodAndDetails) {
    final String[] methodDetails = methodAndDetails.split("[()]");
    if (methodDetails.length < 2) {
      throw new NoSuchElementException("Cannot define win method details from " + methodAndDetails);
    }
    return methodDetails[1].trim();
  }

}
