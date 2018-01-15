package com.example.administrator.a1511ashopping.view.activity.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.a1511ashopping.R;
import com.example.administrator.a1511ashopping.model.bean.CountPriceBean;
import com.example.administrator.a1511ashopping.model.bean.SelectShopping_bean;
import com.example.administrator.a1511ashopping.presenter.SelectShopping_P;
import com.example.administrator.a1511ashopping.util.My_api;
import com.example.administrator.a1511ashopping.util.OkHttp3Util;
import com.example.administrator.a1511ashopping.view.activity.MainActivity;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/1/14.
 */

public class MyExpandableAdapter extends BaseExpandableListAdapter {

    private SelectShopping_P selectShopping_p;
    private Handler handler;
    private Context context;
    private SelectShopping_bean selectShopping_bean;
    private int childIndex;
    private int allIndex;

    public MyExpandableAdapter(Context context, SelectShopping_bean selectShopping_bean, Handler handler, SelectShopping_P selectShopping_p) {
        this.context = context;
        this.selectShopping_bean = selectShopping_bean;
        this.handler = handler;
        this.selectShopping_p = selectShopping_p;
    }

    @Override
    public int getGroupCount() {
        return selectShopping_bean.getData().size();
    }

    @Override
    public int getChildrenCount(int i) {
        return selectShopping_bean.getData().get(i).getList().size();
    }

    @Override
    public Object getGroup(int i) {
        return selectShopping_bean.getData().get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return selectShopping_bean.getData().get(i).getList().get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    //在这里加载 优化
    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        final groupVH holder;
        if (view == null) {
            view = View.inflate(context, R.layout.group_layout, null);
            holder = new groupVH();
            holder.group_check = view.findViewById(R.id.group_check);
            holder.group_name = view.findViewById(R.id.group_name);
            view.setTag(holder);
        } else {
            holder = (groupVH) view.getTag();
        }

        //为了方便写   找到当前的一级列表
        final SelectShopping_bean.DataBean dataBean = selectShopping_bean.getData().get(i);
        //赋值
        holder.group_check.setChecked(dataBean.isGroup_checked());//设置单选框状态
        holder.group_name.setText(dataBean.getSellerName());//设置一级列表的名字
        holder.group_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //让当前的所有二级列表随我变化
                childIndex = 0;
                //更新
                updateChildCheckedInGroup(holder.group_check.isChecked(),dataBean);
            }
        });
        return view;
    }

    //让所有的二级列表都随着当前的一级列表变化
    private void updateChildCheckedInGroup(final boolean checked, final SelectShopping_bean.DataBean dataBean) {

        final SelectShopping_bean.DataBean.ListBean listBean = dataBean.getList().get(childIndex);
        Map<String, String> params = new HashMap<>();
        params.put("uid","4123");
        params.put("sellerid", String.valueOf(listBean.getSellerid()));
        params.put("pid", String.valueOf(listBean.getPid()));
        params.put("selected", String.valueOf(checked ? 1:0));
        params.put("num", String.valueOf(listBean.getNum()));

        OkHttp3Util.doPost(My_api.ShoppingUpdata, params, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    childIndex++;
                    if(childIndex<dataBean.getList().size()){
                        //继续更新

                        updateChildCheckedInGroup(checked,dataBean);
                    }else{
                        //查看购物车
                        selectShopping_p.getdata(My_api.goShopping);
                    }
                }
            }
        });

    }

    //在这里加载二级列表 优化
    @Override
    public View getChildView(final int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        childVH holder;
        if (view == null) {
            view = View.inflate(context, R.layout.child_layout, null);
            holder = new childVH();
            holder.child_check = view.findViewById(R.id.child_check);
            holder.child_image = view.findViewById(R.id.child_image);
            holder.content = view.findViewById(R.id.content);
            holder.price = view.findViewById(R.id.price);
            holder.jia = view.findViewById(R.id.jia);
            holder.count = view.findViewById(R.id.count);
            holder.jian = view.findViewById(R.id.jian);
            holder.btn = view.findViewById(R.id.btn);
            view.setTag(holder);
        } else {
            holder = (childVH) view.getTag();
        }
        final SelectShopping_bean.DataBean.ListBean listBean = selectShopping_bean.getData().get(i).getList().get(i1);
        //赋值
        holder.child_check.setChecked(listBean.getSelected()==0?false:true);
        String[] split = listBean.getImages().split("\\|");
        Glide.with(context).load(split[0]).into(holder.child_image);
        holder.content.setText(listBean.getTitle());
        holder.price.setText(listBean.getBargainPrice()+"");
        holder.count.setText(listBean.getNum()+"");

        //当我点击二级列表的单选框的时候
        holder.child_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //当我点击二级列表的单选框的时候更新接口,重新访问数据
                HashMap<String, String> map = new HashMap<>();
                map.put("uid","4123");
                map.put("sellerid",listBean.getSellerid()+"");
                map.put("pid",listBean.getPid()+"");
                map.put("num",listBean.getNum()+"");
                map.put("selected", String.valueOf(listBean.getSelected()==0?1:0));

           OkHttp3Util.doPost(My_api.ShoppingUpdata, map, new Callback() {
               @Override
               public void onFailure(Call call, IOException e) {

               }

               @Override
               public void onResponse(Call call, Response response) throws IOException {
                    //在重新获取数据展示
                   selectShopping_p.getdata(My_api.goShopping);
               }
           });

            }
        });
        //加号
        holder.jia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //请求更新购物车接口
                Map<String, String> params = new HashMap<>();
                params.put("uid","4123");
                params.put("sellerid", String.valueOf(listBean.getSellerid()));
                params.put("pid", String.valueOf(listBean.getPid()));
                params.put("selected", String.valueOf(listBean.getSelected()));//listBean.getSelected()...0--->1,,,1--->0

                params.put("num", String.valueOf(listBean.getNum() +1 ));

                OkHttp3Util.doPost(My_api.ShoppingUpdata, params, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if(response.isSuccessful()){
                            selectShopping_p.getdata(My_api.goShopping);
                        }
                    }
                });
            }
        });
        //减号
        holder.jian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, String> params = new HashMap<>();
                params.put("uid","4123");
                params.put("sellerid", String.valueOf(listBean.getSellerid()));
                params.put("pid", String.valueOf(listBean.getPid()));
                params.put("selected", String.valueOf(listBean.getSelected()));//listBean.getSelected()...0--->1,,,1--->0

                params.put("num", String.valueOf(listBean.getNum() -1 ));

                OkHttp3Util.doPost(My_api.ShoppingUpdata, params, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if(response.isSuccessful()){
                            selectShopping_p.getdata(My_api.goShopping);
                        }
                    }
                });
            }
        });
        //删除
        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> map = new HashMap<>();
                map.put("uid","4123");
                map.put("pid",String.valueOf(listBean.getPid()));

                OkHttp3Util.doPost(My_api.ShoppingDelete, map, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                         if(response.isSuccessful()){
                             selectShopping_p.getdata(My_api.goShopping);
                         }
                    }
                });
            }
        });
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    //实现全选框,根据传过来的当前全选框的状态
    public void setAllChildChecked(boolean checked) {
        //通过遍历,把所有的孩子装到一个大的集合中
        List<SelectShopping_bean.DataBean.ListBean> allList = new ArrayList<>();
        for (int i=0;i<selectShopping_bean.getData().size();i++){
            for (int j = 0;j<selectShopping_bean.getData().get(i).getList().size();j++){
                allList.add(selectShopping_bean.getData().get(i).getList().get(j));

            }
        }
        //更新每一个子孩子的状态...递归
        allIndex = 0;

        updateAllChild(checked,allList);

    }

    private void updateAllChild(final boolean checked, final List<SelectShopping_bean.DataBean.ListBean> allList) {
        //得到每一个子孩子
        SelectShopping_bean.DataBean.ListBean listBean = allList.get(allIndex);
        //更新
        HashMap<String, String> map = new HashMap<>();
        map.put("uid","4123");
        map.put("sellerid",listBean.getSellerid()+"");
        map.put("pid",listBean.getPid()+"");
        map.put("num",listBean.getNum()+"");
        map.put("selected", String.valueOf(checked ?1:0));

        OkHttp3Util.doPost(My_api.ShoppingUpdata, map, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                  if(response.isSuccessful()){
                      allIndex++;
                      if(allIndex<allList.size()){
                          updateAllChild(checked,allList);
                      }else{
                          //查询购物车
                          selectShopping_p.getdata(My_api.goShopping);
                      }

                  }
            }
        });

    }

    //计算商品价格 和 数量
    public void sendPriceAndCount() {
        //先定义两个变量
        double price = 0;
        int count = 0;
        //找到所有的一级列表
        for (int i=0;i<selectShopping_bean.getData().size();i++){
            //当前一级列表有集合,然后找到每个二级列表
            List<SelectShopping_bean.DataBean.ListBean> listBeans = selectShopping_bean.getData().get(i).getList();
            for (int j = 0; j< listBeans.size(); j++){
                //这个是当前的二级列表
                SelectShopping_bean.DataBean.ListBean listBean = listBeans.get(j);

                //选中的时候计算价格和数量    如果这个二级列表是选中的状态
                if (listBean.getSelected() == 1){
                    //计算价钱  数量
                    price += listBean.getBargainPrice() * listBean.getNum();
                    count += listBean.getNum();
                }

            }
        }
        //目的是为了让价钱更精准一些
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String priceString = decimalFormat.format(price);

        //封装一下
        CountPriceBean countPriceBean = new CountPriceBean(priceString, count);
        //发送给activity/fragment进行显示

        Message msg = Message.obtain();

        msg.what = 0;
        msg.obj = countPriceBean;
        handler.sendMessage(msg);
    }

    class groupVH {
        CheckBox group_check;
        TextView group_name;
    }

    class childVH {
        CheckBox child_check;
        ImageView child_image;
        TextView content;
        TextView price;
        TextView jia;
        TextView count;
        TextView jian;
        Button btn;
    }
}
