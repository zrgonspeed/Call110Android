package top.cnzrg.call110.view;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * FileName: MyDecoration
 * Author: ZRG
 * Date: 2019/8/27 17:18
 */
public class MyDecoration extends RecyclerView.ItemDecoration {
    /**
     * step1 获得条目的偏移量 ---- 就相当于空出一块矩形空间 会在矩形空间里进行绘制
     * 目标针对每一个item个体
     *
     * @param outRect
     * @param view
     * @param parent
     * @param state
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
    }

    /**
     * step2 在item间绘制  常用于绘制分割线
     * 针对整个Recyclerview 绘制需要循环遍历item子布局 然后方能针对具体的item进行增加绘制
     *
     * @param c
     * @param parent
     * @param state
     */
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
    }

    /**
     * 在item上绘制
     *
     * @param c
     * @param parent
     * @param state
     */
    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }
}
