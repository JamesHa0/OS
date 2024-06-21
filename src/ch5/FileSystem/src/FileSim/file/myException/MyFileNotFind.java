package FileSim.file.myException;

@SuppressWarnings("serial")
public class MyFileNotFind extends Exception{
public MyFileNotFind(){
	super("文件不存在！");
}
}
