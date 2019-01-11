package com.digisky.canglong.msgtest.tester.jdk;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.digisky.canglong.msgtest.db.Equip;
import com.digisky.canglong.msgtest.msg.EquipInventoryInit;
import com.digisky.canglong.msgtest.tester.AbstractTester;

/**
 * JdkTester.java
 *
 * @author LJX
 * @since 2019/1/9.
 */
public class JdkTester extends AbstractTester {
    
    @Override
    protected String name() {
        return "jdk";
    }
    
    @Override
    protected boolean needCompress() {
        return false;
    }
    
    @Override
    protected byte[] serializeTest(Equip[] equips) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oo = new ObjectOutputStream(baos);
            oo.writeObject(EquipInventoryInit.createMsg(equips));
            oo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }
    
    @Override
    protected void deserializeTest(byte[] bytes) {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        try {
            ObjectInputStream oi = new ObjectInputStream(bais);
            EquipInventoryInit jdkObject = (EquipInventoryInit) oi.readObject();
            oi.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
