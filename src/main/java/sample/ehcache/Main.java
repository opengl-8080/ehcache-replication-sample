package sample.ehcache;

import java.io.InputStream;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public class Main {

    public static void main(String[] args) {
        boolean isMain = "main".equals(args[0]);
        boolean isSub1 = "sub1".equals(args[0]);
        String config = isMain ? "/main-ehcache.xml"
                               : isSub1 ? "/sub1-ehcache.xml"
                                        : "/sub2-ehcache.xml";
        
        InputStream is = Main.class.getResourceAsStream(config);
        
        CacheManager manager = CacheManager.create(is);
        
        try {
            Cache cache = manager.getCache("myCache");
            
            if (isMain) {
                mainProcess(cache, args[1]);
            } else {
                subProcess(cache, isSub1 ? 1 : 2);
            }
        } finally {
            manager.shutdown();
        }
    }
    
    private static void mainProcess(Cache cache, String message) {
        cache.put(new Element("msg", message));
        System.out.println("[main] put msg(\"" + message + "\") into cache");
    }
    
    private static void subProcess(Cache cache, int number) {
        Element e = cache.get("msg");
        
        while (e == null) {
            e = cache.get("msg");
            sleep(1000);
        }
        
        System.out.println("[sub" + number + "] msg : " + e.getObjectValue());
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
            System.out.println("> " + ms + " ms...");
        } catch (InterruptedException e) {}
    }
}