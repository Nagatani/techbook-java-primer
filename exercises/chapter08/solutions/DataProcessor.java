package chapter08.solutions;

import java.util.*;
import java.util.stream.Collectors;

/**
 * データ処理システムのクラス
 * 
 * 様々なデータ構造を適切に選択・使用して、
 * 効率的なデータ処理を実現します。
 */
public class DataProcessor {
    
    /**
     * データエントリを表すクラス
     */
    public static class DataEntry {
        private String id;
        private String category;
        private double value;
        private Date timestamp;
        private Map<String, Object> properties;
        
        public DataEntry(String id, String category, double value) {
            this.id = id;
            this.category = category;
            this.value = value;
            this.timestamp = new Date();
            this.properties = new HashMap<>();
        }
        
        public DataEntry(String id, String category, double value, Date timestamp) {
            this.id = id;
            this.category = category;
            this.value = value;
            this.timestamp = timestamp;
            this.properties = new HashMap<>();
        }
        
        // Getters and Setters
        public String getId() { return id; }
        public String getCategory() { return category; }
        public double getValue() { return value; }
        public Date getTimestamp() { return timestamp; }
        public Map<String, Object> getProperties() { return properties; }
        
        public void setValue(double value) { this.value = value; }
        public void setProperty(String key, Object value) { properties.put(key, value); }
        public Object getProperty(String key) { return properties.get(key); }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            DataEntry dataEntry = (DataEntry) obj;
            return Objects.equals(id, dataEntry.id);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
        
        @Override
        public String toString() {
            return String.format("DataEntry{id='%s', category='%s', value=%.2f, timestamp=%s}",
                               id, category, value, timestamp);
        }
    }
    
    /**
     * 処理結果を表すクラス
     */
    public static class ProcessingResult {
        private String operation;
        private Map<String, Double> results;
        private List<String> errors;
        private Date processedAt;
        
        public ProcessingResult(String operation) {
            this.operation = operation;
            this.results = new HashMap<>();
            this.errors = new ArrayList<>();
            this.processedAt = new Date();
        }
        
        public void addResult(String key, double value) {
            results.put(key, value);
        }
        
        public void addError(String error) {
            errors.add(error);
        }
        
        // Getters
        public String getOperation() { return operation; }
        public Map<String, Double> getResults() { return results; }
        public List<String> getErrors() { return errors; }
        public Date getProcessedAt() { return processedAt; }
        
        public boolean hasErrors() { return !errors.isEmpty(); }
        
        @Override
        public String toString() {
            return String.format("ProcessingResult{operation='%s', results=%s, errors=%s}",
                               operation, results, errors);
        }
    }
    
    // データの管理
    private List<DataEntry> dataEntries;          // 全データ（順序保持）
    private Map<String, DataEntry> dataById;      // ID別インデックス
    private Map<String, List<DataEntry>> dataByCategory; // カテゴリ別インデックス
    private TreeMap<Double, List<DataEntry>> dataByValue; // 値別インデックス（ソート済み）
    private TreeMap<Date, List<DataEntry>> dataByTimestamp; // 時刻別インデックス（ソート済み）
    
    // 処理結果の履歴
    private List<ProcessingResult> processingHistory;
    
    // 統計情報のキャッシュ
    private Map<String, Double> statisticsCache;
    private long lastCacheUpdate;
    
    /**
     * DataProcessorのコンストラクタ
     */
    public DataProcessor() {
        dataEntries = new ArrayList<>();
        dataById = new HashMap<>();
        dataByCategory = new HashMap<>();
        dataByValue = new TreeMap<>();
        dataByTimestamp = new TreeMap<>();
        processingHistory = new ArrayList<>();
        statisticsCache = new HashMap<>();
        lastCacheUpdate = 0;
    }
    
    /**
     * データエントリを追加する
     * 
     * @param entry 追加するデータエントリ
     * @return 追加に成功した場合true
     */
    public boolean addData(DataEntry entry) {
        if (entry == null || dataById.containsKey(entry.getId())) {
            return false;
        }
        
        // 各インデックスに追加
        dataEntries.add(entry);
        dataById.put(entry.getId(), entry);
        
        dataByCategory.computeIfAbsent(entry.getCategory(), k -> new ArrayList<>()).add(entry);
        dataByValue.computeIfAbsent(entry.getValue(), k -> new ArrayList<>()).add(entry);
        dataByTimestamp.computeIfAbsent(entry.getTimestamp(), k -> new ArrayList<>()).add(entry);
        
        // キャッシュを無効化
        invalidateCache();
        
        return true;
    }
    
    /**
     * データエントリを削除する
     * 
     * @param id 削除するデータのID
     * @return 削除に成功した場合true
     */
    public boolean removeData(String id) {
        DataEntry entry = dataById.remove(id);
        if (entry == null) {
            return false;
        }
        
        // 各インデックスから削除
        dataEntries.remove(entry);
        
        List<DataEntry> categoryList = dataByCategory.get(entry.getCategory());
        if (categoryList != null) {
            categoryList.remove(entry);
            if (categoryList.isEmpty()) {
                dataByCategory.remove(entry.getCategory());
            }
        }
        
        List<DataEntry> valueList = dataByValue.get(entry.getValue());
        if (valueList != null) {
            valueList.remove(entry);
            if (valueList.isEmpty()) {
                dataByValue.remove(entry.getValue());
            }
        }
        
        List<DataEntry> timestampList = dataByTimestamp.get(entry.getTimestamp());
        if (timestampList != null) {
            timestampList.remove(entry);
            if (timestampList.isEmpty()) {
                dataByTimestamp.remove(entry.getTimestamp());
            }
        }
        
        // キャッシュを無効化
        invalidateCache();
        
        return true;
    }
    
    /**
     * IDでデータを検索する
     * 
     * @param id データID
     * @return 見つかったデータ（存在しない場合null）
     */
    public DataEntry findById(String id) {
        return dataById.get(id);
    }
    
    /**
     * カテゴリでデータを検索する
     * 
     * @param category カテゴリ名
     * @return 該当するデータのリスト
     */
    public List<DataEntry> findByCategory(String category) {
        return dataByCategory.getOrDefault(category, new ArrayList<>());
    }
    
    /**
     * 値の範囲でデータを検索する
     * 
     * @param minValue 最小値
     * @param maxValue 最大値
     * @return 該当するデータのリスト
     */
    public List<DataEntry> findByValueRange(double minValue, double maxValue) {
        return dataByValue.subMap(minValue, true, maxValue, true)
                         .values()
                         .stream()
                         .flatMap(List::stream)
                         .collect(Collectors.toList());
    }
    
    /**
     * 時刻の範囲でデータを検索する
     * 
     * @param startTime 開始時刻
     * @param endTime 終了時刻
     * @return 該当するデータのリスト
     */
    public List<DataEntry> findByTimeRange(Date startTime, Date endTime) {
        return dataByTimestamp.subMap(startTime, true, endTime, true)
                             .values()
                             .stream()
                             .flatMap(List::stream)
                             .collect(Collectors.toList());
    }
    
    /**
     * 統計情報を計算する
     * 
     * @return 統計情報の処理結果
     */
    public ProcessingResult calculateStatistics() {
        ProcessingResult result = new ProcessingResult("統計計算");
        
        try {
            if (dataEntries.isEmpty()) {
                result.addError("データが存在しません");
                return result;
            }
            
            // キャッシュが有効な場合は使用
            if (isStatisticsCacheValid()) {
                statisticsCache.forEach(result::addResult);
                processingHistory.add(result);
                return result;
            }
            
            // 統計計算
            DoubleSummaryStatistics stats = dataEntries.stream()
                                                      .mapToDouble(DataEntry::getValue)
                                                      .summaryStatistics();
            
            result.addResult("count", stats.getCount());
            result.addResult("sum", stats.getSum());
            result.addResult("min", stats.getMin());
            result.addResult("max", stats.getMax());
            result.addResult("average", stats.getAverage());
            
            // 中央値の計算
            List<Double> sortedValues = dataEntries.stream()
                                                  .mapToDouble(DataEntry::getValue)
                                                  .sorted()
                                                  .boxed()
                                                  .collect(Collectors.toList());
            
            double median = calculateMedian(sortedValues);
            result.addResult("median", median);
            
            // 標準偏差の計算
            double variance = dataEntries.stream()
                                        .mapToDouble(entry -> Math.pow(entry.getValue() - stats.getAverage(), 2))
                                        .average()
                                        .orElse(0.0);
            result.addResult("variance", variance);
            result.addResult("standardDeviation", Math.sqrt(variance));
            
            // キャッシュを更新
            updateStatisticsCache(result.getResults());
            
        } catch (Exception e) {
            result.addError("統計計算エラー: " + e.getMessage());
        }
        
        processingHistory.add(result);
        return result;
    }
    
    /**
     * カテゴリ別の統計情報を計算する
     * 
     * @return カテゴリ別統計情報の処理結果
     */
    public ProcessingResult calculateCategoryStatistics() {
        ProcessingResult result = new ProcessingResult("カテゴリ別統計計算");
        
        try {
            for (String category : dataByCategory.keySet()) {
                List<DataEntry> categoryData = dataByCategory.get(category);
                
                DoubleSummaryStatistics stats = categoryData.stream()
                                                          .mapToDouble(DataEntry::getValue)
                                                          .summaryStatistics();
                
                result.addResult(category + "_count", stats.getCount());
                result.addResult(category + "_sum", stats.getSum());
                result.addResult(category + "_average", stats.getAverage());
                result.addResult(category + "_min", stats.getMin());
                result.addResult(category + "_max", stats.getMax());
            }
            
        } catch (Exception e) {
            result.addError("カテゴリ別統計計算エラー: " + e.getMessage());
        }
        
        processingHistory.add(result);
        return result;
    }
    
    /**
     * 上位N件のデータを取得する
     * 
     * @param n 取得件数
     * @return 上位N件のデータ
     */
    public List<DataEntry> getTopN(int n) {
        return dataEntries.stream()
                         .sorted(Comparator.comparingDouble(DataEntry::getValue).reversed())
                         .limit(n)
                         .collect(Collectors.toList());
    }
    
    /**
     * 下位N件のデータを取得する
     * 
     * @param n 取得件数
     * @return 下位N件のデータ
     */
    public List<DataEntry> getBottomN(int n) {
        return dataEntries.stream()
                         .sorted(Comparator.comparingDouble(DataEntry::getValue))
                         .limit(n)
                         .collect(Collectors.toList());
    }
    
    /**
     * データを値でソートする
     * 
     * @param ascending 昇順の場合true
     * @return ソート済みデータのリスト
     */
    public List<DataEntry> sortByValue(boolean ascending) {
        Comparator<DataEntry> comparator = Comparator.comparingDouble(DataEntry::getValue);
        if (!ascending) {
            comparator = comparator.reversed();
        }
        
        return dataEntries.stream()
                         .sorted(comparator)
                         .collect(Collectors.toList());
    }
    
    /**
     * データを時刻でソートする
     * 
     * @param ascending 昇順の場合true
     * @return ソート済みデータのリスト
     */
    public List<DataEntry> sortByTimestamp(boolean ascending) {
        Comparator<DataEntry> comparator = Comparator.comparing(DataEntry::getTimestamp);
        if (!ascending) {
            comparator = comparator.reversed();
        }
        
        return dataEntries.stream()
                         .sorted(comparator)
                         .collect(Collectors.toList());
    }
    
    /**
     * 重複データを検出する
     * 
     * @return 重複データのリスト
     */
    public List<DataEntry> findDuplicates() {
        Map<String, List<DataEntry>> duplicateMap = new HashMap<>();
        
        for (DataEntry entry : dataEntries) {
            String key = entry.getCategory() + "_" + entry.getValue();
            duplicateMap.computeIfAbsent(key, k -> new ArrayList<>()).add(entry);
        }
        
        return duplicateMap.values().stream()
                          .filter(list -> list.size() > 1)
                          .flatMap(List::stream)
                          .collect(Collectors.toList());
    }
    
    /**
     * 中央値を計算する
     * 
     * @param sortedValues ソート済みの値のリスト
     * @return 中央値
     */
    private double calculateMedian(List<Double> sortedValues) {
        int size = sortedValues.size();
        if (size % 2 == 0) {
            return (sortedValues.get(size / 2 - 1) + sortedValues.get(size / 2)) / 2.0;
        } else {
            return sortedValues.get(size / 2);
        }
    }
    
    /**
     * 統計キャッシュを無効化する
     */
    private void invalidateCache() {
        lastCacheUpdate = 0;
        statisticsCache.clear();
    }
    
    /**
     * 統計キャッシュが有効かどうかを判定する
     * 
     * @return 有効な場合true
     */
    private boolean isStatisticsCacheValid() {
        return System.currentTimeMillis() - lastCacheUpdate < 60000; // 1分間有効
    }
    
    /**
     * 統計キャッシュを更新する
     * 
     * @param statistics 統計情報
     */
    private void updateStatisticsCache(Map<String, Double> statistics) {
        statisticsCache.clear();
        statisticsCache.putAll(statistics);
        lastCacheUpdate = System.currentTimeMillis();
    }
    
    // Getter methods
    public List<DataEntry> getAllData() { return new ArrayList<>(dataEntries); }
    public int getDataCount() { return dataEntries.size(); }
    public Set<String> getAllCategories() { return dataByCategory.keySet(); }
    public List<ProcessingResult> getProcessingHistory() { return new ArrayList<>(processingHistory); }
    public ProcessingResult getLatestResult() { 
        return processingHistory.isEmpty() ? null : processingHistory.get(processingHistory.size() - 1);
    }
}