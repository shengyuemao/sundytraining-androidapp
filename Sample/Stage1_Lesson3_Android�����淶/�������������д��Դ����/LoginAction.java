package net.ohbuy.admin.action;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.ohbuy.admin.function.model.Function;
import net.ohbuy.admin.function.service.FunctionService;
import net.ohbuy.admin.model.Admin;
import net.ohbuy.admin.model.Log;
import net.ohbuy.admin.service.AdminService;
import net.ohbuy.commons.base.BaseAction;

/**
 * 系统后台登陆
 * @author zhengzhi
 *
 * 2011-1-8 下午04:32:27
 */
public class LoginAction extends BaseAction{

	private String code;//验证码
	private String account;
	private String password;
	
	private AdminService adminService;
	private FunctionService functionService;
	
	@Override
	public String execute() throws Exception {
		String authCode = (String) super.getSession().getAttribute("authCode");
		/*
		if(null == authCode || !authCode.equals(code)){
			message = "验证码输入错误!";
			this.getSession().removeAttribute("rand");
			return ERROR;
		}
		*/
		Admin admin = adminService.checkLogin(account, password);//验证用户名和密码
		
		if (null == admin) {
			message = "用户名或密码不正确";
			return ERROR;
		}
		
		String ip = super.getIp(super.getRequest());
		admin.setLastLoginIp(ip);
		admin.setLastLoginTime(new Date());
		admin.setLoginCount(admin.getLoginCount()+1);
		adminService.saveOrUpdateObject(admin);//修改
		
		super.getSession().setAttribute("admin", admin);//保存session
		
		Set<Function> functionSet = new HashSet<Function>();
		List<Function> functionList = functionService.findFunctionByRole(admin.getRole());
		
		for (Function function : functionList) {
			functionSet.add(function);
		}
		
		this.getSession().setAttribute("functionSet", functionSet);//保存权限session
		
		
		//保存日志
		Log log = new Log();
		log.setActionClassName("/login/login.action");
		log.setActionMethodName("execute()");
		log.setCreateTime(new Date());
		log.setOperator(admin.getName());
		log.setIp(ip);
		adminService.saveOrUpdateObject(log);
		
		return SUCCESS;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public AdminService getAdminService() {
		return adminService;
	}

	public void setAdminService(AdminService adminService) {
		this.adminService = adminService;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public FunctionService getFunctionService() {
		return functionService;
	}

	public void setFunctionService(FunctionService functionService) {
		this.functionService = functionService;
	}

}
