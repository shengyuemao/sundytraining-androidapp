package local.spring.action;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import local.LocalConstant;
import local.spring.help.IndexHelp;
import local.spring.login.UserConfig;
import local.spring.table.Corerror;
import local.spring.table.Users;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.PeriodAxis;
import org.jfree.chart.axis.PeriodAxisLabelInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.servlet.ServletUtilities;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimePeriodAnchor;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.RectangleInsets;
import org.springframework.web.servlet.ModelAndView;

import com.unitech.hibernate.HibernateHelp;
import com.unitech.http.RequestHelp;
import com.unitech.http.ResponseHelp;
import local.unitech.page.Pagination;

public class PrpcindexController {
	Object[][] list = null;
	

	/**
	 * 纠错
	 * 
	 * @param request
	 * @param response
	 * @param command
	 * @return
	 * @throws Exception
	 */
	public ModelAndView savecorerror(HttpServletRequest request, HttpServletResponse response, Object command) throws Exception {
		String url = request.getParameter("url");
		String title = request.getParameter("title");
		String content = request.getParameter("content");
		String username = request.getParameter("username");
		String tel = request.getParameter("tel");
		String type = request.getParameter("type");
		// BaseMgr bmgr = new BaseMgr();
		Corerror cor = new Corerror();
		url = url.replaceAll("%", "&");
		cor.setUrl(url);
		cor.setTitle(title);
		cor.setContent(content);
		cor.setUsername(username);
		cor.setTel(tel);
		cor.setType(type);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date currentTime = new Date();
		String date = formatter.format(currentTime);
		cor.setPubdatetime(date);
		cor.setIsres("N");
		String itemno = "nb" + date.substring(0, 4) + date.substring(5, 7) + date.substring(8, 10) + date.substring(11, 13) + date.substring(14, 16) + date.substring(17, 19);
		cor.setItemno(itemno);
		cor.setRescontent("");
		cor.setResdatetime("");
		cor.setShowcorerror("N");
		// 保存
		try {
			HibernateHelp.saveObject(cor);
		} catch (Exception e) {
			e.printStackTrace();
		}

		StringBuilder tables = new StringBuilder();
		tables.append("<br><br><br><br><br><font style=\"font-size:14px\" color=\"#ff0000\">提交成功，请记住您的查询编号：" + itemno + " </font><br><br><br><br><br>");
		ResponseHelp.wirteAjax(response, tables.toString());
		return null;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param command
	 * @return
	 * @throws Exception
	 */
	public ModelAndView showdetail(HttpServletRequest request, HttpServletResponse response, Object command,StringBuilder tables) {
		String type = request.getParameter("type").trim();
		String type2 = request.getParameter("type2");
		String searpro = request.getParameter("searpro");
		String searno = request.getParameter("searno");
		String _type = request.getParameter("_type");
		
		tables.append("<table cellpadding=\"0\" cellspacing=\"0\" width=\"782\">");
		tables.append("<tr><td width=\"7\" align=\"center\"></td>");
		tables.append("<td  width=\"782\" align=\"center\" >");
		Pagination pageInfo;
		Object[][] someinfo;
		tables.append("<table cellpadding=\"0\" cellspacing=\"0\" width=\"782\" class=\"center_table2\" id=\"table89\">");
		if (!"czw".equals(type2)){
		tables.append("<tr><td width=\"259\">&nbsp;&nbsp;<img alt=\"\" src=\"images/search_" + type + ".jpg\"></td><td width=\"541\"><p align=\"right\"><font color=\"#F55000\">*</font> 请按下面选择框和输入框要求填入完整查询内容，按查询按钮进行查询。&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td></tr>");
		}else{
		tables.append("<tr><td width=\"13\">&nbsp;&nbsp;</td><td width=\"541\"><p align=\"left\"><font color=\"#F55000\">*</font> 请按下面选择框和输入框要求填入完整查询内容，按查询按钮进行查询。&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td></tr>");	
		}
		tables.append("<tr><td style=\"border-bottom-style: solid; border-bottom-width: 1px\" height=\"32\" valign=\"top\" width=\"782\" colspan=\"2\"><p align=\"center\">&nbsp;&nbsp; &nbsp; ");
		if("jsydys".equals(type) || "jsydsp".equals(type) || "tdcrjg".equals(type) || "jsydfhys".equals(type)){
			tables.append("项目名称:");
		}else if("blqk2".equals(type)){			
			tables.append("项目编号:");
		}else if("ckqsp".equals(type)){
			tables.append("许可证号:");
		}
		tables.append("<input id=\"searpro\" style=\"WIDTH: 200px\" name=\"infoTitle\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; ");
		if("jsydys".equals(type) || "jsydsp".equals(type)){			
			tables.append("申请单位:");
		}else if("tdcrjg".equals(type)){			
			tables.append("用途:");
		}else if("jsydfhys".equals(type)){			
			tables.append("开发单位:");
		}else if("blqk2".equals(type)){			
			tables.append("权利人:");
		}else if("ckqsp".equals(type)){
			tables.append("矿山名称:");
			
		}
		tables.append(" <input id=\"searno\" style=\"WIDTH: 200px\" name=\"infoNumber\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src=\"images/nn01.gif\" onclick=\"pubdetail_search('" + type + "')\"></p></td></tr>");
		tables.append("<tr><td align=\"center\" style=\"border-top-style: solid; border-top-width: 1px\" width=\"782\" colspan=\"2\">");
		tables.append("<TABLE id=ConTable cellSpacing=1 cellPadding=2 width=\"782\" bgColor=#ffffff border=0 id=\"table1\"><TBODY>");
		tables.append("<TR valign=\"top\" height=3><TD colSpan=4 ></TD></TR>");
		tables.append("<TR valign=\"top\"><TD colSpan=4>");
		tables.append("<div id=\"show_pubdetail_page\" align=\"center\">");
		tables.append("<table cellSpacing=\"0\" cellPadding=\"0\" width=\"100%\" border=\"0\" id=\"table17\"><TBODY>");
		tables.append("<TR valign=\"top\" height=\"315\"><TD colSpan=4>");
		
		String sql = "";
		if ("jsydys".equals(type)) {
			sql = "select project,applicant,symbol,apptime from TJsydys";
			if("search".equals(_type)){
				sql +=" where project is not null";
				if (!"".equals(searpro) && searpro != null) {
					sql = sql + " and project like '%" + searpro + "%'";
				}
				if (!"".equals(searno) && searno != null) {
					sql = sql + " and applicant like '%" + searno + "%'";
				}
			}
			sql += " order by apptime desc";
			tables.append("<table><TR><TD align=middle width=\"30\" bgColor=#4fbef3 height=19><font color=\"#FFFFFF\">序号</font></TD><TD align=middle width=\"345\" bgColor=#4fbef3><font color=\"#FFFFFF\">项目名称</font></TD><TD align=middle width=\"345\" bgColor=#4fbef3><font color=\"#FFFFFF\">申请单位</font></TD><TD align=middle width=\"150\" bgColor=#4fbef3><font color=\"#FFFFFF\">审批文号</font></TD><TD width=\"80\" bgColor=#4fbef3 align=\"center\"><font color=\"#FFFFFF\">批准时间</font></TD></TR></table>");
		} else if ("jsydsp".equals(type)) {
			sql = "select project,applicant,symbol,apptime  from TJsydsp";
			if("search".equals(_type)){
				sql +=" where project is not null";
				if (!"".equals(searpro) && searpro != null) {
					sql = sql + " and project like '%" + searpro + "%'";
				}
				if (!"".equals(searno) && searno != null) {
					sql = sql + " and applicant like '%" + searno + "%'";
				}
			}
			sql += " order by apptime desc";
			tables.append("<table><TR><TD align=middle width=\"30\" bgColor=#4fbef3 height=19><font color=\"#FFFFFF\">序号</font></TD><TD align=middle width=\"345\" bgColor=#4fbef3><font color=\"#FFFFFF\">项目名称</font></TD><TD align=middle width=\"345\" bgColor=#4fbef3><font color=\"#FFFFFF\">申请单位</font></TD><TD align=middle width=\"150\" bgColor=#4fbef3><font color=\"#FFFFFF\">审批文号</font></TD><TD width=\"80\" bgColor=#4fbef3 align=\"center\"><font color=\"#FFFFFF\">批准时间</font></TD></TR></table>");
		} else if ("tdcrjg".equals(type)) {
			sql = "select id,name,uses,area,price from TTdcrjg ";
			if("search".equals(_type)){
				sql +=" where name is not null";
				if (!"".equals(searpro) && searpro != null) {
					sql = sql + " and name like '%" + searpro + "%'";
				}
				if (!"".equals(searno) && searno != null) {
					sql = sql + " and uses = '" + searno + "'";
				}
			}
			sql += " order by confirmtime desc";
			tables.append("<table><TR><TD align=middle width=\"30\" bgColor=#4fbef3 height=19><font color=\"#FFFFFF\">序号</font></TD><TD align=middle width=\"345\" bgColor=#4fbef3><font color=\"#FFFFFF\">地块名称</font></TD><TD align=middle width=\"345\" bgColor=#4fbef3><font color=\"#FFFFFF\">用途</font></TD><TD align=middle width=\"150\" bgColor=#4fbef3><font color=\"#FFFFFF\">出让面积(平方米)</font></TD><TD width=\"80\" bgColor=#4fbef3 align=\"center\"><font color=\"#FFFFFF\">成交价格(万元)</font></TD></TR></table>");
		} else if ("jsydfhys".equals(type)) {
			sql = "select project,developer,users,completiontime from TJsydfhys ";
			if("search".equals(_type)){
				sql +=" where project is not null";
				if (!"".equals(searpro) && searpro != null) {
					sql = sql + " and project like '%" + searpro + "%'";
				}
				if (!"".equals(searno) && searno != null) {
					sql = sql + " and developer like '%" + searno + "%'";
				}
			}
			sql += " order by completiontime desc";
			tables.append("<table><TR><TD align=middle width=\"30\" bgColor=#4fbef3 height=19><font color=\"#FFFFFF\">序号</font></TD><TD align=middle width=\"345\" bgColor=#4fbef3><font color=\"#FFFFFF\">项目名称</font></TD><TD align=middle width=\"345\" bgColor=#4fbef3><font color=\"#FFFFFF\">开发单位</font></TD><TD align=middle width=\"150\" bgColor=#4fbef3><font color=\"#FFFFFF\">土地用途</font></TD><TD width=\"80\" bgColor=#4fbef3 align=\"center\"><font color=\"#FFFFFF\">发布时间</font></TD></TR></table>");
		} else if ("blqk2".equals(type)) {
			sql = "select projectId,sqrMc,actinstName,startDate from TBlqk2 ";
			if("search".equals(_type)){
				sql +=" where projectId is not null";
				if (!"".equals(searpro) && searpro != null) {
					sql = sql + " and projectId like '%" + searpro + "%'";
				}
				if (!"".equals(searno) && searno != null) {
					sql = sql + " and sqrMc like '%" + searno + "%'";
				}
			}
			sql += " order by startDate desc";
			tables.append("<table><TR><TD align=middle width=\"30\" bgColor=#4fbef3 height=19><font color=\"#FFFFFF\">序号</font></TD><TD align=middle width=\"345\" bgColor=#4fbef3><font color=\"#FFFFFF\">项目编号</font></TD><TD align=middle width=\"345\" bgColor=#4fbef3><font color=\"#FFFFFF\">权利人</font></TD><TD align=middle width=\"150\" bgColor=#4fbef3><font color=\"#FFFFFF\">项目状态</font></TD><TD width=\"80\" bgColor=#4fbef3 align=\"center\"><font color=\"#FFFFFF\">受理时间</font></TD></TR></table>");
		} else if ("ckqsp".equals(type)) {
			sql = "select licensenno,minename,person,organ from TCkqsp";
			if("search".equals(_type)){
				sql +=" where licensenno is not null";
				if (!"".equals(searpro) && searpro != null) {
					sql = sql + " and licensenno like '%" + searpro + "%'";
				}
				if (!"".equals(searno) && searno != null) {
					sql = sql + " and minename like '%" + searno + "%'";
				}
			}
			tables.append("<table><TR><TD align=middle width=\"30\" bgColor=#4fbef3 height=19><font color=\"#FFFFFF\">序号</font></TD><TD align=middle width=\"345\" bgColor=#4fbef3><font color=\"#FFFFFF\">许可证号</font></TD><TD align=middle width=\"345\" bgColor=#4fbef3><font color=\"#FFFFFF\">矿山名称</font></TD><TD align=middle width=\"150\" bgColor=#4fbef3><font color=\"#FFFFFF\">采矿权人</font></TD><TD width=\"80\" bgColor=#4fbef3 align=\"center\"><font color=\"#FFFFFF\">发证机关</font></TD></TR></table>");
		}
		pageInfo = new Pagination(request, sql, 10, false);
		someinfo = pageInfo.getTwoArray();
		request.getSession().setAttribute(LocalConstant.PAGENITION_TEST, pageInfo);
		for (int i = 0; i < someinfo.length; i++) {
			tables.append("<table>");
			if ((i + 1) % 2 == 0) {
				tables.append("<TR bgColor=#e2f4f8>");
			} else {
				tables.append("<TR bgColor=#ffffff>");
			}
			tables.append("<TD align=middle height=25 width=\"30\">" + (i + 1) + "</TD><TD align=middle height=25 width=\"345\"><p align=\"left\"><font color=\"#575757\">&nbsp;<a  href=\"#\" onclick=\"pubdetail_one('" + type + "','" + someinfo[i][0] + "');return false;\">");
			if(!"tdcrjg".equals(type)){
				tables.append(someinfo[i][0]);
			}else{
				tables.append(someinfo[i][1]);
			}
			tables.append("</a></font></TD><TD align=middle height=25 width=\"345\"><p align=\"left\">&nbsp;<a  href=\"#\" onclick=\"pubdetail_one('" + type + "','" + someinfo[i][0] + "');return false;\">");
			if(!"tdcrjg".equals(type)){
				tables.append(someinfo[i][1]);
			}else{
				String uses = someinfo[i][2]+"";
				if(uses.length()>37){
					uses = uses.substring(0,36)+"..";
				}
				tables.append(uses);
			}
			tables.append("</a></TD><TD align=middle height=25 width=\"150\"><a  href=\"#\" onclick=\"pubdetail_one('" + type + "','" + someinfo[i][0] + "');return false;\">");
			if(!"tdcrjg".equals(type)){
				tables.append(someinfo[i][2]);
			}else{
				String area = someinfo[i][3]+"";
				if(area.length()>20){
					area = area.substring(0,19)+"..";
				}
				tables.append(area);
			}
			tables.append("</a></TD><TD height=25 align=\"center\" width=\"80\" ><p><font color=\"#575757\">");
			if("jsydys".equals(type) || "jsydsp".equals(type) || "jsydfhys".equals(type) || "blqk2".equals(type)){ 
				if (someinfo[i][3] != null && !"".equals(someinfo[i][3])) {
					tables.append((someinfo[i][3] + "").substring(0, 10));
				}
			}else if("tdcrjg".equals(type)){
				String price=someinfo[i][4] + "";
				int indexprice=price.indexOf("万");
				price.substring(0,indexprice);
				tables.append(price.substring(0,indexprice));
			}else if("ckqsp".equals(type)){
				tables.append(someinfo[i][3] + "");
			}
			tables.append("</font></TD></TR></table>");
		}
		tables.append("<TR><TD align=\"center\" bgColor=\"#ffffff\" colSpan=\"5\" height=\"26\">");
		tables.append("<iframe src=\"\" name=\"if1\" id=\"if1\" border=\"0\" width=\"98%\" height=\"30\" align=\"center\" frameborder=\"no\" border=\"0\" marginwidth=\"0\" marginheight=\"0\" scrolling=\"no\" allowtransparency=\"yes\"></iframe>");
		
		tables.append("</td></tr></table>");
		tables.append("</td></tr></table>");
		ResponseHelp.wirteAjax(response, tables.toString());
		return null;
	}

	

	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param command
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getShowdetail_one(HttpServletRequest request, HttpServletResponse response, Object command) throws Exception {
		String type = request.getParameter("type");
		String project = request.getParameter("project");
		StringBuilder tables = new StringBuilder();
		Object[][] list_one = null;
		// BaseMgr bmgr = new BaseMgr();
		if ("jsydys".equals(type)) {
			tables.append("<table  cellSpacing=\"0\" cellPadding=\"0\" width=\"100%\" border=\"0\">");
			tables.append("<tr><td height=\"10\"></td></tr>");
			tables.append("<tr><td>");
			tables.append("<table  width=\"100%\" border=\"0\" cellPadding=\"2\"  cellSpacing=\"1\"  bgColor=\"#D2DFE6\">");
			tables.append("<tr>");
			tables.append("<TD align=\"center\" colspan=\"4\" height=\"25\" bgColor=\"#F3F7FA\" >详  细  信  息</TD>");
			tables.append("</tr>");
			list_one = HibernateHelp.queryTwoArray("select  project,symbol,applicant,area,itemtype,apptime,address from TJsydys where project = '" + project + "'");
			// TJsydys inf = (TJsydys) list_one.get(0);
			tables.append("<tr>");
			tables.append("<TD width=\"80\" height=\"25\" bgColor=\"#ffffff\" >项目名称</TD><td bgColor=\"#ffffff\" width=\"300\" height=\"25\">" + list_one[0][0] + "</td><TD width=\"80\" height=\"25\" bgColor=\"#ffffff\" >审批文号</TD><td bgColor=\"#ffffff\" width=\"290\" height=\"25\">" + list_one[0][1] + "</td>");
			tables.append("</tr>");
			tables.append("<tr>");
			tables.append("<TD width=\"80\" height=\"25\" bgColor=\"#ffffff\" >申请单位</TD><td bgColor=\"#ffffff\" height=\"25\">" + list_one[0][2] + "</td><TD width=\"80\" height=\"25\" bgColor=\"#ffffff\" >用地规模</TD><td bgColor=\"#ffffff\" width=\"290\" height=\"25\">" + list_one[0][3] + "</td>");
			tables.append("</tr>");
			tables.append("<tr>");
			tables.append("<TD width=\"80\" height=\"25\" bgColor=\"#ffffff\" >项目类型</TD><td bgColor=\"#ffffff\" height=\"25\">" + list_one[0][4] + "</td><TD width=\"80\" height=\"25\" bgColor=\"#ffffff\" >批准时间</TD><td bgColor=\"#ffffff\" width=\"290\" height=\"25\">" + list_one[0][5] + "</td>");
			tables.append("</tr>");
			tables.append("<tr>");
			tables.append("<TD width=\"80\" height=\"25\" bgColor=\"#ffffff\" >拟建地点</TD><td bgColor=\"#ffffff\" colspan=\"3\" height=\"25\">" + list_one[0][6] + "</td>");
			tables.append("</tr>");
			tables.append("</table>");
			tables.append("</td></tr>");
			tables.append("<tr><td height=\"10\"></td></tr>");
			tables.append("</table>");
		} else if ("jsydsp".equals(type)) {
			tables.append("<table cellSpacing=\"0\" cellPadding=\"0\" width=\"100%\" border=\"0\">");
			tables.append("<tr><td height=\"10\"></td></tr>");
			tables.append("<tr><td>");
			tables.append("<table  width=\"100%\" border=\"0\" cellPadding=\"2\"  cellSpacing=\"1\"  bgColor=\"#D2DFE6\">");
			tables.append("<tr>");
			tables.append("<TD align=\"center\" colspan=\"4\" height=\"25\" bgColor=\"#F3F7FA\" >详  细  信  息</TD>");
			tables.append("</tr>");
			list_one = HibernateHelp.queryTwoArray("select  project,symbol,applicant,area,itemtype,apptime from TJsydsp where project = '" + project + "'");
			tables.append("<tr>");
			tables.append("<TD width=\"80\" height=\"25\" bgColor=\"#ffffff\" >项目名称</TD><td bgColor=\"#ffffff\" width=\"300\" height=\"25\">" + list_one[0][0] + "</td><TD width=\"80\" height=\"25\" bgColor=\"#ffffff\" >批准文号</TD><td bgColor=\"#ffffff\" width=\"290\" height=\"25\">" + list_one[0][1] + "</td>");
			tables.append("</tr>");
			tables.append("<tr>");
			tables.append("<TD width=\"80\" height=\"25\" bgColor=\"#ffffff\" >申请单位</TD><td bgColor=\"#ffffff\"  height=\"25\">" + list_one[0][2] + "</td><TD width=\"80\" height=\"25\" bgColor=\"#ffffff\" >批准面积</TD><td bgColor=\"#ffffff\" width=\"290\" height=\"25\">" + list_one[0][3] + "</td>");
			tables.append("</tr>");
			tables.append("<tr>");
			tables.append("<TD width=\"80\" height=\"25\" bgColor=\"#ffffff\" >项目类型</TD><td bgColor=\"#ffffff\"  height=\"25\">" + list_one[0][4] + "</td><TD width=\"80\" height=\"25\" bgColor=\"#ffffff\" >批准时间</TD><td bgColor=\"#ffffff\" width=\"290\" height=\"25\">" + list_one[0][5] + "</td>");
			tables.append("</tr>");
			tables.append("</table>");
			tables.append("</td></tr>");
			tables.append("<tr><td height=\"10\"></td></tr>");
			tables.append("</table>");
		} else if ("tdcrjg".equals(type)) {
			tables.append("<table cellSpacing=\"0\" cellPadding=\"0\" width=\"100%\" border=\"0\">");
			tables.append("<tr><td height=\"10\"></td></tr>");
			tables.append("<tr><td>");
			tables.append("<table  width=\"100%\" border=\"0\" cellPadding=\"2\"  cellSpacing=\"1\"  bgColor=\"#D2DFE6\">");
			tables.append("<tr>");
			tables.append("<TD align=\"center\" colspan=\"4\" height=\"25\" bgColor=\"#F3F7FA\" >详  细  信  息</TD>");
			tables.append("</tr>");
			list_one = HibernateHelp.queryTwoArray("select name,area,person,levell,uses,far,way,price,confirmtime,address  from TTdcrjg where id = '" + project + "'");
			tables.append("<tr>");
			tables.append("<TD width=\"80\" height=\"25\" bgColor=\"#ffffff\" >地块名称</TD><td bgColor=\"#ffffff\" width=\"300\" height=\"25\">" + list_one[0][0] + "</td><TD width=\"100\" height=\"25\" bgColor=\"#ffffff\" >出让面积(平方米)</TD><td bgColor=\"#ffffff\" width=\"270\" height=\"25\">" + list_one[0][1] + "</td>");
			tables.append("</tr>");
			tables.append("<tr>");
			tables.append("<TD width=\"80\" height=\"25\" bgColor=\"#ffffff\" >受让人</TD><td bgColor=\"#ffffff\" height=\"25\">" + list_one[0][2] + "</td><TD width=\"100\" height=\"25\" bgColor=\"#ffffff\" >土地级别</TD><td bgColor=\"#ffffff\" width=\"270\" height=\"25\">" + list_one[0][3] + "</td>");
			tables.append("</tr>");
			tables.append("<tr>");
			tables.append("<TD width=\"80\" height=\"25\" bgColor=\"#ffffff\" >用途</TD><td bgColor=\"#ffffff\" height=\"25\">" + list_one[0][4] + "</td><TD width=\"100\" height=\"25\" bgColor=\"#ffffff\" >容积率</TD><td bgColor=\"#ffffff\" width=\"270\" height=\"25\">" + list_one[0][5] + "</td>");
			tables.append("</tr>");
			tables.append("<tr>");
			tables.append("<TD width=\"80\" height=\"25\" bgColor=\"#ffffff\" >供地方式</TD><td bgColor=\"#ffffff\"  height=\"25\">" + list_one[0][6] + "</td><TD width=\"100\" height=\"25\" bgColor=\"#ffffff\" >成交价格(万元)</TD><td bgColor=\"#ffffff\" width=\"270\" height=\"25\">" + list_one[0][7] + "</td>");
			tables.append("</tr>");
			tables.append("<tr>");
			tables.append("<TD width=\"80\" height=\"25\" bgColor=\"#ffffff\">确认时间</TD><td bgColor=\"#ffffff\"  height=\"25\"  colspan=\"3\">" + list_one[0][8] + "</td>");
			tables.append("</tr>");
			tables.append("<tr>");
			tables.append("<TD width=\"80\" height=\"25\" bgColor=\"#ffffff\">地块位置</TD><td bgColor=\"#ffffff\"  height=\"25\"  colspan=\"3\">" + list_one[0][9] + "</td>");
			tables.append("</tr>");
			tables.append("</table>");
			tables.append("</td></tr>");
			tables.append("<tr><td height=\"10\"></td></tr>");
			tables.append("</table>");
		} else if ("jsydfhys".equals(type)) {
			tables.append("<table cellSpacing=\"0\" cellPadding=\"0\" width=\"100%\" border=\"0\">");
			tables.append("<tr><td height=\"10\"></td></tr>");
			tables.append("<tr><td>");
			tables.append("<table  width=\"100%\" border=\"0\" cellPadding=\"2\"  cellSpacing=\"1\"  bgColor=\"#D2DFE6\">");
			tables.append("<tr>");
			tables.append("<TD align=\"center\" colspan=\"4\" height=\"25\" bgColor=\"#F3F7FA\" >详  细  信  息</TD>");
			tables.append("</tr>");
			list_one = HibernateHelp.queryTwoArray("select  project,users,developer,area,address,completiontime from TJsydfhys where project = '" + project + "'");
			tables.append("<tr>");
			tables.append("<TD width=\"80\" height=\"25\" bgColor=\"#ffffff\" >项目名称</TD><td bgColor=\"#ffffff\" width=\"300\" height=\"25\">" + list_one[0][0] + "</td><TD width=\"80\" height=\"25\" bgColor=\"#ffffff\" >土地用途</TD><td bgColor=\"#ffffff\" width=\"290\" height=\"25\">" + list_one[0][1] + "</td>");
			tables.append("</tr>");
			tables.append("<tr>");
			tables.append("<TD width=\"80\" height=\"25\" bgColor=\"#ffffff\" >开发单位</TD><td bgColor=\"#ffffff\" height=\"25\">" + list_one[0][2] + "</td><TD width=\"80\" height=\"25\" bgColor=\"#ffffff\" >土地面积</TD><td bgColor=\"#ffffff\" width=\"290\" height=\"25\">" + list_one[0][3] + "</td>");
			tables.append("</tr>");
			tables.append("<tr>");
			tables.append("<TD width=\"80\" height=\"25\" bgColor=\"#ffffff\" >土地坐落</TD><td bgColor=\"#ffffff\" height=\"25\">" + list_one[0][4] + "</td><TD width=\"80\" height=\"25\" bgColor=\"#ffffff\" >发布时间</TD><td bgColor=\"#ffffff\" width=\"290\" height=\"25\">" + list_one[0][5] + "</td>");
			tables.append("</tr>");
			tables.append("</table>");
			tables.append("</td></tr>");
			tables.append("<tr><td height=\"10\"></td></tr>");
			tables.append("</table>");
		} else if ("blqk2".equals(type)) {
			tables.append("<table width=\"100%\" cellSpacing=\"0\" cellPadding=\"0\" border=\"0\">");
			tables.append("<tr><td height=\"10\"></td></tr>");
			tables.append("<tr><td>");
			tables.append("<table  width=\"100%\" border=\"0\" cellPadding=\"2\"  cellSpacing=\"1\"  bgColor=\"#D2DFE6\">");
			tables.append("<tr>");
			tables.append("<TD align=\"center\" colspan=\"4\" height=\"25\" bgColor=\"#F3F7FA\" >详  细  信  息</TD>");
			tables.append("</tr>");
			list_one = HibernateHelp.queryTwoArray("select projectId,actinstName,sqrMc,startDate,sqrDz,endDate from TBlqk2 where projectId = '" + project + "'");
			// TBlqk2 inf = (TBlqk2) list_one.get(0);
			tables.append("<tr>");
			tables.append("<TD width=\"80\" height=\"25\" bgColor=\"#ffffff\" >项目编号</TD><td bgColor=\"#ffffff\" width=\"300\" height=\"25\">" + list_one[0][0] + "</td><TD width=\"80\" height=\"25\" bgColor=\"#ffffff\" >项目状态</TD><td bgColor=\"#ffffff\" width=\"290\" height=\"25\">" + list_one[0][1] + "</td>");
			tables.append("</tr>");
			tables.append("<tr>");
			tables.append("<TD width=\"80\" height=\"25\" bgColor=\"#ffffff\" >权利人</TD><td bgColor=\"#ffffff\" height=\"25\">" + list_one[0][2] + "</td><TD width=\"80\" height=\"25\" bgColor=\"#ffffff\" >发布日期</TD><td bgColor=\"#ffffff\" width=\"290\" height=\"25\">" + list_one[0][3] + "</td>");
			tables.append("</tr>");
			tables.append("<tr>");
			tables.append("<TD width=\"80\" height=\"25\" bgColor=\"#ffffff\" >土地坐落</TD><td bgColor=\"#ffffff\" height=\"25\">" + list_one[0][4] + "</td><TD width=\"80\" height=\"25\" bgColor=\"#ffffff\" >终止日期</TD><td bgColor=\"#ffffff\" width=\"290\" height=\"25\">" + list_one[0][5] + "</td>");
			tables.append("</tr>");
			tables.append("</table>");
			tables.append("</td></tr>");
			tables.append("<tr><td height=\"10\"></td></tr>");
			tables.append("</table>");
		} else if ("ckqsp".equals(type)) {
			tables.append("<table cellSpacing=\"0\" cellPadding=\"0\" width=\"100%\" border=\"0\">");
			tables.append("<tr><td height=\"10\"></td></tr>");
			tables.append("<tr><td>");
			tables.append("<table  width=\"100%\" border=\"0\" cellPadding=\"2\"  cellSpacing=\"1\"  bgColor=\"#D2DFE6\">");
			tables.append("<tr>");
			tables.append("<TD align=\"center\" colspan=\"4\" height=\"25\" bgColor=\"#F3F7FA\" >详  细  信  息</TD>");
			tables.append("</tr>");
			list_one = HibernateHelp.queryTwoArray("select licensenno,area,minename,validtime,person,organ,minerals,businesstype from TCkqsp where licensenno = '" + project + "'");
			// TCkqsp inf = (TCkqsp) list_one.get(0);
			tables.append("<tr>");
			tables.append("<TD width=\"80\" height=\"25\" bgColor=\"#ffffff\" >许可证号</TD><td bgColor=\"#ffffff\" width=\"300\" height=\"25\">" + list_one[0][0] + "</td><TD width=\"80\" height=\"25\" bgColor=\"#ffffff\" >矿区面积</TD><td bgColor=\"#ffffff\" width=\"290\" height=\"25\">" + list_one[0][1] + "</td>");
			tables.append("</tr>");
			tables.append("<tr>");
			tables.append("<TD width=\"80\" height=\"25\" bgColor=\"#ffffff\" >矿山名称</TD><td bgColor=\"#ffffff\" height=\"25\">" + list_one[0][2] + "</td><TD width=\"80\" height=\"25\" bgColor=\"#ffffff\" >有限期限</TD><td bgColor=\"#ffffff\" width=\"290\" height=\"25\">" + list_one[0][3] + "</td>");
			tables.append("</tr>");
			tables.append("<tr>");
			tables.append("<TD width=\"80\" height=\"25\" bgColor=\"#ffffff\" >采矿权人</TD><td bgColor=\"#ffffff\" height=\"25\">" + list_one[0][4] + "</td><TD width=\"80\" height=\"25\" bgColor=\"#ffffff\" >发证机关</TD><td bgColor=\"#ffffff\" width=\"290\" height=\"25\">" + list_one[0][5] + "</td>");
			tables.append("</tr>");
			tables.append("<tr>");
			tables.append("<TD width=\"80\" height=\"25\" bgColor=\"#ffffff\" >主矿种</TD><td bgColor=\"#ffffff\" height=\"25\">" + list_one[0][6] + "</td><TD width=\"80\" height=\"25\" bgColor=\"#ffffff\" >登记类型</TD><td bgColor=\"#ffffff\" width=\"290\" height=\"25\">" + list_one[0][7] + "</td>");
			tables.append("</tr>");
			tables.append("</table>");
			tables.append("</td></tr>");
			tables.append("<tr><td height=\"10\"></td></tr>");
			tables.append("</table>");
		}
		ResponseHelp.wirteAjax(response, tables.toString());
		return null;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param command
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getShow_count(HttpServletRequest request, HttpServletResponse response, Object command) throws Exception {
		String type = request.getParameter("type");
		String type2 = request.getParameter("type2");	//是否为纯中文，为czw的时候
		// BaseMgr bmgr = new BaseMgr();
		StringBuilder tables = new StringBuilder();
		Pagination pageInfo;
		Object[][] someinfo;
		String label = "";		//标题名称
		for(int i=0;i<IndexHelp.ywcx_children.length;i++){
			if(type.equals(IndexHelp.ywcx_children[i][2])){
				label = IndexHelp.ywcx_children[i][0];
				break;
			}
		}
		tables.append("<TABLE height=3 cellSpacing=0 cellPadding=0 width=\"100%\" border=0 id=\"table12\"><TBODY><TR><TD></TD></TR></TBODY></TABLE>");
		tables.append("<TABLE class=cente_wzdh height=33 cellSpacing=0 cellPadding=0 width=\"782\" border=0 id=\"table13\"><TBODY>");
		if (!"czw".equals(type2)){
		tables.append("<TR><TD align=left width=4 height=4><IMG height=4 src=\"images/wzdh_dqwz1.jpg\" width=4></TD><TD align=left background=\"images/wzdh_dqwz4.jpg\" colSpan=2></TD><TD align=left width=75 background=\"images/wzdh_dqwz4.jpg\"></TD><TD align=left width=4><IMG height=4  src=\"images/wzdh_dqwz6.jpg\" width=4></TD></TR>");
		tables.append("<TR><TD align=left background=\"images/wzdh_dqwz2.jpg\"></TD><TD align=middle width=39><IMG height=13 src=\"images/wzdh_dqwz_bt.jpg\" width=5></TD><TD align=left width=654>当前位置： <a href=\"index.jsp\">首页</a> &gt;&gt; <a href=\"showpage/fwdt.jsp\">服务大厅</a> &gt;&gt; <a href=\"showpage/pubcount.jsp?type=jsydys\">业务查询</a> &gt;&gt; <a class=\"red1\"  href=\"#\" onclick=\"show_count('"+type+"','"+label+"-宁波市国土资源局');return false;\">"+label+"</a></TD><TD align=left width=77></TD><TD align=left width=4 background=\"images/wzdh_dqwz7.jpg\"></TD></TR>");
		tables.append("<TR><TD align=left height=4><IMG height=4 src=\"images/wzdh_dqwz3.jpg\" width=4></TD><TD align=left background=\"images/wzdh_dqwz5.jpg\" colSpan=2></TD><TD align=left width=75 background=\"images/wzdh_dqwz5.jpg\"></TD><TD align=left width=4><IMG height=4 src=\"images/wzdh_dqwz8.jpg\" width=4></TD></TR>");
		}else{
		tables.append("<TR><TD align=left width=20></TD><TD align=left width=654>当前位置： <a class=\"STYLE9\" href=\"showczw/index.jsp\">首页</a> &gt;&gt; <a class=\"STYLE9\" href=\"showczw/pubcount.jsp?type=fwdt\">服务大厅</a> &gt;&gt; <a class=\"STYLE9\" href=\"showczw/pubcount.jsp?type=fwdt\">业务查询</a> &gt;&gt; <a class=\"STYLE9\"  href=\"#\" onclick=\"show_count('"+type+"','czw');return false;\">"+label+"</a></TD><TD align=left width=77></TD><TD align=left width=4 ></TD></TR>");
		}
		tables.append("</TBODY></TABLE>");
		tables.append("<TABLE><TBODY><TR><TD height=\"5\"></td></tr></TBODY></TABLE>");
		if ("tddjtj".equals(type)) {
			tables.append("<TABLE class=\"center_table2\" cellSpacing=\"0\" cellPadding=\"0\" width=\"100%\" border=\"0\" id=\"table17\"><TBODY>");
			tables.append("<tr><td height=\"360\" align=\"center\" valign=\"top\">");
			tables.append("<div id=\"count_content\">");
			tables.append("<TABLE id=\"ConTable\" cellSpacing=\"1\" cellPadding=\"2\" width=\"750\" bgColor=\"#ffffff\" border=\"0\" id=\"table1\" height=\"360\"><TBODY>");
			tables.append("<tr><td colspan=\"6\" width=\"100%\" height=\"5\" valign=\"top\"></td></tr>");
			tables.append("<tr><td colspan=\"6\" width=\"100%\"  valign=\"top\">");
			tables.append("<table width=\"750\">");
			tables.append("<TR><TD align=\"center\" width=\"115\" bgColor=#4fbef3 height=25><font color=\"#FFFFFF\">登记时间</font></TD><TD align=\"center\" width=\"118\" bgColor=#4fbef3><font color=\"#FFFFFF\">海曙区</font></TD><TD width=\"118\" bgColor=#4fbef3 align=\"center\"><font color=\"#FFFFFF\">江东区</font></TD><TD width=\"118\" bgColor=#4fbef3 align=\"center\"><font color=\"#FFFFFF\">江北区</font></TD><TD width=\"118\" bgColor=#4fbef3 align=\"center\"><font color=\"#FFFFFF\">东钱湖区</font></TD><TD width=\"118\" bgColor=#4fbef3 align=\"center\"><font color=\"#FFFFFF\">总计</font></TD></TR>");
			tables.append("</table>");
			tables.append("</td></tr>");
			tables.append("<tr><td colspan=\"6\" width=\"100%\" height=\"290\" valign=\"top\">");
			pageInfo = new Pagination(request, "select t.fzyf,t.hs,t.jd,t.jb,t.dqh,t.count from T_webtdzstj t order by fzyf desc", 10, false);
			someinfo = pageInfo.getTwoArray();
			request.getSession().setAttribute(LocalConstant.PAGENITION_TEST, pageInfo);
			
			for (int j = 0; j < someinfo.length; j++) {
				tables.append("<table width=\"750\">");
				if (j % 2 == 0) {
					tables.append("<TR bgColor=\"#ffffff\" valign=\"top\">");
				} else {
					tables.append("<TR bgColor=\"#e2f4f8\" valign=\"top\">");
				}
				tables.append("<TD align=\"center\" width=\"115\" height=\"25\">" + someinfo[j][0] + "</TD><TD align=\"center\" height=\"25\" width=\"118\"><p align=\"center\"><font color=\"#575757\">" + someinfo[j][1] + "</font></TD><TD align=\"center\" height=\"25\" width=\"118\"><p align=\"center\"><font color=\"#575757\">" + someinfo[j][2] + "</font></TD><TD align=\"center\" height=\"25\" width=\"118\"><p align=\"center\"><font color=\"#575757\">" + someinfo[j][3] + "</font></TD><TD align=\"center\" height=\"25\" width=\"118\"><p align=\"center\"><font color=\"#575757\">" + someinfo[j][4] + "</font></TD><TD align=\"center\" height=\"25\" width=\"118\"><p align=\"center\"><font color=\"#575757\">" + someinfo[j][5] + "</font></TD></TR></table>");
			}
			tables.append("</td></tr>");
			tables.append("<tr><td>");
			tables.append("<iframe src=\"\" name=\"if1\" id=\"if1\" border=\"0\" width=\"98%\" height=\"30\" align=\"center\" frameborder=\"no\" border=\"0\" marginwidth=\"0\" marginheight=\"0\" scrolling=\"no\" allowtransparency=\"yes\"></iframe>");
			tables.append("</td></tr>");
			tables.append("<tr><td>");
			tables.append("<div id=\"showcontent\" align=\"center\">");
			// 调整位置
			TimeSeries timeseries = new TimeSeries("海曙区", org.jfree.data.time.Month.class);
			TimeSeries timeseries1 = new TimeSeries("江东区", org.jfree.data.time.Month.class);
			TimeSeries timeseries2 = new TimeSeries("江北区", org.jfree.data.time.Month.class);
			TimeSeries timeseries3 = new TimeSeries("东钱湖区", org.jfree.data.time.Month.class);
			TimeSeries timeseries4 = new TimeSeries("全部", org.jfree.data.time.Month.class);
			String fzyf = HibernateHelp.queryTwoArray("select max(fzyf) from T_webtdzstj")[0][0] + "";
			int jcyear = Integer.parseInt(fzyf.substring(0, 4)) - 1;
			String jcfzyf1 = jcyear + fzyf.substring(4, 7);
			String jcfzyf2 = fzyf;
			String sql = "select t.yf,t.nf,t.hs,t.jd,t.jb,t.dqh,t.count from T_webtdzstj t where fzyf>'" + jcfzyf1 + "' and fzyf<='" + jcfzyf2 + "'";
			Object[][] listvt = HibernateHelp.queryTwoArray(sql);
			for (int i = 0; i < listvt.length; i++) {
				// TWebtdzstj vt = (TWebtdzstj) listvt.get(i);
				
				double yf1 = Double.parseDouble(listvt[i][0] + "");
				double nf1 = Double.parseDouble(listvt[i][1] + "");
				int yf = (int) yf1;
				int nf = (int) nf1;
				timeseries.add(new Month(yf, nf), Double.parseDouble(listvt[i][2] + ""));
				timeseries1.add(new Month(yf, nf), Double.parseDouble(listvt[i][3] + ""));
				timeseries2.add(new Month(yf, nf), Double.parseDouble(listvt[i][4] + ""));
				timeseries3.add(new Month(yf, nf), Double.parseDouble(listvt[i][5] + ""));
				timeseries4.add(new Month(yf, nf), Double.parseDouble(listvt[i][6] + ""));
			}
			TimeSeriesCollection timeseriescollection = new TimeSeriesCollection();
			timeseriescollection.addSeries(timeseries);
			timeseriescollection.addSeries(timeseries1);
			timeseriescollection.addSeries(timeseries2);
			timeseriescollection.addSeries(timeseries3);
			timeseriescollection.addSeries(timeseries4);
			timeseriescollection.setXPosition(TimePeriodAnchor.MIDDLE);
			JFreeChart jfreechart = ChartFactory.createTimeSeriesChart("土地登记变化趋势", "Date", "数量(单位：宗)", timeseriescollection, true, true, true);
			XYPlot xyplot = (XYPlot) jfreechart.getPlot();
			xyplot.setBackgroundPaint(Color.lightGray);
			xyplot.setDomainGridlinePaint(Color.white);
			xyplot.setRangeGridlinePaint(Color.white);
			xyplot.setAxisOffset(new RectangleInsets(5D, 5D, 5D, 5D));
			xyplot.setDomainCrosshairVisible(true);
			xyplot.setRangeCrosshairVisible(true);
			org.jfree.chart.renderer.xy.XYItemRenderer xyitemrenderer = xyplot.getRenderer();
			if (xyitemrenderer instanceof XYLineAndShapeRenderer) {
				XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer) xyitemrenderer;
				xylineandshaperenderer.setShapesVisible(true);
				xylineandshaperenderer.setShapesFilled(true);
				xylineandshaperenderer.setBaseItemLabelsVisible(true);
			}
			PeriodAxis periodaxis = new PeriodAxis("日期");
			periodaxis.setTimeZone(TimeZone.getTimeZone("Pacific/Auckland"));
			periodaxis.setAutoRangeTimePeriodClass(org.jfree.data.time.Month.class);
			periodaxis.setMajorTickTimePeriodClass(org.jfree.data.time.Month.class);
			PeriodAxisLabelInfo aperiodaxislabelinfo[] = new PeriodAxisLabelInfo[2];
			aperiodaxislabelinfo[0] = new PeriodAxisLabelInfo(org.jfree.data.time.Month.class, new SimpleDateFormat("MMM"), new RectangleInsets(2D, 2D, 2D, 2D), new Font("SansSerif", 1, 10), Color.blue, false, new BasicStroke(0.0F), Color.lightGray);
			aperiodaxislabelinfo[1] = new PeriodAxisLabelInfo(org.jfree.data.time.Year.class, new SimpleDateFormat("yyyy"));
			periodaxis.setLabelInfo(aperiodaxislabelinfo);
			xyplot.setDomainAxis(periodaxis);
			String filename = ServletUtilities.saveChartAsPNG(jfreechart, 710, 400, null, request.getSession());
			String graphURL = request.getContextPath() + "/DisplayChart?filename=" + filename;
			tables.append("<table  width=\"725\" id=\"table89\">");
			tables.append("<tr><td height=\"10\" colspan=\"2\"></tr>");
			if(!"czw".equals(type2)){
			tables.append("<tr><td>&nbsp;&nbsp;<img height=\"16\" src=\"images/search.gif\" width=\"22\">土地登记查询（按日期查询）</td><td height=\"10\" align=\"right\">从<input class=\"xx_input\" id=\"jccx_jfree1\" name=\"operatedate\" size=\"15\" onclick=\"addCalendar(this)\" readonly=\"true\">&nbsp;&nbsp;&nbsp;&nbsp;到<input class=\"xx_input\" id=\"jccx_jfree2\" name=\"operatedate\" size=\"15\" onclick=\"addCalendar(this)\" readonly=\"true\">&nbsp;&nbsp;&nbsp;&nbsp;<input  type=\"image\" src=\"images/nn01.gif\" border=\"0\" name=\"image\" onclick=\"business_jccx_find('tddjtj','columns_jfree');\">&nbsp;&nbsp;&nbsp;&nbsp;</td></tr>");
			tables.append("<tr><td height=\"10\" colspan=\"2\"></tr>");
			tables.append("<tr><td colspan=\"2\" align=\"center\" id=\"showcontent\">");
			tables.append("<img src=\"" + graphURL + "\" width=\"750\" height=\"400\" border=\"0\">");
			}
			tables.append("</td></tr>");
			tables.append("<tr><td colspan=\"2\" height=\"10\"></td></tr>");
			tables.append("</table>");
			tables.append("</div>");
			tables.append("</td></tr>");
			tables.append("</TBODY></TABLE>");
		} else if ("tddytj".equals(type)) {
			pageInfo = new Pagination(request, "select hsje,jdje,jbje,dqhje,dyze,djmj,djsj  from T_webdytj order by djsj desc", 10, false);
			someinfo = pageInfo.getTwoArray();
			request.getSession().setAttribute(LocalConstant.PAGENITION_TEST, pageInfo);
			
			tables.append("<TABLE class=\"center_table2\" cellSpacing=\"0\" cellPadding=\"0\" width=\"100%\" border=\"0\" id=\"table17\"><TBODY>");
			tables.append("<tr><td height=\"360\" align=\"center\" valign=\"top\">");
			tables.append("<div id=\"count_content\" valign=\"top\">");
			tables.append("<TABLE id=\"ConTable\" cellSpacing=\"1\" cellPadding=\"2\" width=\"98%\" bgColor=\"#ffffff\" border=\"0\" id=\"table1\" height=\"360\"><TBODY>");
			tables.append("<tr><td colspan=\"6\" width=\"100%\" height=\"5\" valign=\"top\"></td></tr>");
			tables.append("<tr><td colspan=\"6\" width=\"100%\"  valign=\"top\">");
			tables.append("<table width=\"750\">");
			tables.append("<TR><TD  align=\"center\" width=\"120\" bgColor=#4fbef3 height=25><font color=\"#FFFFFF\">抵押时间</font></TD><TD align=\"center\" width=\"118\" bgColor=#4fbef3><font color=\"#FFFFFF\">海曙区</font></TD><TD width=\"118\" bgColor=#4fbef3 align=\"center\"><font color=\"#FFFFFF\">江东区</font></TD><TD width=\"118\" bgColor=#4fbef3 align=\"center\"><font color=\"#FFFFFF\">江北区</font></TD><TD width=\"118\" bgColor=#4fbef3 align=\"center\"><font color=\"#FFFFFF\">东钱湖区</font></TD><TD width=\"118\" bgColor=#4fbef3 align=\"center\"><font color=\"#FFFFFF\">总计抵押金额</font></TD></TR>");
			tables.append("</table>");
			tables.append("</td></tr>");
			tables.append("<tr><td colspan=\"6\" width=\"100%\" height=\"290\" valign=\"top\">");
			int z = 0;
			for (int j = 0; j < someinfo.length; j++) {
				// TWebdytj inf = (TWebdytj) someinfo.get(j);
				String hsje = someinfo[j][0] + "";
				int hsid = hsje.indexOf(".");
				if (hsid != -1) {
					hsje = hsje.substring(0, hsid);
				}
				String jdje = someinfo[j][1] + "";
				int jdid = jdje.indexOf(".");
				if (jdid != -1) {
					jdje = jdje.substring(0, jdid);
				}
				String jbje = someinfo[j][2] + "";
				int jbid = jbje.indexOf(".");
				if (jbid != -1) {
					jbje = jbje.substring(0, jbid);
				}
				String dqhje = someinfo[j][3] + "";
				int dqhid = dqhje.indexOf(".");
				if (dqhid != -1) {
					dqhje = dqhje.substring(0, dqhid);
				}
				String dyze = someinfo[j][4] + "";
				int dyzeid = dyze.indexOf(".");
				if (dyzeid != -1) {
					dyze = dyze.substring(0, dyzeid);
				}
				if (z % 2 == 0) {
					tables.append("<table width=\"750\"><TR bgColor=\"#ffffff\" valign=\"top\"><TD align=\"center\" width=\"120\" height=\"25\">" + someinfo[j][6] + "</TD><TD align=\"center\" height=\"25\" width=\"118\"><p align=\"center\"><font color=\"#575757\">" + hsje + "</font></TD><TD align=\"center\" height=\"25\" width=\"118\"><p align=\"center\"><font color=\"#575757\">" + jdje + "</font></TD><TD align=\"center\" height=\"25\" width=\"118\"><p align=\"center\"><font color=\"#575757\">" + jbje + "</font></TD><TD align=\"center\" height=\"25\" width=\"118\"><p align=\"center\"><font color=\"#575757\">" + dqhje + "</font></TD><TD align=\"center\" height=\"25\" width=\"118\"><p align=\"center\"><font color=\"#575757\">" + dyze + "</font></TD></TR></table>");
				} else {
					tables.append("<table width=\"750\"><TR bgColor=\"#e2f4f8\" valign=\"top\"><TD align=\"center\" width=\"120\" height=\"25\">" + someinfo[j][6] + "</TD><TD align=\"center\" height=\"25\" width=\"118\"><p align=\"center\"><font color=\"#575757\">" + hsje + "</font></TD><TD align=\"center\" height=\"25\" width=\"118\"><p align=\"center\"><font color=\"#575757\">" + jdje + "</font></TD><TD align=\"center\" height=\"25\" width=\"118\"><p align=\"center\"><font color=\"#575757\">" + jbje + "</font></TD><TD align=\"center\" height=\"25\" width=\"118\"><p align=\"center\"><font color=\"#575757\">" + dqhje + "</font></TD><TD align=\"center\" height=\"25\" width=\"118\"><p align=\"center\"><font color=\"#575757\">" + dyze + "</font></TD></TR></table>");
				}
				z++;
			}
			tables.append("</td></tr>");
			tables.append("<tr><td>");
			tables.append("<iframe src=\"\" name=\"if1\" id=\"if1\" border=\"0\" width=\"98%\" height=\"30\" align=\"center\" frameborder=\"no\" border=\"0\" marginwidth=\"0\" marginheight=\"0\" scrolling=\"no\" allowtransparency=\"yes\"></iframe>");
			tables.append("</td></tr>");
			tables.append("</TBODY></TABLE>");
			tables.append("</div>");
			tables.append("</td></tr>");
			tables.append("<tr><td>");
			tables.append("<div id=\"showcontent\" align=\"center\">");
			// 调整位置
			TimeSeries timeseries = new TimeSeries("海曙区抵押金额", org.jfree.data.time.Month.class);
			TimeSeries timeseries1 = new TimeSeries("江东区抵押金额", org.jfree.data.time.Month.class);
			TimeSeries timeseries2 = new TimeSeries("江北区抵押金额", org.jfree.data.time.Month.class);
			TimeSeries timeseries3 = new TimeSeries("东钱湖区抵押金额", org.jfree.data.time.Month.class);
			TimeSeries timeseries4 = new TimeSeries("全部抵押金额", org.jfree.data.time.Month.class);
			String fzyf = HibernateHelp.queryTwoArray("select max(djsj) from T_webdytj")[0][0] + "";
			int jcyear = Integer.parseInt(fzyf.substring(0, 4)) - 1;
			String jcfzyf1 = jcyear + fzyf.substring(4, 7);
			String jcfzyf2 = fzyf;
			String sql = "select djsj,hsje,jdje,jbje,dqhje,dyze from T_webdytj where djsj>'" + jcfzyf1 + "' and djsj<='" + jcfzyf2 + "'";
			Object[][] listvt = HibernateHelp.queryTwoArray(sql);
			for (int i = 0; i < listvt.length; i++) {
				// TWebdytj vt = (TWebdytj) listvt.get(i);
				String djsj = (listvt[i][0] + "").trim();
				int nf = Integer.parseInt(djsj.substring(0, 4));
				int yf = Integer.parseInt(djsj.substring(5, 7));
				timeseries.add(new Month(yf, nf), Double.parseDouble(listvt[i][1] + ""));
				timeseries1.add(new Month(yf, nf), Double.parseDouble(listvt[i][2] + ""));
				timeseries2.add(new Month(yf, nf), Double.parseDouble(listvt[i][3] + ""));
				timeseries3.add(new Month(yf, nf), Double.parseDouble(listvt[i][4] + ""));
				timeseries4.add(new Month(yf, nf), Double.parseDouble(listvt[i][5] + ""));
			}
			TimeSeriesCollection timeseriescollection = new TimeSeriesCollection();
			timeseriescollection.addSeries(timeseries);
			timeseriescollection.addSeries(timeseries1);
			timeseriescollection.addSeries(timeseries2);
			timeseriescollection.addSeries(timeseries3);
			timeseriescollection.addSeries(timeseries4);
			timeseriescollection.setXPosition(TimePeriodAnchor.MIDDLE);
			JFreeChart jfreechart = ChartFactory.createTimeSeriesChart("土地抵押统计变化趋势", "Date", "数量(单位：万元)", timeseriescollection, true, true, true);
			XYPlot xyplot = (XYPlot) jfreechart.getPlot();
			xyplot.setBackgroundPaint(Color.lightGray);
			xyplot.setDomainGridlinePaint(Color.white);
			xyplot.setRangeGridlinePaint(Color.white);
			xyplot.setAxisOffset(new RectangleInsets(5D, 5D, 5D, 5D));
			xyplot.setDomainCrosshairVisible(true);
			xyplot.setRangeCrosshairVisible(true);
			org.jfree.chart.renderer.xy.XYItemRenderer xyitemrenderer = xyplot.getRenderer();
			if (xyitemrenderer instanceof XYLineAndShapeRenderer) {
				XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer) xyitemrenderer;
				xylineandshaperenderer.setShapesVisible(true);
				xylineandshaperenderer.setShapesFilled(true);
				xylineandshaperenderer.setBaseItemLabelsVisible(true);
			}

			PeriodAxis periodaxis = new PeriodAxis("日期");
			periodaxis.setTimeZone(TimeZone.getTimeZone("Pacific/Auckland"));
			periodaxis.setAutoRangeTimePeriodClass(org.jfree.data.time.Month.class);
			periodaxis.setMajorTickTimePeriodClass(org.jfree.data.time.Month.class);
			PeriodAxisLabelInfo aperiodaxislabelinfo[] = new PeriodAxisLabelInfo[2];
			aperiodaxislabelinfo[0] = new PeriodAxisLabelInfo(org.jfree.data.time.Month.class, new SimpleDateFormat("MMM"), new RectangleInsets(2D, 2D, 2D, 2D), new Font("SansSerif", 1, 10), Color.blue, false, new BasicStroke(0.0F), Color.lightGray);
			aperiodaxislabelinfo[1] = new PeriodAxisLabelInfo(org.jfree.data.time.Year.class, new SimpleDateFormat("yyyy"));
			periodaxis.setLabelInfo(aperiodaxislabelinfo);
			xyplot.setDomainAxis(periodaxis);
			String filename = ServletUtilities.saveChartAsPNG(jfreechart, 710, 400, null, request.getSession());
			String graphURL = request.getContextPath() + "/DisplayChart?filename=" + filename;
			tables.append("<table  width=\"725\" id=\"table89\">");
			tables.append("<tr><td height=\"10\" colspan=\"2\"></tr>");
			if(!"czw".equals(type2)){
			tables.append("<tr><td>&nbsp;&nbsp;<img height=\"16\" src=\"images/search.gif\" width=\"22\">土地抵押查询（按日期查询）</td><td height=\"10\" align=\"right\">从<input class=\"xx_input\" id=\"jccx_jfree1\" name=\"operatedate\" size=\"15\" onclick=\"addCalendar(this)\" readonly=\"true\">&nbsp;&nbsp;&nbsp;&nbsp;到<input class=\"xx_input\" id=\"jccx_jfree2\" name=\"operatedate\" size=\"15\" onclick=\"addCalendar(this)\" readonly=\"true\">&nbsp;&nbsp;&nbsp;&nbsp;<input  type=\"image\" src=\"images/nn01.gif\" border=\"0\" name=\"image\" onclick=\"business_jccx_find('tddytj','columns_jfree');\">&nbsp;&nbsp;&nbsp;&nbsp;</td></tr>");
			tables.append("<tr><td height=\"10\" colspan=\"2\"></tr>");
			tables.append("<tr><td colspan=\"2\" align=\"center\">");
			tables.append("<img src=\"" + graphURL + "\" width=\"750\" height=\"400\" border=\"0\">");
			}
			tables.append("</td></tr>");
			tables.append("<tr><td colspan=\"2\" height=\"10\"></td></tr>");
			tables.append("</table>");
			tables.append("</div>");
			tables.append("</td></tr>");
			tables.append("</TBODY></TABLE>");
		}else if ("tdsyqdjgg".equals(type)) {
			/**
			 * 使用Pagination替换原分页hl
			 */
			pageInfo = new Pagination(request, "select pubnumber,username,position,area from Landpubinfo order by id desc", 10, false);
			someinfo = pageInfo.getTwoArray();
			request.getSession().setAttribute(LocalConstant.PAGENITION_TEST, pageInfo);

			tables.append("<tr><td height=\"485\" align=\"center\">");
			tables.append("<div id=\"columns_content\">");
			tables.append("<TABLE id=\"ConTable\" cellSpacing=\"1\" cellPadding=\"2\" width=\"98%\" bgColor=\"#ffffff\" border=\"0\" id=\"table1\" height=\"479\"><TBODY>");
			tables.append("<TR><TD align=\"center\" width=\"160\" bgColor=#4fbef3 height=19><font color=\"#FFFFFF\">地号</font></TD><TD align=\"center\" width=\"190\" bgColor=#4fbef3><font color=\"#FFFFFF\">权利人</font></TD><TD width=\"45\" bgColor=#4fbef3 align=\"center\"><font color=\"#FFFFFF\">面积</font></TD><TD width=\"315\" bgColor=#4fbef3 align=\"center\"><font color=\"#FFFFFF\">土地坐落</font></TD></TR>");
			tables.append("<tr><td colspan=\"4\" width=\"100%\" height=\"375\" valign=\"top\">");
			tables.append("<input type=\"hidden\" name=\"typeG\" id=\"typeG\" value=\"tdsyqdjgg\">");
			listTddjgj(someinfo, tables);

			// 加入iframe
			tables.append("<TR><TD align=\"right\" bgColor=\"#ffffff\" colSpan=\"5\" height=\"26\">");
			tables.append("<iframe src=\"public/pagination.jsp?pagination=PAGENITION_TEST\" name=\"if1\" id=\"if1\" border=\"0\" width=\"98%\" height=\"30\" align=\"center\" frameborder=\"no\" border=\"0\" marginwidth=\"0\" marginheight=\"0\" scrolling=\"no\" allowtransparency=\"yes\"></iframe>");
			tables.append("</td></tr>");

			tables.append("</TBODY></TABLE>");
			tables.append("</div>");
			tables.append("</td></tr>");
			tables.append("<tr><td>");
			tables.append("<div id=\"columns_jfree\" align=\"center\">");
			tables.append("<table  width=\"725\"  id=\"table89\">");
			tables.append("<tr><td height=\"10\" colspan=\"2\"></td></tr>");
			tables.append("<tr><td>&nbsp;&nbsp;<img height=\"16\" src=\"images/search.gif\" width=\"22\">土地使用权登记公告查询</td><td height=\"10\" align=\"right\">地号<input class=\"xx_input\" id=\"jccx_jfree1\" name=\"operatedate\" size=\"18\">&nbsp;&nbsp;&nbsp;&nbsp;权利人<input class=\"xx_input\" id=\"jccx_jfree2\" name=\"operatedate\" size=\"18\">&nbsp;&nbsp;&nbsp;&nbsp;<input  type=\"image\" src=\"images/nn01.gif\" border=\"0\" name=\"image\" onclick=\"business_jccx_find('tdsyqdjgg','columns_cxtj');\">&nbsp;&nbsp;&nbsp;&nbsp;</td></tr>");
			tables.append("<tr><td height=\"10\" colspan=\"2\"></td></tr>");
			tables.append("<tr><td colspan=\"2\"><div id=\"columns_cxtj\" align=\"center\"></div></td></tr>");
			tables.append("<tr><td height=\"10\" colspan=\"2\"></td></tr>");
			tables.append("</table>");
			tables.append("</div>");
			tables.append("</td></tr>");
		} else if ("tddjjg".equals(type)) {	
			/**
			 * 使用Pagination替换原分页hl
			 */
			pageInfo = new Pagination(request, "select tdzh,zdh,qlr,tdzl,tdmj from Blqk order by id desc", 10, false);
			someinfo = pageInfo.getTwoArray();
			request.getSession().setAttribute(LocalConstant.PAGENITION_TEST, pageInfo);
			tables.append("<tr><td height=\"485\" align=\"center\">");
			tables.append("<div id=\"columns_content\">");
			tables.append("<TABLE id=\"ConTable\" cellSpacing=\"1\" cellPadding=\"2\" width=\"98%\" bgColor=\"#ffffff\" border=\"0\" id=\"table1\" height=\"479\"><TBODY>");
			
			tables.append("<TR><TD align=\"center\" width=\"170\" bgColor=#4fbef3 height=19><font color=\"#FFFFFF\">土地证号</font></TD><TD align=\"center\" width=\"170\" bgColor=#4fbef3><font color=\"#FFFFFF\">宗地号</font></TD><TD align=\"center\" width=\"170\" bgColor=#4fbef3><font color=\"#FFFFFF\">权利人</font></TD><TD width=\"100\" bgColor=#4fbef3 align=\"center\"><font color=\"#FFFFFF\">土地面积(平方米)</font></TD><TD width=\"320\" bgColor=#4fbef3 align=\"center\"><font color=\"#FFFFFF\">土地座落</font></TD></TR>");
			tables.append("<tr><td colspan=\"5\" width=\"100%\" height=\"375\" valign=\"top\">");
			// 土地办理进程
			listTdbljc(someinfo, tables);
			// 分页条
			// 加入iframe
			tables.append("<TR><TD align=\"right\" bgColor=\"#ffffff\" colSpan=\"5\" height=\"26\">");
			tables.append("<iframe src=\"public/pagination.jsp?pagination=PAGENITION_TEST\" name=\"if1\" id=\"if1\" border=\"0\" width=\"98%\" height=\"30\" align=\"center\" frameborder=\"no\" border=\"0\" marginwidth=\"0\" marginheight=\"0\" scrolling=\"no\" allowtransparency=\"yes\"></iframe>");
			tables.append("</td></tr>");

			tables.append("</TBODY></TABLE>");
			tables.append("</div>");
			tables.append("</td></tr>");
			tables.append("<tr><td>");
			tables.append("<div id=\"columns_jfree\" align=\"center\">");

			tables.append("<table  width=\"725\" id=\"table89\">");
			tables.append("<tr><td height=\"10\" colspan=\"2\"></td></tr>");
			// 搜索栏
			tables.append("<tr><td>&nbsp;&nbsp;<img height=\"16\" src=\"images/search.gif\" width=\"22\">土地登记结果查询</td><td height=\"10\" align=\"right\">土地证号:<input class=\"xx_input\" id=\"jccx_jfree1\" name=\"operatedate\" size=\"18\">&nbsp;&nbsp;&nbsp;&nbsp;权利人:<input class=\"xx_input\" id=\"jccx_jfree2\" name=\"operatedate\" size=\"18\">&nbsp;&nbsp;&nbsp;&nbsp;<input  type=\"image\" src=\"images/nn01.gif\" border=\"0\" name=\"image\" onclick=\"business_jccx_find('tddjjg','columns_cxtj');\">&nbsp;&nbsp;&nbsp;&nbsp;</td></tr>");
			tables.append("<tr><td height=\"10\" colspan=\"2\"></td></tr>");
			tables.append("<tr><td colspan=\"2\"><div id=\"columns_cxtj\" align=\"center\"></div></td></tr>");
			tables.append("<tr><td height=\"10\" colspan=\"2\"></td></tr>");
			tables.append("</table>");
			tables.append("</div>");
			tables.append("</td></tr>");
		} else {			
			showdetail(request, response, command, tables);
		}
		ResponseHelp.wirteAjax(response, tables.toString());
		return null;
	}
	private void listTdbljc(Object[][] someinfo, StringBuilder tables) {
		int z = 0;
		String color = "#ffffff";
		for (int j = 0; j < someinfo.length; j++) {
			String tdzh = someinfo[j][0] + "".trim();
			String zdh = someinfo[j][1] + "".trim();

			
			String qlr = someinfo[j][2] + "";

			String tdzl = someinfo[j][3] + "";
			String tdmj = someinfo[j][4]+"";
			
			
			if (z % 2 != 0) {// 土地登记结果
				color = "#e2f4f8";
			}
			z++;
			tables.append("<table><TR bgColor=\"" + color + "\"><TD align=\"center\" width=\"170\"height=\"28\"><a href=\"#\" onclick=\"business_jccx_find_1('"+tdzh+"','"+qlr+"','tddjjg');return false;\">" + tdzh + "</a></TD><TD align=\"left\" height=\"28\" width=\"170\"><font color=\"#575757\">" + zdh + "</font></TD><TD align=\"left\" height=\"28\" width=\"170\"><font color=\"#575757\">" + qlr + "</font></TD><TD align=\"left\" height=\"28\" width=\"100\"><font color=\"#575757\">" + tdmj + "</font></TD><TD align=\"left\" height=\"28\" width=\"320\"><font color=\"#575757\">" + tdzl + "</font></TD></TR></table>");
		}
		tables.append("</td></tr>");

	}


	private void listTddjgj(Object[][] someinfo, StringBuilder tables) {
		int z = 0;
		for (int j = 0; j < someinfo.length; j++) {
			String pubnumber = (someinfo[j][0] + "").trim();
			int pubnumberid = pubnumber.length();
			String pubnumber1 = pubnumber.substring(0, 6) + "-**-**" + pubnumber.substring(pubnumberid - 2, pubnumberid);
			String username = someinfo[j][1] + "".trim();

			if (username.length() > 15) {
				username = username.substring(0, 15);
			}
			String position = (someinfo[j][2] + "").trim();
			String area = (someinfo[j][3] + "").trim();
			if (position.length() > 22) {
				position = position.substring(0, 22);
			} else {

			}
			if (z % 2 == 0) {
				listTddjgj("#ffffff", pubnumber, pubnumber1, username, area, position, tables);
			} else {
				listTddjgj("#e2f4f8", pubnumber, pubnumber1, username, area, position, tables);
			}
			z++;
		}
		tables.append("</td></tr>");

	}

	/**
	 * 土地登记公告的表格
	 * 
	 * @param color
	 *            背景色
	 * @param pubnumber地号
	 * @param pubnumber1
	 *            隐藏中间几位的地号

	 * @param username
	 *            姓名
	 * @param area
	 *            地区
	 * @param position
	 *            位置
	 * @param tables
	 */
	private void listTddjgj(String color, String pubnumber, String pubnumber1, String username, String area, String position, StringBuilder tables) {
		tables.append("<table><TR bgColor=\"" + color + "\"><TD align=\"center\" width=\"160\"height=\"28\"><a href=\"#\" onclick=\"business_jccx_find_1('" + pubnumber + "','" + username + "','tdsyqdjgg','columns_cxtj');return false;\">" + pubnumber1 + "</a></TD><TD align=\"left\" height=\"28\" width=\"170\" style=\"padding-left:30px;\"><font color=\"#575757\">" + username + "</font></TD><TD align=\"left\" height=\"28\" width=\"65\"><font color=\"#575757\">" + area + "</font></TD><TD align=\"left\" height=\"28\" width=\"315\"><font color=\"#575757\">" + position + "</font></TD></TR></table>");

	}

	

	public ModelAndView getCount_jfree(HttpServletRequest request, HttpServletResponse response, Object command) throws Exception {
		String type = request.getParameter("type");
		String jccx1 = request.getParameter("jccx1");
		String jccx2 = request.getParameter("jccx2");
		// BaseMgr bmgr = new BaseMgr();
		StringBuilder tables = new StringBuilder();
		System.out.println(type);
		if ("tddjtj".equals(type)) {
			TimeSeries timeseries = new TimeSeries("海曙区", org.jfree.data.time.Month.class);
			TimeSeries timeseries1 = new TimeSeries("江东区", org.jfree.data.time.Month.class);
			TimeSeries timeseries2 = new TimeSeries("江北区", org.jfree.data.time.Month.class);
			TimeSeries timeseries3 = new TimeSeries("东钱湖区", org.jfree.data.time.Month.class);
			TimeSeries timeseries4 = new TimeSeries("全部", org.jfree.data.time.Month.class);
			jccx1 = jccx1.substring(0, 7);
			jccx2 = jccx2.substring(0, 7);
			String sql = "select t.yf,t.nf,t.hs,t.jd,t.jb,t.dqh,t.count  from T_webtdzstj t where t.fzyf>'" + jccx1 + "' and t.fzyf<='" + jccx2 + "'";
			Object[][] listvt = HibernateHelp.queryTwoArray(sql);
			for (int i = 0; i < listvt.length; i++) {
				// TWebtdzstj vt = (TWebtdzstj) listvt.get(i);
				double yf1 = Double.parseDouble(listvt[i][0] + "");
				double nf1 = Double.parseDouble(listvt[i][1] + "");
				int yf = (int) yf1;
				int nf = (int) nf1;
				timeseries.add(new Month(yf, nf), Double.parseDouble(listvt[i][2] + ""));
				timeseries1.add(new Month(yf, nf), Double.parseDouble(listvt[i][3] + ""));
				timeseries2.add(new Month(yf, nf), Double.parseDouble(listvt[i][4] + ""));
				timeseries3.add(new Month(yf, nf), Double.parseDouble(listvt[i][5] + ""));
				timeseries4.add(new Month(yf, nf), Double.parseDouble(listvt[i][6] + ""));
			}
			TimeSeriesCollection timeseriescollection = new TimeSeriesCollection();
			timeseriescollection.addSeries(timeseries);
			timeseriescollection.addSeries(timeseries1);
			timeseriescollection.addSeries(timeseries2);
			timeseriescollection.addSeries(timeseries3);
			timeseriescollection.addSeries(timeseries4);
			timeseriescollection.setXPosition(TimePeriodAnchor.MIDDLE);
			JFreeChart jfreechart = ChartFactory.createTimeSeriesChart("土地登记变化趋势", "Date", "数量(单位：宗)", timeseriescollection, true, true, true);
			XYPlot xyplot = (XYPlot) jfreechart.getPlot();
			xyplot.setBackgroundPaint(Color.lightGray);
			xyplot.setDomainGridlinePaint(Color.white);
			xyplot.setRangeGridlinePaint(Color.white);
			xyplot.setAxisOffset(new RectangleInsets(5D, 5D, 5D, 5D));
			xyplot.setDomainCrosshairVisible(true);
			xyplot.setRangeCrosshairVisible(true);
			org.jfree.chart.renderer.xy.XYItemRenderer xyitemrenderer = xyplot.getRenderer();
			if (xyitemrenderer instanceof XYLineAndShapeRenderer) {
				XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer) xyitemrenderer;
				xylineandshaperenderer.setShapesVisible(true);
				xylineandshaperenderer.setShapesFilled(true);
				xylineandshaperenderer.setBaseItemLabelsVisible(true);
			}
			PeriodAxis periodaxis = new PeriodAxis("日期");
			periodaxis.setTimeZone(TimeZone.getTimeZone("Pacific/Auckland"));
			periodaxis.setAutoRangeTimePeriodClass(org.jfree.data.time.Month.class);
			periodaxis.setMajorTickTimePeriodClass(org.jfree.data.time.Month.class);
			PeriodAxisLabelInfo aperiodaxislabelinfo[] = new PeriodAxisLabelInfo[2];
			aperiodaxislabelinfo[0] = new PeriodAxisLabelInfo(org.jfree.data.time.Month.class, new SimpleDateFormat("MMM"), new RectangleInsets(2D, 2D, 2D, 2D), new Font("SansSerif", 1, 10), Color.blue, false, new BasicStroke(0.0F), Color.lightGray);
			aperiodaxislabelinfo[1] = new PeriodAxisLabelInfo(org.jfree.data.time.Year.class, new SimpleDateFormat("yyyy"));
			periodaxis.setLabelInfo(aperiodaxislabelinfo);
			xyplot.setDomainAxis(periodaxis);
			String filename = ServletUtilities.saveChartAsPNG(jfreechart, 710, 400, null, request.getSession());
			String graphURL = request.getContextPath() + "/DisplayChart?filename=" + filename;

			tables.append("<table  width=\"725\" id=\"table89\">");
			tables.append("<tr><td height=\"10\" colspan=\"2\"></tr>");
			tables.append("<tr><td>&nbsp;&nbsp;<img height=\"16\" src=\"images/search.gif\" width=\"22\">土地登记查询（按日期查询）</td><td height=\"10\" align=\"right\">从<input class=\"xx_input\" id=\"jccx_jfree1\" name=\"operatedate\" size=\"15\" onclick=\"addCalendar(this)\" readonly=\"true\">&nbsp;&nbsp;&nbsp;&nbsp;到<input class=\"xx_input\" id=\"jccx_jfree2\" name=\"operatedate\" size=\"15\" onclick=\"addCalendar(this)\" readonly=\"true\">&nbsp;&nbsp;&nbsp;&nbsp;<input  type=\"image\" src=\"images/nn01.gif\" border=\"0\" name=\"image\" onclick=\"business_jccx_find('tddytj','columns_jfree');\">&nbsp;&nbsp;&nbsp;&nbsp;</td></tr>");
			tables.append("<tr><td height=\"10\" colspan=\"2\"></tr>");
			tables.append("<tr><td colspan=\"2\" align=\"center\">");
			tables.append("<img src=\"" + graphURL + "\" width=\"710\" height=\"400\" border=\"0\">");
			tables.append("</td></tr>");
			tables.append("<tr><td colspan=\"2\" height=\"10\"></td></tr>");
			tables.append("</table>");
		} else if ("tddytj".equals(type)) {
			TimeSeries timeseries = new TimeSeries("海曙区抵押金额", org.jfree.data.time.Month.class);
			TimeSeries timeseries1 = new TimeSeries("江东区抵押金额", org.jfree.data.time.Month.class);
			TimeSeries timeseries2 = new TimeSeries("江北区抵押金额", org.jfree.data.time.Month.class);
			TimeSeries timeseries3 = new TimeSeries("东钱湖区抵押金额", org.jfree.data.time.Month.class);
			TimeSeries timeseries4 = new TimeSeries("全部抵押金额", org.jfree.data.time.Month.class);
			jccx1 = jccx1.substring(0, 7);
			jccx2 = jccx2.substring(0, 7);
			String sql = "select djsj,hsje,jdje,jbje,dqhje,dyze  from T_webdytj where djsj>='" + jccx1 + "' and djsj<='" + jccx2 + "'";
			Object[][] listvt = HibernateHelp.queryTwoArray(sql);
			for (int i = 0; i < listvt.length; i++) {
				// TWebdytj vt = (TWebdytj) listvt.get(i);
				String djsj = (listvt[i][0] + "").trim();
				int nf = Integer.parseInt(djsj.substring(0, 4));
				int yf = Integer.parseInt(djsj.substring(5, 7));
				timeseries.add(new Month(yf, nf), Double.parseDouble(listvt[i][1] + ""));
				timeseries1.add(new Month(yf, nf), Double.parseDouble(listvt[i][2] + ""));
				timeseries2.add(new Month(yf, nf), Double.parseDouble(listvt[i][3] + ""));
				timeseries3.add(new Month(yf, nf), Double.parseDouble(listvt[i][4] + ""));
				timeseries4.add(new Month(yf, nf), Double.parseDouble(listvt[i][5] + ""));
			}
			TimeSeriesCollection timeseriescollection = new TimeSeriesCollection();
			timeseriescollection.addSeries(timeseries);
			timeseriescollection.addSeries(timeseries1);
			timeseriescollection.addSeries(timeseries2);
			timeseriescollection.addSeries(timeseries3);
			timeseriescollection.addSeries(timeseries4);
			timeseriescollection.setXPosition(TimePeriodAnchor.MIDDLE);

			JFreeChart jfreechart = ChartFactory.createTimeSeriesChart("土地抵押统计变化趋势", "Date", "数量(单位：万元)", timeseriescollection, true, true, true);

			XYPlot xyplot = (XYPlot) jfreechart.getPlot();
			xyplot.setBackgroundPaint(Color.lightGray);
			xyplot.setDomainGridlinePaint(Color.white);
			xyplot.setRangeGridlinePaint(Color.white);
			xyplot.setAxisOffset(new RectangleInsets(5D, 5D, 5D, 5D));
			xyplot.setDomainCrosshairVisible(true);
			xyplot.setRangeCrosshairVisible(true);
			org.jfree.chart.renderer.xy.XYItemRenderer xyitemrenderer = xyplot.getRenderer();
			if (xyitemrenderer instanceof XYLineAndShapeRenderer) {
				XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer) xyitemrenderer;
				xylineandshaperenderer.setShapesVisible(true);
				xylineandshaperenderer.setShapesFilled(true);
				xylineandshaperenderer.setBaseItemLabelsVisible(true);
			}

			PeriodAxis periodaxis = new PeriodAxis("日期");
			periodaxis.setTimeZone(TimeZone.getTimeZone("Pacific/Auckland"));
			periodaxis.setAutoRangeTimePeriodClass(org.jfree.data.time.Month.class);
			periodaxis.setMajorTickTimePeriodClass(org.jfree.data.time.Month.class);
			PeriodAxisLabelInfo aperiodaxislabelinfo[] = new PeriodAxisLabelInfo[2];
			aperiodaxislabelinfo[0] = new PeriodAxisLabelInfo(org.jfree.data.time.Month.class, new SimpleDateFormat("MMM"), new RectangleInsets(2D, 2D, 2D, 2D), new Font("SansSerif", 1, 10), Color.blue, false, new BasicStroke(0.0F), Color.lightGray);
			aperiodaxislabelinfo[1] = new PeriodAxisLabelInfo(org.jfree.data.time.Year.class, new SimpleDateFormat("yyyy"));
			periodaxis.setLabelInfo(aperiodaxislabelinfo);
			xyplot.setDomainAxis(periodaxis);
			String filename = ServletUtilities.saveChartAsPNG(jfreechart, 710, 400, null, request.getSession());
			String graphURL = request.getContextPath() + "/DisplayChart?filename=" + filename;
			tables.append("<table  width=\"725\" id=\"table89\">");
			tables.append("<tr><td height=\"10\" colspan=\"2\"></td></tr>");
			tables.append("<tr><td>&nbsp;&nbsp;<img height=\"16\" src=\"images/search.gif\" width=\"22\">土地抵押查询（按日期查询）</td><td height=\"10\" align=\"right\">从<input class=\"xx_input\" id=\"jccx_jfree1\" name=\"operatedate\" size=\"15\" onclick=\"addCalendar(this)\" readonly=\"true\">&nbsp;&nbsp;&nbsp;&nbsp;到<input class=\"xx_input\" id=\"jccx_jfree2\" name=\"operatedate\" size=\"15\" onclick=\"addCalendar(this)\" readonly=\"true\">&nbsp;&nbsp;&nbsp;&nbsp;<input  type=\"image\" src=\"images/nn01.gif\" border=\"0\" name=\"image\" onclick=\"business_jccx_find('tddytj','columns_jfree');\">&nbsp;&nbsp;&nbsp;&nbsp;</td></tr>");
			tables.append("<tr><td height=\"10\" colspan=\"2\"></td></tr>");
			tables.append("<tr><td colspan=\"2\" align=\"center\">");
			tables.append("<img src=\"" + graphURL + "\" width=\"710\" height=\"400\" border=\"0\">");
			tables.append("</td></tr>");
			tables.append("<tr><td colspan=\"2\" height=\"10\"></td></tr>");
			tables.append("</table>");
		}
		ResponseHelp.wirteAjax(response, tables.toString());
		return null;
	}
	public ModelAndView getPersonalRecord(HttpServletRequest request, HttpServletResponse response, Object command) throws Exception {
		StringBuilder tables=new StringBuilder();
		String type=RequestHelp.getPaString(request, "type");
		Users user = (Users) request.getSession().getAttribute(UserConfig.ATTRIBUTE_NAME);
		String sql="";
		Object[] infos=null;
		Pagination pa=null;
		if(user==null){
			tables.append("请先登录");
		}else{
			if("jzxx".equals(type)){
				sql="from Record where poster='"+user.getUsername()+ "' and kind=3 order by id desc";
				pa = new Pagination(request,sql,1,true);
				infos = pa.getOneArray();
				tables.append("<TABLE id=\"ConTable\" cellSpcing=\"1\" cellPadding=\"2\" width=\"98%\" bgColor=\"#ffffff\" border=\"0\" id=\"table1\" height=\"470\"><TBODY><TR><TD align=\"center\" width=\"32\" bgColor=#4fbef3 height=19></TD><TD align=\"center\" width=\"102\" bgColor=#4fbef3><font color=\"#FFFFFF\">答复情况</font></TD><TD align=\"center\" width=\"204\" bgColor=#4fbef3><font color=\"#FFFFFF\">标题</font></TD><TD align=\"center\" width=\"200\" bgColor=#4fbef3><font color=\"#FFFFFF\">编号</font></TD><TD align=\"center\" width=\"102\" bgColor=#4fbef3><font color=\"#FFFFFF\">发布时间</font></TD></TR>");
				IndexHelp.getJzxxList(tables, infos);
			}else if("tsjb".equals(type)){
				sql="from Record where poster='"+user.getUsername()+ "' and kind=2 order by id desc";
				pa=new Pagination(request,sql,1,true);
				infos = pa.getOneArray();
				tables.append("<TABLE id=\"ConTable\" cellSpcing=\"1\" cellPadding=\"2\" width=\"98%\" bgColor=\"#ffffff\" border=\"0\" id=\"table1\" height=\"470\"><TBODY><TR><TD align=\"center\" width=\"32\" bgColor=#4fbef3 height=19></TD><TD align=\"center\" width=\"102\" bgColor=#4fbef3><font color=\"#FFFFFF\">答复情况</font></TD><TD align=\"center\" width=\"204\" bgColor=#4fbef3><font color=\"#FFFFFF\">标题</font></TD><TD align=\"center\" width=\"200\" bgColor=#4fbef3><font color=\"#FFFFFF\">编号</font></TD><TD align=\"center\" width=\"102\" bgColor=#4fbef3><font color=\"#FFFFFF\">时间</font></TD></TR>");
				IndexHelp.getTsjbList(tables, infos);
			}else if("jllt".equals(type)){
				sql="from HelloerThread where userid="+user.getId()+" order by posttime desc";
				pa = new Pagination(request,sql,1,true);
				infos=pa.getOneArray();
				tables.append("<TABLE id=\"ConTable\" cellSpcing=\"1\" cellPadding=\"2\" width=\"98%\" bgColor=\"#ffffff\" border=\"0\" id=\"table1\" height=\"470\"><TBODY><TR><TD align=\"center\" width=\"32\" bgColor=#4fbef3 height=19></TD><TD align=\"center\" width=\"102\" bgColor=#4fbef3><font color=\"#FFFFFF\">帖子标题</font></TD><TD align=\"center\" width=\"204\" bgColor=#4fbef3><font color=\"#FFFFFF\">帖子内容</font></TD><TD align=\"center\" width=\"200\" bgColor=#4fbef3><font color=\"#FFFFFF\">所属版块</font></TD><TD align=\"center\" width=\"102\" bgColor=#4fbef3><font color=\"#FFFFFF\">帖子时间</font></TD></TR>");
				IndexHelp.getBBSList(tables, infos);
			}else if("wzpl".equals(type)){
				sql ="from Info_comment where name='"+user.getUsername()+"' and pass='Y' order by replydate desc";
				pa = new Pagination(request,sql,1,true);
				infos=pa.getOneArray();
				tables.append("<TABLE id=\"ConTable\" cellSpcing=\"1\" cellPadding=\"2\" width=\"98%\" bgColor=\"#ffffff\" border=\"0\" id=\"table1\" height=\"470\"><TBODY><TR><TD align=\"center\" width=\"32\" bgColor=#4fbef3 height=19></TD><TD align=\"center\" width=\"102\" bgColor=#4fbef3><font color=\"#FFFFFF\">文章标题</font></TD><TD align=\"center\" width=\"204\" bgColor=#4fbef3><font color=\"#FFFFFF\">评论内容</font></TD><TD align=\"center\" width=\"200\" bgColor=#4fbef3><font color=\"#FFFFFF\">所属栏目</font></TD><TD align=\"center\" width=\"102\" bgColor=#4fbef3><font color=\"#FFFFFF\">评论时间</font></TD></TR>");
				IndexHelp.getCommentList(tables, infos);
				
			}
			request.getSession().setAttribute(LocalConstant.PAGENITION_TEST, pa);
		}
		ResponseHelp.wirteAjax(response, tables.toString());
		return null;
	}
	public ModelAndView getPersonal(HttpServletRequest request, HttpServletResponse response, Object command) throws Exception {
		Users user = (Users) request.getSession().getAttribute(UserConfig.ATTRIBUTE_NAME);
		StringBuilder tables = new StringBuilder();
		if(user!=null){
			tables.append("<table><tr><td><table cellpadding=\"0\" cellspacing=\"0\" width=\"790\" height=\"521\" class=\"center_table2\" id=\"table89\"><tr><td height=\"36\" background=\"images/xzxk1.jpg\" ><table cellpadding=\"0\" cellspacing=\"0\" width=\"723\" height=\"100%\"><tr><td height=\"18\" width=\"136\" background=\"images/dian.jpg\" id=\"td_1\"><p align=\"center\"><span style=\"letter-spacing: 2pt\"><b><font color=\"#FFFFFF\"><a href=\"javascript:void(0)\" onclick=\"show_info('jzxx',1)\"><font color=\"#FFFFFF\">局长信箱</font></a></font></b></span></td><td height=\"18\" width=\"9\"></td><td height=\"18\" width=\"136\" background=\"images/mei.jpg\" id=\"td_2\"><p align=\"center\"><span style=\"letter-spacing: 2pt\"><b><font color=\"#FFFFFF\"><a href=\"javascript:void(0)\" onclick=\"show_info('tsjb',2)\"><font color=\"#FFFFFF\">投诉举报</font></a></font></b></span></td><td height=\"18\" width=\"9\"></td><td height=\"18\" width=\"136\" background=\"images/mei.jpg\" style=\"\" id=\"td_3\"><p align=\"center\"><span style=\"letter-spacing: 2pt\"><b><font color=\"#FFFFFF\"><a href=\"javascript:void(0)\" onclick=\"show_info('jllt',3)\"><font color=\"#FFFFFF\">国土论坛</font></a></font></b></span></td><td height=\"18\" width=\"9\"></td><td height=\"18\" width=\"136\" background=\"images/mei.jpg\" id=\"td_4\"><p align=\"center\"><span style=\"letter-spacing: 2pt\"><b><font color=\"#FFFFFF\"><a href=\"javascript:void(0)\" onclick=\"show_info('wzpl',4)\"><font color=\"#FFFFFF\">文章评论</font></a></font></b></span></td><td height=\"18\" width=\"9\"></td><td height=\"18\" width=\"143\"></td></tr></table></td></tr><tr><td height=\"485\" align=\"center\" ><div id=\"content\" style=\"height: 485\"></div></td></tr></table></td></tr></table></td></tr></table>");
		}else{
			tables.append("<a href=\"login/login.jsp\">请先登录</a>");
		}
		ResponseHelp.wirteAjax(response, tables.toString());
		return null;
	}
	
	/**
	 * 搜索
	 * 
	 * @param request
	 * @param response
	 * @param command
	 * @return
	 * @author 黄磊
	 * @date 2.27
	 * @throws Exception
	 */
	public ModelAndView getInfo_jccx_jfree(HttpServletRequest request, HttpServletResponse response, Object command) throws Exception {
		String type = request.getParameter("type");
		String jccx1 = request.getParameter("jccx1");
		String jccx2 = request.getParameter("jccx2");
		// BaseMgr bmgr=new BaseMgr();
		StringBuilder tables = new StringBuilder();

		/**
		 * 土地使用权登记公告查询
		 */
		if ("tdsyqdjgg".equals(type)) {
			tables.append("<table  width=\"710\" border=\"0\" cellPadding=\"2\"  cellSpacing=\"1\"  bgColor=\"#D2DFE6\">");
			tables.append("<tr>");
			tables.append("<TD align=\"center\" colspan=\"4\" height=\"25\" bgColor=\"#F3F7FA\" >详细信息</TD>");
			tables.append("</tr>");
			// List list=bmgr.getOneObject("from Landpubinfo where
			// pubnumber='"+jccx1+"' and username='"+jccx2+"'");
			Object[][] list = HibernateHelp.queryTwoArray("select pubnumber,username,position,area,usetype,usemethod,mapnum,booktype,pubenddate,enddate from Landpubinfo where pubnumber='" + jccx1 + "' and username='" + jccx2 + "'");

			if (list.length <= 0) {
				tables.append("<tr>");
				tables.append("<TD align=\"center\" width=\"710\" colspan=\"4\" height=\"25\" bgColor=\"#ffffff\" >您输入的查询条件有误</TD>");
				tables.append("</tr>");
			} else {
				// Landpubinfo land=(Landpubinfo)list.get(0);
				String pubnumber = (list[0][0] + "").trim();
				String username = (list[0][1] + "").trim();
				String position = (list[0][2] + "").trim();
				String area = (list[0][3] + "").trim();
				String usetype = (list[0][4] + "").trim();
				String usemethod = (list[0][5] + "").trim();
				String mapnum = (list[0][6] + "").trim();
				String booktype = (list[0][7] + "").trim();
				String pubenddate = (list[0][8] + "").trim();
				String enddate = (list[0][9] + "").trim();

				tables.append("<tr>");
				tables.append("<TD width=\"80\" height=\"25\" bgColor=\"#ffffff\" >地号</TD><td bgColor=\"#ffffff\" width=\"275\" height=\"25\">" + pubnumber + "</td><TD width=\"80\" height=\"25\" bgColor=\"#ffffff\" >权利人</TD><td bgColor=\"#ffffff\" width=\"275\" height=\"25\">" + username + "</td>");
				tables.append("</tr>");
				tables.append("<tr>");
				tables.append("<TD width=\"80\" height=\"25\" bgColor=\"#ffffff\" >土地座落</TD><td bgColor=\"#ffffff\" width=\"275\" height=\"25\">" + position + "</td><TD width=\"80\" height=\"25\" bgColor=\"#ffffff\" >土地面积</TD><td bgColor=\"#ffffff\" width=\"275\" height=\"25\">" + area + "</td>");
				tables.append("</tr>");
				tables.append("<tr>");
				tables.append("<TD width=\"80\" height=\"25\" bgColor=\"#ffffff\" >土地性质</TD><td bgColor=\"#ffffff\" width=\"275\" height=\"25\">" + usetype + "</td><TD width=\"80\" height=\"25\" bgColor=\"#ffffff\" >使用类型</TD><td bgColor=\"#ffffff\" width=\"275\" height=\"25\">" + usemethod + "</td>");
				tables.append("</tr>");
				tables.append("<tr>");
				tables.append("<TD width=\"80\" height=\"25\" bgColor=\"#ffffff\" >登记类型</TD><td bgColor=\"#ffffff\" width=\"275\" height=\"25\">" + booktype + "</td><TD width=\"80\" height=\"25\" bgColor=\"#ffffff\" >图号</TD><td bgColor=\"#ffffff\" width=\"275\" height=\"25\">" + mapnum + "</td>");
				tables.append("</tr>");
				tables.append("<tr>");
				tables.append("<TD width=\"80\" height=\"25\" bgColor=\"#ffffff\" >终止日期</TD><td bgColor=\"#ffffff\" width=\"275\" height=\"25\">" + pubenddate + "</td><TD width=\"80\" height=\"25\" bgColor=\"#ffffff\" >公告日期</TD><td bgColor=\"#ffffff\" width=\"275\" height=\"25\">" + enddate + "</td>");
				tables.append("</tr>");
			}
			tables.append("</table>");
		} else if ("tddjjg".equals(type)) {
			tables.append("<table  width=\"710\" border=\"0\" cellPadding=\"2\"  cellSpacing=\"1\"  bgColor=\"#D2DFE6\">");
			tables.append("<tr>");
			tables.append("<TD align=\"center\" colspan=\"4\" height=\"25\" bgColor=\"#F3F7FA\" >详细信息</TD>");
			tables.append("</tr>");
			Object[][] list = HibernateHelp.queryTwoArray("select tdzh,zdh,qlr,tdzl,tdmj from Blqk where tdzh='" + jccx1 + "' and qlr='" + jccx2 + "'");

			if (list.length <= 0) {
				tables.append("<tr>");
				tables.append("<TD align=\"center\" width=\"710\" colspan=\"4\" height=\"25\" bgColor=\"#ffffff\" >您输入的查询条件有误</TD>");
				tables.append("</tr>");
			} else {
				String tdzh = list[0][0] + "".trim();
				String zdh = list[0][1] + "".trim();
				String qlr = list[0][2] + "".trim();
				String tdzl = list[0][3] + "".trim();
				String tdmj = list[0][4] + "".trim();

				tables.append("<tr>");
				tables.append("<TD width=\"80\" height=\"25\" bgColor=\"#ffffff\" >土地证号</TD><td bgColor=\"#ffffff\" width=\"275\" height=\"25\">" + tdzh + "</td><TD width=\"80\" height=\"25\" bgColor=\"#ffffff\" >宗地号</TD><td bgColor=\"#ffffff\" width=\"275\" height=\"25\">" + zdh + "</td>");
				tables.append("</tr>");
				tables.append("<tr>");
				tables.append("<TD width=\"80\" height=\"25\" bgColor=\"#ffffff\" >权利人</TD><td bgColor=\"#ffffff\" width=\"275\" height=\"25\">" + qlr + "</td><TD width=\"80\" height=\"25\" bgColor=\"#ffffff\" >土地座落</TD><td bgColor=\"#ffffff\" width=\"275\" height=\"25\">" + tdzl + "</td>");
				tables.append("</tr>");
				tables.append("<tr>");
				tables.append("<TD width=\"80\" height=\"25\" bgColor=\"#ffffff\" >土地面积</TD><td bgColor=\"#ffffff\" width=\"275\" height=\"25\">" + tdmj + "</td><td bgColor=\"#ffffff\" width=\"275\" height=\"25\" colspan=\"2\"></td>");
				tables.append("</tr>");
			}
			tables.append("</table>");
		}
		ResponseHelp.wirteAjax(response, tables.toString());
		return null;
	}
}