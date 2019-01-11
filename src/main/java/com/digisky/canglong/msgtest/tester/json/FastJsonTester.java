package com.digisky.canglong.msgtest.tester.json;

import com.alibaba.fastjson.JSON;
import com.digisky.canglong.msgtest.db.Equip;
import com.digisky.canglong.msgtest.msg.EquipInventoryInit;
import com.digisky.canglong.msgtest.tester.AbstractTester;

/**
 * FastJsonTester.java
 *
 * @author LJX
 * @since 2019/1/9.
 */
public class FastJsonTester extends AbstractTester {
    
    @Override
    protected String name() {
        return "fastjson";
    }
    
    @Override
    protected boolean needCompress() {
        return true;
    }
    
    @Override
    protected byte[] serializeTest(Equip[] equips) {
        return JSON.toJSONBytes(EquipInventoryInit.createMsg(equips));
    }
    
    @Override
    protected void deserializeTest(byte[] bytes) {
        EquipInventoryInit o = JSON.parseObject(bytes, EquipInventoryInit.class);
    }
}
