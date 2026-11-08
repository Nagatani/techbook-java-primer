import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.imageio.ImageIO;

public class ImageManager {
    private final Map<String, BufferedImage> imageCache = new HashMap<>();
    private final int MAX_CACHE_SIZE = 100;
    
    public BufferedImage loadImage(String path, int width, int height) {
        String key = path + "_" + width + "_" + height;
        
        // キャッシュから取得
        BufferedImage cached = imageCache.get(key);
        if (cached != null) {
            return cached;
        }
        
        // 画像を読み込み、リサイズ
        try {
            BufferedImage original = ImageIO.read(new File(path));
            BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            
            Graphics2D g2d = resized.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
                               RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(original, 0, 0, width, height, null);
            g2d.dispose();
            
            // 元画像を解放
            original.flush();
            
            // キャッシュサイズを制限
            if (imageCache.size() >= MAX_CACHE_SIZE) {
                clearOldestCache();
            }
            
            imageCache.put(key, resized);
            return resized;
            
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private void clearOldestCache() {
        // 簡単な実装：最初の要素を削除
        Iterator<Map.Entry<String, BufferedImage>> iterator = imageCache.entrySet().iterator();
        if (iterator.hasNext()) {
            Map.Entry<String, BufferedImage> entry = iterator.next();
            entry.getValue().flush();  // 画像リソースを解放
            iterator.remove();
        }
    }
    
    public void clearCache() {
        for (BufferedImage image : imageCache.values()) {
            image.flush();
        }
        imageCache.clear();
    }
}
