package com.lzm.refreshlistview.utils;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.lzm.refreshlistview.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzm on 2017/8/19.
 * 参照博客： http://blog.csdn.net/lmj623565791/article/details/45059587
 */
public class TestActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private List<String> mDatas;
    private TestAdapter mTestAdapter;
    private SwipeRefreshLayout mSpRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
        initData();
        mRecyclerView.setLayoutManager(new GridLayoutManager(this,3));
        mTestAdapter = new TestAdapter(this,mDatas);
        mRecyclerView.setAdapter(mTestAdapter);
        mRecyclerView.addItemDecoration(new DividerGridItemDecoration(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mTestAdapter.setOnItemClickListener(new TestAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(TestActivity.this, position + " click",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Toast.makeText(TestActivity.this, position + " long click",
                        Toast.LENGTH_SHORT).show();
                mTestAdapter.removeData(position);
            }
        });

        mSpRefresh = (SwipeRefreshLayout) findViewById(R.id.sp_refresh);
        mSpRefresh.setColorSchemeResources(android.R.color.holo_red_light, android.R.color.holo_green_light, android.R.color.holo_blue_light);
//        mSpRefresh.setDistanceToTriggerSync();
        mSpRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                System.out.println("---------- onRefresh ");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSpRefresh.setRefreshing(false);
                    }
                }, 2000);
            }
        });
    }

    protected void initData()
    {
        mDatas = new ArrayList<String>();
        for (int i = 'A'; i < 'z'; i++)
        {
            mDatas.add("" + (char) i);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mian,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                mTestAdapter.addData(1);
                break;
            case R.id.delete:
                mTestAdapter.removeData(1);
                break;
        }
        return true;
    }

}
