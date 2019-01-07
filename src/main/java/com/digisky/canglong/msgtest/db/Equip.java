package com.digisky.canglong.msgtest.db;

import java.util.Random;

/**
 * 装备
 *
 * @author LJX
 * @since 2019/1/7.
 */
public class Equip {
    
    private long id;
    private int lv;
    private int quality;
    private int star;
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
