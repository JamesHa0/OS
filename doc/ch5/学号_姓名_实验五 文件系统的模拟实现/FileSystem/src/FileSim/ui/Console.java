package FileSim.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import DiskSim.Hdd;
import FileSim.file.FileSystem;
import FileSim.file.myException.MyFileNotFind;

public class Console {// 控制台

	/**
	 * @param args
	 */
	private boolean isRun;// 是否运行

	private FileSystem system;

	public Console() {
		this.isRun = true;
		this.system =FileSystem.getFileSystem();
	}

	private String getInput() {
		String s = null;
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		try {
			s = in.readLine();
		} catch (IOException e) {
			// TODO 自动生成 catch 块
			e.printStackTrace();
		}
		return s.trim();
	}

	public void run() {
		StringTokenizer st = null;//
		String command = null;// 命令
		String s1 = null, s2 = null;// 命令参数
		while (this.isRun) {
			System.out.println("请输入你所要执行的命令(输入“help”查看帮助信息):");
			st = new StringTokenizer(this.getInput());
			if (st.hasMoreTokens()) {
				command = st.nextToken();
			} else {
				continue;
			}
			if (command.equals("help")) {
				this.help();
			} else if (command.equals("exit")) {
				System.exit(-1);
			} else if (command.equals("creat")&&st.countTokens()==2) {
				s1 = st.nextToken();
				s2 = st.nextToken();
				this.creat(s1, s2);
			}else if(command.equals("delete")&&st.countTokens()==1){
				s1 = st.nextToken();
				this.delete(s1);
			}else if(command.equals("map")){
				this.system.showMap();
			}else if(command.equals("attrib")&&st.countTokens()==1){
				s1=st.nextToken();
				this.attrib(s1);
			}else if(command.equals("showList")){
				system.showList();
			}else if(command.endsWith("showIndex")&&st.countTokens()==1){
				system.showIndex(st.nextToken());
			}else if(command.endsWith("openWindow")){
				this.openWindow();
			}
		}
	}

	private void openWindow() {//打开窗口
		Gui gui=new Gui();		
	}

	private void attrib(String s1) {//显示文件信息
		// TODO 自动生成方法存根
		try {
			System.out.println(this.system.getFileIndex(s1));
		} catch (MyFileNotFind e) {
			// TODO 自动生成 catch 块
			e.printStackTrace();
		}
	}

	private void delete(String s1) {//删除文件 
		this.system.delete(s1);		
	}

	private void creat(String fileName, String size) {
		try{
		int i = Integer.valueOf(size);
		i=i/Hdd.getVhd().getSize()+1;
		this.system.creat(fileName,i);
		}catch(NumberFormatException e){
			System.out.println("命令格式错误");
		}
		}

	private void help() {// 帮助信息
		System.out.println("区分大小写");
		System.out.println("help\t查看帮助信息");
		System.out
				.println("creat [文件名] [文件大小]\t创建新文件，文件名要求在16个字符以下");
		System.out.println("delete [文件名]\t删除文件");
		System.out.println("attrib [文件名]\t显示文件信息");
		System.out.println("map\t显示磁盘使用情况");
		System.out.println("openWindow\t打开文件控制窗口");
		System.out.println("showList\t显示文件目录");
		System.out.println("showIndex [文件名]\t显示文件的索引信息");
		System.out.println("exit\t退出程序");
	}

	public static void main(String[] args) {
		// TODO 自动生成方法存根
		Console console = new Console();
		console.run();
	}

}
