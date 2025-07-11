/**
 * リスト3-58
 * コードスニペット
 * 
 * 元ファイル: chapter03-oop-basics.md (2527行目)
 */

int[] original = {1, 2, 3, 4, 5};
int[] copy1 = new int[original.length];

// System.arraycopy()を使用
System.arraycopy(original, 0, copy1, 0, original.length);

// Arrays.copyOf()を使用（より簡潔）
int[] copy2 = Arrays.copyOf(original, original.length);