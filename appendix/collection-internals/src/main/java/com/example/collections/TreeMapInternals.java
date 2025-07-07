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
                
                System.out.println("Left rotation on node: " + p.key);
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
                
                System.out.println("Right rotation on node: " + p.key);
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
            System.out.print("Inorder: ");
            inorderTraversal(root);
            System.out.println();
        }
        
        private void inorderTraversal(Node node) {
            if (node != null) {
                inorderTraversal(node.left);
                System.out.print(node + " ");
                inorderTraversal(node.right);
            }
        }
        
        /**
         * 木の構造を視覚化
         */
        public void printTree() {
            System.out.println("Tree structure:");
            printTree(root, "", true);
        }
        
        private void printTree(Node node, String prefix, boolean isLast) {
            if (node != null) {
                System.out.println(prefix + (isLast ? "└── " : "├── ") + node);
                
                List<Node> children = new ArrayList<>();
                if (node.left != null) children.add(node.left);
                if (node.right != null) children.add(node.right);
                
                for (int i = 0; i < children.size(); i++) {
                    boolean isLastChild = (i == children.size() - 1);
                    String newPrefix = prefix + (isLast ? "    " : "│   ");
                    
                    if (children.get(i) == node.left) {
                        printTree(node.left, newPrefix, !isLastChild || node.right == null);
                    } else {
                        printTree(node.right, newPrefix, isLastChild);
                    }
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
                System.err.println("Black height violation at node: " + node.key);
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
        System.out.println("=== Red-Black Tree Construction Demo ===");
        
        SimpleRedBlackTree<Integer, String> tree = new SimpleRedBlackTree<>();
        
        // 順序立てて挿入してバランシング動作を観察
        int[] keys = {10, 5, 15, 3, 7, 12, 17, 1, 4, 6, 8, 11, 13, 16, 18};
        
        for (int key : keys) {
            System.out.println("\n--- Inserting " + key + " ---");
            tree.put(key, "Value" + key);
            
            System.out.println("Tree after insertion:");
            tree.printTree();
            
            TreeStats stats = tree.getStats();
            System.out.println(stats);
            
            boolean isValid = tree.isValidRedBlackTree();
            System.out.println("Valid red-black tree: " + isValid);
            
            if (key == 8 || key == 13 || key == 18) {
                System.out.println("\nPress Enter to continue...");
                try { System.in.read(); } catch (Exception ignored) {}
            }
        }
        
        System.out.println("\nFinal inorder traversal:");
        tree.inorderTraversal();
    }
    
    /**
     * 平衡木 vs 非平衡木のパフォーマンス比較
     */
    public static void compareBalancedVsUnbalanced() {
        System.out.println("\n=== Balanced vs Unbalanced Tree Performance ===");
        
        // 最悪ケース：昇順データ
        int[] sortedData = new int[1000];
        for (int i = 0; i < sortedData.length; i++) {
            sortedData[i] = i;
        }
        
        // 赤黒木での挿入
        SimpleRedBlackTree<Integer, String> rbTree = new SimpleRedBlackTree<>();
        long startTime = System.nanoTime();
        for (int key : sortedData) {
            rbTree.put(key, "Value" + key);
        }
        long rbInsertTime = System.nanoTime() - startTime;
        
        // 単純二分探索木での挿入
        SimpleBST<Integer, String> bst = new SimpleBST<>();
        startTime = System.nanoTime();
        for (int key : sortedData) {
            bst.put(key, "Value" + key);
        }
        long bstInsertTime = System.nanoTime() - startTime;
        
        // 検索性能の比較
        startTime = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            rbTree.get(i);
        }
        long rbSearchTime = System.nanoTime() - startTime;
        
        startTime = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            bst.get(i);
        }
        long bstSearchTime = System.nanoTime() - startTime;
        
        System.out.println("Data: 1000 sorted integers (worst case for BST)");
        System.out.println("\nRed-Black Tree:");
        System.out.println("  Height: " + rbTree.getHeight());
        System.out.println("  Insert time: " + rbInsertTime / 1_000_000 + "ms");
        System.out.println("  Search time: " + rbSearchTime / 1_000_000 + "ms");
        
        System.out.println("\nSimple BST:");
        System.out.println("  Height: " + bst.getHeight());
        System.out.println("  Insert time: " + bstInsertTime / 1_000_000 + "ms");
        System.out.println("  Search time: " + bstSearchTime / 1_000_000 + "ms");
        
        System.out.println("\nPerformance improvement:");
        System.out.println("  Height reduction: " + String.format("%.1fx", 
                         (double) bst.getHeight() / rbTree.getHeight()));
        System.out.println("  Search speedup: " + String.format("%.1fx", 
                         (double) bstSearchTime / rbSearchTime));
    }
    
    /**
     * TreeMapとHashMapの比較
     */
    public static void compareTreeMapVsHashMap() {
        System.out.println("\n=== TreeMap vs HashMap Performance Comparison ===");
        
        int itemCount = 100_000;
        
        // TreeMapのテスト
        TreeMap<Integer, String> treeMap = new TreeMap<>();
        long startTime = System.nanoTime();
        for (int i = 0; i < itemCount; i++) {
            treeMap.put(i, "Value" + i);
        }
        long treeMapInsertTime = System.nanoTime() - startTime;
        
        startTime = System.nanoTime();
        for (int i = 0; i < itemCount; i++) {
            treeMap.get(i);
        }
        long treeMapSearchTime = System.nanoTime() - startTime;
        
        // HashMapのテスト
        HashMap<Integer, String> hashMap = new HashMap<>();
        startTime = System.nanoTime();
        for (int i = 0; i < itemCount; i++) {
            hashMap.put(i, "Value" + i);
        }
        long hashMapInsertTime = System.nanoTime() - startTime;
        
        startTime = System.nanoTime();
        for (int i = 0; i < itemCount; i++) {
            hashMap.get(i);
        }
        long hashMapSearchTime = System.nanoTime() - startTime;
        
        // ソート済み反復のテスト
        startTime = System.nanoTime();
        for (Map.Entry<Integer, String> entry : treeMap.entrySet()) {
            // ソート済みで反復
        }
        long treeMapIterationTime = System.nanoTime() - startTime;
        
        startTime = System.nanoTime();
        for (Map.Entry<Integer, String> entry : hashMap.entrySet()) {
            // 順序なしで反復
        }
        long hashMapIterationTime = System.nanoTime() - startTime;
        
        System.out.println("Test with " + itemCount + " integer keys:");
        
        System.out.println("\nTreeMap (Red-Black Tree):");
        System.out.println("  Insert: " + treeMapInsertTime / 1_000_000 + "ms");
        System.out.println("  Search: " + treeMapSearchTime / 1_000_000 + "ms");
        System.out.println("  Iteration: " + treeMapIterationTime / 1_000_000 + "ms (sorted)");
        
        System.out.println("\nHashMap:");
        System.out.println("  Insert: " + hashMapInsertTime / 1_000_000 + "ms");
        System.out.println("  Search: " + hashMapSearchTime / 1_000_000 + "ms");
        System.out.println("  Iteration: " + hashMapIterationTime / 1_000_000 + "ms (unsorted)");
        
        System.out.println("\nHashMap advantage:");
        System.out.println("  Insert speedup: " + String.format("%.1fx", 
                         (double) treeMapInsertTime / hashMapInsertTime));
        System.out.println("  Search speedup: " + String.format("%.1fx", 
                         (double) treeMapSearchTime / hashMapSearchTime));
        
        // 範囲検索のテスト（TreeMapの利点）
        startTime = System.nanoTime();
        NavigableMap<Integer, String> subMap = treeMap.subMap(1000, true, 2000, false);
        int count = subMap.size();
        long rangeSearchTime = System.nanoTime() - startTime;
        
        System.out.println("\nTreeMap advantage (range operations):");
        System.out.println("  Range search (1000-2000): " + rangeSearchTime / 1_000_000 + "ms (" + count + " items)");
        System.out.println("  HashMap equivalent: O(n) scan through all items");
    }
    
    /**
     * メモリ使用量の比較
     */
    public static void compareMemoryUsage() {
        System.out.println("\n=== Memory Usage Comparison ===");
        
        Runtime runtime = Runtime.getRuntime();
        int itemCount = 100_000;
        
        // HashMap メモリ使用量
        runtime.gc();
        long beforeHashMap = runtime.totalMemory() - runtime.freeMemory();
        
        HashMap<Integer, String> hashMap = new HashMap<>();
        for (int i = 0; i < itemCount; i++) {
            hashMap.put(i, "Value" + i);
        }
        
        runtime.gc();
        long afterHashMap = runtime.totalMemory() - runtime.freeMemory();
        long hashMapMemory = afterHashMap - beforeHashMap;
        
        // TreeMap メモリ使用量
        hashMap = null; // GCの対象にする
        runtime.gc();
        long beforeTreeMap = runtime.totalMemory() - runtime.freeMemory();
        
        TreeMap<Integer, String> treeMap = new TreeMap<>();
        for (int i = 0; i < itemCount; i++) {
            treeMap.put(i, "Value" + i);
        }
        
        runtime.gc();
        long afterTreeMap = runtime.totalMemory() - runtime.freeMemory();
        long treeMapMemory = afterTreeMap - beforeTreeMap;
        
        System.out.println("Memory usage for " + itemCount + " entries:");
        System.out.println("HashMap: " + hashMapMemory / 1024 + " KB");
        System.out.println("TreeMap: " + treeMapMemory / 1024 + " KB");
        System.out.println("TreeMap overhead: " + String.format("%.1fx", 
                         (double) treeMapMemory / hashMapMemory));
        
        // エントリあたりのメモリ使用量
        System.out.println("\nPer-entry memory usage:");
        System.out.println("HashMap: " + hashMapMemory / itemCount + " bytes/entry");
        System.out.println("TreeMap: " + treeMapMemory / itemCount + " bytes/entry");
    }
    
    /**
     * メインメソッド
     */
    public static void main(String[] args) {
        demonstrateRedBlackTreeConstruction();
        compareBalancedVsUnbalanced();
        compareTreeMapVsHashMap();
        compareMemoryUsage();
        
        System.out.println("\n=== TreeMap Internals Summary ===");
        System.out.println("1. Red-black tree maintains O(log n) performance");
        System.out.println("2. Self-balancing prevents worst-case O(n) degradation");
        System.out.println("3. Automatic sorting enables efficient range operations");
        System.out.println("4. Higher memory overhead compared to HashMap");
        System.out.println("5. Slower than HashMap but provides ordering guarantees");
    }
}