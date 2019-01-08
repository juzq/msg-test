package com.digisky.canglong.msgtest.cl.gzip;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import flex.messaging.io.SerializationContext;
import flex.messaging.io.amf.ASObject;
import flex.messaging.io.amf.Amf3Input;
import org.apache.mina.core.buffer.IoBuffer;

public class SendPacketUtil {
	private static final SerializationContext context = new SerializationContext();

	private ByteArrayOutputStream baos = new ByteArrayOutputStream();
	private DataOutputStream dos = new DataOutputStream(baos);
	private Amf3Output amfOut = new Amf3Output(context);

	private Amf3Input amfIn = new Amf3Input(context);

	private ByteArrayOutputStream deflaterOut = new ByteArrayOutputStream();
	private Deflater deflater = new Deflater();

	public static void main(String[] args) throws IOException {
		String send =" {lt={5={qs=100, jsid=3300021, zdsm=6753, wz=9, sm=6753}, 4={qs=100, jsid=3300016, zdsm=7376, wz=8, sm=7376}, 3={qs=100, jsid=3300011, zdsm=6602, wz=7, sm=6602},"+
				" 2={qs=100, jsid=3300005, zdsm=7512, wz=3, sm=7512}, 1={qs=100, jsid=3300002, zdsm=7585, wz=1, sm=7585}}, rt={5={qs=0, jsid=3200429, zdsm=23865, wz=18, sm=23865"+
				"}, 4={qs=0, jsid=3200254, zdsm=12942, wz=14, sm=12942}, 3={qs=0, jsid=3200254, zdsm=12942, wz=13, sm=12942}, 2={qs=0, jsid=3200255, zdsm=15187, wz=12, sm=15187}"+
				 ", 1={qs=0, jsid=3200255, zdsm=15187, wz=11, sm=15187}}, res=4, turn={12={fyfs={1={qs=75, xg=100, sh=4429, wz=9}}, gjf={qs=50, fs=1, jn=4100402, wz=14}}, 11={fyf"+
				 "s={1={qs=50, xg=100, sh=4414, wz=9}}, gjf={qs=75, fs=1, jn=4100402, wz=12, sh1={1=2512}}}, 10={fyfs={1={qs=50, xg=110, sh=3028, wz=12}}, gjf={qs=25, fs=1, jn=41"+
				 "00403, wz=9}}, 9={fyfs={4={xg=100, sh=3539, wz=18}, 3={xg=100, sh=3742, wz=14}, 2={xg=101, sh=1679, wz=12}, 1={xg=100, sh=3460, wz=11}}, gjf={qs=0, fs=2, jn=410"+
				 "0021, wz=9}}, 8={fyfs={1={qs=25, xg=100, sh=8906, wz=3}}, gjf={qs=25, fs=1, jn=4100402, wz=18}}, 7={fyfs={3={xg=100, sh=2795, wz=14}, 2={fs=4456, xg=100, sh=275"+
				 "1, nbf={1=4200025}, wz=12}, 1={xg=100, sh=2672, nbf={1=4200025}, wz=11}}, gjf={qs=0, fs=2, jn=4100016, wz=8}}, 6={fyfs={1={qs=25, xg=110, sh=7817, wz=7}}, gjf={"+
				 "qs=25, fs=1, jn=4100402, wz=14}}, 5={fyfs={5={xg=100, sh=5339, wz=18}, 4={xg=100, sh=5338, wz=14}, 3={xg=110, sh=7845, wz=13}, 2={xg=101, sh=2474, wz=12}, 1={xg"+
				 "=100, sh=4949, wz=11}}, gjf={qs=0, fs=2, jn=4100011, wz=7}}, 4={fyfs={1={qs=125, xg=100, sh=4240, wz=8}}, gjf={qs=25, fs=1, jn=4100402, wz=12}}, 3={fyfs={1={xg="+
				 "100, sh=6786, wz=13}}, gjf={qs=0, fs=2, jn=4100005, wz=3}}, 2={fyfs={1={qs=25, xg=110, sh=6699, wz=1}}, gjf={qs=25, fs=1, jn=4100402, wz=11}}, 1={fyfs={1={fs=43"+
				 "36, xg=111, sh=6308, wz=11}}, gjf={qs=0, fs=2, jn=4100002, wz=1}}}}";
		
//		byte[] bs  = send.getBytes();
		for(int i = 0 ; i < 1;i++){ 
			SendPacketUtilPool.getUtil().getCompressedBytes(send);
		}
		
		long ms = System.currentTimeMillis();
		for(int i = 0 ; i < 200000;i++){ 
			 SendPacketUtilPool.getUtil().getCompressedBytes(send);
		}
		ms = System.currentTimeMillis() - ms;
		System.out.println("10000 gzip cost time:"+ms);
		
		Scanner in = new Scanner(System.in);
		in.nextLine();
	}
	
	
	private ByteArrayOutputStream gzipos;
	private CustomGzipOutputStream gos;
	{
		gzipos = new ByteArrayOutputStream();
		try {
			gos = new CustomGzipOutputStream(gzipos);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//	private LuaOutputStream los = LuaOutputStream.allocate(1024);

	private byte[] cacheByte = new byte[512];

	private ByteArrayOutputStream inflaterOut = new ByteArrayOutputStream();
	private Inflater inflater = new Inflater();

	public SendPacketUtil(){
		amfOut.setOutputStream(dos);
	}
	/**
	 * 获取压缩后的数据
	 * @param pak
	 * @return
	 */
	public  byte[] getCompressedBytes(Object pak){
		synchronized (baos) {
			byte[] compressedByte = null;
			try {
//				long t1 = System.currentTimeMillis();
				baos.reset();
				amfOut.reset();
				amfOut.writeObject(pak);
				compressedByte = compress(baos.toByteArray());
//				long t2 = System.currentTimeMillis() - t1;
//				System.out.println("压缩消耗时间:=>" + t2 + " 压缩前:" + baos.toByteArray().length + " 压缩以后|:" + compressedByte.length );	
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				SendPacketUtilPool.setIdle(this);
			}
			return compressedByte;
		}
		
	}
	/**
	 * 从接收到的数据中提取出信息并封装为Packet
	 * @param buffer
	 * @param dataLen
	 * @return
	 */
	public Object getData(IoBuffer buffer, int dataLen){
		Packet pak = null;
		try{
			inflaterOut.reset();
			inflater.reset();
//			inflater=new Inflater();
			amfIn.reset();

			byte[] b = new byte[dataLen];
			buffer.get(b);
			inflater.setInput(b);
			int len;
//			cacheByte = new byte[512];
			do{
				len = inflater.inflate(cacheByte);
				inflaterOut.write(cacheByte, 0, len);
			}while(!inflater.finished());

			amfIn.setInputStream(new ByteArrayInputStream( inflaterOut.toByteArray() ));

			ASObject asPak = null;
			try {
				Object o = amfIn.readObject();
				if(!(o instanceof ASObject)){
					return o;
				}
				asPak = (ASObject)o;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if( asPak!=null ){
				pak = new Packet((Integer) asPak.get("command"), asPak.get("object"));
			}
		} catch(DataFormatException e){
			e.printStackTrace();
		} finally{
			SendPacketUtilPool.setIdle(this);
		}
		return pak;
	}

	public Object getData(IoBuffer buf) throws DataFormatException {
		return getData(buf, buf.limit());
	}
	/**
	 * 进行压缩
	 * @param bs
	 * @return
	 */
	private byte[] compress(byte[] bs){
		deflaterOut.reset();
		deflater.reset();
		deflater.setInput(bs);
		deflater.finish();
		int len = 0;
		deflater.finish();
		byte outputs[] = new byte[0];
		do{
			len = deflater.deflate(cacheByte);
			deflaterOut.write(cacheByte, 0, len);
		}while(!deflater.finished());

		outputs = deflaterOut.toByteArray();
		//		try {
		//			deflaterOut.close();
		//		} catch (Exception e) {
		//		}
		return outputs;
	}



	/**
	 * 进行压缩
	 * @param bs
	 * @return
	 * @throws IOException
	 */
	public  byte[] gzip(byte[] bs) throws IOException {
//		synchronized(gos){
			byte[] compressedByte = null;
			try {
				gos.write(bs);
				gos.finish();
				gzipos.flush();
				compressedByte = gzipos.toByteArray();
				gzipos.reset();
				gos.reset();
				gos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				SendPacketUtilPool.setIdle(this);
			}
			return compressedByte;
//		}
	}

//	public  final  byte[] objectToLuaBytes(Object o) throws IOException {
//		synchronized(los){
//			byte[] ret = null;
//			try{
////				los.free();
//				los.buff.sweep();
//				los.write_object(o);
//				ret = los.toByteArray();
//			}catch (IOException e) {
//				e.printStackTrace();
//			} finally {
//				SendPacketUtilPool.setIdle(this);
//			}
////			los.free();
//			return ret;
//		}
//	}
//
//	public   final  byte[] objectToLuaBytes(Object o, int buffLen)	throws IOException {
//		synchronized(los){
//			byte[] ret = null;
//			try{
//				los.buff.sweep();
//				los.capacity(buffLen);
//				los.write_object(o);
//				ret = los.toByteArray();
//				los.free();
//			}catch (IOException e) {
//				e.printStackTrace();
//			} finally {
//				SendPacketUtilPool.setIdle(this);
//			}
//			return ret;
//		}
//	}
//
//
//	public  final  byte[] mapToLuaBytes(Map m) throws IOException {
//		return objectToLuaBytes(m);
//	}
//	//
//	public  final  byte[] mapToLuaBytes(Map m, int buffLen) throws IOException {
//		return objectToLuaBytes(m, buffLen);
//	}


}
