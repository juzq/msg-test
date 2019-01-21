package com.digisky.canglong.msgtest.tester.flatbuffer;

import java.nio.ByteBuffer;

import com.digisky.canglong.msgtest.Start;
import com.digisky.canglong.msgtest.db.Equip;
import com.digisky.canglong.msgtest.tester.AbstractTester;
import com.google.flatbuffers.FlatBufferBuilder;

/**
 * FbTester.java
 *
 * @author LJX
 * @since 2019/1/14.
 */
public class FbTester extends AbstractTester {
    
    @Override
    protected String name() {
        return "flatbuffers";
    }
    
    @Override
    protected boolean needCompress() {
        return true;
    }
    
    @Override
    protected byte[] serializeTest(Equip[] equips) {
        FlatBufferBuilder builder = new FlatBufferBuilder();
        int[] equipsIndex = new int[equips.length];
        for (int i = 0; i < equips.length; i++) {
            Equip equip = equips[i];
            // atts
            int attOffset = FbEquip.createAttsVector(builder, equip.getExtralAtts());
            int equipOffset = FbEquip.createFbEquip(builder, equip.getId(), equip.getLv(), equip.getQuality(),
                    equip.getStar(), attOffset);
            equipsIndex[i] = equipOffset;
        }
        // 一定要先create里层的FbEquip然后再createEquipsVector，否则会报错
        int equipsOffset = FbEquipInventoryInit.createEquipsVector(builder, equipsIndex);
        FbEquipInventoryInit.startFbEquipInventoryInit(builder);
        // type
        FbEquipInventoryInit.addType(builder, 1);
        // extralCapacity
        FbEquipInventoryInit.addExtralCapacity(builder, Start.NUM);
        // equips
        FbEquipInventoryInit.addEquips(builder, equipsOffset);
        int end = FbEquipInventoryInit.endFbEquipInventoryInit(builder);
        builder.finish(end);
        return builder.sizedByteArray();
    }
    
    @Override
    protected void deserializeTest(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        FbEquipInventoryInit init = FbEquipInventoryInit.getRootAsFbEquipInventoryInit(bb);
        int type = init.type();
        int extralCapacity = init.extralCapacity();
//        init.equips(1312);
//        System.out.println(init.type());
//        System.out.println(init.extralCapacity());
//        System.out.println(init.equipsLength());
//        FbEquip equip = init.equips(1651);
//        System.out.println(equip.id());
//        System.out.println(equip.lv());
//        System.out.println(equip.quality());
//        System.out.println(equip.star());
//        System.out.println(equip.atts(0));
    }
}
