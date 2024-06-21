package FileSim.file;

import DiskSim.Hdd;
import FileSim.file.myException.MyFileNotFind;

public class FileSystem {// �ļ�ϵͳ

	private byte[] indexAdd;// �����ڵ��λ��

	private int[] fileNameHashCode;// �ļ�����ϣֵ

	private byte[] map;// �̿�ʹ�õ�λͼ

	private static FileSystem system;// �ļ�ϵͳ����

	private FileSystem() {

		Hdd vhd = Hdd.getVhd();
		byte buf[] = vhd.read(0);
		this.indexAdd = new byte[36];
		for (int i = 0; i < this.indexAdd.length; i++) {
			this.indexAdd[i] = buf[i];
		}

		this.map = new byte[buf.length - this.indexAdd.length];

		for (int i = this.indexAdd.length; i < buf.length; i++) {
			map[i - this.indexAdd.length] = buf[i];
		}
		map[0] = (byte) (this.map[0] | '\u0001');// �����ĵ�0������Ҫ�������ȥ
		this.flush();
		this.fileNameHashCode = new int[this.indexAdd.length];

		this.fillfileName();
	}

	public int getMap(int number) {// �鿴λͼ��ĳһλ�õ�״̬

		int y = number / 8;
		int x = number % 8;
		byte k = 0;
		switch (x) {
		case 0:
			k = (byte) (this.map[y] & '\u0001');
			break;
		case 1:
			k = (byte) (this.map[y] & '\u0002');
			break;
		case 2:
			k = (byte) (this.map[y] & '\u0004');
			break;
		case 3:
			k = (byte) (this.map[y] & '\u0008');
			break;
		case 4:
			k = (byte) (this.map[y] & '\u0010');
			break;
		case 5:
			k = (byte) (this.map[y] & '\u0020');
			break;
		case 6:
			k = (byte) (this.map[y] & '\u0040');
			break;
		case 7:
			k = (byte) (this.map[y] & '\u0080');
			break;
		}
		if (k != 0)
			k = 1;

		return k;
	}

	private void setMap(int i, boolean b) {// ��������� b tureΪ���䣬����Ϊ�ͷ�
		int y = i / 8;
		int x = i % 8;
		if (b) {
			switch (x) {// ���������
			case 0:
				map[y] = (byte) (this.map[y] | '\u0001');
				break;
			case 1:
				map[y] = (byte) (this.map[y] | '\u0002');
				break;
			case 2:
				map[y] = (byte) (this.map[y] | '\u0004');
				break;
			case 3:
				map[y] = (byte) (this.map[y] | '\u0008');
				break;
			case 4:
				map[y] = (byte) (this.map[y] | '\u0010');
				break;
			case 5:
				map[y] = (byte) (this.map[y] | '\u0020');
				break;
			case 6:
				map[y] = (byte) (this.map[y] | '\u0040');
				break;
			case 7:
				map[y] = (byte) (this.map[y] | '\u0080');
				break;
			}
		} else {// �ͷ������
			switch (x) {
			case 0:
				map[y] = (byte) (this.map[y] & '\uFFFE');
				break;
			case 1:
				map[y] = (byte) (this.map[y] & '\uFFFD');
				break;
			case 2:
				map[y] = (byte) (this.map[y] & '\uFFFB');
				break;
			case 3:
				map[y] = (byte) (this.map[y] & '\uFFF7');
				break;
			case 4:
				map[y] = (byte) (this.map[y] & '\uFFEF');
				break;
			case 5:
				map[y] = (byte) (this.map[y] & '\uFFDF');
				break;
			case 6:
				map[y] = (byte) (this.map[y] & '\uFFBf');
				break;
			case 7:
				map[y] = (byte) (this.map[y] & '\uFF7F');
				break;
			}
		}
	}

	private int[] assign(int size) {// ��������� ���ص��ǿ��Է�����������������
		int buf[] = new int[size];
		int i = 0;
		int k = 1;
		for (int j = 0; j < size; j++) {
			for (i = k; i < this.map.length * 8; i++) {
				if (this.getMap(i) == 0) {
					k = i;
					break;
				}
			}
			buf[j] = i;
			// System.out.println(i);
			this.setMap(i, true);
		}
		this.flush();
		return buf;
	}

	public void delete(String fileName) {// ɾ���ļ�
		Index index = null;
		int hash = fileName.hashCode();
		for (int i = 0; i < this.indexAdd.length; i++) {// �����ļ�λ��
			if (hash == this.fileNameHashCode[i]) {
				index = new Index(this.indexAdd[i]);
				this.setMap(this.indexAdd[i], false);// �ͷ��ļ���������
				this.indexAdd[i] = 0;// �ͷŸ��ļ���Ŀ¼�еı�ʾ
				this.fileNameHashCode[i] = 0;
				break;
			}
		}
		if(index==null)
			try {
				throw new MyFileNotFind();
			} catch (MyFileNotFind e) {
				// TODO �Զ����� catch ��
				e.printStackTrace();
				return;
			}
		byte[] buf = index.getPtr();
		for (int i = 0; i < buf.length; i++) {// ���ļ��������ڵ������ɾ��
			if (buf[i] != 0)
				this.setMap(buf[i], false);
		}
		this.flush();

	}

	public void showMap() {// ��ӡλʾͼ��Ϣ
		for (int i = 0; i < this.map.length; i++) {
			for (int j = 0; j < 8; j++)
				System.out.print(this.getMap(i * 8 + j) + " ");
			System.out.println();
		}
	}

	public void showList() {// ��ӡ�ļ�Ŀ¼
		Index index;
		for (int i = 0; i < this.indexAdd.length; i++) {
			if (this.indexAdd[i] != 0) {
				index = new Index(this.indexAdd[i]);
				System.out.println(index);
			}
		}
	}

	private void fillfileName() {// ��д�ļ����б�
		Index index;
		for (int i = 0; i < this.indexAdd.length; i++) {
			if (this.indexAdd[i] != 0) {
				index = new Index(this.indexAdd[i]);

				this.fileNameHashCode[i] = index.getName().trim().hashCode();
			} else {
				this.fileNameHashCode[i] = 0;
			}

		}
		/*
		 * for (int i = 0; i < this.indexAdd.length; i++) {
		 * System.out.println(this.fileNameHashCode[i]); }
		 */
	}

	public Index getFileIndex(String fileName) throws MyFileNotFind {// �����ļ�����ȡ���ļ���������
		Index index = null;
		int hash = fileName.hashCode();
		for (int i = 0; i < this.indexAdd.length; i++) {
			if (hash == this.fileNameHashCode[i]) {
				index = new Index(this.indexAdd[i]);
				break;
			}
		}
		if(index==null) throw new MyFileNotFind();// �׳��ļ�δ�ҵ��쳣
		return index;
	}

	private void flush() {// ˢ������Ӳ���д洢���ļ�Ŀ¼��Ϣ
		Hdd vhd = Hdd.getVhd();
		byte buf[] = new byte[vhd.getSize()];

		for (int i = 0; i < this.indexAdd.length; i++) {
			buf[i] = this.indexAdd[i];
		}

		for (int i = this.indexAdd.length; i < buf.length; i++) {
			buf[i] = map[i - this.indexAdd.length];
		}
		vhd.write(buf, 0);
	}

	public void creat(String fileName, int size) {// �����ļ�
		int hash = fileName.hashCode();

		for (int i = 0; i < this.fileNameHashCode.length; i++) {
			if (hash == this.fileNameHashCode[i]) {
				System.out.println("�ļ����ظ�");
				return;
			}
		}
		for (int i = 0; i < this.indexAdd.length; i++) {
			if (this.indexAdd[i] == 0) {// ����ļ�Ŀ¼���п�λ���ļ�����
				int add[] = this.assign(size + 1);
				Index index = new Index(fileName);
				for (byte j = 1; j < add.length; j++) {
					index.setIndexId((byte) (j - 1), j);
					index.setPtr((byte) (j - 1), (byte) add[j]);
				}
				this.indexAdd[i] = (byte) add[0];
				this.fileNameHashCode[i] = fileName.hashCode();
				index.save(add[0]);
				System.out.println("�ļ������ɹ���\n"+index.toString());
				break;
			}
		}
		
		this.flush();
	}

	public void write(String fileName, String text) {// ���ļ���д���ı�
		Index index;
		try {
			index = this.getFileIndex(fileName);
		} catch (MyFileNotFind e) {
			// TODO �Զ����� catch ��
			e.printStackTrace();
			return;
		}
		byte[] buf = text.getBytes();
		byte[] ptr = index.getPtr();
		byte[] buf2 = new byte[Hdd.getVhd().getSize()];
		for (int i = 0; i < ptr.length; i++) {
			for (int j = 0; j < Hdd.getVhd().getSize(); j++) {
				if ((i * Hdd.getVhd().getSize() + j) < buf.length)
					buf2[j] = buf[i * Hdd.getVhd().getSize() + j];
				else {
					buf[2] = 0;
					break;
				}
			}
			if (ptr[i] != 0)
				Hdd.getVhd().write(buf2, ptr[i]);
			else
				break;// ����ļ����䵽������鲻�㣬����ѭ��
			if (buf.length < (i + 1) * Hdd.getVhd().getSize())
				break; // ����ļ�����д�꣬����ѭ��
		}

	}

	public String read(String fileName) {// ���ļ�
		String text = "";
		Index index;
		try {
			index = this.getFileIndex(fileName);
		} catch (MyFileNotFind e) {
			// TODO �Զ����� catch ��
			e.printStackTrace();
			return "";
		}
		byte ptr[] = index.getPtr();
		byte[] buf;
		for (int i = 0; i < ptr.length; i++) {
			if (ptr[i] != 0) {
				buf = Hdd.getVhd().read(ptr[i]);
				text = text + (new String(buf)).trim();
			} else
				break;
		}
		return text.trim();
	}

	public byte[] getMap() {
		return map;
	}

	public static FileSystem getFileSystem() {// ����ļ�ϵͳ����
		if (FileSystem.system == null)
			FileSystem.system = new FileSystem();
		return system;
	}

	public void showIndex(String fileName) {//��ʾ��������Ϣ
		Index index;
		try {
			index = this.getFileIndex(fileName);
		} catch (MyFileNotFind e) {
			// TODO �Զ����� catch ��
			e.printStackTrace();
			return;
		}
		index.showIndex();
	}
}
