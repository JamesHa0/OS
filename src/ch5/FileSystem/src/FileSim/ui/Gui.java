package FileSim.ui;

import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import DiskSim.Hdd;
import FileSim.file.FileSystem;

public class Gui extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JTextPane TextPane = null;

	private JButton creatFile = null;

	private JEditorPane FileNameEditorPane = null;
	private FileSystem system;

	private JLabel jLabel = null;

	private JEditorPane FindEditorPane = null;

	private JButton findButton = null;

	private MapViewer mapPanel = null;

	private JButton jButton = null;

	private JLabel jLabel1 = null;

	private JButton deleteButton1 = null;

	private JMenu helpMenu;
	private JMenuItem aboutMenuItem;
	private AbotDialog abotDialog;
	

	/**
	 * This is the default constructor
	 */
	public Gui() {
		super();
		this.system=FileSystem.getFileSystem();
		initialize();
		this.setVisible(true);		
		mapPanel.repaint();
	   // this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setJMenuBar(this.getMainMenu());
		this.setSize(1248, 800);
		this.setContentPane(this.getJContentPane());	    
		this.setResizable(false);
		this.setFont(new Font("����",Font.BOLD,25));
		this.setTitle("�ļ�ϵͳģ��");
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jLabel1 = new JLabel();
			jLabel1.setBounds(new Rectangle(694, 16, 440, 56));
			jLabel1.setFont(new Font("����",Font.BOLD,25));
			jLabel1.setText("�������ʹ�����");
			jLabel = new JLabel();
			jLabel.setBounds(new Rectangle(28, 40, 104, 36));
			jLabel.setFont(new Font("����",Font.BOLD,25));
			jLabel.setText("�ļ�����");
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(getTextPane(), null);
			jContentPane.add(getCreatFile(), null);
			jContentPane.add(getFileNameEditorPane(), null);
			jContentPane.add(jLabel, null);
			jContentPane.add(getFindEditorPane(), null);
			jContentPane.add(getFindButton(), null);
			jContentPane.add(getMapPanel(), null);
			jContentPane.add(getJButton(), null);
			jContentPane.add(jLabel1, null);
			jContentPane.add(getDeleteButton1(), null);
		
		}
		return jContentPane;
	}

	/**
	 * This method initializes TextPane	
	 * 	
	 * @return javax.swing.JTextPane	
	 */
	private JTextPane getTextPane() {
		if (TextPane == null) {
			TextPane = new JTextPane();
			TextPane.setBounds(new Rectangle(30, 92, 424, 334));
			TextPane.setFont(new Font("����",Font.BOLD,25));
		}
		return TextPane;
	}

	/**
	 * This method initializes creatFile	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCreatFile() {
		if (creatFile == null) {
			creatFile = new JButton();
			creatFile.setBounds(new Rectangle(176, 446, 120, 56));
			creatFile.setFont(new Font("����",Font.BOLD,25));
			creatFile.setText("�ύ");
			creatFile.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseClicked(java.awt.event.MouseEvent e) {//�������ļ�
				
					String fileName=FileNameEditorPane.getText().trim();
					String text=TextPane.getText().trim();
					int size=text.getBytes().length/Hdd.getVhd().getSize()+1;
					system.creat(fileName, size);
					system.write(fileName, text);
				
				}
			});
		}
		return creatFile;
	}

	/**
	 * This method initializes FileNameEditorPane	
	 * 	
	 * @return javax.swing.JEditorPane	
	 */
	private JEditorPane getFileNameEditorPane() {
		if (FileNameEditorPane == null) {
			FileNameEditorPane = new JEditorPane();
			FileNameEditorPane.setBounds(new Rectangle(162, 30, 294, 48));
			FileNameEditorPane.setFont(new Font("����",Font.BOLD,25));
		}
		return FileNameEditorPane;
	}

	/**
	 * This method initializes FindEditorPane	
	 * 	
	 * @return javax.swing.JEditorPane	
	 */
	private JEditorPane getFindEditorPane() {
		if (FindEditorPane == null) {
			FindEditorPane = new JEditorPane();
			FindEditorPane.setBounds(new Rectangle(34, 560, 228, 48));
			FindEditorPane.setFont(new Font("����",Font.BOLD,25));
		}
		return FindEditorPane;
	}

	/**
	 * This method initializes findButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getFindButton() {
		if (findButton == null) {
			findButton = new JButton();
			findButton.setBounds(new Rectangle(282, 520, 172, 56));
			findButton.setFont(new Font("����",Font.BOLD,25));
			findButton.setText("�����ļ�");
			findButton.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseClicked(java.awt.event.MouseEvent e) {//���ļ��������ļ�
			
				String fileName=FindEditorPane.getText().trim();
				String text=system.read(fileName);
				FileNameEditorPane.setText(fileName);
				TextPane.setText(text);
				}
			});
		}
		return findButton;
	}

	/**
	 * This method initializes mapPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private MapViewer getMapPanel() {
		if (mapPanel == null) {
			mapPanel = new MapViewer(system);
			
			mapPanel.setBounds(new Rectangle(690, 102, 466, 468));
			mapPanel.setFont(new Font("����",Font.BOLD,25));
		}
		return mapPanel;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setBounds(new Rectangle(832, 592, 162, 54));
			jButton.setFont(new Font("����",Font.BOLD,25));
			jButton.setText("ˢ��");
			jButton.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseClicked(java.awt.event.MouseEvent e) {
				mapPanel.rush();
				}
			});
		}
		return jButton;
	}

	/**
	 * This method initializes deleteButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JMenuBar getMainMenu(){
		JMenuBar MainMenu=new JMenuBar();
		MainMenu.add(getHelpMenu());
		return MainMenu;
	}
	private JMenuItem getHelpMenu() {
		// TODO �Զ����ɷ������
		if (helpMenu == null) {
			helpMenu = new JMenu();
			helpMenu.setText("����");
			helpMenu.setFont(new Font("����",Font.BOLD,25));
			helpMenu.add(getAboutMenuItem());
		}
		return helpMenu;
	}

	private JMenuItem getAboutMenuItem() {
		if (aboutMenuItem == null) {
			aboutMenuItem = new JMenuItem();
			aboutMenuItem.setText("����");
			aboutMenuItem.setFont(new Font("����",Font.BOLD,25));
			aboutMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					AbotDialog aboutDialog = getAboutDialog();
					Point loc = getLocation();
					loc.translate(20, 20);
					aboutDialog.setLocation(loc);
					aboutDialog.setVisible(true);
				}

			
			});
		}
		return aboutMenuItem;
	}

	protected AbotDialog getAboutDialog() {
		// TODO �Զ����ɷ������
		if(this.abotDialog==null) this.abotDialog=new AbotDialog(this);
		return this.abotDialog;
	}

	private JButton getDeleteButton1() {
		if (deleteButton1 == null) {
			deleteButton1 = new JButton();
			deleteButton1.setBounds(new Rectangle(284, 594, 172, 54));
			deleteButton1.setFont(new Font("����",Font.BOLD,25));
			deleteButton1.setText("ɾ���ļ�");
			deleteButton1.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseClicked(java.awt.event.MouseEvent e) {				
				String fileName=FileNameEditorPane.getText().trim();
				system.delete(fileName);
				}
			});
		}
		return deleteButton1;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"