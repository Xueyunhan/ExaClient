package com.mandala.fuyou.activity.preuniversity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.alipay.sdk.app.PayTask;
import com.mandalat.basictools.BaseToolBarActivity;
import com.mandala.fuyou.R;
import com.mandala.fuyou.WebPayUrlActivity;
import com.mandala.fuyou.activity.home.BalanceActivity;
import com.example.perunimodule.live.play.TCLivePlayerActivity;
import com.mandala.fuyou.adapter.home.LivePayWayListAdapter;
import com.mandalat.basictools.controller.BroadcastCenter;
import com.mandalat.basictools.controller.ParamsCenter;
import com.mandalat.basictools.mvp.model.home.PayLoginBean;
import com.mandalat.basictools.mvp.model.home.PayWayModule;
import com.mandala.fuyou.presenter.home.PayLoginPresenter;
import com.mandalat.basictools.mvp.view.home.HomePayListener;
import com.mandalat.basictools.utils.DividerItemDecoration;
import com.mandalat.basictools.utils.aliPay.PayResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PayForLiveActivity extends BaseToolBarActivity implements HomePayListener {
    @BindView(R.id.rv_pay)
    RecyclerView mRecyclerView;
    @BindView(R.id.et_money)
    TextView mEtMoney;
    @BindView(R.id.tv_org)
    TextView tv_org;
    @BindView(R.id.tv_name)
    TextView tv_name;

    LivePayWayListAdapter mAdapter;


    List<PayWayModule> mList = new ArrayList<>();

    PayLoginPresenter mPresenter;
    PayLoginBean mDatabean = new PayLoginBean();
    public String paywaycode = "alipay";

    private static final int SDK_PAY_FLAG = 1;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        //发送待完善的广播
                        Intent unSubmitAction = new Intent(BroadcastCenter.CONSULT_STATE_UNSUMIT);
                        sendBroadcast(unSubmitAction);

                        Toast.makeText(PayForLiveActivity.this, "充值成功!", Toast.LENGTH_SHORT).show();

                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(PayForLiveActivity.this, "充值失败!", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_for_live);
        ButterKnife.bind(this);
        initToolbar(R.id.toolbar, R.id.toolbar_title, "确认服务");
        mPresenter = new PayLoginPresenter(this);
//        mPresenter.getPayLoginInfo(this);
//        mWaitDialog.showWait("加载中");
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
//        mRecyclerView.addItemDecoration(new RecycleViewDivider(
//                this, LinearLayoutManager.VERTICAL, 10, getResources().getColor(R.color.line)));
        initData();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.balance, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.balance) {
            Intent intent = new Intent(this, BalanceActivity.class);
            intent.putExtra("url", mDatabean.getChargeService());
            intent.putExtra("Bear", mDatabean.getToken());
            intent.putExtra("spcode", "WXFY");
            intent.putExtra("BizCode", mDatabean.getBizCode());
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void initData() {
//        UserInfo userinfo = UserManager.getInstance(this).getmUserInfo();
//        tv_org.setText(userinfo.getHospiatalName());
//        tv_name.setText(userinfo.getRealName());

        PayWayModule data = new PayWayModule();
        data.setLogo(R.drawable.pay_zhifubao);
        data.setPayway("支付宝");
        data.setPaycode("alipay");
        data.setSelected(true);
        mList.add(data);

        mAdapter = new LivePayWayListAdapter(this, mList);
        mRecyclerView.setAdapter(mAdapter);
    }


    @OnClick(R.id.btn_pay)
    public void pay() {

        startActivity(new Intent(this, TCLivePlayerActivity.class));
        finish();
//        if (!TextUtils.isEmpty(mEtMoney.getText().toString())) {
//            if (Double.parseDouble(mEtMoney.getText().toString()) <= 0) {
//                showToast("请输入充值金额！");
//
//            } else if (Double.parseDouble(mEtMoney.getText().toString()) >= 10000) {
//                showToast("充值金额不能高于10000！");
//            } else {
//                if (!TextUtils.isEmpty(mDatabean.getToken())) {
//                    mWaitDialog.showWait("支付中");
//                    mPresenter.getPrePayInfo(this, mDatabean.getChargeService(), mDatabean.getToken(), mDatabean.getBizCode(), mEtMoney.getText().toString());
//                } else {
//                    showToast("支付异常！");
//                }
//            }
//
//        } else {
//            showToast("请输入充值金额！");
//        }


    }

    @Override
    public void payloginSuccess(PayLoginBean data) {
        if (data != null) {
            mDatabean = data;
        }
        mWaitDialog.dismissWait();
    }

    @Override
    public void payloginFail(String error) {
        mWaitDialog.dismissWait();
        showToast(error);
    }

    @Override
    public void getprepaySuccess(String data) {
        if (!TextUtils.isEmpty(data)) {
            JSONObject object = JSONObject.parseObject(data);
            String orderguid = object.getString("orderguid");
            mPresenter.submitOrderPayInfo(this, mDatabean.getChargeService(), mDatabean.getToken(),
                    orderguid, paywaycode, mEtMoney.getText().toString());
        }


    }


    @Override
    public void getprepayFail(String error) {
        mWaitDialog.dismissWait();
        if (!TextUtils.isEmpty(error)) {
            showToast(error);
        }
    }

    @Override
    public void submitOrderSuccess(String data) {
        if (!TextUtils.isEmpty(data)) {
            JSONObject object = JSONObject.parseObject(data);
            final String urlparasstr = object.getString("urlparasstr");

            if (paywaycode.equals("ccbpay")) {
                Intent intent = new Intent(this, WebPayUrlActivity.class);
                intent.putExtra(ParamsCenter.WEB_URL, urlparasstr);
                intent.putExtra(ParamsCenter.SHOW_TITLE, "支付");
                startActivity(intent);
                finish();
            } else {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        PayTask payTask = new PayTask(PayForLiveActivity.this);
                        Map<String, String> result = payTask.payV2(urlparasstr, true);
                        Message msg = new Message();
                        msg.what = SDK_PAY_FLAG;
                        msg.obj = result;
                        mHandler.sendMessage(msg);
                    }
                };
                Thread payThread = new Thread(runnable);
                payThread.start();
            }
        }
        mWaitDialog.dismissWait();
    }

    @Override
    public void submitOrderFail(String error) {
        mWaitDialog.dismissWait();
        if (!TextUtils.isEmpty(error)) {
            showToast(error);
        }

    }
}
