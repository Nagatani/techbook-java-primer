package com.example.oss.plugins.samples;

import com.example.oss.core.Version;
import com.example.oss.plugins.*;
import com.example.oss.plugins.extension.Extension;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * JSON形式のデータ変換を提供するプラグインの実装例
 * 
 * <p>このプラグインは、JavaオブジェクトとJSON形式の相互変換を提供します。
 * 
 * @since 1.0.0
 */
public class JsonDataTransformerPlugin implements Plugin {
    
    @Extension("dataTransformers")
    private final DataTransformer jsonTransformer = new JsonDataTransformer();
    
    private PluginContext context;
    private ObjectMapper objectMapper;
    
    @Override
    public String getName() {
        return "JsonDataTransformer";
    }
    
    @Override
    public Version getVersion() {
        return Version.parse("1.0.0");
    }
    
    @Override
    public String getDescription() {
        return "Provides JSON data transformation capabilities using Jackson library";
    }
    
    @Override
    public String getAuthor() {
        return "Example Team";
    }
    
    @Override
    public void initialize(PluginContext context) throws PluginException {
        this.context = context;
        this.objectMapper = createObjectMapper();
        
        context.log(PluginContext.LogLevel.INFO, 
            "JsonDataTransformer plugin initialized successfully");
    }
    
    @Override
    public void shutdown() {
        context.log(PluginContext.LogLevel.INFO, 
            "JsonDataTransformer plugin shutting down");
        this.objectMapper = null;
        this.context = null;
    }
    
    /**
     * ObjectMapperを設定して作成します
     */
    private ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Java 8 Time APIのサポート
        mapper.registerModule(new JavaTimeModule());
        
        // 読みやすいフォーマット
        String prettyPrint = context.getConfig("json.prettyPrint", "true");
        if ("true".equals(prettyPrint)) {
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
        }
        
        // 日付をタイムスタンプではなくISO-8601形式で出力
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        return mapper;
    }
    
    /**
     * JSON変換の実装
     */
    private class JsonDataTransformer implements PluginManager.DataTransformer {
        
        @Override
        public String getSupportedFormat() {
            return "json";
        }
        
        @Override
        public boolean canTransform(String fromFormat, String toFormat) {
            return "json".equalsIgnoreCase(fromFormat) || 
                   "json".equalsIgnoreCase(toFormat);
        }
        
        @Override
        public Object transform(Object input, String fromFormat, String toFormat) {
            try {
                if ("json".equalsIgnoreCase(toFormat)) {
                    // オブジェクトからJSONへ
                    return objectMapper.writeValueAsString(input);
                } else if ("json".equalsIgnoreCase(fromFormat)) {
                    // JSONからオブジェクトへ
                    if (input instanceof String) {
                        // 型情報がないため、汎用的なMapとして返す
                        return objectMapper.readValue((String) input, Object.class);
                    }
                }
                
                throw new UnsupportedOperationException(
                    String.format("Cannot transform from %s to %s", fromFormat, toFormat));
                    
            } catch (Exception e) {
                throw new RuntimeException("JSON transformation failed", e);
            }
        }
    }
}