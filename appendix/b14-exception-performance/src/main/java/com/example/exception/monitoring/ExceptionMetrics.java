package com.example.exception.monitoring;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

/**
 * Metrics collection for exception monitoring and analysis.
 */
public class ExceptionMetrics {
    private final Map<Class<? extends Throwable>, ExceptionStats> statistics = new ConcurrentHashMap<>();
    private final boolean captureStackTraceSize;
    private final Instant startTime = Instant.now();

    public ExceptionMetrics() {
        this(false);
    }

    public ExceptionMetrics(boolean captureStackTraceSize) {
        this.captureStackTraceSize = captureStackTraceSize;
    }

    /**
     * Record an exception occurrence with timing information.
     */
    public void recordException(Throwable throwable, long processingTimeNanos) {
        ExceptionStats stats = statistics.computeIfAbsent(
            throwable.getClass(), 
            k -> new ExceptionStats(k.getSimpleName())
        );
        
        stats.recordOccurrence(processingTimeNanos);
        
        if (captureStackTraceSize) {
            stats.recordStackTraceSize(throwable.getStackTrace().length);
        }
    }

    /**
     * Record an exception occurrence without timing.
     */
    public void recordException(Throwable throwable) {
        recordException(throwable, 0);
    }

    /**
     * Get statistics for a specific exception type.
     */
    public ExceptionStats getStats(Class<? extends Throwable> exceptionType) {
        return statistics.get(exceptionType);
    }

    /**
     * Get all collected statistics.
     */
    public Map<Class<? extends Throwable>, ExceptionStats> getAllStats() {
        return new ConcurrentHashMap<>(statistics);
    }

    /**
     * Generate a summary report.
     */
    public String generateReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== Exception Metrics Report ===\n");
        report.append("Uptime: ").append(Duration.between(startTime, Instant.now())).append("\n\n");

        if (statistics.isEmpty()) {
            report.append("No exceptions recorded.\n");
            return report.toString();
        }

        // Sort by occurrence count
        statistics.values().stream()
            .sorted((a, b) -> Long.compare(b.getCount(), a.getCount()))
            .forEach(stats -> {
                report.append(stats.getDetailedReport()).append("\n");
            });

        // Summary statistics
        long totalExceptions = statistics.values().stream()
            .mapToLong(ExceptionStats::getCount)
            .sum();
        
        report.append("\nTotal exceptions: ").append(totalExceptions).append("\n");
        report.append("Exception types: ").append(statistics.size()).append("\n");

        return report.toString();
    }

    /**
     * Reset all metrics.
     */
    public void reset() {
        statistics.clear();
    }

    /**
     * Statistics for a single exception type.
     */
    public static class ExceptionStats {
        private final String exceptionName;
        private final LongAdder count = new LongAdder();
        private final LongAdder totalProcessingTime = new LongAdder();
        private final AtomicLong maxProcessingTime = new AtomicLong(0);
        private final AtomicLong minProcessingTime = new AtomicLong(Long.MAX_VALUE);
        private final LongAdder totalStackTraceSize = new LongAdder();
        private final AtomicLong maxStackTraceSize = new AtomicLong(0);
        private final Instant firstOccurrence = Instant.now();
        private volatile Instant lastOccurrence = Instant.now();

        public ExceptionStats(String exceptionName) {
            this.exceptionName = exceptionName;
        }

        public void recordOccurrence(long processingTimeNanos) {
            count.increment();
            lastOccurrence = Instant.now();
            
            if (processingTimeNanos > 0) {
                totalProcessingTime.add(processingTimeNanos);
                updateMax(maxProcessingTime, processingTimeNanos);
                updateMin(minProcessingTime, processingTimeNanos);
            }
        }

        public void recordStackTraceSize(int size) {
            totalStackTraceSize.add(size);
            updateMax(maxStackTraceSize, size);
        }

        private void updateMax(AtomicLong atomic, long value) {
            long current;
            do {
                current = atomic.get();
                if (value <= current) break;
            } while (!atomic.compareAndSet(current, value));
        }

        private void updateMin(AtomicLong atomic, long value) {
            long current;
            do {
                current = atomic.get();
                if (value >= current) break;
            } while (!atomic.compareAndSet(current, value));
        }

        public long getCount() {
            return count.sum();
        }

        public double getAverageProcessingTimeNanos() {
            long total = totalProcessingTime.sum();
            long occurrences = count.sum();
            return occurrences > 0 ? (double) total / occurrences : 0;
        }

        public long getMaxProcessingTimeNanos() {
            return maxProcessingTime.get();
        }

        public long getMinProcessingTimeNanos() {
            long min = minProcessingTime.get();
            return min == Long.MAX_VALUE ? 0 : min;
        }

        public double getAverageStackTraceSize() {
            long total = totalStackTraceSize.sum();
            long occurrences = count.sum();
            return occurrences > 0 ? (double) total / occurrences : 0;
        }

        public String getDetailedReport() {
            StringBuilder report = new StringBuilder();
            report.append(exceptionName).append(":\n");
            report.append("  Occurrences: ").append(getCount()).append("\n");
            report.append("  First: ").append(firstOccurrence).append("\n");
            report.append("  Last: ").append(lastOccurrence).append("\n");
            
            if (totalProcessingTime.sum() > 0) {
                report.append("  Processing time (ns):\n");
                report.append("    Average: ").append(String.format("%.2f", getAverageProcessingTimeNanos())).append("\n");
                report.append("    Min: ").append(getMinProcessingTimeNanos()).append("\n");
                report.append("    Max: ").append(getMaxProcessingTimeNanos()).append("\n");
            }
            
            if (totalStackTraceSize.sum() > 0) {
                report.append("  Stack trace size:\n");
                report.append("    Average: ").append(String.format("%.2f", getAverageStackTraceSize())).append("\n");
                report.append("    Max: ").append(maxStackTraceSize.get()).append("\n");
            }
            
            return report.toString();
        }
    }
}