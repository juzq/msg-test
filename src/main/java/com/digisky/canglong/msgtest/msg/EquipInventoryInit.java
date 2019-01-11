package com.digisky.canglong.msgtest.msg;

import java.io.Serializable;
import java.lang.instrument.Instrumentation;

import com.digisky.canglong.msgtest.Start;
import com.digisky.canglong.msgtest.db.Equip;
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
    
    public static EquipInventoryInit createMsg(Equip[] equips) {
        long start = System.currentTimeMillis();
        long memStart = Start.TOTAL_MEMORY - Runtime.getRuntime().freeMemory();
        EquipInventoryInit initMsg = new EquipInventoryInit();
        initMsg.setType(1);
        initMsg.setExtralCapacity(Start.NUM);
        initMsg.setEquips(new EquipMsg[equips.length]);
        for (int i = 0; i < equips.length; i++) {
            initMsg.getEquips()[i] = new EquipMsg().fillByEquip(equips[i]);
        }
        long memEnd = Start.TOTAL_MEMORY - Runtime.getRuntime().freeMemory();
        System.out.println("create use time:" + (System.currentTimeMillis() - start) + ", memory:" + (memEnd - memStart));
        return initMsg;
    }
    
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
