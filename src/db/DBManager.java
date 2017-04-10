/*
 * 목적 1)
 * 데이터베이스 계정 정보를 중복해서 기재하지 않기 위함
 * (db연동을 하는 각각의 클래스에서...)
 * 
 * 목적2)
 * 인스턴스의 갯수를 한개만 둬보기!!
 *  - 어플리케이션 가동 중 생성되는 Connection객체를 하나로 통일하기 위함..
 * */

package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBManager {
	static private DBManager instance;
	private String driver="oracle.jdbc.driver.OracleDriver";
	private String url="jdbc:oracle:thin:@localhost:1521:XE";
	private String user="bread";
	private String password="bread";
	private Connection con;
	
	
	static public DBManager getInstance() {
		if(instance==null){
			instance = new DBManager();
		}
		return instance;
	}
	
	
	public Connection getConnection(){
		return con;
	}
	
	public void disConnect(Connection con){
		if(con!=null){
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	
	private DBManager(){
		/*
		 * 1단계 드라이버로드
		 * 2단계 접속
		 * 3단계 쿼리문수행
		 * 4단계 해제
		 * */
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, password);
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	
}
