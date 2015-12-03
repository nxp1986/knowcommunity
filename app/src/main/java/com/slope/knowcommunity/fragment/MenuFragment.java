package com.slope.knowcommunity.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.slope.knowcommunity.R;
import com.slope.knowcommunity.bean.MenuLeftItemSo;
import com.slope.knowcommunity.common.Constant;
import com.squareup.okhttp.Request;

import OkhttpUtils.ResultCallback;
import OkhttpUtils.request.OkHttpRequest;

/**
 * @author Slope
 * @desc
 * @createData 2015/11/26 17:15
 */
public class MenuFragment extends Fragment {

    private LinearLayout mMenuLogin;
    private TextView mMenuStart;
    private TextView mMenuFileDownload;
    private LinearLayout mMenuHomePage;
    private ListView mMenuListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mMenuLeftView = inflater.inflate(R.layout.menu_left_layout, null);
        mMenuLogin = (LinearLayout) mMenuLeftView.findViewById(R.id.menu_lagin);
        mMenuStart = (TextView) mMenuLeftView.findViewById(R.id.menu_star);
        mMenuFileDownload = (TextView) mMenuLeftView.findViewById(R.id.menu_file_download);
        mMenuHomePage = (LinearLayout) mMenuLeftView.findViewById(R.id.menu_home_page);
        mMenuListView = (ListView) mMenuLeftView.findViewById(R.id.menu_listView);
        initView();
        return mMenuLeftView;
    }

    private void initView() {
        new OkHttpRequest.Builder().url(Constant.BASEURL + Constant.THEMES).get(new ResultCallback<MenuLeftItemSo>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(MenuLeftItemSo response) {

            }
        });
    }
}
