package com.lzm.refreshlistview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lzm on 2017/8/18.
 */
public class RefreshListview extends ListView {

    private int mStartY;
    private int mMeasuredHeight;
    private View mHeadView;
    private final int DOWN_PULL = 0;//下拉刷新
    private final int RELEASE_REFRESH = 1;//松开刷新
    private final int REFRESHING = 2;//正在刷新

    private int currentState = DOWN_PULL;//用来记录当前的状态，默认为下拉刷新

    private ImageView         mIv_arrow;//箭头图片
    private ProgressBar       mPb;//进度圈
    private TextView          mTv_state;//文本状态
    private TextView          mTv_time;//文本时间
    private RotateAnimation   mUp;//向上的动画
    private RotateAnimation   mDown;//向下的动画
    private OnRefreshListener RefreshListener;

    private boolean isLoadMore = false;

    private int mFooterviewHeight;
    private View mFooterView;

    public RefreshListview(Context context) {
        this(context,null);
    }

    public RefreshListview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

        /**
         * 1.添加头布局
         * 2.影藏头布局
         * 3.滑动出头布局
         * 4.3种状态的切换
         *
         */
        initHeaderView();
        initFooterView();

        //初始化动画
        initAnimation();

        //加载更多
        LoadMore();
    }

    private void initHeaderView() {
        mHeadView = View.inflate(getContext(), R.layout.listview_header, null);

        //获取控件
        mIv_arrow = (ImageView) mHeadView.findViewById(R.id.iv_arrow);
        mPb = (ProgressBar) mHeadView.findViewById(R.id.pb);
        mTv_state = (TextView) mHeadView.findViewById(R.id.tv_state);
        mTv_time = (TextView) mHeadView.findViewById(R.id.tv_time);

//      int height = headView.getHeight(); 拿到为0 此时没有测量
        mHeadView.measure(0, 0);
        mMeasuredHeight = mHeadView.getMeasuredHeight();
        System.out.println("measuredHeight :　" + mMeasuredHeight);
        mHeadView.setPadding(0,-mMeasuredHeight,0,0);
        addHeaderView(mHeadView);
    }

    private void initFooterView() {
        mFooterView = View.inflate(getContext(), R.layout.listview_footer, null);
        //先测量再获取高度
        mFooterView.measure(0, 0);
        mFooterviewHeight = mFooterView.getMeasuredHeight();
        mFooterView.setPadding(0, -mFooterviewHeight, 0, 0);
        this.addFooterView(mFooterView);
    }

    private void LoadMore() {
        /**
         * OnScrollListener.SCROLL_STATE_FLING;2  手指用力滑动界面，离开屏幕，listview产生一个惯性的滑动状态
         OnScrollListener.SCROLL_STATE_IDLE;0   手指离开屏幕，listview处于停滞状态
         OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;1 手指触摸着屏幕，上下滑动的状态
         */
        this.setOnScrollListener(new OnScrollListener() {
            //当滑动状态改变的时候调用这个方法
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                System.out.println("scrollState:"+scrollState);
                int lastVisiblePosition = RefreshListview.this.getLastVisiblePosition();
                //获取当前屏幕中最后一个条目的索引

                //手指离开屏幕&&当前已经是最后一个条目时才加载更多
                if (scrollState != OnScrollListener.SCROLL_STATE_TOUCH_SCROLL &&
                        (lastVisiblePosition == RefreshListview.this.getCount() - 1)&&!isLoadMore) {
                    System.out.println("加载更多");
                    //显示脚布局
                    mFooterView.setPadding(0,0,0,0);
                    //让listview显示到最后一行
                    RefreshListview.this.setSelection(RefreshListview.this.getCount() -1);
                    isLoadMore = true;
                    if (RefreshListener != null) {
                        RefreshListener.OnLoadMore();
                    }
                }
            }

            //当滑动的时候实时调用该方法
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {

            }
        });
    }

    private void initAnimation() {
        //向上的动画
        mUp = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        mUp.setDuration(500);
        mUp.setFillAfter(true);
        //向下的动画
        mDown = new RotateAnimation(-180, -360, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        mDown.setDuration(500);
        mDown.setFillAfter(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //1、获取起始点
                mStartY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_UP:
                if (currentState == DOWN_PULL) {
                    //状态为下拉刷新时，让头布局完全隐藏
                    mHeadView.setPadding(0, -mMeasuredHeight, 0, 0);
                } else if (currentState == RELEASE_REFRESH) {
                    //状态为松开刷新时，进去正在刷新状态
                    currentState = REFRESHING;
                    updateViewByState();

                    // 回调去加载数据
                    if (RefreshListener != null) {
                        RefreshListener.LoadingData();
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                // 如果正在刷新 不允许移除掉
                if (currentState == REFRESHING) {
                    break;
                }

                //2、获取结束点
                int endY = (int) ev.getY();
                //3、计算间距
                int diffY = endY - mStartY;
                //4、计算paddingtop的值
                int paddingTop = -mMeasuredHeight + diffY;
                //获取当前屏幕中第一个显示条目的索引，如果是0的话，才显示头布局
                int firstVisiblePosition = this.getFirstVisiblePosition();

                if (paddingTop > -mMeasuredHeight && firstVisiblePosition == 0) {
                    mHeadView.setPadding(0, paddingTop, 0, 0);

                    if (paddingTop > 0 && currentState == DOWN_PULL) {
                        System.out.println("松开刷新");
                        currentState = RELEASE_REFRESH;
                        // 更新状态
                        updateViewByState();

                    }else if (paddingTop <= 0 && currentState == RELEASE_REFRESH) {//头布局有隐藏，下拉刷新
//                        System.out.println("下拉刷新");
                        currentState = DOWN_PULL;
                        updateViewByState();
                    }
                    return true; // 自身消费掉 不交给listview处理
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    //根据当前的状态刷新界面
    public void updateViewByState() {
        switch (currentState) {
            case DOWN_PULL://下拉刷新
                mTv_state.setText("下拉刷新");
                //给箭头设置向下的动画
                mIv_arrow.startAnimation(mDown);
                break;
            case RELEASE_REFRESH://松开刷新
                mTv_state.setText("松开刷新");
                //给箭头设置向上的动画
                mIv_arrow.startAnimation(mUp);
                break;
            case REFRESHING://正在刷新
                mTv_state.setText("正在刷新");
                mIv_arrow.clearAnimation();
                mIv_arrow.setVisibility(View.INVISIBLE);
                mPb.setVisibility(View.VISIBLE);
                //让头布局正好完全显示
                mHeadView.setPadding(0, 0, 0, 0);
                break;
        }
    }

    public void setOnRefreshListener(OnRefreshListener refreshListener) {
        this.RefreshListener = refreshListener;
    }

    public void onFinsh() {

        if (isLoadMore) {//加载更多
            isLoadMore = false;
            //脚布局隐藏
            mFooterView.setPadding(0,-mFooterviewHeight,0,0);

        } else {
            currentState = DOWN_PULL;
            mTv_state.setText("下拉刷新");
            mIv_arrow.setVisibility(View.VISIBLE);
            mPb.setVisibility(View.INVISIBLE);
            //头布局要隐藏掉
            mHeadView.setPadding(0, -mMeasuredHeight, 0, 0);

            mTv_time.setText("最近刷新时间：" + getCurrentTime());
        }
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    public interface OnRefreshListener {
        void LoadingData();
        void OnLoadMore();
    }
}
