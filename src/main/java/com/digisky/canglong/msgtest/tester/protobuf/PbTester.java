package com.digisky.canglong.msgtest.tester.protobuf;

import com.digisky.canglong.msgtest.Start;
import com.digisky.canglong.msgtest.db.Equip;
import com.digisky.canglong.msgtest.tester.AbstractTester;
import com.digisky.canglong.msgtest.tester.protobuf.MsgProto.PbEquipInventoryInit;
import com.digisky.canglong.msgtest.tester.protobuf.MsgProto.PbEquipInventoryInit.Builder;
import com.digisky.canglong.msgtest.tester.protobuf.MsgProto.PbExtralAtts;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * PbTester.java
 *
 * @author LJX
 * @since 2019/1/9.
 */
public class PbTester extends AbstractTester {
    
    @Override
    protected String name() {
        return "protobuf";
    }
    
    @Override
    protected boolean needCompress() {
        return false;
    }
    
    @Override
    protected byte[] serializeTest(Equip[] equips) {
        Builder builder = PbEquipInventoryInit.newBuilder();
        builder.setType(1);
        builder.setExtralCapacity(Start.NUM);
        for (Equip equip : equips) {
            MsgProto.PbEquip.Builder equipBuilder = MsgProto.PbEquip.newBuilder();
            equipBuilder.setId(equip.getId());
            equipBuilder.setLv(equip.getLv());
            equipBuilder.setQuality(equip.getQuality());
            equipBuilder.setStar(equip.getStar());
            int[] extralAtts = equip.getExtralAtts();
            for (int i = 0; i < extralAtts.length; i++) {
                PbExtralAtts.Builder attBuilder = PbExtralAtts.newBuilder();
                attBuilder.setAttTypeValue(i);
                attBuilder.setValue(extralAtts[i]);
                equipBuilder.addAtts(attBuilder);
            }
        
            builder.addEquips(equipBuilder.build());
        }
        return builder.build().toByteArray();
    }
    
    @Override
    protected void deserializeTest(byte[] bytes) {
        try {
            Builder mergeFrom = PbEquipInventoryInit.newBuilder().mergeFrom(bytes);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }
}
