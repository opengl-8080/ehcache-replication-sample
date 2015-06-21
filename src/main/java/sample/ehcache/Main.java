package sample.ehcache;

import java.io.InputStream;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public class Main {

    public static void main(String[] args) {
        boolean isMain = "main".equals(args[0]);
        InputStream is = Main.class.getResourceAsStream(isMain ? "/main-ehcache.xml" : "/sub-ehcache.xml");
        
        CacheManager manager = CacheManager.create(is);
        
        try {
            Cache cache = manager.getCache("myCache");
            
            if (isMain) {
                mainProcess(cache);
            } else {
                subProcess(cache);
            }
        } finally {
            manager.shutdown();
        }
    }
    
    private static void mainProcess(Cache cache) {
        cache.put(new Element("msg", "Hello RMI Replication!!"));
        System.out.println("[main] put msg into cache");
    }
    
    private static void subProcess(Cache cache) {
        Element e = cache.get("msg");
        
        while (e == null) {
            e = cache.get("msg");
            sleep(1000);
        }
        
        System.out.println("[sub] msg : " + e.getObjectValue());
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
            System.out.println("> " + ms + " ms...");
        } catch (InterruptedException e) {}
    }
}