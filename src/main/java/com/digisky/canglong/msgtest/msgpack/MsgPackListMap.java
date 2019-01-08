package com.digisky.canglong.msgtest.msgpack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.digisky.canglong.msgtest.db.Equip;
import org.msgpack.annotation.Message;

/**
 * MsgPackListMap.java
 *
 * @author LJX
 * @since 2019/1/8.
 */
@Message
public class MsgPackListMap {
    private List<Equip> equipList = new ArrayList<>();
    private Map<Long, Equip> equipMap = new HashMap<>();
    
    public List<Equip> getEquipList() {
        return equipList;
    }
    
    public void setEquipList(List<Equip> equipList) {
        this.equipList = equipList;
    }
    
    public Map<Long, Equip> getEquipMap() {
        return equipMap;
    }
    
    public void setEquipMap(Map<Long, Equip> equipMap) {
        this.equipMap = equipMap;
    }
}
