package FileSim.ui;

import java.awt.Font;
import java.awt.Frame;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public final class AbotDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JLabel jLabel = null;

	private JLabel jLabel1 = null;

	private JLabel jLabel2 = null;

	private JButton jButton = null;

	private JLabel jLabel3 = null;

	/**
	 * @param owner
	 */
	public AbotDialog(Frame owner) {
		super(owner);
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setTitle("关于本程序");
		this.setResizable(false);
		this.setSize(676, 406);
		this.setFont(new Font("宋体",Font.BOLD,25));
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jLabel3 = new JLabel();
			jLabel3.setBounds(new Rectangle(402, 204, 182, 38));
			jLabel3.setFont(new Font("宋体",Font.BOLD,25));
			jLabel3.setText("  版本： 1.0");
			jLabel2 = new JLabel();
			jLabel2.setBounds(new Rectangle(480, 132, 118, 60));
			jLabel2.setFont(new Font("宋体",Font.BOLD,25));
			jLabel2.setText("  YYY");
			jLabel1 = new JLabel();
			jLabel1.setBounds(new Rectangle(260, 138, 192, 36));
			jLabel1.setFont(new Font("宋体",Font.BOLD,25));
			jLabel1.setText(" XXXXXXXXXX");
			jLabel = new JLabel();
			jLabel.setBounds(new Rectangle(92, 32, 452, 74));
			jLabel.setFont(new Font("宋体",Font.BOLD,25));
			jLabel.setText("  模拟文件系统");
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(jLabel, null);
			jContentPane.add(jLabel1, null);
			jContentPane.add(jLabel2, null);
			jContentPane.add(getJButton(), null);
			jContentPane.add(jLabel3, null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setBounds(new Rectangle(246, 260, 136, 70));
			jButton.setFont(new Font("宋体",Font.BOLD,25));
			jButton.setText("确定");
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					closeDialog();
					//System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
				}
			});
		}
		return jButton;
	}

	protected void closeDialog() {
		// TODO 自动生成方法存根
		this.setVisible(false);
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
