package com.digisky.canglong.msgtest.db;

import java.util.Random;

import org.msgpack.annotation.Message;

/**
 * 装备
 *
 * @author LJX
 * @since 2019/1/7.
 */
@Message
public class Equip {
    
    /** 装备唯一id */
    private long id;
    /** 装备等级 */
    private int lv;
    /** 装备品质 */
    private int quality;
    /** 装备星级 */
    private int star;
    /** 装备附加属性 */
    private int[] extralAtts = new int[5];
    
    public Equip random(long id) {
        this.id = id;
        lv = new Random().nextInt(100);
        quality = new Random().nextInt(100);
        star = new Random().nextInt(100);
        for (int i = 0; i < extralAtts.length; i++) {
            extralAtts[i] = new Random().nextInt(10000);
        }
        return this;
    }
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public int getLv() {
        return lv;
    }
    
    public void setLv(int lv) {
        this.lv = lv;
    }
    
    public int getQuality() {
        return quality;
    }
    
    public void setQuality(int quality) {
        this.quality = quality;
    }
    
    public int getStar() {
        return star;
    }
    
    public void setStar(int star) {
        this.star = star;
    }
    
    public int[] getExtralAtts() {
        return extralAtts;
    }
    
    public void setExtralAtts(int[] extralAtts) {
        this.extralAtts = extralAtts;
    }
}
