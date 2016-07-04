package com.db;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
 
public class Main {
	public static DB db = new DB();
	
	public static void main(String[] args) throws SQLException, IOException {
		db.runSql2("TRUNCATE crawler;");
		processPage("http://www.sina.com.cn/");
	}
	public static void processPage(String URL) throws SQLException, IOException{
		//check if the given URL is already in database
		String sql = "select * from crawler where url = '"+URL+"'";
		ResultSet rs = db.runSql(sql);
		if(rs.next()){
 
		}else{
			//store the URL to database to avoid parsing again
			sql = "insert into crawler values(null,?)";
//			System.out.println(sql);
			PreparedStatement stmt = db.conn.prepareStatement(sql);
			stmt.setString(1, URL);
			stmt.execute();
 
			//get useful information
			Document doc = Jsoup.connect("http://www.sina.com.cn/").get();
 
			if(doc.text().contains("research")){
				System.out.println(URL);
			}
 
			//get all links and recursively call the processPage method
			Elements questions = doc.select("a[href]");
			for(Element link: questions){
				if(link.attr("href").contains("com.cn"))
					//abs:href获取绝对路径
					processPage(link.attr("abs:href"));
			}
		}
	}
}
