package com.digisky.canglong.msgtest;

import com.digisky.canglong.msgtest.db.Equip;
import com.digisky.canglong.msgtest.tester.AbstractTester;
import com.digisky.canglong.msgtest.tester.bio.BioTester;
import com.digisky.canglong.msgtest.tester.flatbuffer.FbTester;
import com.digisky.canglong.msgtest.tester.jdk.JdkTester;
import com.digisky.canglong.msgtest.tester.json.FastJsonTester;
import com.digisky.canglong.msgtest.tester.json.JsoniterTester;
import com.digisky.canglong.msgtest.tester.msgpack.MsgpackTester;
import com.digisky.canglong.msgtest.tester.protobuf.PbTester;

/**
 * 启动类
 *
 * @author LJX
 * @since 2019/1/7.
 */
public class Start {
    
    public static final int NUM = 1000;
    
    public static long TOTAL_MEMORY = Runtime.getRuntime().totalMemory();
    
    public static void main(String[] args) {
        System.out.println("start...");
        long startTime = System.currentTimeMillis();
        // 构造测试数据
        long id = 100000000000L;
        Equip[] equips = new Equip[NUM];
        for (int i = 0; i < NUM; i++) {
            equips[i] = new Equip().random(id++);
        }
        System.out.println("make data use:" + (System.currentTimeMillis() - startTime));
    
        AbstractTester[] testers = {
                new JdkTester(),
                new PbTester(),
                new FbTester(),
                new FastJsonTester(),
                new JsoniterTester(),
                new MsgpackTester(),
                new BioTester()
        };
    
        for (AbstractTester tester : testers) {
            tester.doTest(equips);
        }
    
    }
    
    
}
