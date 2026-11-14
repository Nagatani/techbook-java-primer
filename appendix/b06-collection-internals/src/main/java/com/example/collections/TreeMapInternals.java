package com.example.collections;

import java.util.*;

/**
 * TreeMap内部実装の詳細デモンストレーション
 * 赤黒木、回転操作、平衡維持の仕組みを学習
 */
public class TreeMapInternals {
    
    /**
     * 簡略化された赤黒木実装
     */
    public static class SimpleRedBlackTree<K extends Comparable<K>, V> {
        private static final boolean RED = true;
        private static final boolean BLACK = false;
        
        private Node root;
        private int size;
        private boolean verbose;

        /**
         * 回転のたびにメッセージを出すかどうか
         */
        public void setVerbose(boolean verbose) {
            this.verbose = verbose;
        }

        class Node {
            K key;
            V value;
            Node left, right, parent;
            boolean color;
            
            Node(K key, V value, boolean color) {
                this.key = key;
                this.value = value;
                this.color = color;
            }
            
            @Override
            public String toString() {
                return key + "=" + value + "(" + (color == RED ? "R" : "B") + ")";
            }
        }
        
        /**
         * 値の検索
         */
        public V get(K key) {
            Node node = getNode(key);
            return node != null ? node.value : null;
        }
        
        private Node getNode(K key) {
            Node current = root;
            while (current != null) {
                int cmp = key.compareTo(current.key);
                if (cmp == 0) return current;
                else if (cmp < 0) current = current.left;
                else current = current.right;
            }
            return null;
        }
        
        /**
         * 値の挿入
         */
        public V put(K key, V value) {
            if (root == null) {
                root = new Node(key, value, BLACK);
                size++;
                return null;
            }
            
            Node current = root;
            Node parent = null;
            int cmp = 0;
            
            // 挿入位置の検索
            while (current != null) {
                parent = current;
                cmp = key.compareTo(current.key);
                if (cmp == 0) {
                    V oldValue = current.value;
                    current.value = value;
                    return oldValue;
                } else if (cmp < 0) {
                    current = current.left;
                } else {
                    current = current.right;
                }
            }
            
            // 新しいノードの挿入
            Node newNode = new Node(key, value, RED);
            newNode.parent = parent;
            
            if (cmp < 0) {
                parent.left = newNode;
            } else {
                parent.right = newNode;
            }
            
            size++;
            
            // 赤黒木の性質を回復
            fixAfterInsertion(newNode);
            
            return null;
        }
        
        /**
         * 挿入後の修正（赤黒木の性質を維持）
         */
        private void fixAfterInsertion(Node x) {
            x.color = RED;
            
            while (x != null && x != root && x.parent.color == RED) {
                if (parentOf(x) == leftOf(parentOf(parentOf(x)))) {
                    Node y = rightOf(parentOf(parentOf(x)));
                    if (colorOf(y) == RED) {
                        setColor(parentOf(x), BLACK);
                        setColor(y, BLACK);
                        setColor(parentOf(parentOf(x)), RED);
                        x = parentOf(parentOf(x));
                    } else {
                        if (x == rightOf(parentOf(x))) {
                            x = parentOf(x);
                            rotateLeft(x);
                        }
                        setColor(parentOf(x), BLACK);
                        setColor(parentOf(parentOf(x)), RED);
                        rotateRight(parentOf(parentOf(x)));
                    }
                } else {
                    Node y = leftOf(parentOf(parentOf(x)));
                    if (colorOf(y) == RED) {
                        setColor(parentOf(x), BLACK);
                        setColor(y, BLACK);
                        setColor(parentOf(parentOf(x)), RED);
                        x = parentOf(parentOf(x));
                    } else {
                        if (x == leftOf(parentOf(x))) {
                            x = parentOf(x);
                            rotateRight(x);
                        }
                        setColor(parentOf(x), BLACK);
                        setColor(parentOf(parentOf(x)), RED);
                        rotateLeft(parentOf(parentOf(x)));
                    }
                }
            }
            root.color = BLACK;
        }
        
        /**
         * 左回転
         */
        private void rotateLeft(Node p) {
            if (p != null) {
                Node r = p.right;
                p.right = r.left;
                if (r.left != null) {
                    r.left.parent = p;
                }
                r.parent = p.parent;
                if (p.parent == null) {
                    root = r;
                } else if (p.parent.left == p) {
                    p.parent.left = r;
                } else {
                    p.parent.right = r;
                }
                r.left = p;
                p.parent = r;
                
                if (verbose) {
                    IO.println("Left rotation on node: " + p.key);
                }
            }
        }
        
        /**
         * 右回転
         */
        private void rotateRight(Node p) {
            if (p != null) {
                Node l = p.left;
                p.left = l.right;
                if (l.right != null) {
                    l.right.parent = p;
                }
                l.parent = p.parent;
                if (p.parent == null) {
                    root = l;
                } else if (p.parent.right == p) {
                    p.parent.right = l;
                } else {
                    p.parent.left = l;
                }
                l.right = p;
                p.parent = l;
                
                if (verbose) {
                    IO.println("Right rotation on node: " + p.key);
                }
            }
        }
        
        // ヘルパーメソッド
        private boolean colorOf(Node p) {
            return (p == null ? BLACK : p.color);
        }
        
        private Node parentOf(Node p) {
            return (p == null ? null : p.parent);
        }
        
        private void setColor(Node p, boolean c) {
            if (p != null) p.color = c;
        }
        
        private Node leftOf(Node p) {
            return (p == null) ? null : p.left;
        }
        
        private Node rightOf(Node p) {
            return (p == null) ? null : p.right;
        }
        
        /**
         * 中順走査（ソートされた順序で出力）
         */
        public void inorderTraversal() {
            IO.print("Inorder: ");
            inorderTraversal(root);
            IO.println();
        }
        
        private void inorderTraversal(Node node) {
            if (node != null) {
                inorderTraversal(node.left);
                IO.print(node + " ");
                inorderTraversal(node.right);
            }
        }
        
        /**
         * 木の構造を視覚化
         */
        public void printTree() {
            IO.println("Tree structure:");
            printTree(root, "", true);
        }
        
        private void printTree(Node node, String prefix, boolean isLast) {
            if (node != null) {
                IO.println(prefix + (isLast ? "└── " : "├── ") + node);

                // 左の子を先に、右の子を後に表示する（右の子がなければ左の子が最後）
                String newPrefix = prefix + (isLast ? "    " : "│   ");
                if (node.left != null) {
                    printTree(node.left, newPrefix, node.right == null);
                }
                if (node.right != null) {
                    printTree(node.right, newPrefix, true);
                }
            }
        }
        
        /**
         * 木の高さを計算
         */
        public int getHeight() {
            return getHeight(root);
        }
        
        private int getHeight(Node node) {
            if (node == null) return 0;
            return 1 + Math.max(getHeight(node.left), getHeight(node.right));
        }
        
        /**
         * 黒い高さを計算（赤黒木の性質確認）
         */
        public int getBlackHeight() {
            return getBlackHeight(root);
        }
        
        private int getBlackHeight(Node node) {
            if (node == null) return 1;
            int leftHeight = getBlackHeight(node.left);
            int rightHeight = getBlackHeight(node.right);

            if (leftHeight != rightHeight) {
                return -1; // 黒い高さが左右で違う（赤黒木の性質5に違反）
            }
            if (leftHeight < 0) {
                return -1;
            }

            return leftHeight + (node.color == BLACK ? 1 : 0);
        }

        /**
         * 赤黒木の性質をチェック
         */
        public boolean isValidRedBlackTree() {
            if (root != null && root.color != BLACK) {
                System.err.println("Root is not black");
                return false;
            }
            if (getBlackHeight(root) < 0) {
                System.err.println("Black height differs between paths");
                return false;
            }
            return checkRedBlackProperties(root);
        }
        
        private boolean checkRedBlackProperties(Node node) {
            if (node == null) return true;
            
            // 赤い節点の子は黒でなければならない
            if (node.color == RED) {
                if ((node.left != null && node.left.color == RED) ||
                    (node.right != null && node.right.color == RED)) {
                    System.err.println("Red node has red child: " + node.key);
                    return false;
                }
            }
            
            return checkRedBlackProperties(node.left) && checkRedBlackProperties(node.right);
        }
        
        public int size() {
            return size;
        }
        
        /**
         * 統計情報を取得
         */
        public TreeStats getStats() {
            int height = getHeight();
            int blackHeight = getBlackHeight();
            int redNodes = countRedNodes(root);
            int blackNodes = size - redNodes;
            
            return new TreeStats(size, height, blackHeight, redNodes, blackNodes);
        }
        
        private int countRedNodes(Node node) {
            if (node == null) return 0;
            int count = (node.color == RED) ? 1 : 0;
            return count + countRedNodes(node.left) + countRedNodes(node.right);
        }
    }
    
    /**
     * 木の統計情報
     */
    public static class TreeStats {
        public final int size;
        public final int height;
        public final int blackHeight;
        public final int redNodes;
        public final int blackNodes;
        
        public TreeStats(int size, int height, int blackHeight, int redNodes, int blackNodes) {
            this.size = size;
            this.height = height;
            this.blackHeight = blackHeight;
            this.redNodes = redNodes;
            this.blackNodes = blackNodes;
        }
        
        @Override
        public String toString() {
            return String.format(
                "TreeStats{size=%d, height=%d, blackHeight=%d, red=%d, black=%d, balance=%.2f}",
                size, height, blackHeight, redNodes, blackNodes,
                size > 0 ? Math.log(size) / Math.log(2) / height : 0
            );
        }
    }
    
    /**
     * 二分探索木とのパフォーマンス比較用（平衡しない木）
     */
    public static class SimpleBST<K extends Comparable<K>, V> {
        private Node root;
        private int size;
        
        class Node {
            K key;
            V value;
            Node left, right;
            
            Node(K key, V value) {
                this.key = key;
                this.value = value;
            }
        }
        
        public V put(K key, V value) {
            root = put(root, key, value);
            return null;
        }
        
        private Node put(Node node, K key, V value) {
            if (node == null) {
                size++;
                return new Node(key, value);
            }
            
            int cmp = key.compareTo(node.key);
            if (cmp < 0) {
                node.left = put(node.left, key, value);
            } else if (cmp > 0) {
                node.right = put(node.right, key, value);
            } else {
                node.value = value;
            }
            
            return node;
        }
        
        public V get(K key) {
            Node node = get(root, key);
            return node != null ? node.value : null;
        }
        
        private Node get(Node node, K key) {
            if (node == null) return null;
            
            int cmp = key.compareTo(node.key);
            if (cmp < 0) return get(node.left, key);
            else if (cmp > 0) return get(node.right, key);
            else return node;
        }
        
        public int getHeight() {
            return getHeight(root);
        }
        
        private int getHeight(Node node) {
            if (node == null) return 0;
            return 1 + Math.max(getHeight(node.left), getHeight(node.right));
        }
        
        public int size() {
            return size;
        }
    }
    
    /**
     * 赤黒木の構築過程をデモンストレーション
     */
    public static void demonstrateRedBlackTreeConstruction() {
        IO.println("=== Red-Black Tree Construction Demo ===");
        
        SimpleRedBlackTree<Integer, String> tree = new SimpleRedBlackTree<>();
        tree.setVerbose(true);

        // 昇順に挿入すると、ただの二分探索木なら右に一直線に伸びる。
        // 赤黒木では回転と色の変更が起き、高さが抑えられる様子を観察できる
        int[] keys = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

        for (int key : keys) {
            IO.println("\n--- Inserting " + key + " ---");
            tree.put(key, "Value" + key);
            
            IO.println("Tree after insertion:");
            tree.printTree();
            
            TreeStats stats = tree.getStats();
            IO.println(stats);
            
            boolean isValid = tree.isValidRedBlackTree();
            IO.println("Valid red-black tree: " + isValid);
        }
        
        IO.println("\nFinal inorder traversal:");
        tree.inorderTraversal();
    }
    
    /**
     * 処理を数回空回ししてから（ウォームアップ）、最後の1回の時間をナノ秒で返す
     * JITコンパイルの影響を減らすための簡易的な方法で、正確な測定にはJMHを使う
     */
    private static long measureNanos(Runnable task) {
        for (int i = 0; i < 5; i++) {
            task.run();
        }
        long start = System.nanoTime();
        task.run();
        return System.nanoTime() - start;
    }
    
    private static String millis(long nanos) {
        return String.format("%.2fms", nanos / 1_000_000.0);
    }
    
    /**
     * 平衡木 vs 非平衡木のパフォーマンス比較
     */
    public static void compareBalancedVsUnbalanced() {
        IO.println("\n=== Balanced vs Unbalanced Tree Performance ===");
        
        // 最悪ケース：昇順データ
        int[] sortedData = new int[1000];
        for (int i = 0; i < sortedData.length; i++) {
            sortedData[i] = i;
        }
        
        // 赤黒木での挿入
        SimpleRedBlackTree<Integer, String> rbTree = new SimpleRedBlackTree<>();
        for (int key : sortedData) {
            rbTree.put(key, "Value" + key);
        }
        
        // 単純二分探索木での挿入
        SimpleBST<Integer, String> bst = new SimpleBST<>();
        for (int key : sortedData) {
            bst.put(key, "Value" + key);
        }
        
        // 検索性能の比較
        long rbSearchTime = measureNanos(() -> {
            for (int i = 0; i < 1000; i++) {
                rbTree.get(i);
            }
        });
        
        long bstSearchTime = measureNanos(() -> {
            for (int i = 0; i < 1000; i++) {
                bst.get(i);
            }
        });
        
        IO.println("Data: 1000 sorted integers (worst case for BST)");
        IO.println("\nRed-Black Tree:");
        IO.println("  Height: " + rbTree.getHeight());
        IO.println("  Search time: " + millis(rbSearchTime));
        
        IO.println("\nSimple BST:");
        IO.println("  Height: " + bst.getHeight());
        IO.println("  Search time: " + millis(bstSearchTime));
        
        IO.println("\nPerformance improvement:");
        IO.println("  Height reduction: " + String.format("%.1fx", 
                         (double) bst.getHeight() / rbTree.getHeight()));
        IO.println("  Search speedup: " + String.format("%.1fx", 
                         (double) bstSearchTime / rbSearchTime));
    }
    
    /**
     * TreeMapとHashMapの比較
     */
    public static void compareTreeMapVsHashMap() {
        IO.println("\n=== TreeMap vs HashMap Performance Comparison ===");
        
        int itemCount = 100_000;
        
        long treeMapInsertTime = measureNanos(() -> {
            TreeMap<Integer, String> map = new TreeMap<>();
            for (int i = 0; i < itemCount; i++) {
                map.put(i, "Value" + i);
            }
        });
        
        long hashMapInsertTime = measureNanos(() -> {
            HashMap<Integer, String> map = new HashMap<>();
            for (int i = 0; i < itemCount; i++) {
                map.put(i, "Value" + i);
            }
        });
        
        TreeMap<Integer, String> treeMap = new TreeMap<>();
        HashMap<Integer, String> hashMap = new HashMap<>();
        for (int i = 0; i < itemCount; i++) {
            treeMap.put(i, "Value" + i);
            hashMap.put(i, "Value" + i);
        }
        
        long treeMapSearchTime = measureNanos(() -> {
            for (int i = 0; i < itemCount; i++) {
                treeMap.get(i);
            }
        });
        
        long hashMapSearchTime = measureNanos(() -> {
            for (int i = 0; i < itemCount; i++) {
                hashMap.get(i);
            }
        });
        
        IO.println("Test with " + itemCount + " integer keys:");
        
        IO.println("\nTreeMap (Red-Black Tree):");
        IO.println("  Insert: " + millis(treeMapInsertTime));
        IO.println("  Search: " + millis(treeMapSearchTime));
        
        IO.println("\nHashMap:");
        IO.println("  Insert: " + millis(hashMapInsertTime));
        IO.println("  Search: " + millis(hashMapSearchTime));
        
        // 範囲検索（TreeMapの利点）：開始位置をO(log n)で見つけ、範囲内のk件をたどる
        NavigableMap<Integer, String> subMap = treeMap.subMap(1000, true, 2000, false);
        IO.println("\nTreeMap advantage (range operations):");
        IO.println("  Range search (1000-2000): " + subMap.size() + " items, first="
                + subMap.firstKey() + ", last=" + subMap.lastKey());
        IO.println("  TreeMap: O(log n + k), HashMap equivalent: O(n) scan through all items");
    }
    
    /**
     * メモリ使用量の比較
     * キーと値は先に作っておき、マップの構造（エントリと表）が使う分だけを測る。
     * GCの動きで値がぶれるので、目安として見る
     */
    public static void compareMemoryUsage() {
        IO.println("\n=== Memory Usage Comparison ===");
        
        int itemCount = 100_000;
        Integer[] keys = new Integer[itemCount];
        String[] values = new String[itemCount];
        for (int i = 0; i < itemCount; i++) {
            keys[i] = i;
            values[i] = "Value" + i;
        }
        
        long hashMapMemory = measureRetainedBytes(() -> {
            HashMap<Integer, String> map = new HashMap<>();
            for (int i = 0; i < itemCount; i++) {
                map.put(keys[i], values[i]);
            }
            return map;
        });
        
        long treeMapMemory = measureRetainedBytes(() -> {
            TreeMap<Integer, String> map = new TreeMap<>();
            for (int i = 0; i < itemCount; i++) {
                map.put(keys[i], values[i]);
            }
            return map;
        });
        
        IO.println("Memory used by the map structure for " + itemCount + " entries (approximate):");
        IO.println("HashMap: " + hashMapMemory / 1024 + " KB (" + hashMapMemory / itemCount + " bytes/entry)");
        IO.println("TreeMap: " + treeMapMemory / 1024 + " KB (" + treeMapMemory / itemCount + " bytes/entry)");
    }
    
    /**
     * supplierが作るオブジェクトを保持したまま、増えたヒープの使用量を返す
     */
    private static long measureRetainedBytes(java.util.function.Supplier<Object> supplier) {
        Runtime runtime = Runtime.getRuntime();
        System.gc();
        long before = runtime.totalMemory() - runtime.freeMemory();
        Object retained = supplier.get();
        System.gc();
        long after = runtime.totalMemory() - runtime.freeMemory();
        // ここまでretainedがGCで回収されないようにする
        java.lang.ref.Reference.reachabilityFence(retained);
        return after - before;
    }
    
    /**
     * メインメソッド
     */
    public static void main(String[] args) {
        demonstrateRedBlackTreeConstruction();
        compareBalancedVsUnbalanced();
        compareTreeMapVsHashMap();
        compareMemoryUsage();
        
        IO.println("\n=== TreeMap Internals Summary ===");
        IO.println("1. Red-black tree maintains O(log n) performance");
        IO.println("2. Self-balancing prevents worst-case O(n) degradation");
        IO.println("3. Automatic sorting enables efficient range operations");
        IO.println("4. Memory per entry is comparable (HashMap also needs its bucket array)");
        IO.println("5. Slower than HashMap but provides ordering guarantees");
        IO.println("Timings depend on the environment; use JMH for accurate measurements.");
    }
}