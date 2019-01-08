package com.digisky.canglong.msgtest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.alibaba.fastjson.JSON;
import com.digisky.canglong.msgtest.cl.BioHelper;
import com.digisky.canglong.msgtest.cl.gzip.SendPacketUtil;
import com.digisky.canglong.msgtest.cl.gzip.SendPacketUtilPool;
import com.digisky.canglong.msgtest.db.Equip;
import com.digisky.canglong.msgtest.msg.EquipInventoryInit;
import com.digisky.canglong.msgtest.msg.EquipMsg;
import com.digisky.canglong.msgtest.protobuf.TestProto;
import com.digisky.canglong.msgtest.protobuf.TestProto.PbEquipInventoryInit;
import com.digisky.canglong.msgtest.protobuf.TestProto.PbEquipInventoryInit.Builder;
import com.digisky.canglong.msgtest.protobuf.TestProto.PbExtralAtts;
import com.jsoniter.JsonIterator;
import com.jsoniter.output.JsonStream;
import org.msgpack.MessagePack;

/**
 * 启动类
 *
 * @author LJX
 * @since 2019/1/7.
 */
public class Start {
    
    
    private static final int NUM = 1000000;
    
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        System.out.println("start...");
        long startTime = System.currentTimeMillis();
        // 构造测试数据
        AtomicLong id = new AtomicLong(100000000000L);
        Equip[] equips = new Equip[NUM];
        for (int i = 0; i < NUM; i++) {
            equips[i] = new Equip().random(id.getAndIncrement());
        }
        System.out.println("make data use:" + (System.currentTimeMillis() - startTime));
        
        // jdk
        long jdkSerializeStart = System.currentTimeMillis();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oo = new ObjectOutputStream(baos);
        oo.writeObject(createMsg(equips));
        oo.close();
        byte[] jdkBytes = baos.toByteArray();
        System.out.println("jdk serialize use:" + (System.currentTimeMillis() - jdkSerializeStart) + ", size:" + jdkBytes.length);
        long jdkDeerializeStart = System.currentTimeMillis();
        ByteArrayInputStream bais = new ByteArrayInputStream(jdkBytes);
        ObjectInputStream oi = new ObjectInputStream(bais);
        EquipInventoryInit jdkObject = (EquipInventoryInit) oi.readObject();
        oi.close();
        System.out.println("jdk deserialize use:" + (System.currentTimeMillis() - jdkDeerializeStart));
        System.out.println(jdkObject.getExtralCapacity());
    
        // protobuf
        long protobufSerializeStart = System.currentTimeMillis();
        Builder builder = PbEquipInventoryInit.newBuilder();
        builder.setType(1);
        builder.setExtralCapacity(NUM);
        for (Equip equip : equips) {
            TestProto.PbEquip.Builder equipBuilder = TestProto.PbEquip.newBuilder();
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
        byte[] protobufBytes = builder.build().toByteArray();
        System.out.println("protobuf serialize use:" + (System.currentTimeMillis() - protobufSerializeStart) + ", size:" + protobufBytes.length);
        long protobufDeerializeStart = System.currentTimeMillis();
        Builder mergeFrom = PbEquipInventoryInit.newBuilder().mergeFrom(protobufBytes);
        System.out.println("protobuf deserialize use:" + (System.currentTimeMillis() - protobufDeerializeStart));
        System.out.println(mergeFrom.getExtralCapacity());
        
        // fastjson
        long fastjsonSerializeStart = System.currentTimeMillis();
        byte[] fastjsonBytes = JSON.toJSONBytes(createMsg(equips));
        System.out.println("fastjson serialize use:" + (System.currentTimeMillis() - fastjsonSerializeStart) + ", size:" + fastjsonBytes.length);
        long fastjsonDeerializeStart = System.currentTimeMillis();
        EquipInventoryInit o = JSON.parseObject(fastjsonBytes, EquipInventoryInit.class);
        System.out.println("fastjson deserialize use:" + (System.currentTimeMillis() - fastjsonDeerializeStart));
        System.out.println(o.getExtralCapacity());
        
        // jsoniter
        long jsoniterSerializeStart = System.currentTimeMillis();
        byte[] jsoniterBytes = JsonStream.serialize(createMsg(equips)).getBytes();
        System.out.println("jsoniter serialize use:" + (System.currentTimeMillis() - jsoniterSerializeStart) + ", size:" + jsoniterBytes.length);
        long jsoniterDeerializeStart = System.currentTimeMillis();
        EquipInventoryInit jsoniterMsg = JsonIterator.deserialize(fastjsonBytes, EquipInventoryInit.class);
        System.out.println("jsoniter deserialize use:" + (System.currentTimeMillis() - jsoniterDeerializeStart));
        System.out.println(jsoniterMsg.getExtralCapacity());
    
        // msgpack
        long msgpackSerializeStart = System.currentTimeMillis();
        MessagePack msgpack = new MessagePack();
        byte[] msgpackBytes = msgpack.write(createMsg(equips));
        System.out.println("msgpack serialize use:" + (System.currentTimeMillis() - msgpackSerializeStart) + ", size:" + msgpackBytes.length);
        long msgpackDeerializeStart = System.currentTimeMillis();
        EquipInventoryInit msgpackMsg = msgpack.read(msgpackBytes, EquipInventoryInit.class);
        System.out.println("msgpack deserialize use:" + (System.currentTimeMillis() - msgpackDeerializeStart));
        System.out.println(msgpackMsg.getExtralCapacity());
        
        // map
        long mapSerializeStart = System.currentTimeMillis();
        // 构建map数据
        Map msg = new HashMap();
        Map r = new HashMap();
        msg.put("r", r);
        r.put("type", 1);
        r.put("cap", NUM);
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
        byte[] mapBytes = BioHelper.mapToBytes(msg, 1024);
        long gzipStart = System.currentTimeMillis();
        // gzip
        byte[] gzipBytes = SendPacketUtilPool.getUtil().gzip(mapBytes);
        long now = System.currentTimeMillis();
        System.out.println("map serialize use:" + (now - mapSerializeStart) + ", size:" +
                mapBytes.length + ", gzip size:" + gzipBytes.length + ", gzip use:" + (now - gzipStart));
        long mapDeserializeStart = System.currentTimeMillis();
        Map map = BioHelper.mapFromBytes(mapBytes);
        System.out.println("map deserialize use:" + (System.currentTimeMillis() - mapDeserializeStart));
        Map r2 = (Map) map.get("r");
        System.out.println(r2.get("cap"));
    }
    
    private static EquipInventoryInit createMsg(Equip[] equips) {
        long start = System.currentTimeMillis();
        EquipInventoryInit initMsg = new EquipInventoryInit();
        initMsg.setType(1);
        initMsg.setExtralCapacity(NUM);
        initMsg.setEquips(new EquipMsg[equips.length]);
        for (int i = 0; i < equips.length; i++) {
            initMsg.getEquips()[i] = new EquipMsg().fillByEquip(equips[i]);
        }
        System.out.println("create use time:" + (System.currentTimeMillis() - start));
        return initMsg;
    }
}
