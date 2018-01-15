package com.example.administrator.a1511ashopping.view.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.a1511ashopping.R;
import com.example.administrator.a1511ashopping.model.bean.CountPriceBean;
import com.example.administrator.a1511ashopping.model.bean.SelectShopping_bean;
import com.example.administrator.a1511ashopping.presenter.SelectShopping_P;
import com.example.administrator.a1511ashopping.util.My_api;
import com.example.administrator.a1511ashopping.view.activity.adapter.MyExpandableAdapter;
import com.example.administrator.a1511ashopping.view.activity.interF.SelectShoppingV_I;
import com.google.gson.Gson;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SelectShoppingV_I {

    private ExpandableListView my_expandableListView;
    private CheckBox allcheck;
    private TextView total;
    private TextView gobuy;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == 0) {
                CountPriceBean countPriceBean = (CountPriceBean) msg.obj;

                total.setText("合计:¥" + countPriceBean.getPriceString());
                gobuy.setText("去结算(" + countPriceBean.getCount() + ")");
            }
        }
    };
    private SelectShopping_P selectShopping_p;
    private MyExpandableAdapter myExpandableAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //首先得展示出来,先是一个二级列表  找到控件
        my_expandableListView = findViewById(R.id.My_ExpandableListView);
        //全选框
        allcheck = findViewById(R.id.allcheck);
        //合计
        total = findViewById(R.id.total);
        //去结算
        gobuy = findViewById(R.id.gobuy);
        //去掉默认的指示器
        my_expandableListView.setGroupIndicator(null);


        allcheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myExpandableAdapter!=null){
                    //调用适配器中自定义的方法,实现  根据当前全选框的状态
                    myExpandableAdapter.setAllChildChecked(allcheck.isChecked());
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        //获取数据  设置二级列表
        selectShopping_p = new SelectShopping_P(this);
        selectShopping_p.getdata(My_api.goShopping);
    }

    @Override
    public void success(final String s) {



        //获取到数据
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if(s!=null){
                    //获取到数据,进行解析
                    Gson gson = new Gson();
                    SelectShopping_bean selectShopping_bean = gson.fromJson(s, SelectShopping_bean.class);

                    //设置二级列表的适配器
                    myExpandableAdapter = new MyExpandableAdapter(MainActivity.this, selectShopping_bean, handler, selectShopping_p);
                    my_expandableListView.setAdapter(myExpandableAdapter);
                    //设置列表全部展开
                    for (int i = 0; i < selectShopping_bean.getData().size(); i++) {
                        my_expandableListView.expandGroup(i);
                    }
                    //这只是逻辑
                    //1.根据某一个组中的二级所有的子条目是否选中,确定当前一级列表是否选中
                    for(int i=0;i<selectShopping_bean.getData().size();i++){//找到了每个一级列表
                        //根据当前一级列表下的所有二级列表是否选中判断当前一级列表是否选中
                        if(isChildInGroupChecked(i,selectShopping_bean)){
                            //如果当前的二级列表全部选中,那么选中当前的一级列表
                            selectShopping_bean.getData().get(i).setGroup_checked(true);
                        }
                    }
                    //全选的逻辑
                    //2.设置是否全选选中...根据所有的一级列表是否选中,确定全选是否选中
                    allcheck.setChecked(isGroupAllChecked(selectShopping_bean));
                    //3.计算总价和商品的数量
                    myExpandableAdapter.sendPriceAndCount();


                }

            }
        });
    }

    //根据全选框是否选中,设置所有的一级列表
    private boolean isGroupAllChecked(SelectShopping_bean selectShopping_bean) {
        for (int i = 0;i<selectShopping_bean.getData().size();i++){
            //如果有没有选中的组
            if(!selectShopping_bean.getData().get(i).isGroup_checked()){
                return false;
            }
        }
        return true;
    }

    //为了看当前的二级列表是否全部选中,全部选中返回true,如果有一个没选中,返回false
    private boolean isChildInGroupChecked(int i, SelectShopping_bean selectShopping_bean) {
      for(int j=0;j<selectShopping_bean.getData().get(i).getList().size();j++){
          //判断当前的一级列表中是否有没选中的二级列表,如果有返回false
          if(selectShopping_bean.getData().get(i).getList().get(j).getSelected()==0){
              return false;
          }

      }
        return true;
    }

}
