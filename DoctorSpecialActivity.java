package com.mandala.fuyou.activity.preuniversity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.mandalat.basictools.BaseToolBarActivity;
import com.mandala.fuyou.R;
import com.example.perunimodule.adapter.PreDocSpecialAdapter;
import com.mandalat.basictools.controller.ConstantCenter;
import com.mandalat.basictools.mvp.model.AdvertisementModule;
import com.mandalat.basictools.mvp.model.preuniversity.VideoSpecialData;
import com.example.perunimodule.presenter.PregnantForumPresenter;
import com.mandalat.basictools.mvp.view.preuniversity.DoctorSpecialListener;
import com.mandalat.basictools.view.LoadFooterView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ldy.com.baserecyclerview.BaseQuickAdapter;
import ldy.com.baserecyclerview.refresh.PullToRefreshLayout;

/**
 */
public class DoctorSpecialActivity extends BaseToolBarActivity implements DoctorSpecialListener
        , BaseQuickAdapter.RequestLoadMoreListener, PullToRefreshLayout.OnRefreshListener {

    @BindView(R.id.pregnant_forum_swipeLayout)
    PullToRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.pregnant_forum_pullRefreshRecycler)
    RecyclerView mRecyclerView;
    @BindView(R.id.no_result_layout_main)
    View mNoResultView;
    @BindView(R.id.no_result_image)
    ImageView mNoResultImage;
    @BindView(R.id.no_result_text)
    TextView mTextView;
    @BindView(R.id.ll_title_classify)
    LinearLayout mClassifyView;

    private List<VideoSpecialData> mDatas;
    private SpecialHeaderView mHeaderView;
    private PregnantForumPresenter mPresenter;
    static final int TURNING_TIME = 3 * 1000;
    PopupWindow mPopWindow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_special);
        ButterKnife.bind(this);
//        initToolbar(R.id.toolbar);
        initToolbar(R.id.toolbar, R.id.toolbar_title, "专题");
//        mRecyclerView.addItemDecoration(new SpacesItemDecoration(15));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mHeaderView = new SpecialHeaderView(this);
        mPresenter = new PregnantForumPresenter(this);
        mPresenter.loadSpecialList(this, "0");
        mPresenter.loadBannerData(this, "");
        mWaitDialog.showWait(getString(R.string.loading));
    }

    @OnClick(R.id.ll_title_classify)
    public void classify() {
        showPopupWindow(mClassifyView);
    }

    private void showPopupWindow(View view) {
        //设置contentView
        if (mPopWindow == null) {
            View contentView = LayoutInflater.from(this).inflate(R.layout.pop_doc_classify, null);
            mPopWindow = new PopupWindow(contentView,
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
            mPopWindow.setContentView(contentView);
        }

        //设置各个控件的点击响应
//        TextView tv1 = (TextView) contentView.findViewById(R.id.pop_computer);
//        TextView tv2 = (TextView) contentView.findViewById(R.id.pop_financial);
//        TextView tv3 = (TextView) contentView.findViewById(R.id.pop_manage);
//        tv1.setOnClickListener(this);
//        tv2.setOnClickListener(this);
//        tv3.setOnClickListener(this);
        //显示PopupWindow
        mPopWindow.showAsDropDown(view);
//        mPopWindow.showAtLocation(rootview, Gravity.BOTTOM, 0, 0);

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (null != mHeaderView) {
            mHeaderView.getBanner().startTurning(TURNING_TIME);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != mHeaderView) {
            mHeaderView.getBanner().stopTurning();
        }
    }

    @Override
    public void loadBannerSuccess(List<AdvertisementModule.AdvertisementData> advLists) {
        if (null != advLists) {
            mHeaderView.bindBannerData(advLists);
        }
    }

    @Override
    public void loadBannerFail(String failReason) {
    }

    @Override
    public void loadDataSuccess(List<VideoSpecialData> datas) {
        mWaitDialog.dismissWait();
        mDatas = new ArrayList<>();
        if (datas == null || datas.size() == 0) {
            showToast(getString(R.string.not_fount_data));
            return;
        }
        mDatas = datas;
        PreDocSpecialAdapter adapter = new PreDocSpecialAdapter(this, mDatas);
        adapter.setLoadingView(new LoadFooterView(this));
        adapter.openLoadAnimation();
        adapter.addHeaderView(mHeaderView);
        mRecyclerView.setAdapter(adapter);
        adapter.setOnLoadMoreListener(this);
        adapter.openLoadMore(mDatas.size(), true);
    }

    @Override
    public void loadDataFail(String failReasonStr) {
        mWaitDialog.dismissWait();
        if (TextUtils.isEmpty(failReasonStr)) {
            showToast(getString(R.string.service_err));
        } else {
            showToast(failReasonStr);
        }

        if (mDatas == null || mDatas.size() == 0) {
            mNoResultView.setVisibility(View.VISIBLE);
            mSwipeRefreshLayout.setVisibility(View.GONE);
            mNoResultImage.setImageResource(R.drawable.feedback_result_a);
            mTextView.setText(getString(R.string.result_no_wifi));
        }
    }

    @OnClick(R.id.no_result_layout_main)
    public void refreshAction() {
        if (!mTextView.getText().toString().equals(getString(R.string.result_no_wifi))) {
            return;
        }
        mNoResultView.setVisibility(View.GONE);
        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
        mPresenter.loadSpecialList(this, "0");
        mWaitDialog.showWait(getString(R.string.loading));
    }

    @Override
    public void loadNextPageSuccess(List<VideoSpecialData> data) {
        if (data == null || data.size() == 0) {
            //没有数据了，全部加载结束
            PreDocSpecialAdapter adapter = (PreDocSpecialAdapter) mRecyclerView.getAdapter();
            adapter.notifyDataChangedAfterLoadMore(false);
            View view = (View) getLayoutInflater().inflate(R.layout.loading_end, (ViewGroup) mRecyclerView.getParent(), false);
            adapter.addFooterView(view);
            return;
        }

        mDatas.addAll(data);

        PreDocSpecialAdapter adapter = (PreDocSpecialAdapter) mRecyclerView.getAdapter();
        adapter.notifyDataChangedAfterLoadMore(true);
    }

    @Override
    public void loadNextPageFail(String failReasonStr) {
        showToast(failReasonStr);
    }

    @Override
    public void onLoadMoreRequested() {
        int pageIndex = (mDatas.size() - 1) / ConstantCenter.PAGE_COUNT + ConstantCenter.FIRST_PAGE + 1;
        mPresenter.loadNextSpecialList(this, "0", pageIndex);
    }

    @Override
    public void onRefresh() {
        mPresenter.loadSpecialList(this, "0");
        mPresenter.loadBannerData(this, "");
        mWaitDialog.showWait(getString(R.string.loading));
        mSwipeRefreshLayout.endRefresh();
    }
}
