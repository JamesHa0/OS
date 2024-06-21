package DiskSim;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Hdd {// ����Ӳ����
	private String HddName;// ����Ӳ�̵�����

	private int size;//�����Ĵ�С
	
	private static Hdd vhd;//����Ӳ�̶���

	private Hdd() {
		this.HddName = "hdd.vhd";
		this.size = 96;
	}

	public byte[] read(int n) {//��ȡָ������������
		byte buf[] = new byte[this.size];
		try {
			RandomAccessFile ra = new RandomAccessFile(this.HddName, "r");
			ra.seek(n * this.size);
			for (int i = 0; i < this.size; i++) {
				buf[i] = ra.readByte();
			}
			ra.close();
		} catch (FileNotFoundException e) {
			// TODO �Զ����� catch ��
			for(int i=0;i<this.size;i++){
				buf[i]=0;
			}
			this.write(buf, n);
			//e.printStackTrace();
		} catch (IOException e) {
			// TODO �Զ����� catch ��
			e.printStackTrace();
		}
		return buf;
	}

	public void write(byte[] buf,int n){//��ָ���������д������
		try {
			RandomAccessFile ra=new RandomAccessFile(this.HddName,"rw");
			ra.seek(n*this.size);
			ra.write(buf);
			ra.close();
		} catch (FileNotFoundException e) {
			// TODO �Զ����� catch ��
			e.printStackTrace();
		} catch (IOException e) {
			// TODO �Զ����� catch ��
			e.printStackTrace();
		}
	}
	public static Hdd getVhd(){//ȡ��������̶���
		if(vhd==null){
			vhd=new Hdd();
		}
		return vhd;
	}

	public int getSize() {
		return size;
	}
}
