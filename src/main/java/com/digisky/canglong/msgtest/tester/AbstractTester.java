package com.digisky.canglong.msgtest.tester;

import com.digisky.canglong.msgtest.db.Equip;
import com.digisky.canglong.msgtest.utils.GzipUtils;

/**
 * 抽象测试
 *
 * @author LJX
 * @since 2019/1/9.
 */
public abstract class AbstractTester {
    
    /**
     * 名字
     * @return 名字
     */
    protected abstract String name();
    
    /**
     * 是否需要压缩
     * @return 是/否
     */
    protected abstract boolean needCompress();
    
    /**
     * 序列化测试
     * @param equips 测试数据
     * @return 序列化后的byte[]
     */
    protected abstract byte[] serializeTest(Equip[] equips);
    
    /**
     * 反序列化测试
     * @param bytes 序列化后的byte[]
     */
    protected abstract void deserializeTest(byte[] bytes);
    
    public void doTest(Equip[] equips) {
        long total = Runtime.getRuntime().totalMemory();
        System.out.println(name() + " test begin...");
        long serializeStart = System.currentTimeMillis();
        // 测试序列化
        gc();
        long memBeforeSerialize = total - Runtime.getRuntime().freeMemory();
        byte[] bytes = serializeTest(equips);
        long memAfterSerialize = total - Runtime.getRuntime().freeMemory();
        long serializeTime = System.currentTimeMillis() - serializeStart;
        long ungzipTime = 0;
        System.out.print(name() + " serialize use:" + serializeTime + ", data length:" + bytes.length +
                ", memory use:" + (memAfterSerialize - memBeforeSerialize));
        if (needCompress()) {
            long gzipStart = System.currentTimeMillis();
            byte[] gzipBytes = GzipUtils.compress(bytes);
//            long memAfterCompress = total - Runtime.getRuntime().freeMemory();
            System.out.print(", compress use:" + (System.currentTimeMillis() - gzipStart) + ", gzip length:" +
                    gzipBytes.length + " ");
            long ungzipStart = System.currentTimeMillis();
            bytes = GzipUtils.uncompress(gzipBytes);
            ungzipTime = System.currentTimeMillis() - ungzipStart;
            
        }
        System.out.println();
        gc();
        long deserializeStart = System.currentTimeMillis();
        long memBeforeDeserialize = total - Runtime.getRuntime().freeMemory();
        deserializeTest(bytes);
        long memAfterDeserialize = total - Runtime.getRuntime().freeMemory();
        long deserializeTime = System.currentTimeMillis() - deserializeStart;
        System.out.print(name() + " deserialize use:" + deserializeTime + ", memory use:" + (memAfterDeserialize -
                memBeforeDeserialize));
        if (needCompress()) {
            System.out.print(", decompress use:" + ungzipTime + "");
        }
        System.out.println();
    }
    
    private void gc() {
        System.gc();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
