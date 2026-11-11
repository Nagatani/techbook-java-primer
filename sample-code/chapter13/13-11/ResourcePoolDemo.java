public class ResourcePoolDemo {
    public static void main(String[] args) {
        // tryブロックを抜けると、pool.close()が自動的に呼ばれる
        try (ResourcePool pool = new ResourcePool(2)) {
            ResourcePool.Resource resource = pool.acquire();
            IO.println("リソースを借りました");
            pool.release(resource);
            IO.println("リソースを返しました");
        }
        IO.println("tryブロックを抜けました");
    }
}
