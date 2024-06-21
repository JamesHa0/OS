package FileSim.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import DiskSim.Hdd;
import FileSim.file.FileSystem;
import FileSim.file.myException.MyFileNotFind;

public class Console {// ����̨

	/**
	 * @param args
	 */
	private boolean isRun;// �Ƿ�����

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
			// TODO �Զ����� catch ��
			e.printStackTrace();
		}
		return s.trim();
	}

	public void run() {
		StringTokenizer st = null;//
		String command = null;// ����
		String s1 = null, s2 = null;// �������
		while (this.isRun) {
			System.out.println("����������Ҫִ�е�����(���롰help���鿴������Ϣ):");
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

	private void openWindow() {//�򿪴���
		Gui gui=new Gui();		
	}

	private void attrib(String s1) {//��ʾ�ļ���Ϣ
		// TODO �Զ����ɷ������
		try {
			System.out.println(this.system.getFileIndex(s1));
		} catch (MyFileNotFind e) {
			// TODO �Զ����� catch ��
			e.printStackTrace();
		}
	}

	private void delete(String s1) {//ɾ���ļ� 
		this.system.delete(s1);		
	}

	private void creat(String fileName, String size) {
		try{
		int i = Integer.valueOf(size);
		i=i/Hdd.getVhd().getSize()+1;
		this.system.creat(fileName,i);
		}catch(NumberFormatException e){
			System.out.println("�����ʽ����");
		}
		}

	private void help() {// ������Ϣ
		System.out.println("���ִ�Сд");
		System.out.println("help\t�鿴������Ϣ");
		System.out
				.println("creat [�ļ���] [�ļ���С]\t�������ļ����ļ���Ҫ����16���ַ�����");
		System.out.println("delete [�ļ���]\tɾ���ļ�");
		System.out.println("attrib [�ļ���]\t��ʾ�ļ���Ϣ");
		System.out.println("map\t��ʾ����ʹ�����");
		System.out.println("openWindow\t���ļ����ƴ���");
		System.out.println("showList\t��ʾ�ļ�Ŀ¼");
		System.out.println("showIndex [�ļ���]\t��ʾ�ļ���������Ϣ");
		System.out.println("exit\t�˳�����");
	}

	public static void main(String[] args) {
		// TODO �Զ����ɷ������
		Console console = new Console();
		console.run();
	}

}
