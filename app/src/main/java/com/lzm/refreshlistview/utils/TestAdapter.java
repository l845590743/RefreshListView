package com.lzm.refreshlistview.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lzm.refreshlistview.R;

import java.util.List;

/**
 * Created by lzm on 2017/8/19.
 */

class TestAdapter extends RecyclerView.Adapter<TestAdapter.TestViewHolder> {

    private OnItemClickListener mItemClickListener;
    private List<String> mDatas;
    private Context mContext;

    public TestAdapter(Context context,List<String> list) {
        this.mDatas = list;
        this.mContext = context;
    }

    @Override
    public TestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_test, parent, false);
        TestViewHolder testViewHolder = new TestViewHolder(view);
        return testViewHolder;
    }

    @Override
    public void onBindViewHolder(final TestViewHolder holder, int position) {
        holder.tv.setText(mDatas.get(position));
        if (mItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mItemClickListener.onItemClick(holder.itemView,pos);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mItemClickListener.onItemLongClick(holder.itemView,pos);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class TestViewHolder extends RecyclerView.ViewHolder{

        TextView tv;
        public TestViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.test_tv);
        }
    }

    public void addData(int position) {
        mDatas.add(position, "Insert One");
        notifyItemInserted(position);
    }

    public void removeData(int position) {
        mDatas.remove(position);
        notifyItemRemoved(position);
    }

    public interface OnItemClickListener {

        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener){
        this.mItemClickListener = itemClickListener;
    }


    /**
     * 多条目的实现：
     */
    /*
    public interface TypeInterf {
        public int getType();
    }

    public class Item_a implements TypeInterf {
        String text1;
        String text2;

        public String getText1() {
            return text1;
        }

        public void setText1(String text1) {
            this.text1 = text1;
        }
        @Override
        public int getType() {
            return R.layout.item_a;
        }
    }
        重写getItemViewType方法
        @Override
        public int getItemViewType(int position) {
            return list.get(position).getType();
        }

            根据不同的type实现不同的ViewHolder
     @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        View view = LayoutInflater.from(context).inflate(viewType, parent, false);
        if(viewType==R.layout.item_a){
            holder = new ViewHolder_a(view);
        }else{
            holder = new ViewHolder_b(view);
        }
        return holder;
    }
     */

}
