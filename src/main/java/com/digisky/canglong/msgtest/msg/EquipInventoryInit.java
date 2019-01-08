package com.digisky.canglong.msgtest.msg;

import java.io.Serializable;

import org.msgpack.annotation.Message;

/**
 * 装备背包初始化
 *
 * @author LJX
 * @since 2019/1/7.
 */
@Message
public class EquipInventoryInit implements Serializable {
    
    private static final long serialVersionUID = -5170790983991466787L;
    
    /** 背包类型 */
    private int type;
    /** 背包里的装备 */
    private EquipMsg[] equips;
    /** 背包的扩展容量 */
    private int extralCapacity;
    
    public int getType() {
        return type;
    }
    
    public void setType(int type) {
        this.type = type;
    }
    
    public int getExtralCapacity() {
        return extralCapacity;
    }
    
    public void setExtralCapacity(int extralCapacity) {
        this.extralCapacity = extralCapacity;
    }
    
    public EquipMsg[] getEquips() {
        return equips;
    }
    
    public void setEquips(EquipMsg[] equips) {
        this.equips = equips;
    }
}
