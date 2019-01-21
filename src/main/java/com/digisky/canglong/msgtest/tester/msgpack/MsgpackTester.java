package com.digisky.canglong.msgtest.tester.msgpack;

import java.io.IOException;

import com.digisky.canglong.msgtest.db.Equip;
import com.digisky.canglong.msgtest.msg.EquipInventoryInit;
import com.digisky.canglong.msgtest.tester.AbstractTester;
import org.msgpack.MessagePack;

/**
 * MsgpackTester.java
 *
 * @author LJX
 * @since 2019/1/9.
 */
public class MsgpackTester extends AbstractTester {
    
    @Override
    protected String name() {
        return "msgpack";
    }
    
    @Override
    protected boolean needCompress() {
        return true;
    }
    
    @Override
    protected byte[] serializeTest(Equip[] equips) {
        MessagePack msgpack = new MessagePack();
        try {
            return msgpack.write(EquipInventoryInit.createMsg(equips));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    protected void deserializeTest(byte[] bytes) {
        MessagePack msgpack = new MessagePack();
        try {
            EquipInventoryInit msgpackMsg = msgpack.read(bytes, EquipInventoryInit.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
