package com.qixingbang.qxb.common.listviewutil;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by cr30 on 2015/9/24.
 */
public class MyViewHold {
    private SparseArray<View> Views;
    private SparseArray<RelativeLayout>Layouts;
    private View mView;
    private View mconvertview;
    private int  mposition;
    public MyViewHold(Context context,int position,ViewGroup parent,int layoutId)
    {
        this.mconvertview= LayoutInflater.from(context).inflate(layoutId,parent,false);
        Views=new SparseArray<View>();
        Layouts=new SparseArray<RelativeLayout>();
        this.mposition=position;
        mconvertview.setTag(this);
    }
    public static MyViewHold get(Context context,int position,View mconvertview,ViewGroup parent,int layoutId)
    {
        if(mconvertview==null)
        {
            return new MyViewHold(context,position,parent,layoutId);
        }
        else {
            MyViewHold myViewHold=(MyViewHold)mconvertview.getTag();
            myViewHold.mposition=position;
            return myViewHold;
        }
    }

    public <T extends View>T getView(int viewId)
    {
        mView=Views.get(viewId);
        if(mView==null)
        {
            mView=mconvertview.findViewById(viewId);
            Views.put(viewId,mView);
        }
        return (T)mView;
    }
    public <T extends RelativeLayout>T getLayout(int layoutId)
    {
        RelativeLayout mLayout=Layouts.get(layoutId);
        if(mLayout==null)

        {
            mLayout=(RelativeLayout)mconvertview.findViewById(layoutId);
            Layouts.put(layoutId,mLayout);
        }
        return(T)mLayout;
    }
//    公布方法，方便其他类使用内部参数
    public View getconvert()
    {
        return mconvertview;
    }
    public int getposition()
    {
        return  mposition;
    }
//    补充方法可以直接设置控件内容
    public MyViewHold setText(int viewId,String text)
    {
        TextView mTextView=getView(viewId);
        mTextView.setText(text);
        return this;
    }
    public MyViewHold setImageResourse(int layoutId,int resId)
    {
        ImageView imageView=getView(layoutId);
        imageView.setImageResource(resId);
        return this;
    }
}
