package top.cnzrg.call110.view;

import android.content.Context;
import android.graphics.ColorMatrixColorFilter;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import top.cnzrg.call110.util.LogUtils;

/**
 * 增加imageView点击变暗的效果
 *
 * @author lmy
 */
public class MyImageView extends android.support.v7.widget.AppCompatImageView {

    public MyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnTouchListener(VIEW_TOUCH_DARK);
    }


    public static final OnTouchListener VIEW_TOUCH_DARK = new OnTouchListener() {

        //变暗(三个-50，值越大则效果越深)
        public final float[] BT_SELECTED_DARK = new float[]{1, 0, 0, 0, -50, 0, 1,
                0, 0, -50, 0, 0, 1, 0, -50, 0, 0, 0, 1, 0};
        /*
        //变亮
        public final float[] BT_SELECTED_LIGHT = new float[] { 1, 0, 0, 0, 50, 0, 1,  
                0, 0, 50, 0, 0, 1, 0, 50, 0, 0, 0, 1, 0 };
        
        //恢复
        public final float[] BT_NOT_SELECTED = new float[] { 1, 0, 0, 0, 0, 0,  
                1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0 };  
         */

        /**
         * true和false在其中起着标志事件是否被消耗，如果消耗了就不再传递给其他控件了。
         * 如果没有消耗则还会传递给其他控件，触发其他控件的事件处理函数。
         * @param v
         * @param event
         * @return
         */
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                ImageView iv = (ImageView) v;
                iv.setColorFilter(new ColorMatrixColorFilter(BT_SELECTED_DARK));

                // 部分机型的长按抬起事件是   ACTION_CANCEL
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                ImageView iv = (ImageView) v;
                iv.clearColorFilter();

                LogUtils.w("up: ---------");
            }
            // 返回false才能继续执行点击事件，同时需要为使用了该MyImageView的控件设置点击事件
            return false;
        }


    };

}
