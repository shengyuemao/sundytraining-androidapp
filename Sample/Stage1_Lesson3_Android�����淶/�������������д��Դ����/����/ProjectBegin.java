/**
 * @author QingchaoShi
 *2010-11-12
 *类说明：整个项目的主线程类
 */

package com.tykmAndroid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class ProjectBegin extends Thread {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public static AutoRequest autoRequest = new AutoRequest();
	/**
	 * update
	 * 用来标示更新过程是否出错，
	 * 初始状态“true”，标示没有出错，
	 * 如果更新过程(xml解析)出现异常该标志位赋值为false，标示更新出错
	 * 每次更新结束以后，复位标志为update为true,保证下次更新时正确的
	 */

	public static boolean update;
	public static boolean OneMinUpdating = false;

	@Override
	public void run() {
		Log.i("ProjectBegin", "ProjectBegin.run()");
		tykmAndroid.tykmAndroid_Entity.updateCalculation();
		try {
			Thread.sleep(60 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (!(Environment.getExternalStorageState()
				.equals(android.os.Environment.MEDIA_MOUNTED))) {
			tykmAndroid.tykmAndroid_Entity.handler.sendEmptyMessage(4);// sd卡不存在
			return;// sd卡不存在直接返回
		}
		if (!NetHandle.getInstance().isNetWorkAvailable())// 网络不可用
		{
			tykmAndroid.tykmAndroid_Entity.handler.sendEmptyMessage(7);
			return;
		}
		update = true; // 复位更新标志
		/********** 以下判断为只有用户没有点击立即更新和立即更新结束时才会调用否则直接执行零点更新 *********/
		/********** 也就是说如果开机一分钟以后用户正在执行立即更新才不再执行此次 ************/
		boolean isAutoupdate;
		SharedPreferences settings;
		settings = tykmAndroid.tykmAndroid_Entity.getSharedPreferences(AutoRequest.SETTING_INFOS, 0);
		String updateMessage = settings.getString("AUTOUPDATE", "noautoupdate");
		if(updateMessage.equals("noautoupdate"))
			isAutoupdate = false;
		else
			isAutoupdate = true;
		HandleAdverEvents.beginAdver(tykmAndroid.tykmAndroid_Entity);
		if ((tykmAndroid.UpdateMenuItem != null && tykmAndroid.UpdateMenuItem.isEnabled())
		|| (tykmAndroid.UpdateMenuItem == null)&&(!AutoRequest.isUpdating)) {
			OneMinUpdating = true;
			if (tykmAndroid.UpdateMenuItem != null)
				tykmAndroid.tykmAndroid_Entity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						tykmAndroid.UpdateMenuItem.setEnabled(true);
					}

				});
			autoRequest.startRequestNow(tykmAndroid.tykmAndroid_Entity,"tykmAndroid");
			OneMinUpdating = false;
			if (tykmAndroid.UpdateMenuItem != null)
				tykmAndroid.tykmAndroid_Entity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						tykmAndroid.UpdateMenuItem.setEnabled(true);
					}

				});
		}
			
		update = true; // 复位更新标志
		if(isAutoupdate){
			autoRequest.startRequestByTime(tykmAndroid.tykmAndroid_Entity);
		}

		// //上传点击率和应用的位置
		// final Timer timer = new Timer();
		// TimerTask task = new TimerTask() {
		// public void run() {
		// if(isTodayUpload())
		// return;
		// //没有更新时才允许上传点击和同步数据
		// if((tykmAndroid.UpdateMenuItem!=null&&tykmAndroid.UpdateMenuItem.isEnabled())||(tykmAndroid.UpdateMenuItem==null))
		// uploadClick();//上传应用和广告的点击率
		// if(firstCat1.SyncDataToService(tykmAndroid.tykmAndroid_Entity))
		// //同步应用的位置到服务器
		// tykmAndroid.tykmAndroid_Entity.runOnUiThread(new Runnable(){
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		// Toast.makeText(tykmAndroid.tykmAndroid_Entity, "同步成功",
		// Toast.LENGTH_SHORT).show();
		// }
		//					
		// });
		// else if(!firstCat1.SyncDataToService(tykmAndroid.tykmAndroid_Entity))
		// //同步应用的位置到服务器
		// tykmAndroid.tykmAndroid_Entity.runOnUiThread(new Runnable(){
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		// Toast.makeText(tykmAndroid.tykmAndroid_Entity, "同步失败",
		// Toast.LENGTH_SHORT).show();
		// }
		//					
		// });
		// }
		// };
		// timer.schedule(task, 1000, 24*60*60*1000);
	}

	/**
	 * @return true:今天已经上传过点击率;false:今天没有上传过点击信息
	 */
	protected static boolean isTodayUpload() {
		Calendar ca = Calendar.getInstance();
		int year = ca.get(Calendar.YEAR);
		int month = ca.get(Calendar.MONTH) + 1;
		int day = ca.get(Calendar.DATE);
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("" + year + month + day);
		String str = strBuf.toString();
		Log.v("date", "" + str + "" + year + "," + month + "," + day);
		int today = Integer.parseInt(str);
		SharedPreferences settings = tykmAndroid.tykmAndroid_Entity
				.getSharedPreferences(AutoRequest.SETTING_INFOS, 0);
		int uploadDate = settings.getInt("uploadDate", 0);
		if (uploadDate == 0)
			return false;
		else {
			if (today - uploadDate > 0)// 没上传过
				return false;
			else
				return true;// 今天已经上传过
		}
	}

	/**
	 * 
	 */
	public static void uploadClick() {
		if (!NetHandle.getInstance().isNetWorkAvailable())// 网络不可用
			return;
//		if(isTodayUpload())//如果今天已经上过点击率则不再上传
//			return;
		SharedPreferences settings = tykmAndroid.tykmAndroid_Entity
				.getSharedPreferences(AutoRequest.SETTING_INFOS, 0);
		String clickcountmessage;// appClick
		String ad_clickcountmessage;// adverClick
		int fee_clickCounts;// feeClick

		clickcountmessage = settings.getString("appClick", "");
		ad_clickcountmessage = settings.getString("adverClick", "");
		fee_clickCounts = settings.getInt("feeClick", 0);
		String[] str = getArrayClickMessages(clickcountmessage);
		String[] str1 = getArrayClickMessages(ad_clickcountmessage);
		if(fee_clickCounts>0)
		{
			String s = Constant.feeClickURL +tykmAndroid.imsi + "&clickNumber="+ fee_clickCounts;
			Log.v("ssss", s+"");
			StringBuffer html = new StringBuffer();
			try {
				URL url = new URL(s);
				HttpURLConnection conn = NetHandle.getInstance().getConnection(url);
				if (conn == null) {
					tykmAndroid.tykmAndroid_Entity.handler
							.sendEmptyMessage(6);// APN设置出错
					return;
				}
				InputStreamReader isr = new InputStreamReader(conn
						.getInputStream());
				BufferedReader br = new BufferedReader(isr);
				String temp;
				while ((temp = br.readLine()) != null) {
					html.append(temp);
				}
				br.close();
				isr.close();
				conn.disconnect();
				if(!html.toString().trim().equals("1"))//上传点击率到服务器失败则直接返回
				{
					return;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			fee_clickCounts = 0;
			settings.edit().putInt("feeClick", fee_clickCounts)
					.commit();
		}
		if (!(clickcountmessage == null || clickcountmessage.equals("") || clickcountmessage
				.equals(" "))) {
			Log.v("timer", "app begin");
			if (str == null) {
				String s = Constant.appClickURL + clickcountmessage;
				StringBuffer html = new StringBuffer();
				try {
					URL url = new URL(s);
					HttpURLConnection conn = NetHandle.getInstance().getConnection(url);
					if (conn == null) {
						tykmAndroid.tykmAndroid_Entity.handler
								.sendEmptyMessage(6);// APN设置出错
						return;
					}
					InputStreamReader isr = new InputStreamReader(conn
							.getInputStream());
					BufferedReader br = new BufferedReader(isr);
					String temp;
					while ((temp = br.readLine()) != null) {
						html.append(temp);
					}
					br.close();
					isr.close();
					conn.disconnect();
					if(!html.toString().trim().equals("1"))//上传点击率到服务器失败则直接返回
					{
						return;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}
				clickcountmessage = "";
				settings.edit().putString("appClick", clickcountmessage)
						.commit();
			} else {
				for (int i = 0; i < str.length; i++) {
					String s = Constant.appClickURL + str[i];
					StringBuffer html = new StringBuffer();
					try {
						URL url = new URL(s);
						HttpURLConnection conn = NetHandle.getInstance().getConnection(url);
						if (conn == null) {
							tykmAndroid.tykmAndroid_Entity.handler
									.sendEmptyMessage(6);// APN设置出错
							return;
						}
						InputStreamReader isr = new InputStreamReader(conn
								.getInputStream());
						BufferedReader br = new BufferedReader(isr);
						String temp;
						while ((temp = br.readLine()) != null) {
							html.append(temp);
						}
						br.close();
						isr.close();
						conn.disconnect();
						if(!html.toString().trim().equals("1"))//上传点击率到服务器失败则直接返回
						{
							return;
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return;
					}
				}
				clickcountmessage = "";
				settings.edit().putString("appClick", clickcountmessage)
						.commit();
			}
		}
		if (!(ad_clickcountmessage == null || ad_clickcountmessage.equals("") || ad_clickcountmessage
				.equals(" "))) {
			Log.v("timer", "adver begin");
			if (str1 == null) {

				String s1 = Constant.adverClickURL + ad_clickcountmessage;
				StringBuffer html1 = new StringBuffer();
				try {
					URL url = new URL(s1);
					HttpURLConnection conn = NetHandle.getInstance().getConnection(url);
					if (conn == null) {
						tykmAndroid.tykmAndroid_Entity.handler
								.sendEmptyMessage(6);// APN设置出错
						return;
					}
					InputStreamReader isr = new InputStreamReader(conn
							.getInputStream());
					BufferedReader br = new BufferedReader(isr);
					String temp;
					while ((temp = br.readLine()) != null) {
						html1.append(temp);
					}
					br.close();
					isr.close();
					conn.disconnect();
					if(!html1.toString().trim().equals("1"))//上传点击率到服务器失败则直接返回
					{
						return;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}
				ad_clickcountmessage = "";
				settings.edit().putString("adverClick", ad_clickcountmessage)
						.commit();
			} else {
				for (int i = 0; i < str1.length; i++) {
					String s1 = Constant.adverClickURL + str1[i];
					StringBuffer html1 = new StringBuffer();
					try {
						URL url = new URL(s1);
						HttpURLConnection conn = NetHandle.getInstance().getConnection(url);
						if (conn == null) {
							tykmAndroid.tykmAndroid_Entity.handler
									.sendEmptyMessage(6);// APN设置出错
							return;
						}
						InputStreamReader isr = new InputStreamReader(conn
								.getInputStream());
						BufferedReader br = new BufferedReader(isr);
						String temp;
						while ((temp = br.readLine()) != null) {
							html1.append(temp);
						}
						br.close();
						isr.close();
						conn.disconnect();
						if(!html1.toString().trim().equals("1"))//上传点击率到服务器失败则直接返回
						{
							return;
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return;
					}
				}
				ad_clickcountmessage = "";
				settings.edit().putString("adverClick", ad_clickcountmessage)
						.commit();
			}

		}
		
        //上传点击率成功把上传的时间写入到SharedPreferences中
		Calendar ca = Calendar.getInstance();
		int year = ca.get(Calendar.YEAR);
		int month = ca.get(Calendar.MONTH) + 1;
		int day = ca.get(Calendar.DATE);
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("" + year + month + day);
		String str_today = strBuf.toString();
		Log.v("date", "" + str + "" + year + "," + month + "," + day);
		int today = Integer.parseInt(str_today);
		settings.edit().putInt("uploadDate", today).commit();
	}

	/**
	 * @param clickcountmessage
	 * @return
	 */
	private static String[] getArrayClickMessages(String clickcountmessage) {
		// TODO Auto-generated method stub
		if (clickcountmessage.length() < 1024) {
			String[] allMessageStr = clickcountmessage.split("\\$");
			// String[] head = new String[2];
			// String[] content = new String[allMessageStr.length-2];
			return null;
		}
		int temp = clickcountmessage.length() / 820;
		String[] allMessageStr = clickcountmessage.split("\\$");
		String[] head = new String[2];
		String[] content = new String[allMessageStr.length - 2];
		for (int k = 0; k < head.length; k++) {
			head[k] = allMessageStr[k];
		}
		for (int m = 0; m < content.length; m++) {
			content[m] = allMessageStr[m + 2];
		}
		int allClickNums = allMessageStr.length - 2;// 总共点击的应用数目
		int everySend = 90;// 每次传送 的应用数目
		int sendTimes;// 传送的次数
		if (allClickNums % everySend == 0)
			sendTimes = allClickNums / everySend;
		else
			sendTimes = allClickNums / everySend + 1;
		String[] strSend = new String[sendTimes];
		for (int i = 0; i < sendTimes; i++) {
			String strTemp = "";
			for (int j = i; j < i + 100; j++) {
				if (strTemp.equals(""))
					strTemp = content[j];
				else
					strTemp = strTemp + "$" + content[j];
			}
			strSend[i] = head[0] + "$" + head[1] + "$" + strTemp;
		}

		return strSend;
	}
}
