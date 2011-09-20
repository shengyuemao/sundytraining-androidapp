package local.spring.action;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import local.spring.help.InfoHelp;
import local.spring.table.Columns;
import local.unitech.page.Pagination;

import oracle.toplink.essentials.internal.parsing.SomeNode;

import org.springframework.web.servlet.ModelAndView;

import com.unitech.common.DateHelp;
import com.unitech.hibernate.HibernateHelp;
import com.unitech.io.FileHelp;

/**
 * Filename: JspToHtml.java <br>
 * Ttitle: jsp转换成html<br>
 * De.ion: 把动态网页转换成静态网页<br>
 * Copyright: Copyright (c) 2002-2008 BocSoft,Inc.All Rights Reserved. <br>
 * Company: BocSoft<br>
 * Author: <a href="[url=mailto:sgicer@163.com]mailto:sgicer@163.com">阿汐</a> <br>
 * Date: 2006-6-19 <br>
 * Time: 16:41:09 <br>
 * Version: 2.0.0 <br>
 */
public class JspToHtml {
	/**
	 * 根据本地模板生成静态页面
	 * 
	 * @param JspFile
	 *            jsp路经
	 * @param HtmlFile
	 *            html路经
	 *            type  leixin
	 * @return
	 */
	public static boolean JspToHtmlFile(String filePath, String HtmlFile,String content,String type) {
		String str = "";
		try {
			String tempStr = "";
			FileInputStream is = new FileInputStream(filePath);// 读取模块文件
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			while ((tempStr = br.readLine()) != null)
				str = str + tempStr;
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		try {
			str = str.replaceAll("#content#", content);
			File f = new File(HtmlFile);
			BufferedWriter o = new BufferedWriter(new FileWriter(f));
			o.write(str);
			o.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static void removeFile(String path) {    
        removeFile(new File(path));    
    }    
   
    public static void removeFile(File path) {
        if (path.isDirectory()) {    
            File[] child = path.listFiles();    
            if (child != null && child.length != 0) {    
                for (int i = 0; i < child.length; i++) {    
                    removeFile(child[i]);    
                    child[i].delete();    
                }    
            }    
        }  
    }
    
	public static void JspToHtmlFile(String filePath, String HtmlFile,String content,String type,String page) {
		if(page==null)page="1";
		String path = HtmlFile + "/showpage/bdyw";
		
		FileHelp.createDirectory(path, false);
		if("1".equals(page)){
			HtmlFile = path+"/"+type+".html";
		}else{
			HtmlFile = path+"/"+type+"_"+page+".html";
		}
		
		File f = new File(HtmlFile);
		if(!f.exists()){
			String str = "";
			try {
				String tempStr = "";
				FileInputStream is = new FileInputStream(filePath);// 读取模块文件
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				while ((tempStr = br.readLine()) != null)
					str = str + tempStr;
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
				
			}
			try {
				str = str.replaceAll("#content#", content);
				BufferedWriter o = new BufferedWriter(new FileWriter(f));
				o.write(str);
				o.close();
			} catch (IOException e) {
				e.printStackTrace();
				
			}	
		}
		
	}
	/**
	 * 根据url生成静态页面
	 * 
	 * @param u
	 *            动态文件路经 如：http://www.163.com/x.jsp
	 * @param path
	 *            文件存放路经如：x:\\abc\bbb.html
	 * @return
	 */
	public static boolean JspToHtmlByURL(String u, String path) {
		// 从utl中读取html存为str
		String str = "";
		try {
			URL url = new URL(u);
			URLConnection uc = url.openConnection();
			InputStream is = uc.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			System.out.println(br.toString());
			while (br.ready()) {
				str += br.readLine() + "\n";
				
			}
			is.close();
			// 写入文件
			File f = new File(path);
			BufferedWriter o = new BufferedWriter(new FileWriter(f));
			o.write(str);
			o.close();
			str = "";
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 根据url生成静态页面
	 * 
	 * @param url
	 *            动态文件路经 如：http://www.163.com/x.jsp
	 * @return d
	 */
	public static StringBuffer getHtmlTextByURL(String url) {
		// 从utl中读取html存为str
		StringBuffer sb = new StringBuffer();
		try {
			URL u = new URL(url);
			URLConnection uc = u.openConnection();
			InputStream is = uc.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			while (br.ready()) {
				sb.append(br.readLine() + "\n");
			}
			System.out.println(sb.toString());
			is.close();
			return sb;
		} catch (Exception e) {
			e.printStackTrace();
			return sb;
		}
	}

	/**
	 * 测试main 函数
	 * 
	 * @param arg
	 */
	public static void main(String[] arg) {
		long begin = System.currentTimeMillis();
		// 循环生成20个html文件
//		for (int k = 0; k < 20; k++) {
//			String url = "D:\\mb.template";// 模板文件地址
//			String savepath = "d:/" + k + ".html";// 生成文件地址
//			JspToHtmlFile(url, savepath);
//		}
//		removeFile("d:/Tomcat6.0.20/webapps/nblr/showpage/bdyw");
//		JspToHtmlByURL("http://localhost:8083/showpage/bdyw.jsp", "showpage/bdyw/htmlMB.template");
		System.out.println("用时:" + (System.currentTimeMillis() - begin) + "ms");
	}
	
	
	/**
	 * 静态生成html函数
	 * @param request
	 * @param response
	 * @param command
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getxwdt_list(){
		StringBuilder tables = new StringBuilder();
		StringBuilder table = new StringBuilder();
		Columns list_name = (Columns) HibernateHelp.queryOne("from Columns where name='bdyw'");
		
		int record = 0;
		Pagination pageInfo = null;
		Object[][] someinfo = null;
		// 获得一级栏目信息

		tables.append("<div id=\"show_zwgk_layer\">");
		// 二级栏目导航菜单
		tables.append("<img src=\"images/point002.gif\"	style=\"padding-right:5px;\"> 您现在的位置：</strong><a href=\"http://www.nblr.gov.cn\">首页</a> &gt;&gt;");
		tables.append(InfoHelp.getTitle(2+"",list_name));
		tables.append("</div>");
		tables.append("<TABLE><TBODY><TR><TD><table height=\"3\"><tr><td></td></tr></table></td></tr>");
		tables.append("<TR><TD vAlign=\"top\" height=\"570\">");
		tables.append("<div id=\"show_zwgk_page\">");
		/**
		 * 使用Pagination替换原分页hl
		 */
		String sql = "select title,id,pubdatetime,accesscount,lastupdatedatetime from Info where node='" + list_name.getId() + "' and commit='Y' order by pubdatetime desc";
		Object[][] ob2 = HibernateHelp.queryTwoArray("select count(*) from Info where node='" +  list_name.getId() + "' and commit='Y' order by pubdatetime desc");
		record = Integer.parseInt(ob2[0][0].toString());
		String lastdatetime = "";
		File file = null;
		
		//JspToHtml.removeFile("D:/Tomcat5.0/webapps/nblr/showpage/bdyw");
		JspToHtml.removeFile("F:/NBLR/Tomcat6.0.20/webapps/nblr/showpage/bdyw");
		String path = "";
		for(int i=1;i<=(record+19)/20;i++){
			table = new StringBuilder();
			pageInfo = new Pagination(i, sql, 20, false);
			someinfo = pageInfo.getTwoArray();
			table.append("<TABLE class=\"center_table2\" cellSpacing=\"0\" cellPadding=\"0\" width=\"100%\" border=\"0\" id=\"table17\"><TBODY>");
			String label = "";
			table.append("<TR><TD height=\"580\" valign=\"top\">");
			for (int j = 0; j < someinfo.length; j++) {
				label = someinfo[j][0]+"";					
				if(label.length()>50){
					label = label.substring(0,49);
				}
				table.append("<TABLE width=\"778\" id=\"table85\" height=\"29\" cellSpacing=\"0\" cellPadding=\"0\" border=\"0\" class=\"Table_mouseover\"><TBODY><TR class=\"tr_mouseout\" onmouseover=\"this.className='tr_mouseover'\" onmouseout=\"this.className='tr_mouseout'\"><TD align=\"center\" width=\"25\"><IMG height=\"5\" alt=\"\" src=\"images/tb2.gif\" width=5></TD><TD><a title=\"" + someinfo[j][0] + "\" target=\"_blank\" class=\"red1\" href=\"showpage/detail/" + someinfo[j][1] + ".html\">" + label + "</a></TD>");
				table.append("<td style=\"width:100px;align:right;\">"+(someinfo[j][2]+"").substring(0,10)+"</td>");
				table.append("<td style=\"width:100px;align:right;\">"+someinfo[j][3]+" 次</td>");
				table.append("</TR></TBODY></TABLE>");
				lastdatetime = (someinfo[j][2]+"").substring(0,10);
				//if(DateHelp.getStringOfDateFormat(new Date(), "yyyy-MM-dd").equals(lastdatetime)){
					//path = "D:/Tomcat5.0/webapps/nblr/showpage/detail/" + someinfo[j][1] + ".html";
					path = "F:/NBLR/Tomcat6.0.20/webapps/nblr/showpage/detail/" + someinfo[j][1] + ".html";
					file = new File(path);
					if(file.exists()){
						file.delete();
					}
					JspToHtml.JspToHtmlByURL("http://www.nblr.gov.cn/showpage/bdywdetail.jsp?layer=2&id="+someinfo[j][1],path);
					//JspToHtml.JspToHtmlByURL("http://localhost:8081/nblr/showpage/bdywdetail.jsp?layer=2&id="+someinfo[j][1],path);
			//	}
			}
			table.append("</TD></TR>");
			table.append("<TR><TD align=\"right\">");
			table.append("<table cellpadding=\"0\" cellspacing=\"0\" width=\"620\" height=\"78\"><tr><td height=\"78\">");
			table.append("<input type=\"hidden\" name=\"page\" id=\"page\" value="+i+">每页");
			table.append("<span class=\"c1\">20 </span> 条记录 ／共");
			table.append("<span class=\"c1\">"+record+"</span> 条记录 第");
			table.append("<span class=\"c1\">"+i+" </span> 页／<span class=\"c1\">"+(record/20+1)+" </span> 页");
			if(i==1||i==2){				
				table.append("<a href=\"showpage/bdyw/bdyw.html\" > 上一页 </a>");
			}else{
				table.append("<a href=\"showpage/bdyw/bdyw_"+(i-1)+".html\" > 上一页 </a>");
			}
			if(i==(record+19)/20){
				table.append("<a href=\"showpage/bdyw/bdyw_"+(record+19)/20+".html\"> 下一页 </a>");
			}else{
				table.append("<a href=\"showpage/bdyw/bdyw_"+(i+1)+".html\"> 下一页 </a>");
			}
			table.append("<a href=\"showpage/bdyw/bdyw.html\" > 首页 </a>");
			table.append("<a href=\"showpage/bdyw/bdyw_"+((record+19)/20)+".html\" > 末页 </a>");
			table.append("<select onchange=\"gopage2(this.value)\">");
			for(int j=1;j<=(record+19)/20;j++){
				if(i==j){
					table.append("<option selected value=\""+j+"\">"+j+"</option>");
				}else{
					table.append("<option value=\""+j+"\">"+j+"</option>");
				}
			}
			table.append("</select>");
			table.append("</td></tr></table>");
			table.append("</td></tr></table>");
			table.append("</TD></TR>");
			table.append("</TBODY></TABLE>");
			table.append("</div>");
			table.append("</TD></TR></TBODY></TABLE>");
			table.append("</div>");
			table.append("</TD></TR></TBODY></TABLE></DIV></td></tr></table>");
			//JspToHtml.JspToHtmlFile("D:/Tomcat5.0/webapps/nblr/showpage/bdywMB/htmlMB.template", "d:/Tomcat5.0/webapps/nblr",tables.toString()+table.toString(),"bdyw",i+"");
			JspToHtml.JspToHtmlFile("F:/NBLR/Tomcat6.0.20/webapps/nblr/showpage/bdywMB/htmlMB.template", "F:/NBLR/Tomcat6.0.20/webapps/nblr",tables.toString()+table.toString(),"bdyw",i+"");
			
		}
		return null;
	}
}