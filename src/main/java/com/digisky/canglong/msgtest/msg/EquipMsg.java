package com.digisky.canglong.msgtest.msg;

import java.io.Serializable;

import com.digisky.canglong.msgtest.db.Equip;
import org.msgpack.annotation.Message;

/**
 * EquipMsg.java
 *
 * @author LJX
 * @since 2019/1/7.
 */
@Message
public class EquipMsg implements Serializable {
    
    private static final long serialVersionUID = -8038931657932458720L;
    
    private long id;
    private int lv;
    private int quality;
    private int star;
    private int[] extralAtts = new int[5];
    
    public EquipMsg fillByEquip(Equip equip) {
        id = equip.getId();
        lv = equip.getLv();
        quality = equip.getQuality();
        star = equip.getStar();
        for (int i = 0; i < extralAtts.length; i++) {
            extralAtts[i] = equip.getExtralAtts()[i];
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
