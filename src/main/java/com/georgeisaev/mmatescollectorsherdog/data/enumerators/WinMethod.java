package com.georgeisaev.mmatescollectorsherdog.data.enumerators;

public enum WinMethod {

    KO, TKO, DECISION, SUBMISSION, OTHER;

    public static WinMethod defineWinMethod(String method) {

        String methodUpperCase = method.toUpperCase();
        switch (methodUpperCase) {
            case "KO":
            case "KNOCKOUT":
            case "K.O.":
                return KO;
            case "TKO":
            case "TECHNICAL KNOCKOUT":
            case "T.K.O.":
                return TKO;
            case "DECISION":
            case "DEC":
                return DECISION;
            case "SUBMISSION":
            case "SUB":
                return SUBMISSION;
            default:
                return OTHER;
        }
    }

}
