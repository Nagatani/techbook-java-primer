/**
 * リストAC-1
 * コードスニペット
 * 
 * 元ファイル: appendix-c-theoretical-foundations.md (42行目)
 */

// 複雑度が高いコード例（複雑度: 6）
public String processData(int type, boolean flag, String data) {
    if (type == 1) {                    // +1
        if (flag) {                     // +1
            if (data.length() > 10) {   // +1
                return "Type1-Long";
            } else {                    // +1
                return "Type1-Short";
            }
        } else {                        // +1
            return "Type1-Default";
        }
    } else if (type == 2) {             // +1
        return "Type2";
    }
    return "Unknown";
}