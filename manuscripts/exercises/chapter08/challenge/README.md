# 第8章 チャレンジ課題：高性能検索エンジンの実装

## 課題概要

実用的な検索エンジンの基本機能を、Javaのコレクションフレームワークを駆使して実装します。大規模データに対する高速検索、ランキング、ファセット検索などの高度な機能を実現します。

## チャレンジ課題：ドキュメント検索エンジン

### 目的
- 大規模データの効率的な管理
- 高度な検索アルゴリズムの実装
- パフォーマンスとメモリ効率の最適化
- 実用的なアプリケーションの構築

### 要求仕様

1. **コアデータ構造**
   ```java
   public class Document {
       private String id;
       private String title;
       private String content;
       private Map<String, String> metadata;
       private LocalDateTime timestamp;
       private Set<String> tags;
   }
   
   public class IndexedTerm {
       private String term;
       private Map<String, TermFrequency> documentFrequencies;
       private double inverseDocumentFrequency;
   }
   
   public class TermFrequency {
       private String documentId;
       private int frequency;
       private List<Integer> positions; // 単語の出現位置
   }
   ```

2. **転置インデックスの実装**
   ```java
   public class InvertedIndex {
       // 単語 -> ドキュメントリスト（TF-IDF情報付き）
       private Map<String, IndexedTerm> index;
       
       // N-gramインデックス（部分一致検索用）
       private Map<String, Set<String>> ngramIndex;
       
       // メタデータインデックス（ファセット検索用）
       private Map<String, Map<String, Set<String>>> metadataIndex;
       
       public void indexDocument(Document doc) {
           // 形態素解析、ステミング、インデックス構築
       }
       
       public List<SearchResult> search(Query query) {
           // 複雑なクエリの処理とランキング
       }
   }
   ```

3. **高度な検索機能**
   - **ブール検索**：AND、OR、NOT演算子
   - **フレーズ検索**：連続する単語の検索
   - **近接検索**：単語間の距離を指定
   - **ワイルドカード検索**：*、?を使った検索
   - **ファジー検索**：編集距離による類似検索
   - **ファセット検索**：カテゴリー別の絞り込み

4. **ランキングアルゴリズム**
   ```java
   public interface RankingAlgorithm {
       double score(Document doc, Query query, SearchContext context);
   }
   
   public class TFIDFRanking implements RankingAlgorithm {
       @Override
       public double score(Document doc, Query query, SearchContext context) {
           // TF-IDFスコアの計算
       }
   }
   
   public class BM25Ranking implements RankingAlgorithm {
       private double k1 = 1.2;
       private double b = 0.75;
       
       @Override
       public double score(Document doc, Query query, SearchContext context) {
           // BM25アルゴリズムの実装
       }
   }
   ```

5. **クエリパーサー**
   ```java
   public class QueryParser {
       public Query parse(String queryString) {
           // 複雑なクエリ構文の解析
           // 例: "java AND (programming OR development) -beginner"
       }
   }
   
   public abstract class Query {
       public abstract Set<String> getMatchingDocuments(InvertedIndex index);
   }
   
   public class BooleanQuery extends Query {
       private List<Query> must;     // AND条件
       private List<Query> should;   // OR条件
       private List<Query> mustNot;  // NOT条件
   }
   ```

6. **パフォーマンス最適化**
   - **圧縮技術**：ポスティングリストの圧縮
   - **スキップリスト**：大規模リストの高速スキャン
   - **キャッシング**：頻出クエリの結果キャッシュ
   - **並列処理**：インデックス構築と検索の並列化

### 実装例
```java
public class SearchEngine {
    private InvertedIndex index;
    private QueryParser parser;
    private RankingAlgorithm ranker;
    private SearchCache cache;
    
    public SearchResults search(String queryString, SearchOptions options) {
        // キャッシュチェック
        String cacheKey = generateCacheKey(queryString, options);
        if (cache.contains(cacheKey)) {
            return cache.get(cacheKey);
        }
        
        // クエリ解析
        Query query = parser.parse(queryString);
        
        // ドキュメント検索
        Set<String> matchingDocs = query.getMatchingDocuments(index);
        
        // スコアリングとランキング
        List<SearchResult> results = matchingDocs.parallelStream()
            .map(docId -> {
                Document doc = getDocument(docId);
                double score = ranker.score(doc, query, createContext());
                return new SearchResult(doc, score);
            })
            .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
            .limit(options.getMaxResults())
            .collect(Collectors.toList());
        
        // ファセット集計
        Map<String, Map<String, Long>> facets = 
            computeFacets(matchingDocs, options.getFacetFields());
        
        SearchResults searchResults = new SearchResults(results, facets);
        cache.put(cacheKey, searchResults);
        
        return searchResults;
    }
}
```

### パフォーマンス要件
- 100万ドキュメントのインデックス構築：5分以内
- 単純なキーワード検索：50ms以内
- 複雑なブール検索：200ms以内
- メモリ使用量：インデックスサイズの2倍以内

### 評価ポイント
- アルゴリズムの正確性と効率性
- メモリ効率的なデータ構造
- 並列処理の適切な活用
- キャッシング戦略
- コードの可読性と保守性
- 包括的なテストカバレッジ

### ボーナス機能
1. **スペルチェッカー**：入力ミスの自動修正提案
2. **オートコンプリート**：検索候補の自動補完
3. **類似文書検索**：コサイン類似度による検索
4. **多言語対応**：日本語形態素解析の実装
5. **分散検索**：複数ノードでのインデックス分割

## 提出要件

1. **完全な実装**
   - すべての必須機能の実装
   - 少なくとも2つのボーナス機能

2. **パフォーマンステスト**
   - 大規模データセットでのベンチマーク
   - メモリプロファイリング結果
   - ボトルネック分析レポート

3. **設計ドキュメント**
   - システムアーキテクチャ図
   - データフロー図
   - APIドキュメント

4. **デモアプリケーション**
   - Webインターフェイスまたは<Swing GUI
   - サンプルデータセット（Wikipedia記事など）

## 発展的な考察

- **実際の検索エンジンとの比較**：Elasticsearch、Solrとの機能比較
- **機械学習の統合**：検索結果のパーソナライゼーション
- **分散システム化**：Apache Luceneのような分散アーキテクチャ
- **リアルタイム検索**：ストリーミングデータのインデックス

## 参考リソース

- Information Retrieval（Christopher D. Manning他）
- Lucene in Action（Michael McCandless他）
- Introduction to Information Retrieval（無料オンライン版あり）
- Elasticsearch: The Definitive Guide