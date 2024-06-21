package FileSim.file;

import DiskSim.Hdd;
import FileSim.file.myException.MyFileNotFind;

public class FileSystem {// 文件系统

	private byte[] indexAdd;// 索引节点的位置

	private int[] fileNameHashCode;// 文件名哈希值

	private byte[] map;// 盘块使用的位图

	private static FileSystem system;// 文件系统单例

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
		map[0] = (byte) (this.map[0] | '\u0001');// 物理块的第0块首先要被分配出去
		this.flush();
		this.fileNameHashCode = new int[this.indexAdd.length];

		this.fillfileName();
	}

	public int getMap(int number) {// 查看位图上某一位置的状态

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

	private void setMap(int i, boolean b) {// 设置物理块 b ture为分配，否则为释放
		int y = i / 8;
		int x = i % 8;
		if (b) {
			switch (x) {// 分配物理块
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
		} else {// 释放物理块
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

	private int[] assign(int size) {// 分配物理块 返回的是可以分配的物理块的序号数组
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

	public void delete(String fileName) {// 删除文件
		Index index = null;
		int hash = fileName.hashCode();
		for (int i = 0; i < this.indexAdd.length; i++) {// 查找文件位置
			if (hash == this.fileNameHashCode[i]) {
				index = new Index(this.indexAdd[i]);
				this.setMap(this.indexAdd[i], false);// 释放文件的索引表
				this.indexAdd[i] = 0;// 释放该文件在目录中的标示
				this.fileNameHashCode[i] = 0;
				break;
			}
		}
		if(index==null)
			try {
				throw new MyFileNotFind();
			} catch (MyFileNotFind e) {
				// TODO 自动生成 catch 块
				e.printStackTrace();
				return;
			}
		byte[] buf = index.getPtr();
		for (int i = 0; i < buf.length; i++) {// 将文件数据所在的物理块删除
			if (buf[i] != 0)
				this.setMap(buf[i], false);
		}
		this.flush();

	}

	public void showMap() {// 打印位示图信息
		for (int i = 0; i < this.map.length; i++) {
			for (int j = 0; j < 8; j++)
				System.out.print(this.getMap(i * 8 + j) + " ");
			System.out.println();
		}
	}

	public void showList() {// 打印文件目录
		Index index;
		for (int i = 0; i < this.indexAdd.length; i++) {
			if (this.indexAdd[i] != 0) {
				index = new Index(this.indexAdd[i]);
				System.out.println(index);
			}
		}
	}

	private void fillfileName() {// 填写文件名列表
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

	public Index getFileIndex(String fileName) throws MyFileNotFind {// 根据文件名获取该文件的索引表
		Index index = null;
		int hash = fileName.hashCode();
		for (int i = 0; i < this.indexAdd.length; i++) {
			if (hash == this.fileNameHashCode[i]) {
				index = new Index(this.indexAdd[i]);
				break;
			}
		}
		if(index==null) throw new MyFileNotFind();// 抛出文件未找到异常
		return index;
	}

	private void flush() {// 刷新虚拟硬盘中存储的文件目录信息
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

	public void creat(String fileName, int size) {// 创建文件
		int hash = fileName.hashCode();

		for (int i = 0; i < this.fileNameHashCode.length; i++) {
			if (hash == this.fileNameHashCode[i]) {
				System.out.println("文件名重复");
				return;
			}
		}
		for (int i = 0; i < this.indexAdd.length; i++) {
			if (this.indexAdd[i] == 0) {// 如果文件目录中有空位则将文件插入
				int add[] = this.assign(size + 1);
				Index index = new Index(fileName);
				for (byte j = 1; j < add.length; j++) {
					index.setIndexId((byte) (j - 1), j);
					index.setPtr((byte) (j - 1), (byte) add[j]);
				}
				this.indexAdd[i] = (byte) add[0];
				this.fileNameHashCode[i] = fileName.hashCode();
				index.save(add[0]);
				System.out.println("文件创建成功！\n"+index.toString());
				break;
			}
		}
		
		this.flush();
	}

	public void write(String fileName, String text) {// 向文件中写入文本
		Index index;
		try {
			index = this.getFileIndex(fileName);
		} catch (MyFileNotFind e) {
			// TODO 自动生成 catch 块
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
				break;// 如果文件分配到的物理块不足，跳出循环
			if (buf.length < (i + 1) * Hdd.getVhd().getSize())
				break; // 如果文件内容写完，跳出循环
		}

	}

	public String read(String fileName) {// 读文件
		String text = "";
		Index index;
		try {
			index = this.getFileIndex(fileName);
		} catch (MyFileNotFind e) {
			// TODO 自动生成 catch 块
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

	public static FileSystem getFileSystem() {// 获得文件系统单例
		if (FileSystem.system == null)
			FileSystem.system = new FileSystem();
		return system;
	}

	public void showIndex(String fileName) {//显示索引表信息
		Index index;
		try {
			index = this.getFileIndex(fileName);
		} catch (MyFileNotFind e) {
			// TODO 自动生成 catch 块
			e.printStackTrace();
			return;
		}
		index.showIndex();
	}
}
