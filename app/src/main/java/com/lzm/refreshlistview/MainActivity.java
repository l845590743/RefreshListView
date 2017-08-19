package com.lzm.refreshlistview;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RefreshListview mRefreshListview;
    private ArrayList<String> mList;
    private Handler mHandler = new Handler();
    private MyAdapter mMyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
        onLoadData();
    }

    private void onLoadData() {
        mRefreshListview.setOnRefreshListener(new RefreshListview.OnRefreshListener() {
            @Override
            public void LoadingData() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mList.add(0, "This is new Data 1");
                        mList.add(1, "This is new Data 2");
                        mMyAdapter.notifyDataSetChanged();

                        mRefreshListview.onFinsh();
                    }
                }, 2000);
            }

            @Override
            public void OnLoadMore() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mList.add("This is more Data 1");
                        mList.add("This is more Data 2");
                        mMyAdapter.notifyDataSetChanged();

                        mRefreshListview.onFinsh();
                    }
                }, 2000);
            }
        });
    }

    private void initData() {
        mList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            mList.add("My refresh ListView " + i);
        }
    }

    private void initView() {
        mRefreshListview = (RefreshListview) findViewById(R.id.refresh_listView);
        mMyAdapter = new MyAdapter();
        mRefreshListview.setAdapter(mMyAdapter);
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int i) {
            return mList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            TextView textView = new TextView(MainActivity.this);
            textView.setText(mList.get(position));
            textView.setPadding(18,18,18,18);
            return textView;
        }
    }
}
