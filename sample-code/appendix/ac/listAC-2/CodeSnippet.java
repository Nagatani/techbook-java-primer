/**
 * リストAC-2
 * コードスニペット
 * 
 * 元ファイル: appendix-c-theoretical-foundations.md (64行目)
 */

public String processData(int type, boolean flag, String data) {
    switch (type) {                     // +1
        case 1:
            return processType1(flag, data);
        case 2:
            return "Type2";
        default:
            return "Unknown";
    }
}

private String processType1(boolean flag, String data) {
    if (!flag) return "Type1-Default"; // +1
    return data.length() > 10          // +1
        ? "Type1-Long" 
        : "Type1-Short";
}