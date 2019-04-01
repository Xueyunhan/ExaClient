package com.mandala.fuyou.activity.preuniversity;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.mandala.fuyou.R;
import com.mandala.fuyou.adapter.NetworkImageHolder;
import com.mandala.fuyou.controller.BannerController;
import com.mandalat.basictools.mvp.model.AdvertisementModule;
import com.mandala.fuyou.widget.CusConvenientBanner;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 孕妇讲堂上的横幅
 */
public class SpecialHeaderView extends LinearLayout {

    @BindView(R.id.banner)
    CusConvenientBanner mCusConvenientBanner;

    public SpecialHeaderView(final Context context) {
        super(context);
        View rootView = LayoutInflater.from(context).inflate(R.layout.special_top_banner, this);
        ButterKnife.bind(this);

//        //动态改变banner的长宽，为72：25
//        int bannerWidth = (int) ApplicationUtil.getScreenWidth(context);
//        int bannerHeight = bannerWidth * 25 / 72;
//        LinearLayout.LayoutParams bannerParams = (LinearLayout.LayoutParams) mCusConvenientBanner.getLayoutParams();
//        bannerParams.width = bannerWidth;
//        bannerParams.height = bannerHeight;
//        mCusConvenientBanner.setLayoutParams(bannerParams);


    }

    public CusConvenientBanner getBanner() {
        return mCusConvenientBanner;
    }

    /**
     * 绑定横幅的数据
     *
     * @param bannerLists
     */
    public void bindBannerData(final List<AdvertisementModule.AdvertisementData> bannerLists) {
        if (null == bannerLists) {
            return;
        }

        List<String> bannerImageUrls = new ArrayList<>();
        for (AdvertisementModule.AdvertisementData adData : bannerLists) {

            bannerImageUrls.add(adData.getAdvImage());
        }

        mCusConvenientBanner.setPages(
                new CBViewHolderCreator<NetworkImageHolder>() {
                    @Override
                    public NetworkImageHolder createHolder() {
                        return new NetworkImageHolder();
                    }
                }, bannerImageUrls)
                //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
                .setPageIndicator(new int[]{R.drawable.ic_page_indicator, R.drawable.ic_page_indicator_focused});
        //监听跳转
        mCusConvenientBanner.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (null == bannerLists || bannerLists.size() <= position) {
                    return;
                }
                MobclickAgent.onEvent(getContext(), "forum_banner");
                BannerController.clickBannerItem(getContext(), bannerLists.get(position));
                if (BannerController.isInsuranceView(bannerLists.get(position))) {
                    //关闭当前界面
                    ((Activity) getContext()).finish();
                }
            }
        });
        mCusConvenientBanner.startTurning(3000);
    }

}
