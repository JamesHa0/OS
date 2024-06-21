package FileSim.ui;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import FileSim.file.FileSystem;

public class MapViewer extends JPanel {//磁盘使用情况
private FileSystem system;
private byte[] map;
private int W,H,cellW,cellH,x,y;
public MapViewer(FileSystem system){
	this.system=system;
	map=new byte[system.getMap().length*8];
	x=32;
	y=map.length/32;
	this.rush();
	//this.setVisible(true);
	}
public void rush(){//刷新
	for(int i=0;i<map.length;i++){
		map[i]=(byte) system.getMap(i);
	}
	this.W=this.getWidth();
	this.H=this.getHeight();
	this.cellW=this.W/x;
	this.cellH=this.H/y;
	this.repaint();
}
public void paint(Graphics g){
	super.paint(g);
	
	g.setColor(Color.gray);
	for(int i=0;i<y;i++){
		for(int j=0;j<x;j++){
			if(this.map[i*x+j]==1){	
				g.setColor(Color.red);
				g.fillRect(j*cellW,i*cellH, this.cellW,this.cellH);
				g.setColor(Color.gray);
				g.drawRect(j*cellW,i*cellH, this.cellW,this.cellH); 
				
				}
			else g.drawRect(j*cellW,i*cellH, this.cellW,this.cellH); 
		}
	}
	g.drawString("fffff",0,0);
}
}
