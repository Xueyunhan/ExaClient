package com.mandala.fuyou.activity.preuniversity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.perunimodule.activity.PreUniSearchActivity;
import com.github.florent37.fiftyshadesof.FiftyShadesOf;
import com.mandalat.basictools.BaseToolBarActivity;
import com.mandala.fuyou.R;
import com.mandala.fuyou.adapter.preuniversity.PregnantForumAdapter;
import com.mandalat.basictools.mvp.model.AdvertisementModule;
import com.mandalat.basictools.mvp.model.home.HomeArticleModule;
import com.mandalat.basictools.mvp.model.preuniversity.PickVideoData;
import com.mandalat.basictools.mvp.model.preuniversity.VideoSpecialData;
import com.example.perunimodule.presenter.PregnantForumPresenter;
import com.mandalat.basictools.mvp.view.preuniversity.PregnantForumListener;
import com.mandalat.basictools.view.LoadFooterView;
import com.mandala.fuyou.view.PregnantForumHeaderView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ldy.com.baserecyclerview.refresh.PullToRefreshLayout;

/**
 * Created by ludeyuan on 16/6/25.
 * 孕妇讲堂
 */
public class PregnantForumActivity extends BaseToolBarActivity implements PregnantForumListener
        , PullToRefreshLayout.OnRefreshListener {

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
    @BindView(R.id.action_pre_search)
    LinearLayout action_pre_search;

    private List<PickVideoData> mDatas;
    private PregnantForumHeaderView mHeaderView;
    private PregnantForumPresenter mPresenter;
    static final int TURNING_TIME = 3 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pregnant_forum);
        ButterKnife.bind(this);
        initToolbar(R.id.toolbar, R.id.toolbar_title, R.string.service_pregnant_forum);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mHeaderView = new PregnantForumHeaderView(this);
        mPresenter = new PregnantForumPresenter(this);

        mPresenter.loadBannerData(this);
        mPresenter.getSpecialList(this);
        mPresenter.loadPickVideos(this, "1", "", "");
        mWaitDialog.showWait(getString(R.string.loading));

    }


    @OnClick(R.id.action_pre_search)
    public void search() {
        startActivity(new Intent(this, PreUniSearchActivity.class));
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
    public void getSpecialListSuccess(List<VideoSpecialData> list) {
        mHeaderView.notifyDatas(this, list);
    }

    @Override
    public void getSpecialListFail(String failReason) {

    }

    @Override
    public void loadDataSuccess(List<PickVideoData> datas) {
        mWaitDialog.dismissWait();

        if (datas == null || datas.size() == 0) {
            showToast(getString(R.string.not_fount_data));
            return;
        }

        mDatas = datas;
        PregnantForumAdapter adapter = new PregnantForumAdapter(this, mDatas);
        adapter.setLoadingView(new LoadFooterView(this));
        adapter.openLoadAnimation();
        adapter.addHeaderView(mHeaderView);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void loadDataFail(String failReasonStr) {
        if (TextUtils.isEmpty(failReasonStr)) {
            showToast(getString(R.string.service_err));
        } else {
            showToast(failReasonStr);
        }
        mWaitDialog.dismissWait();
    }

    @OnClick(R.id.no_result_layout_main)
    public void refreshAction() {
        if (!mTextView.getText().toString().equals(getString(R.string.result_no_wifi))) {
            return;
        }

        mNoResultView.setVisibility(View.GONE);
        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
        mPresenter.loadBannerData(this);
        mPresenter.loadPickVideos(this, "1", "", "");
        mWaitDialog.showWait(getString(R.string.loading));
    }

    @Override
    public void loadNextPageSuccess(List<HomeArticleModule> data) {
    }

    @Override
    public void loadNextPageFail(String failReasonStr) {
        showToast(failReasonStr);
    }

    @Override
    public void onRefresh() {
        mPresenter.loadBannerData(this);
        mPresenter.getSpecialList(this);
        mPresenter.loadPickVideos(this, "1", "", "");
        mWaitDialog.showWait(getString(R.string.loading));
        mSwipeRefreshLayout.endRefresh();
    }


}
