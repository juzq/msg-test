package com.digisky.canglong.msgtest.tester.bio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.digisky.canglong.msgtest.Start;
import com.digisky.canglong.msgtest.db.Equip;
import com.digisky.canglong.msgtest.tester.AbstractTester;
import com.digisky.canglong.msgtest.tester.bio.tools.BioHelper;

/**
 * BioTester.java
 *
 * @author LJX
 * @since 2019/1/9.
 */
public class BioTester extends AbstractTester {
    
    @Override
    protected String name() {
        return "bio";
    }
    
    @Override
    protected boolean needCompress() {
        return true;
    }
    
    @Override
    protected byte[] serializeTest(Equip[] equips) {
        // 构建map数据
        Map msg = new HashMap();
        Map r = new HashMap();
        msg.put("r", r);
        r.put("type", 1);
        r.put("cap", Start.NUM);
        List equipList = new ArrayList();
        r.put("equips", equipList);
        for (Equip equip : equips) {
            Map equipMsg = new HashMap();
            equipMsg.put("id", equip.getId());
            equipMsg.put("lv", equip.getLv());
            equipMsg.put("quality", equip.getQuality());
            equipMsg.put("star", equip.getStar());
            Map extralAttsMsg = new HashMap();
            equipMsg.put("atts", extralAttsMsg);
            int[] extralAtts = equip.getExtralAtts();
            for (int i = 0; i < extralAtts.length; i++) {
                extralAttsMsg.put(i, extralAtts[i]);
            }
            equipList.add(equipMsg);
        }
        try {
            return BioHelper.mapToBytes(msg, 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    protected void deserializeTest(byte[] bytes) {
        try {
            Map map = BioHelper.mapFromBytes(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
