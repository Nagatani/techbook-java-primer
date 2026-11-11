import java.util.*;

public class ResourcePool implements AutoCloseable {
    private final Queue<Resource> availableResources = new LinkedList<>();
    private final Set<Resource> usedResources = new HashSet<>();
    private boolean closed = false;
    
    public ResourcePool(int poolSize) {
        for (int i = 0; i < poolSize; i++) {
            availableResources.offer(new Resource(i));
        }
    }
    
    public Resource acquire() {
        if (closed) {
            throw new IllegalStateException("プールはクローズされています");
        }
        if (availableResources.isEmpty()) {
            throw new IllegalStateException("使えるリソースが残っていません");
        }
        
        Resource resource = availableResources.poll();
        usedResources.add(resource);
        return resource;
    }
    
    public void release(Resource resource) {
        if (usedResources.remove(resource)) {
            availableResources.offer(resource);
        }
    }
    
    @Override
    public void close() {
        closed = true;
        
        // すべてのリソースをクリーンアップ
        for (Resource resource : availableResources) {
            resource.cleanup();
        }
        for (Resource resource : usedResources) {
            resource.cleanup();
        }
    }
    
    // リソースクラス
    static class Resource {
        private final int id;
        
        Resource(int id) {
            this.id = id;
        }
        
        void cleanup() {
            IO.println("リソース " + id + " をクリーンアップしました");
        }
    }
}
