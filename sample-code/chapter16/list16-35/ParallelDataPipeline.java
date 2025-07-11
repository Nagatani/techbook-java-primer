/**
 * リスト16-35
 * ParallelDataPipelineクラス
 * 
 * 元ファイル: chapter16-multithreading.md (2959行目)
 */

import java.util.concurrent.*;
import java.util.List;
import java.util.ArrayList;

public class ParallelDataPipeline {
    
    // データ処理のステージ
    interface Stage<I, O> {
        O process(I input);
    }
    
    // 並列パイプライン
    static class Pipeline<T> {
        private final ExecutorService executor;
        private final List<Stage<?, ?>> stages;
        
        public Pipeline(int threadPoolSize) {
            this.executor = Executors.newFixedThreadPool(threadPoolSize);
            this.stages = new ArrayList<>();
        }
        
        public <O> Pipeline<O> addStage(Stage<T, O> stage) {
            stages.add(stage);
            return (Pipeline<O>) this;
        }
        
        public CompletableFuture<T> process(T input) {
            CompletableFuture<Object> result = CompletableFuture.completedFuture(input);
            
            for (Stage<?, ?> stage : stages) {
                result = result.thenApplyAsync(data -> 
                    ((Stage<Object, Object>) stage).process(data), executor);
            }
            
            return (CompletableFuture<T>) result;
        }
        
        public void shutdown() {
            executor.shutdown();
        }
    }
    
    public static void main(String[] args) throws Exception {
        // データ処理パイプラインの例
        Pipeline<String> pipeline = new Pipeline<>(4);
        
        // ステージ1: データ取得
        Stage<String, String> fetchStage = url -> {
            System.out.println("Fetching data from: " + url);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "Raw data from " + url;
        };
        
        // ステージ2: データ変換
        Stage<String, String> transformStage = data -> {
            System.out.println("Transforming: " + data);
            return data.toUpperCase();
        };
        
        // ステージ3: データ保存
        Stage<String, String> saveStage = data -> {
            System.out.println("Saving: " + data);
            return "Saved: " + data;
        };
        
        // パイプライン構築
        CompletableFuture<String> future = pipeline
            .addStage(fetchStage)
            .addStage(transformStage)
            .addStage(saveStage)
            .process("http://example.com");
        
        // 結果取得
        String result = future.get();
        System.out.println("Pipeline result: " + result);
        
        pipeline.shutdown();
    }
}