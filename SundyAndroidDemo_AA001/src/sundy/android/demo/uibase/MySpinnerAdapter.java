package sundy.android.demo.uibase;

import java.util.List;

import sundy.android.demo.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MySpinnerAdapter extends BaseAdapter {

	List<MyItem> mMyItems  ;
	Context mContext  ;
	
	
	public MySpinnerAdapter(Context context,List<MyItem> items)
	{
		mContext = context  ;
		mMyItems = items  ;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mMyItems.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mMyItems.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0 ;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		MyItem myItem = mMyItems.get(arg0)  ;
		arg1 = LayoutInflater.from(mContext).inflate(R.layout.layout_spinner_item2, null)  ;
		TextView textView1 = (TextView)arg1.findViewById(R.id.textView1)  ;
		TextView textView2 = (TextView)arg1.findViewById(R.id.textView2)  ;
		textView1.setText("Name:"+myItem.getName())  ;
		textView2.setText("Company:"+myItem.getCompany())  ;
		return arg1 ;
	}
	

}
