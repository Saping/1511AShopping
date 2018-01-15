package com.example.administrator.a1511ashopping.presenter;

import com.example.administrator.a1511ashopping.model.SelectShopping_M;
import com.example.administrator.a1511ashopping.presenter.interF.SelectShoppingP_I;
import com.example.administrator.a1511ashopping.view.activity.MainActivity;
import com.example.administrator.a1511ashopping.view.activity.interF.SelectShoppingV_I;

/**
 * Created by Administrator on 2018/1/14.
 */

public class SelectShopping_P implements SelectShoppingP_I {

    private SelectShopping_M selectShopping_m;
    private SelectShoppingV_I selectShoppingV_i;

    public SelectShopping_P(SelectShoppingV_I selectShoppingV_i) {
        this.selectShoppingV_i = selectShoppingV_i;
        selectShopping_m = new SelectShopping_M(this);
    }

    public void getdata(String goShopping) {
        selectShopping_m.getdata(goShopping);
    }

    @Override
    public void success(String s) {
        selectShoppingV_i.success(s);
    }
}
