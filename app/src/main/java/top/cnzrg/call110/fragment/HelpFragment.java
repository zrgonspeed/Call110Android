package top.cnzrg.call110.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import top.cnzrg.call110.R;
import top.cnzrg.call110.util.LogUtils;

public class HelpFragment extends Fragment {
    private final static String mName = "Help---------";
    /**
     * 上下文
     */
    private FragmentActivity mContext;

    // Fragment的布局View
    private View mView;

    private ExpandableListView elv_help;

    private String[] groupStringArr = {"语音报警", "地图定位", "紧急联系人"};
    private String[][] childStringArrs = {
            {"    在主界面点击\"语音报警\"图标，即会拨打110报警电话"},
            {"    在主界面点击\"地图定位\"图标，即可看到当前的地图定位，当使用GPS定位时且在室外，定位精确度最高。网络定位的精确度较低。"},
            {"    在主界面点击\"紧急联系人\"图标，进入紧急联系人界面，点击添加按钮弹出对话框，输入好姓名和号码后点击确定，即可添加。"},
    };

    class HelpElvAdapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            return groupStringArr.length;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return childStringArrs[groupPosition].length;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return groupStringArr[groupPosition];
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return childStringArrs[groupPosition][childPosition];
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition * 2;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return groupPosition * 2 + childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            View itemGroupView = LayoutInflater.from(parent.getContext()).inflate(R.layout.elv_item_group_help, null);

            TextView tvGroup = itemGroupView.findViewById(R.id.tv_group);

            Object groupData = getGroup(groupPosition);

            tvGroup.setText((groupPosition + 1) + "." + (String) groupData);

            return itemGroupView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View itemChildView = LayoutInflater.from(parent.getContext()).inflate(R.layout.elv_item_child_help, null);

            TextView tvChild = itemChildView.findViewById(R.id.tv_child);

            Object childData = getChild(groupPosition, childPosition);
            tvChild.setText((String) childData);

            return itemChildView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }

    //--------------------------------------------------------------------------------------------------------------
    public HelpFragment() {
    }

    public static HelpFragment newInstance() {
        HelpFragment fragment = new HelpFragment();
        return fragment;
    }

    private void initUI() {
        elv_help = mView.findViewById(R.id.elv_help);
    }

    public void initData() {
        LogUtils.e("initData()");
        elv_help.setAdapter(new HelpElvAdapter());
    }

    /**
     * onAttach()在fragment与Activity关联之后调调查用。
     * 需要注意的是，初始化fragment参数可以从getArguments()获得，但是，当Fragment附加到Activity之后，就无法再调用setArguments()。
     * 所以除了在最开始时，其它时间都无法向初始化参数添加内容。
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        LogUtils.i("onAttach: " + mName);
    }

    /**
     * 这个只是用来创建Fragment的。此时的Activity还没有创建完成。
     * 因为我们的Fragment也是Activity创建的一部分。所以如果你想在这里使用Activity中的一些资源，将会获取不到
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.i("onCreate " + mName);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.i("onCreateView " + mName);
        this.mView = inflater.inflate(R.layout.fragment_help, container, false);

        initUI();
        return this.mView;
    }

    /**
     * 在Activity的OnCreate()结束后，会调用此方法。所以到这里的时候，Activity已经创建完成！
     * 在这个函数中才可以使用Activity的所有资源。
     *
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtils.i("onActivityCreated " + mName);

        mContext = getActivity();
        initData();
    }

    /**
     * 当到OnStart()时，Fragment对用户就是可见的了。但用户还未开始与Fragment交互。
     * 在生命周期中也可以看到Fragment的OnStart()过程与Activity的OnStart()过程是绑定的。
     * 意义即是一样的。以前你写在Activity的OnStart()中来处理的代码，用Fragment来实现时，依然可以放在OnStart()中来处理。
     */
    @Override
    public void onStart() {
        super.onStart();
        LogUtils.i("onStart " + mName);
    }

    /**
     * 当这个fragment对用户可见并且正在运行时调用。这是Fragment与用户交互之前的最后一个回调。
     * 从生命周期对比中，可以看到，Fragment的OnResume与Activity的OnResume是相互绑定的，意义是一样的。
     * 它依赖于包含它的activity的Activity.onResume。当OnResume()结束后，就可以正式与用户交互了。
     */
    @Override
    public void onResume() {
        super.onResume();
        LogUtils.i("onResume " + mName);

    }

    // -------------------------------------

    /**
     * 此回调与Activity的OnPause()相绑定，与Activity的OnPause()意义一样。
     */
    @Override
    public void onPause() {
        super.onPause();
        LogUtils.i("onPause " + mName);

    }

    /**
     * 这个回调与Activity的OnStop()相绑定，意义一样。
     * 已停止的Fragment可以直接返回到OnStart()回调，然后调用OnResume()。
     */
    @Override
    public void onStop() {
        super.onStop();
        LogUtils.i("onStop " + mName);
    }

    /**
     * 如果Fragment即将被结束或保存，那么撤销方向上的下一个回调将是onDestoryView()。
     * 会将在onCreateView创建的视图与这个fragment分离。下次这个fragment若要显示，那么将会创建新视图。
     * 这会在onStop之后和onDestroy之前调用。这个方法的调用同onCreateView是否返回非null视图无关。
     * 它会潜在的在这个视图状态被保存之后以及它被它的父视图回收之前调用。
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtils.i("onDestroyView " + mName);
    }

    /**
     * 当这个fragment不再使用时调用。需要注意的是，它即使经过了onDestroy()阶段，但仍然能从Activity中找到，因为它还没有Detach。
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.i("onDestroy " + mName);

    }

    /**
     * Fragment生命周期中最后一个回调是onDetach()。
     * 调用它以后，Fragment就不再与Activity相绑定，它也不再拥有视图层次结构，它的所有资源都将被释放。
     */
    @Override
    public void onDetach() {
        super.onDetach();
        LogUtils.i("onDetach " + mName);
    }

}
