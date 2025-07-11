/**
 * リスト10-9
 * コードスニペット
 * 
 * 元ファイル: chapter10-collections.md (255行目)
 */

// HashSet: ハッシュテーブルを使用（順序は保証されない）
Set<Integer> hashSet = new HashSet<>();
hashSet.add(3);
hashSet.add(1);
hashSet.add(2);
// 出力順序は不定

// TreeSet: ソートされた順序を維持
Set<Integer> treeSet = new TreeSet<>();
treeSet.add(3);
treeSet.add(1);
treeSet.add(2);
// 出力: 1, 2, 3（昇順）

// LinkedHashSet: 挿入順序を維持
Set<Integer> linkedHashSet = new LinkedHashSet<>();
linkedHashSet.add(3);
linkedHashSet.add(1);
linkedHashSet.add(2);
// 出力: 3, 1, 2（挿入順）