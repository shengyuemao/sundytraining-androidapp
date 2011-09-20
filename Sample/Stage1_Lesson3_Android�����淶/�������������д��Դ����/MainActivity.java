package com.app.poi.yangtse;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.poi.yangtse.constant.Constants;
import com.app.poi.yangtse.dialog.GlobalDialogs;
import com.app.poi.yangtse.entity.Node;
import com.app.poi.yangtse.service.GlobalService;
import com.app.poi.yangtse.util.MyHttpUtil;

public class MainActivity extends ListActivity {
	
	private static final String TAG = "MainActivity";
	
	//主界面视图组件
	private ListView mainListView;
	@SuppressWarnings("unused")
	private Button messageView, newClientView, newGroupView, poiView, exitView;
	
	//软件设置
	private SharedPreferences setting;
	
	//视图操作数据列表
	private ArrayList<Node> nodes = new ArrayList<Node>();
	private ArrayList<Node> nodeAll = new ArrayList<Node>();
	private TreeViewAdapter treeViewAdapter;
	
	//TIMER
	private Timer mainTimer = new Timer();
	private UserLogoutTimerTask userLogoutTimerTask;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "======>onCreate()");
        setContentView(R.layout.main);
        findViews();
        
        //启动后台服务
		startService(new Intent(this, GlobalService.class));
		
        initVariables();
        
        setAdapters();
        
        setListeners();
        
        //设置标题
        setTitle(getTitle() + " - " + GlobalService.userBundle.getString("mId") + "|" + GlobalService.userBundle.getString("mUsername") + "|" + GlobalService.userBundle.getString("mPhoneNumber") + "|在线");
        
        //头像闪动线程
		mainTimer.schedule(new AvatarFlashTimerTask(), 0, 500);
		
		//显示好友请求列表
		if(!GlobalService.userRequestFriends.isEmpty()){
			showDialog(DIALOG_REQUEST_FRIEND_LIST);
		}
    }
    
    private void initVariables(){
    	mainListView = getListView();
    	
    	setting = getSharedPreferences("setting", 0);
    	
        treeViewAdapter = new TreeViewAdapter(this, R.layout.outline, nodes);
    }
    
    private void setAdapters(){
        setListAdapter(treeViewAdapter);
		registerForContextMenu(mainListView);
    }
    
    private int mainListLongClickPosition;
    private void setListeners() {
    	mainListView.setOnItemLongClickListener(new OnItemLongClickListener() {
    		public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
    			mainListLongClickPosition = arg2;
    			
    			int type = nodes.get(arg2).getType();
    			if(type == 1){
    				showDialog(DIALOG_LIST_LONG_CLICK_GROUP);
    			}else if(type == 2){
    				showDialog(DIALOG_LIST_LONG_CLICK_FRIEND);
    			}
    			
    			return false;
    		}
    	});
//    	messageLayout.setEnabled(!userRequestFriends.isEmpty());
//    	
//		messageLayout.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				showDialog(DIALOG_REQUEST_FRIEND_LIST);
//			}
//		});
		newClientView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showDialog(DIALOG_SEARCH_FRIEND);
			}
		});
		newGroupView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showDialog(DIALOG_CREATE_GROUP);
			}
		});
		poiView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), MyFpsListActivity.class);
				intent.putExtras(GlobalService.userBundle);
				startActivity(intent);
			}
		});
		exitView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				new AlertDialog.Builder(MainActivity.this)
				.setTitle("提示信息")
				.setMessage("您确定要退出吗？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						userLogout();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
					}
				})
				.show();
			}
		});
	}
    
    private int count;
	private void requestServerData() {
		count++;
		
    	nodes.clear();
    	nodeAll.clear();
    	
    	GlobalService.userGroups.clear();
    	GlobalService.userGroupNames.clear();
    	GlobalService.userFriends.clear();
    	GlobalService.userFriendIds.clear();
        GlobalService.changedOnlineFriendIds.clear();
        GlobalService.changedOfflineFriendIds.clear();
        GlobalService.userRequestFriends.clear();
        GlobalService.userRequestFriendNames.clear();
        
        String userId = GlobalService.userBundle.getString("mId");
        
        GlobalService.userFriends = searchFriends(userId);
		
		for(String[] friend : GlobalService.userFriends){
			GlobalService.userFriendIds.add(friend[0]);
		}
		
		Map<String, int[]> groupFriendStatus = new HashMap<String, int[]>();
		int[] friendStatus = new int[2];
		
		for(String[] friend : GlobalService.userFriends){
			if(friend[3].equals("0")){
				
				GlobalService.changedOfflineFriendIds.add(friend[0]);
				
				Node node = new Node(2, friend[0], friend[1], true, false, "-2", 1, false, friend, false);
	    		nodeAll.add(node);
	    		
	    		String friendGroupId = friend[5];
				
	    		node = new Node(2, friend[0], friend[1], true, false, friendGroupId, 2, false, friend, false);
	    		nodeAll.add(node);
	    		
	    		int[] status = groupFriendStatus.get(friendGroupId);
	    		if(status == null){
	    			status = new int[2];
	    		}
	    		status[1]++;
	    		groupFriendStatus.put(friendGroupId, status);
	    		
	    		friendStatus[1]++;
			}
		}
		
		for(String[] friend : GlobalService.userFriends){
			if(!friend[3].equals("0")){
				
				GlobalService.changedOnlineFriendIds.add(friend[0]);
				
				Node node = new Node(2, friend[0], friend[1],  true, false, "-2", 1, false, friend, false);
	    		nodeAll.add(node);
	    		
	    		String friendGroupId = friend[5];
	    		
				node = new Node(2, friend[0], friend[1], true, false, friend[5], 2, false, friend, false);
	    		nodeAll.add(node);
	    		
	    		int[] status = groupFriendStatus.get(friendGroupId);
	    		if(status == null){
	    			status = new int[2];
	    		}
	    		status[0]++;
	    		status[1]++; 
	    		groupFriendStatus.put(friendGroupId, status);
	    		
	    		friendStatus[0]++;
	    		friendStatus[1]++;
			}
		}
		
		GlobalService.userGroups = searchGroups(userId);
		for(String[] group : GlobalService.userGroups){
			GlobalService.userGroupNames.add(group[2]);
			int[] status = groupFriendStatus.get(group[0]);
			Node node;
			if(status != null){
				node = new Node(1, group[0], group[2] + " [" + status[0] + "/" + status[1] + "]", true, status[1] > 0, "-1", 1, false, group, false);
			}else{
				node = new Node(1, group[0], group[2] + " [0/0]", true, true, "-1", 1, false, group, false);
			}
    		nodeAll.add(node);
		}
		
		boolean hasFriends = !GlobalService.userFriends.isEmpty();
		Node node = new Node(0, "-2", "全部终端" + " [" + friendStatus[0] + "/" + friendStatus[1] + "]", false, hasFriends, "0", 0, false, null, false);
		nodes.add(node);
		boolean hasGroups = !GlobalService.userGroups.isEmpty();
		node = new Node(0, "-1", "项目分类", false, hasGroups, "0", 0, false, null, false);
		nodes.add(node);
		
		GlobalService.userRequestFriends = searchRequestFriends(GlobalService.userBundle.getString("mId"));
        for(String[] userFriendRequest : GlobalService.userRequestFriends){
        	GlobalService.userRequestFriendNames.add(userFriendRequest[1]);
        }
		
		treeViewAdapter.notifyDataSetChanged();
	}
	
	private List<LinearLayout> avatarViews = new ArrayList<LinearLayout>();
	
	class AvatarFlashTimerTask extends TimerTask {
		private boolean flag = false;
		@Override
		public void run() {
			
			flag = !flag;
			
			Message msg = new Message();
			msg.what = Constants.MainActivity_HANDLER_FRIEND_AVATAR;
			msg.obj = flag;
			uiHandler.sendMessage(msg);
		}
	}

	private String requestLocationfriendId = "";
	@SuppressWarnings("rawtypes")
	private class TreeViewAdapter extends ArrayAdapter {

		private View view;
		
		@SuppressWarnings("unchecked")
		public TreeViewAdapter(Context context, int textViewResourceId, List objects) {
			super(context, textViewResourceId, objects);
			mInflater = LayoutInflater.from(context);
			mfilelist = objects;
			mIconCollapse_0 = BitmapFactory.decodeResource( context.getResources(), R.drawable.outline_list_collapse);
			mIconExpand_0 = BitmapFactory.decodeResource(context.getResources(), R.drawable.outline_list_expand);
			
			mIconCollapse_1 = BitmapFactory.decodeResource( context.getResources(), R.drawable.open2);
			mIconExpand_1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.close2);
			
			friendOnline = BitmapFactory.decodeResource(context.getResources(), R.drawable.emo_im_happy_online);
			friendOffline = BitmapFactory.decodeResource(context.getResources(), R.drawable.emo_im_happy_offline);
			
			mIconLocation1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_locate_once);
			mIconLocation2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_locate_many);
			mIconLocation3 = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_locate_view);
		}

		private LayoutInflater mInflater;
		private List<Node> mfilelist;
		private Bitmap mIconCollapse_0;
		private Bitmap mIconExpand_0;
		
		private Bitmap mIconCollapse_1;
		private Bitmap mIconExpand_1;
		
		private Bitmap friendOnline;
		private Bitmap friendOffline;
		
		private Bitmap mIconLocation1;
		private Bitmap mIconLocation2;
		private Bitmap mIconLocation3;

		public int getCount() {
			return mfilelist.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder;
			/*if (convertView == null) {*/
				convertView = mInflater.inflate(R.layout.outline, null);
				holder = new ViewHolder();
				holder.layout = (RelativeLayout) convertView.findViewById(R.id.outline_layout);
				holder.text = (TextView) convertView.findViewById(R.id.text);
				holder.iconLayout = (LinearLayout) convertView.findViewById(R.id.icon_layout);
				holder.pngIcon = (ImageView) convertView.findViewById(R.id.icon_png);
				
				holder.location1 = (ImageButton) convertView.findViewById(R.id.outline_location1);
				holder.location2 = (ImageButton) convertView.findViewById(R.id.outline_location2);
				holder.location3 = (ImageButton) convertView.findViewById(R.id.outline_location3);
				
				convertView.setTag(holder);
			/*} else {
				holder = (ViewHolder) convertView.getTag();
			}*/
				
			String[] details = mfilelist.get(position).getDetails();
			
			holder.layout.setTag(position);
			holder.layout.setOnTouchListener(new OnTouchListener() {
				public boolean onTouch(View v, MotionEvent event) {
					int pos = (Integer) v.getTag();
					if(mfilelist.get(pos).getType() == 2){
						if(view != null){
							view.setBackgroundColor(0x00000000);
						}
						
						view = v;
						v.setBackgroundColor(0xFFFFCC00);
					}
					
					return false;
				}
			});
			
			int level = mfilelist.get(position).getLevel();
			holder.text.setText(mfilelist.get(position).getOutlineTitle());
			if(mfilelist.get(position).getType() == 2){
				holder.text.append("(" + details[0] + ")");
				holder.text.setTextColor(0xFF000000);
				holder.iconLayout.setPadding((level), holder.iconLayout.getPaddingTop(), 0, holder.iconLayout.getPaddingBottom());
//				holder.layout.setBackgroundColor(0xFFFFFFFF);
			}else if(mfilelist.get(position).getType() == 1){
				holder.iconLayout.setPadding((level), holder.iconLayout.getPaddingTop(), 0, holder.iconLayout.getPaddingBottom());
//				holder.layout.setBackgroundResource(R.drawable.main_bg_list_item);
				holder.text.setTextColor(0xFF0000FF);
			}else if(mfilelist.get(position).getType() == 0){
//				holder.layout.setBackgroundResource(R.drawable.main_bg_list_item);
				holder.text.setTextColor(0xFF0000FF);
			}
			
			if (mfilelist.get(position).isMhasChild() && (mfilelist.get(position).isExpanded() == false)) {
				if(mfilelist.get(position).getType() == 0){
					holder.pngIcon.setImageResource(R.drawable.outline_list_collapse);
				}else{
					holder.pngIcon.setImageResource(R.drawable.open2);
				}
			} else if (mfilelist.get(position).isMhasChild() && (mfilelist.get(position).isExpanded() == true)) {
				if(mfilelist.get(position).getType() == 0){
					holder.pngIcon.setImageResource(R.drawable.outline_list_expand);
				}else{
					holder.pngIcon.setImageResource(R.drawable.close2);
				}
			} else if (!mfilelist.get(position).isMhasChild()){
				if(mfilelist.get(position).getType() == 0){
					holder.pngIcon.setImageResource(R.drawable.outline_list_expand);
				}else{
					holder.pngIcon.setImageResource(R.drawable.open2);
				}
				if(mfilelist.get(position).getType() == 2){
					holder.pngIcon.setImageResource(R.drawable.emo_im_happy_offline);
				}
			}
			if(mfilelist.get(position).getType() == 2){
				if(Integer.parseInt(details[3]) != 0){
					holder.pngIcon.setImageResource(R.drawable.emo_im_happy_online);
					
					holder.location1.setImageBitmap(mIconLocation1);
					holder.location1.setTag(mfilelist.get(position).getId());
					holder.location1.setFocusable(false);
					holder.location1.setFocusableInTouchMode(false);
					holder.location1.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							String userId = GlobalService.userBundle.getString("mId");
							String friendId = v.getTag().toString();
							try {
								String requestCommand = userId + "_" + friendId + "_0_0_0_b";
								GlobalService.globalSocketTimerTask.ps.println(requestCommand);
								Log.d(TAG, "Once locate======>" + requestCommand);
								
							} catch (Exception e) {
							}
						}
					});
					holder.location2.setImageBitmap(mIconLocation2);
					holder.location2.setTag(mfilelist.get(position).getId());
					holder.location2.setFocusable(false);
					holder.location2.setFocusableInTouchMode(false);
					holder.location2.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							requestLocationfriendId = v.getTag().toString();
							Log.d(TAG, "requestLocationfriendId======>" + requestLocationfriendId);
							System.out.println("requestLocationfriendId=="+requestLocationfriendId);
							showDialog(DIALOG_SELECT_LOCATE_TYPE);
						}
					});
					holder.location3.setImageBitmap(mIconLocation3);
					holder.location3.setTag(mfilelist.get(position).getId());
					holder.location3.setFocusable(false);
					holder.location3.setFocusableInTouchMode(false);
					holder.location3.setOnClickListener(new OnClickListener(){
						public void onClick(View v) {
							String friendId = v.getTag().toString();
							Intent intent = new Intent(MainActivity.this, ViewHistoriesActivity.class);
							intent.putExtras(GlobalService.userBundle);
							intent.putExtra("view_history_friend_id", friendId);
							startActivity(intent);
						}
					});
					
					if(Integer.parseInt(details[3]) == 1){
						holder.location1.setVisibility(View.INVISIBLE);
						holder.location2.setVisibility(View.INVISIBLE);
					}
				}else{
					holder.location1.setVisibility(View.INVISIBLE);
					holder.location2.setVisibility(View.INVISIBLE);
					holder.location3.setImageBitmap(mIconLocation3);
					holder.location3.setTag(mfilelist.get(position).getId());
					holder.location3.setFocusable(false);
					holder.location3.setFocusableInTouchMode(false);
					holder.location3.setOnClickListener(new OnClickListener(){
						public void onClick(View v) {
							String friendId = v.getTag().toString();
							Intent intent = new Intent(MainActivity.this, ViewHistoriesActivity.class);
							intent.putExtras(GlobalService.userBundle);
							intent.putExtra("view_history_friend_id", friendId);
							startActivity(intent);
						}
					});
				}
			}else{
				holder.location1.setVisibility(View.INVISIBLE);
				holder.location2.setVisibility(View.INVISIBLE);
				holder.location3.setVisibility(View.INVISIBLE);
			}
			
			if(mfilelist.get(position).isHasNewMessage()){
				avatarViews.add(holder.iconLayout);
			}
			
			return convertView;
		}

		class ViewHolder {
			RelativeLayout layout;
			TextView text;
			LinearLayout iconLayout;
			ImageView pngIcon;

			ImageButton location1;
			ImageButton location2;
			ImageButton location3;
		}
	}

//	private Map<Integer, Integer> positionClickTimes = new HashMap<Integer, Integer>();
	private int mainListClickPosition = -1;
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		if (nodes.get(position).getType() == 2) {
			
			if(mainListClickPosition == position){
				
				mainListClickPosition = position;
				
				Node node = nodes.get(position);
				
				GlobalService.newMessageFriendIds.remove(node.getId());
				
				avatarViews.clear();
				
				for(Node n : nodeAll){
					if(GlobalService.newMessageFriendIds.contains(n.getId())){
						n.setHasNewMessage(true);
					}else{
						n.setHasNewMessage(false);
					}
				}
				treeViewAdapter.notifyDataSetChanged();
				
				Intent intent = new Intent(MainActivity.this, ChartActivity.class); 
				String[] friendInfo = node.getDetails();
				intent.putExtra("friend_info", friendInfo);
				Bundle bundle = new Bundle();
				intent.putExtras(bundle);
				startActivity(intent);
				
				mainListClickPosition = -1;
			}else{
				mainListClickPosition = position;
			}

			return;
		}
		

		if (nodes.get(position).isExpanded()) {
			nodes.get(position).setExpanded(false);
			Node pdfOutlineElement=nodes.get(position);
			ArrayList<Node> temp=new ArrayList<Node>();
			
			for (int i = position+1; i < nodes.size(); i++) {
				if (pdfOutlineElement.getLevel()>=nodes.get(i).getLevel()) {
					break;
				}
				temp.add(nodes.get(i));
			}
			
			nodes.removeAll(temp);
			
			treeViewAdapter.notifyDataSetChanged();
			
		} else {
			nodes.get(position).setExpanded(true);
			int level = nodes.get(position).getLevel();
			int nextLevel = level + 1;

			for (Node pdfOutlineElement : nodeAll) {
				int j=1;
				if (pdfOutlineElement.getParent().equals(nodes.get(position).getId())) {
					pdfOutlineElement.setLevel(nextLevel);
					pdfOutlineElement.setExpanded(false);
					nodes.add(position+j, pdfOutlineElement);
					j++;
				}			
			}
			treeViewAdapter.notifyDataSetChanged();
			/*fileExploreAdapter = new TreeViewAdapter(this, R.layout.outline,
					mPdfOutlinesCount);*/

			//setListAdapter(fileExploreAdapter);
		}
	}

//	private static final int MENU_LOGOUT = Menu.FIRST;
	private static final int MENU_CHECK_MESSAGE = Menu.FIRST + 1;
	private static final int MENU_SETTING = Menu.FIRST + 2;
	private static final int MENU_EXIT = Menu.FIRST + 3;
	
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_CHECK_MESSAGE, 0, "消息盒子").setIcon(R.drawable.ic_menu_start_conversation).setEnabled(!GlobalService.userRequestFriends.isEmpty());
		
		String[] defaultUserInfo = setting.getString("defaultUser", "").split(",");
		menu.add(0, MENU_SETTING, 0, "自动登录").setIcon(R.drawable.ic_menu_preferences).setEnabled(Boolean.parseBoolean(defaultUserInfo[3]));
		
//		menu.add(0, MENU_LOGOUT, 0, "注销").setIcon(R.drawable.ic_menu_blocked_user);
		menu.add(0, MENU_EXIT, 0, "退出").setIcon(R.drawable.ic_menu_set_as);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
//		case MENU_LOGOUT : 
//			finish();
//			break;
		case MENU_SETTING :
			String[] defaultUserInfo = setting.getString("defaultUser", "").split(",");
			
			String updateDefaultUser = defaultUserInfo[0] + "," + defaultUserInfo[1] + "," + defaultUserInfo[2] + "," + !Boolean.parseBoolean(defaultUserInfo[3]);
			setting.edit().putString("defaultUser", updateDefaultUser).commit();
			
			Toast.makeText(getApplicationContext(), "已取消自动登录", Toast.LENGTH_LONG).show();
			
			item.setEnabled(false);
			break;
		case MENU_CHECK_MESSAGE :
			showDialog(DIALOG_REQUEST_FRIEND_LIST);
			break;
		case MENU_EXIT :
			userLogout();
			break;
		}
		return true;
	}
	
	private static final int DIALOG_CREATE_FRIEND = Menu.FIRST + 201;
	private static final int DIALOG_REMOVE_FRIEND = Menu.FIRST + 202;
	private static final int DIALOG_MODIFY_FRIEND_GROUP = Menu.FIRST + 203;
	private static final int DIALOG_SEARCH_FRIEND = Menu.FIRST + 204;
	
	private static final int DIALOG_CREATE_GROUP = Menu.FIRST + 205;
	private static final int DIALOG_REMOVE_GROUP = Menu.FIRST + 206;
	private static final int DIALOG_MODIFY_GROUP = Menu.FIRST + 207;
//	private static final int DIALOG_SEARCH_GROUP = Menu.FIRST + 208;
	
	private static final int DIALOG_LIST_LONG_CLICK_GROUP = Menu.FIRST + 209;
	private static final int DIALOG_LIST_LONG_CLICK_FRIEND = Menu.FIRST + 210;
	
	private static final int DIALOG_REQUEST_FRIEND_LIST = Menu.FIRST + 211;
	
	private static final int DIALOG_SELECT_LOCATE_TYPE = Menu.FIRST + 212;

	@Override
	protected Dialog onCreateDialog(int id) {
		Log.d(TAG, "===============>onCreateDialog()");
		switch(id){
		case DIALOG_CREATE_FRIEND: return createFriendDialog();
		case DIALOG_REMOVE_FRIEND: return removeFriendDialog();
		case DIALOG_MODIFY_FRIEND_GROUP: return modifyFriendGroupDialog();
		case DIALOG_SEARCH_FRIEND: return searchFriendDialog();
		
		case DIALOG_CREATE_GROUP: return createGroupDialog();
		case DIALOG_REMOVE_GROUP: return removeGroupDialog();
		case DIALOG_MODIFY_GROUP: return modifyGroupDialog();
//		case DIALOG_SEARCH_GROUP: return searchGroupDialog();
		
		case DIALOG_LIST_LONG_CLICK_GROUP: return listLongClickGroup();
		case DIALOG_LIST_LONG_CLICK_FRIEND: return listLongClickFriend();
		
		case DIALOG_REQUEST_FRIEND_LIST: return searchRequestFriendsDialog();
		
		case DIALOG_SELECT_LOCATE_TYPE: return selectLocateType();
		}
		return null;
	}
	
	private Dialog selectLocateType(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("选择定位类型");
		builder.setItems(new String[]{"定时上报", "周期上报"}, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent();
				intent.putExtras(GlobalService.userBundle);
				intent.putExtra("request_location_friend_id", requestLocationfriendId);
				switch(which){
				case 0 : 
					intent.setClass(getApplicationContext(), ReportTimingActivity.class);
					startActivityForResult(intent, 1001);
					break;
				case 1 : 
					intent.setClass(getApplicationContext(), ReportPeriodActivity.class);
					startActivityForResult(intent, 1002);
					break;
				}
			}
		});
		return builder.create();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode){
		case 1001:
			if(resultCode == Activity.RESULT_OK){
				String requestLocationCommand = data.getStringExtra("request_location_command");
				GlobalService.globalSocketTimerTask.ps.println(requestLocationCommand);
				
				Log.d(TAG, "Many locate======>" + requestLocationCommand);
				
				Toast.makeText(getApplicationContext(), "多次定位请求已发出", Toast.LENGTH_LONG).show();
			}
			break;
		case 1002:
			if(resultCode == Activity.RESULT_OK){
				String requestLocationCommand = data.getStringExtra("request_location_command");
				GlobalService.globalSocketTimerTask.ps.println(requestLocationCommand);
				
				Log.d(TAG, "Many locate======>" + requestLocationCommand);
				
				Toast.makeText(getApplicationContext(), "多次定位请求已发出", Toast.LENGTH_LONG).show();
			}
			break;
		}
	}

	private Dialog listLongClickGroup() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("请选择");
		builder.setItems(new String[]{"删除分组", "重命名分组"}, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				switch(which){
				case 0 : showDialog(DIALOG_REMOVE_GROUP); break;
				case 1 : showDialog(DIALOG_MODIFY_GROUP); break;
				}
			}
		});
		return builder.create();
	}
	
	private Dialog listLongClickFriend() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("请选择");
		builder.setItems(new String[]{"聊天", "查看足迹", "删除好友", "将好友移动至"}, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				switch(which){
				
				case 0 : 
					Intent intent0 = new Intent(MainActivity.this, ChartActivity.class); 
					Node pdf = nodes.get(mainListLongClickPosition);
					String[] friendInfo = pdf.getDetails();
					intent0.putExtra("friend_info", friendInfo);
					
					Bundle bundle = new Bundle();
					intent0.putExtras(bundle);
					startActivity(intent0);
					break;
				case 1 : 
					Intent intent1 = new Intent(MainActivity.this, MyFpsListActivity.class);
					String[] friend = nodes.get(mainListLongClickPosition).getDetails();
					String[] metaData = {"mId", "mUsername", "mPhoneNumber", "mLivingAddress", "mUserAvatar", "mLoginTimes", "mLoginStatus", "mUserRating"};
					Bundle userBundle = new Bundle();
					userBundle.putString(metaData[0], friend[0]);
					userBundle.putString(metaData[1], friend[1]);
					userBundle.putString(metaData[2], friend[2]);
					userBundle.putString(metaData[4], friend[4]);
					userBundle.putString(metaData[6], friend[3]);
					intent1.putExtras(userBundle);
					startActivity(intent1);
					break;
				case 2 : showDialog(DIALOG_REMOVE_FRIEND); break;
				case 3 : showDialog(DIALOG_MODIFY_FRIEND_GROUP); break;
				}
			}
		});
		return builder.create();
	}

	
	//好友管理
	private Spinner newClientResultsGroupView;
	private Dialog createFriendDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("添加好友");
		
		LayoutInflater inflater = getLayoutInflater();
		final View textEntryView = inflater.inflate(R.layout.main_newclient_results, null);
		TextView friendInfoView = (TextView) textEntryView.findViewById(R.id.main_newclient_results_friendinfo);
		newClientResultsGroupView = (Spinner) textEntryView.findViewById(R.id.main_newclient_results_friendgroup);
		friendInfoView.setText("☺ 用户名: " + newClientFindFriendInfo.split("_")[1]);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, GlobalService.userGroupNames);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		newClientResultsGroupView.setAdapter(adapter);
		
		builder.setView(textEntryView);
		builder.setPositiveButton("加为好友", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				String userId = GlobalService.userBundle.getString("mId");
				String friendId = newClientFindFriendInfo.split("_")[0];
				String groupId = GlobalService.userGroups.isEmpty() ? "0" : GlobalService.userGroups.get(newClientResultsGroupView.getSelectedItemPosition())[0];
				if(GlobalService.userFriendIds.contains(friendId)){
					Toast.makeText(getApplicationContext(), "该用户已经是您的好友", Toast.LENGTH_LONG).show();
				}else{
					createFriend(userId, friendId, groupId);
				}
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		return builder.create();
	}
	private Dialog removeFriendDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("删除好友");
		String friendName = nodes.get(mainListLongClickPosition).getOutlineTitle();
		builder.setMessage("您确定要删除\"" + friendName + "\"好友吗？");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				String userId = GlobalService.userBundle.getString("mId");
				String friendId = nodes.get(mainListLongClickPosition).getId();
				removeFriend(userId, friendId);
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		return builder.create();
	}
	private String modifyFriendGroupId;
	private Dialog modifyFriendGroupDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("移动好友至");
		builder.setSingleChoiceItems(GlobalService.userGroupNames.toArray(new String[GlobalService.userGroupNames.size()]), -1, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				modifyFriendGroupId = GlobalService.userGroups.get(which)[0];
			}
		});
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				String userId = GlobalService.userBundle.getString("mId");
				String friendId = nodes.get(mainListLongClickPosition).getId() + "";
				modifyFriendGroup(userId, friendId, modifyFriendGroupId);
				requestServerData();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		return builder.create();
	}
	private EditText newClienFindNameView, newClientFindPhoneView;
	private String newClientFindFriendInfo = "";
	private Dialog searchFriendDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("查找好友");
		
		LayoutInflater inflater = getLayoutInflater();
		final View textEntryView = inflater.inflate(R.layout.main_newclient_find, null);
		newClienFindNameView = (EditText) textEntryView.findViewById(R.id.main_newclient_find_username);
		newClientFindPhoneView = (EditText) textEntryView.findViewById(R.id.main_newclient_find_phone);
		
		builder.setView(textEntryView);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				String userId = GlobalService.userBundle.getString("mId");
				String friendName = newClienFindNameView.getText().toString();
				String friendPhone = newClientFindPhoneView.getText().toString();
				searchFriend(userId, friendName, friendPhone);
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		return builder.create();
	}
	
	private List<Integer> friendRequestsCheckedPosition = new ArrayList<Integer>();
	private int agreeFriendIndex = 0;
	private String agreeFriendGroupId = "";
	private Dialog searchRequestFriendsDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("请求加您为好友");
		builder.setMultiChoiceItems(GlobalService.userRequestFriendNames.toArray(new String[GlobalService.userRequestFriendNames.size()]), null, new DialogInterface.OnMultiChoiceClickListener() {
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				if(isChecked){
					friendRequestsCheckedPosition.add(which);
				}else{
					friendRequestsCheckedPosition.remove(which);
				}
			}
		});
		builder.setPositiveButton("同意", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				for(Integer index : friendRequestsCheckedPosition){
					agreeFriendIndex = index;
					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
					builder.setTitle("将好友" + GlobalService.userRequestFriends.get(agreeFriendIndex)[1] + "加入到");
					builder.setSingleChoiceItems(GlobalService.userGroupNames.toArray(new String[GlobalService.userGroupNames.size()]), -1, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							agreeFriendGroupId = GlobalService.userGroups.get(which)[0];
						}
					});
					if(GlobalService.userGroups.size() > 0){
						builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								String friendId = GlobalService.userRequestFriends.get(agreeFriendIndex)[0];
								if(GlobalService.userFriendIds.contains(friendId)){
									Toast.makeText(getApplicationContext(), "该用户已经是您的好友", Toast.LENGTH_LONG).show();
								}else{
									replyFriendRequest(GlobalService.userBundle.getString("mId"), friendId, 1, agreeFriendGroupId);
									GlobalService.globalSocketTimerTask.ps.println(GlobalService.userBundle.getString("mId") + "_" + friendId + "_0_0_0_u");
									requestServerData();
								}
							}
						});
					}
					builder.setNegativeButton("暂不分组", new DialogInterface.OnClickListener() { 
						public void onClick(DialogInterface dialog, int which) {
							String friendId = GlobalService.userRequestFriends.get(agreeFriendIndex)[0];
							if(GlobalService.userFriendIds.contains(friendId)){
								Toast.makeText(getApplicationContext(), "该用户已经是您的好友", Toast.LENGTH_LONG).show();
							}else{
								replyFriendRequest(GlobalService.userBundle.getString("mId"), friendId, 1, "0");
								GlobalService.globalSocketTimerTask.ps.println(GlobalService.userBundle.getString("mId") + "_" + friendId + "_0_0_0_u");
								requestServerData();
							}
						}
					});
					builder.show();
				}
			}
		});
		builder.setNeutralButton("清除", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				for(Integer i : friendRequestsCheckedPosition){
					String userId = GlobalService.userBundle.getString("mId");
					String friendId = GlobalService.userRequestFriends.get(i)[0];
					replyFriendRequest(userId, friendId, 2, "0");
					GlobalService.globalSocketTimerTask.ps.println(GlobalService.userBundle.getString("mId") + "_" + friendId + "_0_0_0_w");
				}
				requestServerData();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		return builder.create();
	}
	
	//好友管理
	private void createFriend(String userId, String friendId, String groupId) {
		String requestUrl = "http://180.168.81.238:7888/poi/dao/friendsadddao.jsp?";
		requestUrl += "u1=" + userId;
		requestUrl += "&u2=" + friendId;
		requestUrl += "&teamid=" + groupId;
	
		String responseText = MyHttpUtil.getUrlContentByStr(getApplicationContext(), requestUrl);
		
		if(responseText.equals("ok")){
			Toast.makeText(getApplicationContext(), "请求已发送，等待对方回应", Toast.LENGTH_LONG).show();
			
			String requestCommand = userId + "_" + friendId + "_0_0_0_r";
			GlobalService.globalSocketTimerTask.ps.println(requestCommand);
		}
	}
	
	private boolean removeFriend(String userId, String friendId) {
		String requestUrl = "http://180.168.81.238:7888/poi/dao/friendsdeldao.jsp?";
		requestUrl += "u1=" + userId;
		requestUrl += "&u2=" + friendId;
	
		String responseText = MyHttpUtil.getUrlContentByStr(MainActivity.this, requestUrl);
		
		if(responseText.equals("ok")){
			requestServerData();
			Toast.makeText(getApplicationContext(), "删除好友成功", Toast.LENGTH_LONG).show();
			return true;
		}
		
		return false;
	}
//	private boolean modifyFriendRemark(String userId, String friendId, String groupId) {
//		return true;
//	}
	private String searchFriend(String userId, String friendName, String friendPhone) {
		String requestUrl = "http://180.168.81.238:7888/poi/dao/friendsfinddao.jsp?";
		requestUrl += "stype=stmap001";
		requestUrl += "&u1=" + userId;
		requestUrl += "&u2name=" + friendName;
		requestUrl += "&cellphone=" + friendPhone;
	
		String responseText = MyHttpUtil.getUrlContentByStr(getApplicationContext(), requestUrl);
		
		if(responseText.contains("_")){
			newClientFindFriendInfo = responseText;
			showDialog(DIALOG_CREATE_FRIEND);
			return responseText;
		}else{
			return new String("");
		}
	}
	private boolean modifyFriendGroup(String userId, String friendId, String newGroupId) {
		String requestUrl = "http://180.168.81.238:7888/poi/dao/fattteamdao.jsp?"; 
		requestUrl += "uid1=" + userId;
		requestUrl += "&uid2=" + friendId;
		requestUrl += "&ttid=" + newGroupId;
	
		String responseText = MyHttpUtil.getUrlContentByStr(MainActivity.this, requestUrl);
		
		if(responseText.equals("ok")){
			Toast.makeText(getApplicationContext(), "修改好友分组成功", Toast.LENGTH_LONG).show();
			return true;
		}
		
		return false;
	}	
	private List<String[]> searchFriends(String userId) {
		List<String[]> userFriends = new ArrayList<String[]>();
		
		String requestUrl = "http://180.168.81.238:7888/poi/dao/friendslistdao.jsp?";
		requestUrl += "&u1=" + userId;
	
		String responseText = MyHttpUtil.getUrlContentByStr(MainActivity.this, requestUrl);
		
		if(responseText.contains("_")){
			for(String friend : responseText.split(";")){
				userFriends.add(friend.split("_"));
			}
		}
		
		return userFriends;
	}
	private boolean replyFriendRequest(String userId, String friendId, int isAgree, String groupId) {
		String requestUrl = "http://180.168.81.238:7888/poi/dao/friendssuredao.jsp?";
		requestUrl += "u1=" + userId;
		requestUrl += "&u2=" + friendId;
		requestUrl += "&au=" + isAgree;
		requestUrl += "&teamid=" + groupId;
	
		String responseText = MyHttpUtil.getUrlContentByStr(MainActivity.this, requestUrl);
		
		if(responseText.equals("ok")){
			
			if(isAgree == 1){
				Toast.makeText(getApplicationContext(), "已同意加为好友", Toast.LENGTH_LONG).show();
				
				String requestCommand = userId + "_" + friendId + "_0_0_0_u";
				GlobalService.globalSocketTimerTask.ps.println(requestCommand);
			}else{
				Toast.makeText(getApplicationContext(), "已拒绝加为好友", Toast.LENGTH_LONG).show();
				
				String requestCommand = userId + "_" + friendId + "_0_0_0_w";
				GlobalService.globalSocketTimerTask.ps.println(requestCommand);
			}

			return true;
		}
		
		return false;
	}	
	private List<String[]> searchRequestFriends(String userId) {
		List<String[]> userFriendRequests = new ArrayList<String[]>();
		
		String requestUrl = "http://180.168.81.238:7888/poi/dao/freqlistdao.jsp?";
		requestUrl += "&u1=" + userId;

		String responseText = MyHttpUtil.getUrlContentByStr(MainActivity.this, requestUrl);
		
		if(responseText.contains("_")){
			for(String userRequestFriend : responseText.split(";")){
				userFriendRequests.add(userRequestFriend.split("_"));
			}
		}
		
		return userFriendRequests;
	}	
	
	
	//分组管理
	private EditText newGroupFindNameView;
	private Dialog createGroupDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("新建项目");
		
		LayoutInflater inflater = getLayoutInflater();
		final View textEntryView = inflater.inflate(R.layout.main_newgroup_find, null);
		newGroupFindNameView = (EditText) textEntryView.findViewById(R.id.main_newgroup_find_groupname);
		
		builder.setView(textEntryView);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				createGroup(GlobalService.userBundle.getString("mId"), newGroupFindNameView.getText().toString());
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		return builder.create();
	}
	private Dialog removeGroupDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("删除分组");
		String groupName = nodes.get(mainListLongClickPosition).getOutlineTitle();
		builder.setMessage("您确定要删除\"" + groupName.substring(0, groupName.indexOf(" [")) + "\"组吗？");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				removeGroup(nodes.get(mainListLongClickPosition).getId(), GlobalService.userBundle.getString("mId"));
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		return builder.create();
	}
	private EditText modifyGroupNameView;
	private Dialog modifyGroupDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("重命名分组");
		
		LayoutInflater inflater = getLayoutInflater();
		final View textEntryView = inflater.inflate(R.layout.main_list_modify_group, null);
		modifyGroupNameView = (EditText) textEntryView.findViewById(R.id.main_list_modify_groupname);
		
		builder.setView(textEntryView);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				modifyGroup(nodes.get(mainListLongClickPosition).getId(), modifyGroupNameView.getText().toString());
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		return builder.create();
	}
	
	private boolean createGroup(String userId, String groupName){
		String newGroupName = "";
		try {
			newGroupName = URLEncoder.encode(groupName, "GBK");
		} catch (UnsupportedEncodingException e) {
		}
		
		String requestUrl = "http://180.168.81.238:7888/poi/dao/addteamdao.jsp?";
		requestUrl += "uuid=" + userId;
		requestUrl += "&tname=" + newGroupName;
	
		String responseText = MyHttpUtil.getUrlContentByStr(MainActivity.this, requestUrl);
	
		if(responseText.length() > 0 && !responseText.startsWith("stmap")){
			requestServerData();
			Toast.makeText(getApplicationContext(), "新建分组成功", Toast.LENGTH_LONG).show();
    		return true;
		}
		
		return false;
	}
	private boolean removeGroup(String groupId, String userId){
		String requestUrl = "http://180.168.81.238:7888/poi/dao/delteamdao.jsp?";
		requestUrl += "ttid=" + groupId;
		requestUrl += "&uuid=" + userId;
	
		String responseText = MyHttpUtil.getUrlContentByStr(MainActivity.this, requestUrl);
		
		if(responseText.equals("ok")){
			requestServerData();
			Toast.makeText(getApplicationContext(), "删除分组成功", Toast.LENGTH_LONG).show();
    		return true;
		}

		return false;
	}
	private boolean modifyGroup(String oldGroupId, String newGroupName){
		String nGroupName = "";
		try {
			nGroupName = URLEncoder.encode(newGroupName, "GBK");
		} catch (UnsupportedEncodingException e) {
		}
		
		String requestUrl = "http://180.168.81.238:7888/poi/dao/modifyteamdao.jsp?";
		requestUrl += "ttid=" + oldGroupId;
		requestUrl += "&tname=" + nGroupName;
		
		String responseText = MyHttpUtil.getUrlContentByStr(MainActivity.this, requestUrl);
	
		if(responseText.equals("ok")){
			requestServerData();
			Toast.makeText(getApplicationContext(), "重命名分组成功", Toast.LENGTH_LONG).show();
    		return true;
		}
		
		return false;
	}
	private List<String[]> searchGroups(String userId){
		List<String[]> userGroups = new ArrayList<String[]>();
		
		String requestUrl = "http://180.168.81.238:7888/poi/dao/getteamdao.jsp?";
		requestUrl += "uuid=" + userId;
	
		String responseText = MyHttpUtil.getUrlContentByStr(MainActivity.this, requestUrl);
		
		if(responseText.contains("_")){
			for(String userGroup : responseText.split(";")){
				userGroups.add(userGroup.split("_"));
			}
		}
		
		return userGroups;
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			new AlertDialog.Builder(this)
				.setTitle("提示信息")
				.setMessage("您确定要退出吗？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						userLogout();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
					}
				})
				.show();
			return true;
		}
		return false;
	}
	
	private void findViews(){
//		messageLayout = (Button) findViewById(R.id.main_tools_message);
		newClientView = (Button) findViewById(R.id.main_new_client);
		newGroupView = (Button) findViewById(R.id.main_new_group);
		poiView = (Button) findViewById(R.id.main_poi);
		exitView = (Button) findViewById(R.id.main_exit);
    }
	
	@Override
	protected void onStart() {
		super.onStart();
		
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.app.poi.yangtse.SocketBroadcast");
		registerReceiver(socketMessageReceiver, filter);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		if(socketMessageReceiver != null){
			unregisterReceiver(socketMessageReceiver);
		}
	}
	

	
	private BroadcastReceiver socketMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			
			String reply = intent.getStringExtra("socket_message");
			
			Message msg = new Message();
			msg.what = Constants.MainActivity_HANDLER_SOCKET_MESSAGE;
			msg.obj = new String(reply);
			uiHandler.sendMessage(msg);
		}
	};
	
	private Handler uiHandler = new Handler(){
		String socketReplyTexts = "";
		String[] socketReplyArray;
		
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case Constants.GLOBAL_HANDLER_DEBUGS : 
				
				Log.e(TAG, "Constants.GLOBAL_HANDLER_DEBUGS======>" + msg.obj);
				
				Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_LONG).show(); 
				
				break;
			case Constants.MainActivity_HANDLER_FRIEND_AVATAR :
				
				boolean flag = (Boolean) msg.obj;
				
				for(LinearLayout layout : avatarViews){
					if(flag == false){
						layout.setPadding(layout.getPaddingLeft() - 5, layout.getPaddingTop() + 5, layout.getPaddingRight() + 5, layout.getPaddingBottom() - 5);
					}else{
						layout.setPadding(layout.getPaddingLeft() + 5, layout.getPaddingTop() - 5, layout.getPaddingRight() - 5, layout.getPaddingBottom() + 5);
					}
				}
				break;
			case Constants.MainActivity_HANDLER_USER_LOGOUT :
				
				Log.d(TAG, "Constants.MainActivity_HANDLER_USER_LOGOUT======>" + msg.obj);
				
				try{
					if(userLogoutTimerTask != null){
						userLogoutTimerTask.cancel();
					}
				}catch(Exception e){
				}
				
				finish();
				
				break;
			case Constants.MainActivity_HANDLER_SOCKET_MESSAGE :
				
				Log.d(TAG, "Constants.MainActivity_HANDLER_SOCKET_MESSAGE=====" + (String)msg.obj);
				
				try{
				
					socketReplyTexts = new String((String)msg.obj);
					
					if(socketReplyTexts.equals("disconnected")){
						GlobalDialogs.showReconnectSocketDialog(MainActivity.this);
						return;
					}
					
					socketReplyArray = socketReplyTexts.split("_");
					
					char replyType = socketReplyArray[5].charAt(0);
					
					switch(replyType){
					case 's':
						if(socketReplyArray[5].equals("st001")){
							
							requestServerData();
							
	//						for(Node pdf : nodeAll){
	//							if(pdf.getId().equals(responseTexts[1])){
	//								String[] detail = pdf.getDetails();
	//								detail[3] = "2";
	//								pdf.setDetails(detail);
	//							}
	//						}
	//						treeViewAdapter.notifyDataSetChanged();
						}else if(socketReplyArray[5].equals("st002")){

							requestServerData();
							
	//						for(Node pdf : nodeAll){
	//							if(pdf.getId().equals(responseTexts[1])){
	//								String[] detail = pdf.getDetails();
	//								detail[3] = "0";
	//								pdf.setDetails(detail);
	//							}
	//						}
	//						treeViewAdapter.notifyDataSetChanged();
						}else if(socketReplyArray[5].equals("st006")){
							
							avatarViews.clear();
							
							for(Node n : nodeAll){
								if(n.getId().equals(socketReplyArray[1])){
									n.setHasNewMessage(true);
								}
							}
							treeViewAdapter.notifyDataSetChanged();
						}else if(socketReplyArray[5].equals("st007")){
							
							avatarViews.clear();
							
							for(Node n : nodeAll){
								if(n.getId().equals(socketReplyArray[1])){
									n.setHasNewMessage(true);
								}
							}
							treeViewAdapter.notifyDataSetChanged();
						}
						break;
					case 'y':
						requestServerData();
						break;
					case 'z':
						break;
					}
					
				}catch(Exception e){
				}
			}
		}
	};
	
	private void userLogout(){
		GlobalService.globalSocketTimerTask.ps.println(GlobalService.userBundle.getString("mId") + "_0_0_0_0_d");
		
		userLogoutTimerTask = new UserLogoutTimerTask();
		
		mainTimer.schedule(userLogoutTimerTask, 0);
	}
	
	private class UserLogoutTimerTask extends TimerTask {
		@Override
		public void run() {
			System.out.println(getClass().getName() + ".run().start");
			
			String requestUrl = "http://180.168.81.238:7888/poi/dao/exitdao.jsp?";
			requestUrl += "uid=" + GlobalService.userBundle.getString("mId");
			
			String responseText = MyHttpUtil.getUrlContentByStr(requestUrl);
			
			System.out.println(getClass().getName() + ".run().HttpRespText======" + responseText);
			
			Message msg = new Message();
			msg.what = Constants.MainActivity_HANDLER_USER_LOGOUT;
			msg.obj = new String(responseText);
			uiHandler.sendMessage(msg);
			
			System.out.println(getClass().getName() + ".run().end");
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		requestServerData();
		
		for(Node n : nodeAll){
			if(GlobalService.newMessageFriendIds.contains(n.getId())){
				n.setHasNewMessage(true);
			}
		}
		treeViewAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		try{
			if(mainTimer != null){
				mainTimer.cancel();
			}
		}catch(Exception e){
		}
		
		Intent intent1 = new Intent(this, GlobalService.class);
		stopService(intent1);
	}
}