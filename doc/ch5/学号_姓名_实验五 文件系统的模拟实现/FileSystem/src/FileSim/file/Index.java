package FileSim.file;

import DiskSim.Hdd;

public class Index {// �����ڵ�
	private byte[] indexId;// ������

	private byte[] name;// �ļ�����

	private byte[] ptr;// ָ��

	public Index(byte index) {
		this.name = new byte[32];
		this.indexId = new byte[32];
		this.ptr = new byte[32];
		Hdd vhd = Hdd.getVhd();
		byte buf[] = vhd.read(index);
		for (int i = 0; i < 32; i++) {
			this.indexId[i] = buf[i];
			this.name[i] = buf[i + 32];
			this.ptr[i] = buf[i + 64];
		}
	}

	public Index(String name) {
		byte buf[] = name.getBytes();
		this.name = new byte[32];
		for (int i = 0; i < this.name.length; i++) {
			if (i < buf.length)
				this.name[i] = buf[i];
			else
				this.name[i] = 0;
		}

		this.indexId = new byte[32];
		this.ptr = new byte[32];
		for (int i = 0; i < 32; i++) {
			this.indexId[i] = 0;
			this.ptr[i] = 0;
		}
	}

	public void setIndexId(byte i, byte b) {
		this.indexId[i] = b;
	}

	public void setPtr(byte i, byte b) {
		this.ptr[i] = b;
	}

	public void save(int n) {// ������Ϣ������Ӳ��
		Hdd vhd = Hdd.getVhd();
		byte buf[] = new byte[vhd.getSize()];

		for (int i = 0; i < 32; i++) {
			buf[i] = this.indexId[i];
			buf[i + 32] = this.name[i];
			buf[i + 64] = this.ptr[i];
		}
		vhd.write(buf, n);
	}

	public String getName() {
		String name = null;
		name = new String(this.name);

		return name.trim();
	}

	public byte[] getIndexId() {
		return indexId;
	}

	public byte[] getPtr() {
		return ptr;
	}

	public String toString() {// ����ļ���Ϣ
		String s = null;
		int i = 0;
		for (; i < this.ptr.length; i++) {
			if (this.ptr[i] == 0)
				break;
		}
		i = (i + 1) * Hdd.getVhd().getSize();
		s = "�ļ�����" + new String(this.name).trim() + "\tռ�ÿռ䣺" + i + "byte";
		return s;
	}

	public void showIndex() {// ��ӡ�ļ��������ڵ���Ϣ
		System.out.println("�ļ�������Ϣ��");
		for (int i = 0; i < this.indexId.length; i++) {
                if(this.indexId[i]==0) break;
                else System.out.println("�߼����"+this.indexId[i]+"\t�����ţ�"+this.ptr[i]);
		}
	}

}
