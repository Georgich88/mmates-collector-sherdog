package com.georgeisaev.mmatescollectorsherdog.data.enumerators;

public enum FightType {
  PRO,
  PRO_EXHIBITION,
  EXHIBITION,
  AMATEUR,
  OTHER,
  UPCOMING;

  public static FightType fromString(String type) {
    return switch (type.toUpperCase().trim()) {
      case "PRO" -> PRO;
      case "PRO EXHIBITION" -> PRO_EXHIBITION;
      case "AMATEUR" -> AMATEUR;
      case "EXHIBITION" -> EXHIBITION;
      default -> OTHER;
    };
  }
}
