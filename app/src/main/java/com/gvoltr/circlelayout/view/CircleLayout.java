package com.gvoltr.circlelayout.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import com.gvoltr.circlelayout.R;

/**
 * Created by Stanislav Gavrosh on 8/5/15.
 */
public class CircleLayout extends ViewGroup implements View.OnClickListener {

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    private static final String LOG_TAG = CircleLayout.class.getName();
    private OnItemClickListener mItemClickListener;

    private Adapter adapter;
    private final DataSetObserver observer = new DataSetObserver() {

        @Override
        public void onChanged() {
            refreshViewsFromAdapter();
        }

        @Override
        public void onInvalidated() {
            removeAllViews();
        }
    };

    private int mRadius;

    public CircleLayout(Context context) {
        super(context);
    }

    public CircleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initXmlParams(attrs);
    }

    public CircleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initXmlParams(attrs);
    }

    private void initXmlParams(AttributeSet attrs) {
        TypedArray arr = getContext().obtainStyledAttributes(attrs, R.styleable.CircleLayout);

        //set the minimum radius to 150dp
        final int minRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150,
                getResources().getDisplayMetrics());

        mRadius = Math.max(arr.getDimensionPixelSize(R.styleable.CircleLayout_radius, minRadius),
                minRadius);
        arr.recycle();
    }

    public void setAdapter(Adapter adapter) {
        if (this.adapter != null) {
            this.adapter.unregisterDataSetObserver(observer);
        }
        this.adapter = adapter;
        if (this.adapter != null) {
            this.adapter.registerDataSetObserver(observer);
        }
        initViewsFromAdapter();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    public int getRadius() {
        return mRadius;
    }

    public void setRadius(int radius) {
        this.mRadius = radius;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int childCount = getChildCount();
        int childWidth = 0;
        int childHeight = 0;

        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            childWidth = child.getMeasuredWidth();
            childHeight = child.getMeasuredHeight();
        }

        int desiredWidth = mRadius * 2 + childWidth;
        int desiredHeight = mRadius * 2 + childHeight;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(desiredHeight, heightSize);
        } else {
            //Be whatever you want
            height = desiredHeight;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        layoutChildrens(l, t, r, b);
    }

    private void layoutChildrens(int left, int top, int right, int bottom) {
        float stepAngle = 360f / getChildCount();
        float startAngle = 270f;
        Point viewCenter = new Point((right - left) / 2, (bottom - top) / 2);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            float angleForElement = startAngle + stepAngle * i;
            Point childCenterPosition = getPointOnCircle(viewCenter, mRadius, angleForElement);

            int childLeft = childCenterPosition.x - childWidth / 2;
            int childTop = childCenterPosition.y - childHeight / 2;

            child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
        }
    }

    protected void initViewsFromAdapter() {
        removeAllViews();
        if (adapter != null) {
            for (int i = 0; i < adapter.getCount(); i++) {
                View element = adapter.getView(i, null, this);
                element.setOnClickListener(this);
                addView(element, i);
            }
        }
    }

    protected void refreshViewsFromAdapter() {
        int childCount = getChildCount();
        int adapterSize = adapter.getCount();
        int reuseCount = Math.min(childCount, adapterSize);

        for (int i = 0; i < reuseCount; i++) {
            adapter.getView(i, getChildAt(i), this);
        }

        if (childCount < adapterSize) {
            for (int i = childCount; i < adapterSize; i++) {
                addView(adapter.getView(i, null, this), i);
            }
        } else if (childCount > adapterSize) {
            removeViews(adapterSize, childCount);
        }
    }

    @Override
    public void onClick(View v) {
        if (mItemClickListener != null) {
            mItemClickListener.onItemClick(v, indexOfChild(v));
        }
    }

    private Point getPointOnCircle(Point center, float radius, float angle) {
        Point p = new Point();
        float anglesToPi = (float) (((Math.PI) / 180) * angle);
        p.set((int) (center.x + (Math.cos(anglesToPi) * radius)), (int) (center.y + (Math.sin(anglesToPi) * radius)));
        return p;
    }
}
