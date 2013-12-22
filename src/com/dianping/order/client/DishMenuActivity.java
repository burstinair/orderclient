package com.dianping.order.client;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;
import java.util.Map;

import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.dianping.order.client.framework.Blur;

/**
 * @author zhongkai.zhao
 *         13-12-20 下午11:33
 */
public class DishMenuActivity extends Activity {

    private static final int[] part = {6, 2, 2};
    private DisplayMetrics dm;
    private int w;
    private TextView tv1, tv2, tv3;

    private ListView listview;
    private mBaseAdapter mAdapter;
    private LayoutInflater mInflate;
    private ListViewHandler handler;

    private List<Map<String, String>> listData;
    private Map<String, String> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

        byte[] result = getIntent().getByteArrayExtra("data");
        Bitmap bitmap = BitmapFactory.decodeByteArray(result, 0, result.length);
        findViewById(R.id.listView1).setBackground(new BitmapDrawable(getResources(), Blur.BoxBlurFilter(bitmap)));

        LinearLayout ll = (LinearLayout) findViewById(R.id.linerlayout1);
        ll.setBackgroundColor(Color.parseColor("#ff9c00"));

        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int allPart = 0;
        for (int i = 0; i < part.length; i++) {
            allPart += part[i];
        }
        w = dm.widthPixels / allPart; // 当前分辨率 宽度 分为15等份

        //为每个表头文本添加一个监听，进行捕获
        OnClickListener click = new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.textView1) {
                    Toast.makeText(DishMenuActivity.this, "你单击了第一个表头文本", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DishMenuActivity.this, "你单击了其他表头文本", Toast.LENGTH_SHORT).show();
                }
            }
        };

        tv1 = (TextView) findViewById(R.id.textView1);
        tv1.setText("菜名");
        tv1.setOnClickListener(click);    //为每个表头文本添加一个监听

        tv2 = (TextView) findViewById(R.id.textView2);
        tv2.setText("单价");
        tv2.setOnClickListener(click);  //为每个表头文本添加一个监听

        tv3 = (TextView) findViewById(R.id.textView3);
        tv3.setText("数量");
        tv3.setOnClickListener(click);   //为每个表头文本添加一个监听

        OnTouchListener touchListener = new OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                motionEvent.getAction();
                return false;  //To change body of implemented methods use File | Settings | File Templates.
            }
        };

        tv3.setOnTouchListener(touchListener);

        tv1.setWidth(w * part[0]);        //为每个表头文本框设置宽度，可根据实际情况 进行自我调整
        tv2.setWidth(w * part[1]);
        tv3.setWidth(w * part[2]);

        initData();

        listview = (ListView) findViewById(R.id.listView1);  //
        mAdapter = new mBaseAdapter();
        listview.setAdapter(mAdapter);  //为listView添加适配器

        mInflate = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
    }

    //初始化listView要用到的数据
    public void initData() {
        listData = new ArrayList<Map<String, String>>();

        List<DishMenu> dishMenus = DefaultActivity.getInstance().getResolveResult();
        for (DishMenu dishMenu : dishMenus) {
            map = new HashMap<String, String>();
            map.put("test1", dishMenu.getName());
            map.put("test2", Double.toString(dishMenu.getPrice()));
            map.put("test3", Integer.toString(dishMenu.getCount()));
            listData.add(map);
        }
    }

    //为适配器要用到的控件对象创建类
    private class ListViewHandler {
        TextView textview1;
        TextView textview2;
        TextView textview3;
    }

    private class mBaseAdapter extends BaseAdapter {         //继承BaseAdapter类，并重写方法

        @Override
        public int getCount() {
            return listData.size();
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int position, View contentView, ViewGroup arg2) {
            if (contentView == null) {
                contentView = mInflate.inflate(R.layout.test1, null);  //使用表头布局test1.xml
                handler = new ListViewHandler();   //与表头的文本控件一一对应
                handler.textview1 = (TextView) contentView.findViewById(R.id.textView1);
                handler.textview2 = (TextView) contentView.findViewById(R.id.textView2);
                handler.textview3 = (TextView) contentView.findViewById(R.id.textView3);

                contentView.setTag(handler);

                handler.textview1.setWidth(w * part[0]); //记住，这里的宽度设置必须和表头文本宽度一致
                handler.textview2.setWidth(w * part[1]);
                handler.textview3.setWidth(w * part[2]);

                handler.textview1.setText("");
                handler.textview2.setText("");
                handler.textview3.setText("");

            } else {
                handler = (ListViewHandler) contentView.getTag();
            }

            //为listview中的TextView布局控件添加内容
            for (int j = 0; j < listData.size(); j++) {
                if (position == j) {
                    map = listData.get(position);
                    handler.textview1.setText("" + map.get("test1"));
                    handler.textview2.setText("" + map.get("test2"));
                    handler.textview3.setText("" + map.get("test3"));
                }
            }

            return contentView;
        }

    }

}
