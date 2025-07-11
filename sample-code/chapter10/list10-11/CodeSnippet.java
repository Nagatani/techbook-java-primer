/**
 * リスト10-11
 * コードスニペット
 * 
 * 元ファイル: chapter10-collections.md (305行目)
 */

// HashMap: 高速な検索・挿入（順序は保証されない）
Map<String, Integer> hashMap = new HashMap<>();
hashMap.put("Java", 25);
hashMap.put("Python", 30);
hashMap.put("JavaScript", 27);

// TreeMap: キーでソートされた順序を維持
Map<String, Integer> treeMap = new TreeMap<>();
treeMap.putAll(hashMap);
// キーのアルファベット順で格納

// LinkedHashMap: 挿入順序を維持
Map<String, Integer> linkedHashMap = new LinkedHashMap<>();
linkedHashMap.putAll(hashMap);
// 挿入した順序で格納