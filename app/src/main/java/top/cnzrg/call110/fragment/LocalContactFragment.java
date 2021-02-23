package top.cnzrg.call110.fragment;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import top.cnzrg.call110.R;
import top.cnzrg.call110.dao.EmergencyContactDao;
import top.cnzrg.call110.pojo.EmergencyContact;
import top.cnzrg.call110.pojo.LocalContact;
import top.cnzrg.call110.util.LogUtils;
import top.cnzrg.call110.util.ToastUtils;

public class LocalContactFragment extends Fragment implements View.OnTouchListener {
    private final static String mName = "LocalContact---------";
    /**
     * 上下文
     */
    private FragmentActivity mContext;

    // Fragment的布局View
    private View mView;

    // 放置本地联系人的列表控件
    private RecyclerView rv_local_contact;

    private Handler mHandler = new Handler() {

        // 告知RecyclerView可以去设置数据适配器
        @Override
        public void handleMessage(Message msg) {
            LogUtils.w("本地联系人集合大小: " + mContacts.size());

            if (mMyAdapter == null) {
                LogUtils.w("mMyAdapter == null");

                mMyAdapter = new MyAdapter(mContacts);
                rv_local_contact.setAdapter(mMyAdapter);
                return;
            }
            LogUtils.w("notifyDataSetChanged");
            mMyAdapter.notifyDataSetChanged();
        }
    };

    private MyAdapter mMyAdapter;
    private List<LocalContact> mContacts = new ArrayList<>();

    // 操作数据库
    private EmergencyContactDao mDao;

    //--------------------------------------------------------------------------------------------------------------
    public LocalContactFragment() {
    }

    public static LocalContactFragment newInstance() {
        LocalContactFragment fragment = new LocalContactFragment();
        return fragment;
    }

    private void initUI() {
        // 本地联系人列表的控件
        rv_local_contact = mView.findViewById(R.id.rv_local_contact);
        rv_local_contact.setLayoutManager(new LinearLayoutManager(null));

        // 返回箭头
        ImageView iv_arror_back = mView.findViewById(R.id.iv_arror_back);
        iv_arror_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 返回上一个fragment
                FragmentSetting.backPreFragment(mContext);
            }
        });

    }

    public void initData() {
        mDao = EmergencyContactDao.getInstance(mContext);

        // 添加系统分隔线
//        rv_local_contact.addItemDecoration(new DividerItemDecoration(mContext.getApplicationContext(), DividerItemDecoration.VERTICAL));

        new Thread(new Runnable() {
            @Override
            public void run() {

                // 获取本地联系人列表
                // 1.获取内容解析器的对象
                ContentResolver contentResolver = mContext.getContentResolver();
                // 2.查询系统联系人数据库(要加读取联系人权限)
                Cursor cursor = contentResolver.query(Uri.parse("content://com.android.contacts/raw_contacts"),
                        new String[]{"contact_id"},
                        null,   // 查询条件 "a = ?"
                        null,
                        null
                );

                mContacts.clear();

                // 3.循环游标，直到没有数据
                while (cursor.moveToNext()) {
                    String id = cursor.getString(0);

                    if (id == null)
                        continue;

                    // Log.i(tag, id);

                    // 4. 根据用户唯一性id，查询data表和mimetype表生成的视图，获取data和mimetype字段
                    Cursor indexCursor = contentResolver.query(
                            Uri.parse("content://com.android.contacts/data"),
                            new String[]{"data1", "mimetype"},
                            "raw_contact_id = ?",
                            new String[]{id},
                            null
                    );

                    LocalContact lc = new LocalContact();

                    // 5.获取每一个联系人的电话号码以及姓名，数据类型
                    while (indexCursor.moveToNext()) {
                        String data = indexCursor.getString(0);
                        String mimetype = indexCursor.getString(1);

                        LogUtils.i("data = " + data);// data1字段
                        LogUtils.i("mimetype = " + mimetype);// mimetype字段

                        if (mimetype.equals("vnd.android.cursor.item/phone_v2")) {
                            if (!TextUtils.isEmpty(data)) {
                                lc.setPhone(data);
                            }
                        } else if (mimetype.equals("vnd.android.cursor.item/name")) {
                            if (!TextUtils.isEmpty(data)) {
                                lc.setName(data);
                            }
                        }
                    }

                    indexCursor.close();

                    if (!TextUtils.isEmpty(lc.getPhone()))
                        mContacts.add(lc);
                }

                cursor.close();

                // 告知主线程可以去使用包含数据的集合
                mHandler.sendEmptyMessage(0);
            }
        }).start();

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
        this.mView = inflater.inflate(R.layout.fragment_local_contact, container, false);

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

    /**
     * 紧急联系人的适配器
     */
    private class MyAdapter extends RecyclerView.Adapter {
        private List<LocalContact> contacts;

        public MyAdapter(List<LocalContact> contacts) {
            this.contacts = contacts;
        }

        /**
         * 这个方法主要生成为每个Item inflater出一个View，但是该方法返回的是一个ViewHolder。
         * 该方法把View直接封装在ViewHolder中，然后我们面向的是ViewHolder这个实例，当然这个ViewHolder需要我们自己去编写。
         *
         * @param viewGroup
         * @param i
         * @return
         */
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_item_lc, viewGroup, false);
            MyViewHolder viewHolder = new MyViewHolder(view);

            return viewHolder;
        }

        /**
         * 这个方法主要用于适配渲染数据到View中。方法提供给你了一viewHolder而不是原来的convertView。
         *
         * @param viewHolder
         * @param i
         */
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
            final LocalContact lc = contacts.get(i);

            MyViewHolder holder = (MyViewHolder) viewHolder;
            holder.tv_name.setText(lc.getName());
            holder.tv_phone.setText(lc.getPhone());

            // 添加按钮的点击事件
            holder.iv_local_item_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 封装成紧急联系人pojo
                    EmergencyContact ec = new EmergencyContact();
                    ec.setPhone(lc.getPhone().trim().replace(" ", ""));
                    ec.setName(lc.getName());

                    // 插入到数据库
                    Integer _id = mDao.insert(ec);

                    // 封装id给实体，否则会出现添加后不能执行修改和删除操作
                    ec.set_id(_id);

                    // 跳转到紧急联系人Fragment
                    // 将该实体加入到控件列表

                    // 返回上一个fragment
                    mContext.getSupportFragmentManager().popBackStack();

                    // 将紧急联系人pojo添加到紧急联系人的Fragment的rv控件中
                    FragmentSetting.emergencyContactFragment.addToRvList(ec);

                    ToastUtils.toast(mContext, "添加成功！");
                }
            });
        }

        @Override
        public int getItemCount() {
            return contacts.size();
        }

        private class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView tv_name;
            private TextView tv_phone;
            private ImageView iv_local_item_add;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                tv_name = itemView.findViewById(R.id.tv_name);
                tv_phone = itemView.findViewById(R.id.tv_phone);

                // 添加 的图片
                iv_local_item_add = itemView.findViewById(R.id.iv_local_item_add);

            }
        }
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // 拦截触摸事件，防止泄露下去
        view.setOnTouchListener(this);
    }
}
