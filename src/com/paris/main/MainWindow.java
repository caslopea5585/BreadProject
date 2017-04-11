package com.paris.main;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import db.DBManager;
import db.SubCategory;
import db.TopCategory;

/*
 * Join���̶�?
 *  - ����ȭ�� ���� ���������� �и��� ���̺��� ��ġ �ϳ��� ���̺�ó�� ������ �� �ִ� ����.
 *  -inner����
 *   -> ���δ���� �Ǵ� ���̺� �� �������� ���ڵ常 �����´�.
 *   -> Ư¡) �������� ���ڵ尡 �ƴ� ��� ������Ų��~ 
 *   -> ex) select * from emp e, dept d where e.deptno=d.deptno;
 *  -Outer����
 *   -> ���δ���� �Ǵ� ���̺� ����� ���ڵ� �Ӹ� �ƴ϶� ������ ���̺��� ���ڵ�� �ϴ� ������ �� �������� ����..
 *   -> ex) select * from dept d left outer join emp e on d.deptno=e.deptno;
 *    
 * */
public class MainWindow extends JFrame implements ItemListener,ActionListener{
	JPanel p_west,p_center,p_east;
	JPanel p_up,p_down;
	JTable table_up,table_down;
	JScrollPane scroll_up,scroll_down;
	
	//���ʿ���
	Choice ch_top,ch_sub;
	JTextField t_name,t_price;
	Canvas can_west;
	JButton bt_regist;
	
	//���ʿ���
	Canvas can_east;
	JTextField t_id,t_name2,t_price2;
	JButton bt_edit,bt_delete;
	
	//DB
	DBManager manager;
	Connection con;
	
	//dto�� ���� �迭(����ī�װ� list)
	ArrayList<TopCategory> topList= new ArrayList<TopCategory>();
	ArrayList<SubCategory> subList= new ArrayList<SubCategory>();
	
	BufferedImage image;
	
	//���̺�� ��ü
	UpModel upModel;
	DownModel downModel;
	
	//��������
	JFileChooser chooser;
	File file;
	
	
	public MainWindow() {
		p_west= new JPanel();
		p_center= new JPanel();
		p_east= new JPanel();
		p_up= new JPanel();
		p_down= new JPanel();
		table_up = new JTable();
		table_down = new JTable(2,2);
		scroll_up = new JScrollPane(table_up);
		scroll_down=new JScrollPane(table_down);
		ch_top = new Choice();
		ch_sub = new Choice();
		t_name = new JTextField(10);
		t_price = new JTextField(10);
		
		try {
			URL url = this.getClass().getResource("/default.png");
			image = ImageIO.read(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		can_west = new Canvas(){
			public void paint(Graphics g) {
				g.drawImage((Image)image, 0, 0, 135,135,this);			
			}
		};
		can_west.setPreferredSize(new Dimension(135, 135));
		bt_regist = new JButton("���");
		can_east=new Canvas(){
			public void paint(Graphics g) {
				g.drawImage((Image)image, 0, 0,135,135,this); 
			}
		};
		can_east.setPreferredSize(new Dimension(135, 135));
		t_id=new JTextField(10);
		t_id.setEditable(false);
		t_name2 = new JTextField(10);
		t_price2 = new JTextField(10);
		bt_edit = new JButton("����");
		bt_delete = new JButton("����");
		
		//���г��� ����
		p_west.setBackground(Color.yellow);
		p_center.setBackground(Color.GREEN);
		p_east.setBackground(Color.PINK);
		p_up.setBackground(Color.WHITE);
		p_down.setBackground(Color.BLUE);
		
		//�г��� ���� ����(����)
		p_west.add(ch_top);
		p_west.add(ch_sub);
		p_west.add(t_name);
		p_west.add(t_price);
		p_west.add(can_west);
		p_west.add(bt_regist);
		
		//�г��� ���� ����(����)
		p_east.add(t_id);
		p_east.add(t_name2);
		p_east.add(t_price2);
		p_east.add(can_east);
		p_east.add(bt_edit);
		p_east.add(bt_delete);
		
		
		//�� �г��� ũ�� ����
		p_west.setPreferredSize(new Dimension(150, 700));
		p_center.setPreferredSize(new Dimension(550, 700));
		p_east.setPreferredSize(new Dimension(150, 700));
		
		//������ �׸��� �����ϰ� ���Ʒ� ����
		p_center.setLayout(new GridLayout(2, 1));
		p_center.add(p_up);
		p_center.add(p_down);
		
		
		//��ũ�� ����
		p_up.setLayout(new BorderLayout());
		p_down.setLayout(new BorderLayout());
		p_up.add(scroll_up);
		p_down.add(scroll_down);
		
		add(p_west,BorderLayout.WEST);
		add(p_center);
		add(p_east,BorderLayout.EAST);
		
		ch_top.setPreferredSize(new Dimension(135, 40));
		ch_sub.setPreferredSize(new Dimension(135, 40));
		ch_top.add("�� ���� ī�װ� ����");
		ch_sub.add("�� ���� ī�װ� ����");
		ch_top.addItemListener(this);	//���̽��� ������ ����
		
		//��ư�� ������ ����
		bt_regist.addActionListener(this);
		
		
		//ĵ������ ���콺 ������ ����
		can_west.addMouseListener(new MouseAdapter() {		
			public void mouseClicked(MouseEvent e) {
				preView();
			}
		});
		
		
		//�����̺�� �����ʿ���
		table_up.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int row = table_up.getSelectedRow();
				int col=0;
					
				String subcategory_id = (String)table_up.getValueAt(row, col);
				
				//������ ID�� �Ʒ��� ���̺� �𵨿� ��������.
				downModel.getList(Integer.parseInt(subcategory_id));
				
				table_down.setModel(downModel);
				table_down.updateUI();
			}
		});
		
		//�ٿ����̺�� ������ ����
		table_down.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int row = table_down.getSelectedRow();
				Vector vec = downModel.data.get(row); //������ ���Ϳ� ����ִ� 1�������͸� ������!
				getDetail(vec);
			}
		});
		
		
		chooser = new JFileChooser("C:/html_workspace/images/");
		
		setSize(850, 700);
		setVisible(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		init();
		getTop();
		getUpList(); //���� ���̺�ó��
		getDownList();
	}
	
	//�ֻ���ī�װ� ���
	public void getTop(){
		PreparedStatement pstmt=null;
		ResultSet rs =null;
		
		String sql="select * from topcategory order by topcategory_id asc";
		
		try {
			pstmt=con.prepareStatement(sql);
			rs= pstmt.executeQuery();
			
			while(rs.next()){
				TopCategory dto = new TopCategory();
				dto.setTopcategory_id(rs.getInt("topcategory_id"));
				dto.setTop_name(rs.getString("top_name"));
				topList.add(dto);
				ch_top.add(dto.getTop_name());
			}
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pstmt!=null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	//����ī�װ� ���ϱ�
	//���ε� ����.
	public void getSub(){
		PreparedStatement pstmt =null;
		ResultSet rs=null;
		
		String sql="select * from subcategory where topcategory_id=?";
		try {
			pstmt = con.prepareStatement(sql);
			//���ε� ���� �� ����
			int index = ch_top.getSelectedIndex();
			if(index-1>=0){
				TopCategory dto = topList.get(index-1);
				pstmt.setInt(1, dto.getTopcategory_id()); //ù��° �߰ߵ� ���ε� ������...(���ε��� �ε���, ���ð�)
				rs= pstmt.executeQuery();
				/*
				 * ���ε庯��
				=======================================
				�Ϲ����� DBMS���� �������� ����Ǳ���� ������ ���������� ��ģ��.
				DBMS�� ����Ʈ�����̱� ������, �ΰ��� �������� ��ü�� ������ �� ������
				�Ľ̰� �����ϰ����� ��ġ�� �ȴ�.
				������ �Ź� ��������� ����ϴ� ���� DB�� �������̳� ���� �˻���� ����Ų�ٸ�
				���ɿ� ����� ������ �ְԵȴ�.
				=======================================
				�ذ�å!!!
				 - select * from member where id=�Է°� and pass = �Է°�
				   �Է��� ���� �޶����Ƿ� �ż��� �������� �̷������ �ȴ�.
				   ���� �Է°��� ���ϴ��� ��ü������ �������� �������� ����!!
				   �̋� ����ϴ� ������ �ٷ�~ ���ε庯����!!!
				=======================================
								(ANSI)ǥ������		�������
				����Ŭ					O				PLSQL
				MSSQL				O				TSQL		
				
				
				 */				
				
				//������� �����
				subList.removeAll(subList); //�޸𸮻� �����
				ch_sub.removeAll();	//�����λ� �����(���̽� �ȿ��ִ� ��)
				
				while(rs.next()){
					SubCategory vo= new SubCategory();
					vo.setSub_name(rs.getString("sub_name"));
					vo.setSubcategory_id(rs.getInt("subcategory_id"));
					vo.setTopcategory_id(rs.getInt("topcategory_id"));
					subList.add(vo);
					ch_sub.add(vo.getSub_name());
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	//�����ͺ��̽� Ŀ�ؼ� ���
	public void init(){
		manager = DBManager.getInstance();
		con =manager.getConnection();
		System.out.println(con);
	}
	
	//���̽� ������Ʈ ���氪 ��Ƴ���
	public void itemStateChanged(ItemEvent e) {
		getSub();		
	}
	
	
	//��ǰ���
	public void regist(){
		PreparedStatement pstmt=null;
		String sql="insert into product(product_id,subcategory_id,product_name,price,img)";
		sql+=" values(seq_product.nextval,?,?,?,?)";
		
		try {
			pstmt =con.prepareStatement(sql);
			
			//ArrayList�ȿ� ����ִ� Subcategory DTO�� �����Ͽ� PK���� �־�����		
			int index = ch_sub.getSelectedIndex();
			SubCategory vo = subList.get(index);
			
			// ���ε忡 ������ �� �� ����
			pstmt.setInt(1,vo.getSubcategory_id());
			pstmt.setString(2, t_name.getText());
			pstmt.setInt(3,  Integer.parseInt(t_price.getText()));
			pstmt.setString(4, file.getName());			
			
			
			//executeUpdate �޼���� ������ ���� �� �ݿ��� ���ڵ��� ������ ��ȯ���ش� ���� insert���� ��쿡�� ������ �����ߴٸ� 1, 
			//update�� 1���̻�, delete 1��
			//���) insert�� ��ȯ���� 0�̶�� insert ����.
			int result = pstmt.executeUpdate();
			if(result!=0){
				JOptionPane.showMessageDialog(this, "��ϼ���");
				upModel.getList(); //DB�� ���Ӱ� ������ ������ ���� ����
				table_up.updateUI();
				//�̹������� �����ϱ�
				copy();
				
			}else{
				JOptionPane.showMessageDialog(this, "��Ͻ���");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(pstmt!=null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}			
		}
		
	}
	
	//��Ϲ�ư
	public void actionPerformed(ActionEvent e) {
		regist();		
	}
	
	public void getUpList(){
		table_up.setModel(upModel = new UpModel(con));
		table_up.updateUI();
	}
	
	public void getDownList(){
		table_down.setModel(downModel = new DownModel(con));
		table_down.updateUI();
	}
	
	//ĵ������ �̹��� �ݿ��ϱ�
	public void preView(){
		int result = chooser.showOpenDialog(this);
		if(result == JFileChooser.APPROVE_OPTION){
			//ĵ������ �̹��� �׸���!
			file = chooser.getSelectedFile();
			//����� ������ ������ �̹����� ��ü�Ͽ� �ٽ� �׸���
			try {
				image=ImageIO.read(file);
				can_west.repaint();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	//����޼��� ����
	public void copy(){
		FileInputStream fis = null;
		FileOutputStream fos= null;
		
		try {
			fis= new FileInputStream(file);
			fos =new FileOutputStream("C:/java_workspace2/BreadProject/data/"+file.getName());
			
			byte[] b = new byte[1024];
			int flag; //�о���̴°��� -1���� Ȯ��
			while(true){
				flag = fis.read(b);
				if(flag==-1)break;
				fos.write(b);
			}
			System.out.println("�̹��� ����Ϸ�");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(fos!=null){
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(fis!=null){
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	//������ �����ֱ�
	public void getDetail(Vector vec){
		t_id.setText(vec.get(0).toString());
		t_name2.setText(vec.get(2).toString());
		t_price2.setText(vec.get(3).toString());
		
		try {
			image = ImageIO.read(new File("C:/java_workspace2/BreadProject/data/"+vec.get(4)));
			can_east.repaint();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new MainWindow();
	}
}
