package com.digisky.canglong.msgtest.cl.gzip;

import java.util.ArrayList;

public class SendPacketUtilPool {

	private static final int defCount = 10;
	//空闲的PalcetUtil
	private static final ArrayList<SendPacketUtil> idleUtil = new ArrayList<SendPacketUtil>();
//	private static final LinkedList<SendPacketUtil> idleUtil = new LinkedArrayListList<SendPacketUtil>();
//	private static final Map<SendPacketUtil, SendPacketUtil> idleUtil = new ConcurrentHashMap<SendPacketUtil, SendPacketUtil>();
//	private static final LockFreeDeque<SendPacketUtil> idleUtil = new LockFreeDeque<SendPacketUtil>();
	//被使用的PalcetUtil
	private static final ArrayList<SendPacketUtil> busyUtil = new ArrayList<SendPacketUtil>();
//	private static final LinkedList<SendPacketUtil> busyUtil = new LinkedList<SendPacketUtil>();
//	private static final Map<SendPacketUtil, SendPacketUtil> busyUtil = new ConcurrentHashMap<SendPacketUtil, SendPacketUtil>();
//	private static final LockFreeDeque<SendPacketUtil> busyUtil = new LockFreeDeque<SendPacketUtil>();

	static{
		for(int i=0;i<defCount;i++){
			SendPacketUtil sp = new SendPacketUtil();
			idleUtil.add(sp);
//			idleUtil.put(sp,sp);
		}
	}
	public synchronized static SendPacketUtil getUtil(){//synchronized
		if( idleUtil.size()<2 ){
//			SendPacketUtil sp;
			for(int i=0;i<100;i++){
				idleUtil.add(new SendPacketUtil());
//				sp = new SendPacketUtil();
//				idleUtil.put(sp,sp);
			}
		}
//		SendPacketUtil util = idleUtil.remove(idleUtil.size()-1);
		
		SendPacketUtil util =  idleUtil.remove(idleUtil.size()-1);
		if(util == null){
			util = new SendPacketUtil();
		}
//		Iterator<SendPacketUtil> it=	idleUtil.keySet().iterator();
//		SendPacketUtil util=null;
//		if(it.hasNext()){
//			util=it.next();
//			it.remove();
//		}
//		else {
////			util = util==null ?  new SendPacketUtil() : util;
//			 util=new SendPacketUtil();
//		}
		busyUtil.add(util);
//		busyUtil.put(util, util);
		return util;
	}

	public synchronized static void setIdle(SendPacketUtil util) {//synchronized
		busyUtil.remove(util);
		idleUtil.add(util);
//		idleUtil.put(util,util);
	}
	
	public int getIdlesSize(){
		return idleUtil.size();
	}
	
	public int getBusysSize(){
		return busyUtil.size();
	}
	
	public static void printStatus(){
		System.out.println("idleUtil:"+idleUtil.size()+" : busyUtil:"+busyUtil.size());
	}
	
}