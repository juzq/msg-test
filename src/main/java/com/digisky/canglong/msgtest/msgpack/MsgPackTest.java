package com.digisky.canglong.msgtest.msgpack;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import com.digisky.canglong.msgtest.db.Equip;
import org.msgpack.MessagePack;

/**
 * 网上据说Messagepack对List和Map的支持不好，测试后发现根本没有的事
 *
 * @author LJX
 * @since 2019/1/8.
 */
public class MsgPackTest {
    
    public static void main(String[] args) throws IOException {
        MsgPackListMap test = new MsgPackListMap();
        AtomicLong id = new AtomicLong(100000000000L);
        for (int i = 0; i < 100; i++) {
            test.getEquipList().add(new Equip().random(id.getAndIncrement()));
        }
        AtomicLong id2 = new AtomicLong(200000000000L);
        for (int i = 0; i < 100; i++) {
            Equip equip = new Equip().random(id2.getAndIncrement());
            test.getEquipMap().put(equip.getId(), equip);
        }
    
        MessagePack msgpack = new MessagePack();
        byte[] msgpackBytes = msgpack.write(test);
    
        MsgPackListMap msgpackMsg = msgpack.read(msgpackBytes, MsgPackListMap.class);
        System.out.println(msgpackMsg.getEquipList().size());
    }
}
