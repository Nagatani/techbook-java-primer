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
    
    public Resource acquire() throws InterruptedException {
        synchronized (this) {
            while (availableResources.isEmpty() && !closed) {
                wait();
            }
            
            if (closed) {
                throw new IllegalStateException("プールはクローズされています");
            }
            
            Resource resource = availableResources.poll();
            usedResources.add(resource);
            return resource;
        }
    }
    
    public void release(Resource resource) {
        synchronized (this) {
            if (usedResources.remove(resource)) {
                availableResources.offer(resource);
                notify();
            }
        }
    }
    
    @Override
    public void close() {
        synchronized (this) {
            closed = true;
            notifyAll();
            
            // すべてのリソースをクリーンアップ
            for (Resource resource : availableResources) {
                resource.cleanup();
            }
            for (Resource resource : usedResources) {
                resource.cleanup();
            }
        }
    }
    
    // リソースクラス
    static class Resource {
        private final int id;
        
        Resource(int id) {
            this.id = id;
        }
        
        void cleanup() {
            System.out.println("リソース " + id + " をクリーンアップしました");
        }
    }
}
