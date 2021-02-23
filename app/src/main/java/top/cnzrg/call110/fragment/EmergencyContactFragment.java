package top.cnzrg.call110.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.List;

import top.cnzrg.call110.R;
import top.cnzrg.call110.dao.EmergencyContactDao;
import top.cnzrg.call110.pojo.EmergencyContact;
import top.cnzrg.call110.util.CallUtils;
import top.cnzrg.call110.util.LogUtils;
import top.cnzrg.call110.util.ToastUtils;
import top.cnzrg.call110.util.ViewUtils;

public class EmergencyContactFragment extends Fragment implements View.OnTouchListener {
    private final static String mName = "EmergencyContact---------";
    /**
     * 上下文
     */
    private FragmentActivity mContext;

    // Fragment的布局View
    private View mView;

    // 放置紧急联系人的列表
    private RecyclerView rv_emergency_contact;

    // 操作数据库
    private EmergencyContactDao mDao;

    private Handler mHandler = new Handler() {

        // 告知RecyclerView可以去设置数据适配器
        @Override
        public void handleMessage(Message msg) {
            LogUtils.w("紧急联系人集合大小: " + mContacts.size());

            if (mMyAdapter == null) {
                LogUtils.w("mMyAdapter == null");

                mMyAdapter = new MyAdapter(mContacts);
                rv_emergency_contact.setAdapter(mMyAdapter);
                return;
            }

            LogUtils.w("notifyDataSetChanged");
            mMyAdapter.notifyDataSetChanged();

            LogUtils.e("添加1个紧急联系人从本地联系人");
            addToRvList(mEc);
        }
    };

    private MyAdapter mMyAdapter;
    private List<EmergencyContact> mContacts;

    //--------------------------------------------------------------------------------------------------------------
    public EmergencyContactFragment() {
    }

    public static EmergencyContactFragment newInstance(EmergencyContact ec) {
        EmergencyContactFragment fragment = new EmergencyContactFragment();
        Bundle bundle = new Bundle();

        bundle.putSerializable("key", ec);
        fragment.setArguments(bundle);

        return fragment;
    }

    public static EmergencyContactFragment newInstance() {
        EmergencyContactFragment fragment = new EmergencyContactFragment();
        return fragment;
    }

    /**
     * 弹出 添加紧急联系人  的对话框
     */
    private void showEcAddDialog() {
        final PopupWindow popupWindow;

        View pop_window_view = LayoutInflater.from(this.mContext).inflate(R.layout.dialog_ec_add, null);
        popupWindow = new PopupWindow(
                pop_window_view, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
        );

        // 对话框参数设置
        popupWindow.setFocusable(true);
        popupWindow.setAnimationStyle(R.style.MyPopupWindow_anim_style);
        popupWindow.showAtLocation(mContext.getWindow().getDecorView(),
                Gravity.CENTER, 0, 0
        );

        // 获取对话框中的控件
        final EditText et_name = pop_window_view.findViewById(R.id.et_name);
        final EditText et_phone = pop_window_view.findViewById(R.id.et_phone);

        Button bt_add_confirm = pop_window_view.findViewById(R.id.bt_add_confirm);
        bt_add_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = et_name.getText().toString().trim();
                String phone = et_phone.getText().toString().trim().replace(" ", "");
                LogUtils.i("(name=" + name + ", phone=" + phone + ")");

                // 数据校验
                // 姓名不能为空
                // 手机号不能为空
                if (TextUtils.isEmpty(name)) {
                    ToastUtils.toast(mContext, "请输入姓名");
                    return;
                }

                if (TextUtils.isEmpty(phone)) {
                    ToastUtils.toast(mContext, "请输入手机号");
                    return;
                }

                // 封装"紧急联系人"实体
                EmergencyContact emergencyContact = new EmergencyContact();
                emergencyContact.setName(name);
                emergencyContact.setPhone(phone);

                // 插入到数据库
                Integer _id = mDao.insert(emergencyContact);

                // 封装id给实体，否则会出现添加后不能执行修改和删除操作
                emergencyContact.set_id(_id);

                // 将该实体加入到控件列表
                addToRvList(emergencyContact);

                // 同时关闭输入法，如果打开的话
                ViewUtils.hideSoftInput(et_name, mContext);
                ViewUtils.hideSoftInput(et_phone, mContext);

                // 关闭对话框
                popupWindow.dismiss();

                ToastUtils.toast(mContext, "添加成功！");
            }
        });

        Button bt_add_cancel = pop_window_view.findViewById(R.id.bt_add_cancel);
        // 点击取消按钮关闭当前页面
        bt_add_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 同时关闭输入法，如果打开的话
                ViewUtils.hideSoftInput(et_name, mContext);
                ViewUtils.hideSoftInput(et_phone, mContext);

                popupWindow.dismiss();
            }
        });

        // 对话框关闭事件监听
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //关闭了 popwindow，需要让界面完全透明
                ViewUtils.changeBackgroundAlpha(1.0f, mContext);
            }
        });

    }

    /**
     * 将实体添加到 rv控件中
     *
     * @param emergencyContact
     */
    public void addToRvList(EmergencyContact emergencyContact) {
        // 添加到集合中, 最顶部
        mContacts.add(0, emergencyContact);

        // 通知数据适配器刷新
        if (mMyAdapter != null) {
//                    mMyAdapter.notifyDataSetChanged();
            mMyAdapter.notifyItemInserted(0);
        }

        // 根据数据添加在集合中的位置，让rv滚动到该位置
        rv_emergency_contact.getLayoutManager().scrollToPosition(0);
    }

    private void initUI() {
        // 紧急联系人列表的控件
        rv_emergency_contact = mView.findViewById(R.id.rv_emergency_contact);
        rv_emergency_contact.setLayoutManager(new LinearLayoutManager(null));

        // 设置Item添加和移除的动画
        rv_emergency_contact.setItemAnimator(new DefaultItemAnimator());

        // 添加 按钮
        Button bt_emergency_add = mView.findViewById(R.id.bt_emergency_add);
        bt_emergency_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 弹出对话框  添加紧急联系人
                showEcAddDialog();
            }
        });

        // 从手机本地联系人列表添加
        Button bt_emergency_add_from_local = mView.findViewById(R.id.bt_emergency_add_from_local);
        bt_emergency_add_from_local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 切换到本地联系人列表的Fragment
                FragmentSetting.switchFragment(LocalContactFragment.newInstance(), mContext);
            }
        });

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
//        rv_emergency_contact.addItemDecoration(new DividerItemDecoration(mContext.getApplicationContext(), DividerItemDecoration.VERTICAL));

        // 从数据库获取 紧急联系人列表, 需要在子线程中执行，因为查询数据库是耗时操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 从数据库获取 紧急联系人集合
                mContacts = mDao.selectAll();

                // 告知主线程可以去使用包含数据的集合
                mHandler.sendEmptyMessage(0);

            }
        }).start();

    }

    /**
     * 外界传入的新增的紧急联系人
     */
    private EmergencyContact mEc;

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

        Bundle args = getArguments();
        if (args != null) {
            mEc = (EmergencyContact) args.getSerializable("key");

        }
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
        this.mView = inflater.inflate(R.layout.fragment_ec_contact, container, false);

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

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // 拦截触摸事件，防止泄露下去
        view.setOnTouchListener(this);
    }
    /**
     * 紧急联系人的适配器
     */
    private class MyAdapter extends RecyclerView.Adapter {
        private List<EmergencyContact> contacts;

        public MyAdapter(List<EmergencyContact> contacts) {
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
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_item_ec, viewGroup, false);
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
            final EmergencyContact ec = contacts.get(i);

            MyViewHolder holder = (MyViewHolder) viewHolder;
            holder.tv_name.setText(ec.getName());
            holder.tv_phone.setText(ec.getPhone());

            // 编辑按钮的点击事件
            holder.iv_em_item_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 弹出 编辑 紧急联系人的对话框 popwindow
                    // 数据回显，  要获得选中的条目的id
                    showEcEditDialog(ec);
                    LogUtils.w("数据从条目控件中回显：name=(" + ec.getName() + ") phone=(" + ec.getPhone() + ")");
                }
            });

            // 拨打电话
            holder.iv_em_item_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CallUtils.callPhone(ec.getPhone(), mContext);
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
            private ImageView iv_em_item_edit;
            private ImageView iv_em_item_call;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                tv_name = itemView.findViewById(R.id.tv_name);
                tv_phone = itemView.findViewById(R.id.tv_phone);

                // 电话 的图片
                iv_em_item_call = itemView.findViewById(R.id.iv_em_item_call);

                // 编辑 的图片
                iv_em_item_edit = itemView.findViewById(R.id.iv_em_item_edit);

            }
        }
    }

    /**
     * 弹出 修改紧急联系人信息的对话框
     */
    private void showEcEditDialog(final EmergencyContact ec) {
        final PopupWindow popupWindow;

        View edit_window_view = LayoutInflater.from(this.mContext).inflate(R.layout.dialog_ec_edit, null);
        popupWindow = new PopupWindow(
                edit_window_view, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
        );

        // 对话框参数设置
        popupWindow.setFocusable(true);
        popupWindow.setAnimationStyle(R.style.MyPopupWindow_anim_style);
        popupWindow.showAtLocation(mContext.getWindow().getDecorView(),
                Gravity.CENTER, 0, 0
        );

        // 获取对话框中的控件
        final EditText et_edit_name = edit_window_view.findViewById(R.id.et_edit_name);
        final EditText et_edit_phone = edit_window_view.findViewById(R.id.et_edit_phone);

        // 数据回显
        et_edit_name.setText(ec.getName());
        et_edit_phone.setText(ec.getPhone());

        Button bt_edit_confirm = edit_window_view.findViewById(R.id.bt_edit_confirm);
        bt_edit_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = et_edit_name.getText().toString().trim();
                String phone = et_edit_phone.getText().toString().trim().replace(" ", "");

                // 数据校验
                // 姓名不能为空
                // 手机号不能为空
                if (TextUtils.isEmpty(name)) {
                    ToastUtils.toast(mContext, "请输入姓名");
                    return;
                }

                if (TextUtils.isEmpty(phone)) {
                    ToastUtils.toast(mContext, "请输入手机号");
                    return;
                }

                ec.setName(name);
                ec.setPhone(phone);

                // 更新数据库对应的记录,有id
                mDao.update(ec);

                // 通知数据适配器刷新
                if (mMyAdapter != null) {
                    mMyAdapter.notifyDataSetChanged();
                }

                // 同时关闭输入法，如果打开的话
                ViewUtils.hideSoftInput(et_edit_name, mContext);
                ViewUtils.hideSoftInput(et_edit_phone, mContext);

                popupWindow.dismiss();

                ToastUtils.toast(mContext, "保存修改成功！");
            }
        });

        Button bt_edit_cancel = edit_window_view.findViewById(R.id.bt_edit_cancel);
        // 点击取消按钮关闭当前页面
        bt_edit_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 同时关闭输入法，如果打开的话
                ViewUtils.hideSoftInput(et_edit_name, mContext);
                ViewUtils.hideSoftInput(et_edit_phone, mContext);

                popupWindow.dismiss();
            }
        });

        // 删除按钮,删除当前的紧急联系人
        Button bt_edit_remove = edit_window_view.findViewById(R.id.bt_edit_remove);
        bt_edit_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 同时关闭输入法，如果打开的话
                ViewUtils.hideSoftInput(et_edit_name, mContext);
                ViewUtils.hideSoftInput(et_edit_phone, mContext);

                popupWindow.dismiss();

                // 弹出是否确定删除的对话框
                showEcRemoveDialog(ec);
            }
        });

        // 对话框关闭事件监听
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //关闭了 popwindow，需要让界面完全透明
                ViewUtils.changeBackgroundAlpha(1.0f, mContext);
            }
        });
    }

    /**
     * 弹出 是否删除紧急联系人  的对话框
     */
    private void showEcRemoveDialog(final EmergencyContact ec) {
        final PopupWindow popupWindow;

        View pop_window_view = LayoutInflater.from(this.mContext).inflate(R.layout.dialog_ec_remove, null);
        popupWindow = new PopupWindow(
                pop_window_view, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
        );

        // 对话框参数设置
        popupWindow.setFocusable(true);
        popupWindow.setAnimationStyle(R.style.MyPopupWindow_anim_style);
        popupWindow.showAtLocation(mContext.getWindow().getDecorView(),
                Gravity.CENTER, 0, 0
        );

        // 获取对话框中的控件
        TextView tv_remove_content = pop_window_view.findViewById(R.id.tv_remove_content);
        tv_remove_content.setText("确定要删除\"" + ec.getName() + "\"吗?");

        Button bt_remove_confirm = pop_window_view.findViewById(R.id.bt_remove_confirm);
        bt_remove_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.w("删除的记录的id: " + ec.get_id());

                // 删除数据库对应的记录
                mDao.delete(ec.get_id());

                int ecIndex = mContacts.indexOf(ec);
                // 删除集合中的记录
                mContacts.remove(ecIndex);

                // 通知适配器更新列表, 之前加了动画效果
                mMyAdapter.notifyItemRemoved(ecIndex);

                popupWindow.dismiss();

                ToastUtils.toast(mContext, "删除成功！");

            }
        });

        Button bt_remove_cancel = pop_window_view.findViewById(R.id.bt_remove_cancel);
        // 点击取消按钮关闭当前页面
        bt_remove_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        // 对话框关闭事件监听
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //关闭了 popwindow，需要让界面完全透明
                ViewUtils.changeBackgroundAlpha(1.0f, mContext);
            }
        });

    }

}
