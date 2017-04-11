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
 * Join문이란?
 *  - 정규화에 의해 물리적으로 분리된 테이블을 마치 하나의 테이블처럼 보여줄 수 있는 쿼리.
 *  -inner조인
 *   -> 조인대상이 되는 테이블 간 공통적인 레코드만 가져온다.
 *   -> 특징) 공통적인 레코드가 아닌 경우 누락시킨다~ 
 *   -> ex) select * from emp e, dept d where e.deptno=d.deptno;
 *  -Outer조인
 *   -> 조인대상이 되는 테이블간 공통된 레코드 뿐만 아니라 지정한 테이블의 레코드는 일단 무조건 다 가져오는 조인..
 *   -> ex) select * from dept d left outer join emp e on d.deptno=e.deptno;
 *    
 * */
public class MainWindow extends JFrame implements ItemListener,ActionListener{
	JPanel p_west,p_center,p_east;
	JPanel p_up,p_down;
	JTable table_up,table_down;
	JScrollPane scroll_up,scroll_down;
	
	//서쪽영역
	Choice ch_top,ch_sub;
	JTextField t_name,t_price;
	Canvas can_west;
	JButton bt_regist;
	
	//동쪽영역
	Canvas can_east;
	JTextField t_id,t_name2,t_price2;
	JButton bt_edit,bt_delete;
	
	//DB
	DBManager manager;
	Connection con;
	
	//dto을 담을 배열(상위카테고리 list)
	ArrayList<TopCategory> topList= new ArrayList<TopCategory>();
	ArrayList<SubCategory> subList= new ArrayList<SubCategory>();
	
	BufferedImage image;
	
	//테이블모델 객체
	UpModel upModel;
	DownModel downModel;
	
	//파일츄저
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
		bt_regist = new JButton("등록");
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
		bt_edit = new JButton("수정");
		bt_delete = new JButton("삭제");
		
		//각패널의 색상
		p_west.setBackground(Color.yellow);
		p_center.setBackground(Color.GREEN);
		p_east.setBackground(Color.PINK);
		p_up.setBackground(Color.WHITE);
		p_down.setBackground(Color.BLUE);
		
		//패널의 세부 부착(서쪽)
		p_west.add(ch_top);
		p_west.add(ch_sub);
		p_west.add(t_name);
		p_west.add(t_price);
		p_west.add(can_west);
		p_west.add(bt_regist);
		
		//패널의 세부 부착(동쪽)
		p_east.add(t_id);
		p_east.add(t_name2);
		p_east.add(t_price2);
		p_east.add(can_east);
		p_east.add(bt_edit);
		p_east.add(bt_delete);
		
		
		//각 패널의 크기 지정
		p_west.setPreferredSize(new Dimension(150, 700));
		p_center.setPreferredSize(new Dimension(550, 700));
		p_east.setPreferredSize(new Dimension(150, 700));
		
		//센터의 그리드 적용하고 위아래 구성
		p_center.setLayout(new GridLayout(2, 1));
		p_center.add(p_up);
		p_center.add(p_down);
		
		
		//스크롤 부착
		p_up.setLayout(new BorderLayout());
		p_down.setLayout(new BorderLayout());
		p_up.add(scroll_up);
		p_down.add(scroll_down);
		
		add(p_west,BorderLayout.WEST);
		add(p_center);
		add(p_east,BorderLayout.EAST);
		
		ch_top.setPreferredSize(new Dimension(135, 40));
		ch_sub.setPreferredSize(new Dimension(135, 40));
		ch_top.add("▼ 상위 카테고리 선택");
		ch_sub.add("▼ 하위 카테고리 선택");
		ch_top.addItemListener(this);	//초이스와 리스너 연결
		
		//버튼과 리스너 연결
		bt_regist.addActionListener(this);
		
		
		//캔버스에 마우스 리스너 연결
		can_west.addMouseListener(new MouseAdapter() {		
			public void mouseClicked(MouseEvent e) {
				preView();
			}
		});
		
		
		//업테이블과 리스너연결
		table_up.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int row = table_up.getSelectedRow();
				int col=0;
					
				String subcategory_id = (String)table_up.getValueAt(row, col);
				
				//구해진 ID를 아래의 테이블 모델에 적용하자.
				downModel.getList(Integer.parseInt(subcategory_id));
				
				table_down.setModel(downModel);
				table_down.updateUI();
			}
		});
		
		//다운테이블과 리스너 연결
		table_down.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int row = table_down.getSelectedRow();
				Vector vec = downModel.data.get(row); //이차원 벡터에 들어있는 1차원벡터를 얻어오자!
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
		getUpList(); //위쪽 테이블처리
		getDownList();
	}
	
	//최상위카테고리 얻기
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
	//하위카테고리 구하기
	//바인드 변수.
	public void getSub(){
		PreparedStatement pstmt =null;
		ResultSet rs=null;
		
		String sql="select * from subcategory where topcategory_id=?";
		try {
			pstmt = con.prepareStatement(sql);
			//바인드 변수 값 지정
			int index = ch_top.getSelectedIndex();
			if(index-1>=0){
				TopCategory dto = topList.get(index-1);
				pstmt.setInt(1, dto.getTopcategory_id()); //첫번째 발견된 바인드 변수를...(바인드의 인덱스, 셋팅값)
				rs= pstmt.executeQuery();
				/*
				 * 바인드변수
				=======================================
				일반적인 DBMS에서 쿼리문의 수행되기까지 과정은 여러절차를 거친다.
				DBMS도 소프트웨어이기 때문에, 인간에 쿼리문장 자체를 이해할 수 없으며
				파싱과 컴파일과정을 거치게 된다.
				하지만 매번 여러사람이 사용하는 공동 DB에 컴파일이나 문법 검사등을 일으킨다면
				성능에 상당한 영향을 주게된다.
				=======================================
				해결책!!!
				 - select * from member where id=입력값 and pass = 입력값
				   입력이 값이 달라지므로 매순간 컴파일이 이루어지게 된다.
				   따라서 입력값이 변하더라도 전체문장의 변경으로 간주하지 말자!!
				   이떄 사용하는 변수가 바로~ 바인드변수다!!!
				=======================================
								(ANSI)표준쿼리		절차언어
				오라클					O				PLSQL
				MSSQL				O				TSQL		
				
				
				 */				
				
				//담기전에 지우기
				subList.removeAll(subList); //메모리상 지우기
				ch_sub.removeAll();	//디자인상 지우기(초이스 안에있는 값)
				
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
	
	//데이터베이스 커넥션 얻기
	public void init(){
		manager = DBManager.getInstance();
		con =manager.getConnection();
		System.out.println(con);
	}
	
	//초이스 컴포넌트 변경값 잡아내기
	public void itemStateChanged(ItemEvent e) {
		getSub();		
	}
	
	
	//상품등록
	public void regist(){
		PreparedStatement pstmt=null;
		String sql="insert into product(product_id,subcategory_id,product_name,price,img)";
		sql+=" values(seq_product.nextval,?,?,?,?)";
		
		try {
			pstmt =con.prepareStatement(sql);
			
			//ArrayList안에 들어있는 Subcategory DTO를 추출하여 PK값을 넣어주자		
			int index = ch_sub.getSelectedIndex();
			SubCategory vo = subList.get(index);
			
			// 바인드에 변수에 들어가 값 결정
			pstmt.setInt(1,vo.getSubcategory_id());
			pstmt.setString(2, t_name.getText());
			pstmt.setInt(3,  Integer.parseInt(t_price.getText()));
			pstmt.setString(4, file.getName());			
			
			
			//executeUpdate 메서드는 쿼리문 수행 후 반영된 레코드의 갯수를 반환해준다 따라서 insert문의 경우에는 언제나 성공했다면 1, 
			//update는 1건이상, delete 1건
			//결론) insert시 반환값이 0이라면 insert 실패.
			int result = pstmt.executeUpdate();
			if(result!=0){
				JOptionPane.showMessageDialog(this, "등록성공");
				upModel.getList(); //DB를 새롭게 가져와 이차원 벡터 변경
				table_up.updateUI();
				//이미지파일 복사하기
				copy();
				
			}else{
				JOptionPane.showMessageDialog(this, "등록실패");
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
	
	//등록버튼
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
	
	//캔버스에 이미지 반영하기
	public void preView(){
		int result = chooser.showOpenDialog(this);
		if(result == JFileChooser.APPROVE_OPTION){
			//캔버스에 이미지 그리기!
			file = chooser.getSelectedFile();
			//얻어진 파일을 기존의 이미지로 대체하여 다시 그리기
			try {
				image=ImageIO.read(file);
				can_west.repaint();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	//복사메서드 정의
	public void copy(){
		FileInputStream fis = null;
		FileOutputStream fos= null;
		
		try {
			fis= new FileInputStream(file);
			fos =new FileOutputStream("C:/java_workspace2/BreadProject/data/"+file.getName());
			
			byte[] b = new byte[1024];
			int flag; //읽어들이는것이 -1인지 확인
			while(true){
				flag = fis.read(b);
				if(flag==-1)break;
				fos.write(b);
			}
			System.out.println("이미지 복사완료");
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
	
	//상세정보 보여주기
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
