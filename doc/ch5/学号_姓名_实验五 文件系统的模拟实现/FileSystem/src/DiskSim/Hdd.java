package DiskSim;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Hdd {// 虚拟硬盘类
	private String HddName;// 虚拟硬盘的名称

	private int size;//物理块的大小
	
	private static Hdd vhd;//虚拟硬盘对象

	private Hdd() {
		this.HddName = "hdd.vhd";
		this.size = 96;
	}

	public byte[] read(int n) {//读取指定物理块的内容
		byte buf[] = new byte[this.size];
		try {
			RandomAccessFile ra = new RandomAccessFile(this.HddName, "r");
			ra.seek(n * this.size);
			for (int i = 0; i < this.size; i++) {
				buf[i] = ra.readByte();
			}
			ra.close();
		} catch (FileNotFoundException e) {
			// TODO 自动生成 catch 块
			for(int i=0;i<this.size;i++){
				buf[i]=0;
			}
			this.write(buf, n);
			//e.printStackTrace();
		} catch (IOException e) {
			// TODO 自动生成 catch 块
			e.printStackTrace();
		}
		return buf;
	}

	public void write(byte[] buf,int n){//向指定的物理块写入数据
		try {
			RandomAccessFile ra=new RandomAccessFile(this.HddName,"rw");
			ra.seek(n*this.size);
			ra.write(buf);
			ra.close();
		} catch (FileNotFoundException e) {
			// TODO 自动生成 catch 块
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自动生成 catch 块
			e.printStackTrace();
		}
	}
	public static Hdd getVhd(){//取得虚拟磁盘对象
		if(vhd==null){
			vhd=new Hdd();
		}
		return vhd;
	}

	public int getSize() {
		return size;
	}
}
