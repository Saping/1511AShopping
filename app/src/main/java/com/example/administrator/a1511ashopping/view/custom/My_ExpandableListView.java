package com.example.administrator.a1511ashopping.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

/**
 * Created by Administrator on 2018/1/14.
 * 为什么要创建这个自定义的二级列表,因为ScrollerView嵌套ListView的时候,
 * 会出现冲突,需要重新测量其高度
 */

public class My_ExpandableListView extends ExpandableListView {
    public My_ExpandableListView(Context context) {
        super(context);
    }

    public My_ExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public My_ExpandableListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //重新测量了它的高度,并且用上  ,就是在测量后,放上
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
