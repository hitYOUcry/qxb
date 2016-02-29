package com.qixingbang.qxb.common.views.ridecycle;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.utils.DensityUtils;
import com.qixingbang.qxb.R;
import com.qixingbang.qxb.beans.ridecycle.drycargo.DryCargoItemBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zqj on 2015/11/3 10:57.
 */
public class DryCargoView extends RelativeLayout {

    /**
     * custom part
     */
    private Context mContext;
    private List<DryCargoItemView> mDryCargoItemViewList;
    private List<DryCargoItemBean> mDryCargoList;

    TextView titleTextView;
    RelativeLayout relativeLayout;

    private DryCargoItemClickedListener mItemClickedListener;

    public DryCargoView(Context context, List<DryCargoItemBean> list, String brandTitle) {
        super(context);
        init(context, list, brandTitle);
    }

    public void setItemClickedListener(DryCargoItemClickedListener listener) {
        mItemClickedListener = listener;
    }

    private void init(Context context, List<DryCargoItemBean> list, String brandTitle) {
        mContext = context;
        inflate(mContext, R.layout.brand_view, this);

        /**
         * view init
         */
        titleTextView = (TextView) findViewById(R.id.textView_title);
        titleTextView.setText(brandTitle);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout_brand_content);

        /**
         * data init
         */
        mDryCargoList = list;
        mDryCargoItemViewList = new ArrayList<>();

        drawContent();
    }

    private void drawContent() {
        if (null == mDryCargoList || mDryCargoList.size() <= 0)
            return;
        int count = mDryCargoList.size();
        int raw;
        if (count % 4 == 0) {
            raw = count / 4;
        } else {
            raw = 1 + count / 4;
        }
        for (int i = 0; i < raw; i++) {
            int loopLength = (i == raw - 1) ? (count - i * 4) : 4;
            for (int j = 0; j < loopLength; j++) {
                final DryCargoItemBean dryCargo = mDryCargoList.get(4 * i + j);

                DryCargoItemView itemView = new DryCargoItemView(mContext, dryCargo);

                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                int leftMargin = 20 + 90 * j;
                int topMargin = 20 + 90 * i;
                layoutParams.leftMargin = DensityUtils.dp2px(mContext, leftMargin);
                layoutParams.topMargin = DensityUtils.dp2px(mContext, topMargin);
                itemView.setLayoutParams(layoutParams);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != mItemClickedListener) {
                            mItemClickedListener.dryCargoItemClicked(dryCargo);
                        }
                    }
                });
                relativeLayout.addView(itemView);
                mDryCargoItemViewList.add(itemView);
            }
        }
    }

    public interface DryCargoItemClickedListener {
        void dryCargoItemClicked(DryCargoItemBean dryCargo);
    }
}
