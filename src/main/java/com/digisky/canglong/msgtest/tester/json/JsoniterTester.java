package com.digisky.canglong.msgtest.tester.json;

import com.digisky.canglong.msgtest.db.Equip;
import com.digisky.canglong.msgtest.msg.EquipInventoryInit;
import com.digisky.canglong.msgtest.tester.AbstractTester;
import com.jsoniter.JsonIterator;
import com.jsoniter.output.JsonStream;

/**
 * JsoniterTester.java
 *
 * @author LJX
 * @since 2019/1/9.
 */
public class JsoniterTester extends AbstractTester {
    
    @Override
    protected String name() {
        return "jsoniter";
    }
    
    @Override
    protected boolean needCompress() {
        return true;
    }
    
    @Override
    protected byte[] serializeTest(Equip[] equips) {
        return JsonStream.serialize(EquipInventoryInit.createMsg(equips)).getBytes();
    }
    
    @Override
    protected void deserializeTest(byte[] bytes) {
        EquipInventoryInit jsoniterMsg = JsonIterator.deserialize(bytes, EquipInventoryInit.class);
    }
}
