package com.example.administrator.a1511ashopping.model;

import com.example.administrator.a1511ashopping.presenter.SelectShopping_P;
import com.example.administrator.a1511ashopping.presenter.interF.SelectShoppingP_I;
import com.example.administrator.a1511ashopping.util.My_api;
import com.example.administrator.a1511ashopping.util.OkHttp3Util;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/1/14.
 */

public class SelectShopping_M {

    private SelectShoppingP_I selectShoppingP_i;

    public SelectShopping_M(SelectShoppingP_I selectShoppingP_i) {
        this.selectShoppingP_i = selectShoppingP_i;
    }

    public void getdata(String goShopping) {

        HashMap<String, String> map = new HashMap<>();
        map.put("uid", "4123");
        OkHttp3Util.doPost(My_api.goShopping, map, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String string = response.body().string();
                    selectShoppingP_i.success(string);
                }
            }
        });
    }
}
