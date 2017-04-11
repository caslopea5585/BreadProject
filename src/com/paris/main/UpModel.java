/*
 * 하위 카테고리와 그 카테고리에 등록된 상품의 수 정보를 제공하는 모델
 *  */
package com.paris.main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

public class UpModel extends AbstractTableModel{
	Vector<String> ColumName = new Vector<String>();
	Vector<Vector> data = new Vector<Vector>();
	Connection con;
	
	public UpModel(Connection con) {
		this.con = con;
		getList();
	}
	
	//목록 가져오기
	public void getList(){
		PreparedStatement pstmt=null;
		ResultSet rs=null;
			
		StringBuffer sql = new StringBuffer();
		sql.append("select s.subcategory_id as subcategory_id,sub_name as 카테고리명,count(product_id) as 갯수 from subcategory s left outer join product p");
		sql.append(" on s.subcategory_id=p.subcategory_id");
		sql.append(" group by s.subcategory_id,sub_name");
		
		//벡터들을 초기화
		ColumName.removeAll(ColumName);
		data.removeAll(data);
		
		try {
			pstmt = con.prepareStatement(sql.toString());
			rs= pstmt.executeQuery();
			//컬럼명 추출
			ResultSetMetaData meta = rs.getMetaData();
			for(int i=1; i<=meta.getColumnCount();i++){
				ColumName.add(meta.getColumnName(i));
			}
			
			while(rs.next()){
				//레코드 1건을 벡터에 옮겨심자..
				//여기에서의 벡터는 DTO역할
				Vector vec = new Vector();
				vec.add(rs.getString("subcategory_id"));
				vec.add(rs.getString("카테고리명"));
				vec.add(rs.getString("갯수"));
				data.add(vec);
				
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
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
	
	public int getRowCount() {
	
		return data.size();
	}

	
	public int getColumnCount() {
		
		return ColumName.size();
	}
	
	public String getColumnName(int col) {
	
		return ColumName.get(col);
	
	}
	
	public Object getValueAt(int row, int col) {
		
		return data.get(row).get(col);
	}
	
}
