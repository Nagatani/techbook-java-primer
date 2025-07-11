/**
 * リスト10-10
 * コードスニペット
 * 
 * 元ファイル: chapter10-collections.md (281行目)
 */

Set<String> set1 = new HashSet<>(Arrays.asList("A", "B", "C"));
Set<String> set2 = new HashSet<>(Arrays.asList("B", "C", "D"));

// 和集合
Set<String> union = new HashSet<>(set1);
union.addAll(set2);  // {A, B, C, D}

// 積集合
Set<String> intersection = new HashSet<>(set1);
intersection.retainAll(set2);  // {B, C}

// 差集合
Set<String> difference = new HashSet<>(set1);
difference.removeAll(set2);  // {A}