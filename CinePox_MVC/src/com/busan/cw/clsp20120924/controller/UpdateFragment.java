package com.busan.cw.clsp20120924.controller;

import utils.ManifestUtils;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.busan.cw.clsp20120924.interfaces.UpdateViewCallback;
import com.busan.cw.clsp20120924.model.CinepoxAppModel;
import com.busan.cw.clsp20120924.view.UpdateView;

public class UpdateFragment extends Fragment implements UpdateViewCallback {

	private UpdateView view;
	private CinepoxAppModel appModel;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		view = new UpdateView(getActivity());
		view.setCallback(this);
		appModel = (CinepoxAppModel) getActivity().getApplication();
	}

	@Override
	public android.view.View onCreateView(android.view.LayoutInflater inflater,
			android.view.ViewGroup container, Bundle savedInstanceState) {
		view.setVersionInfo(ManifestUtils.getVersionNameText(getActivity()),
				appModel.getAppConfig().getRecentVerName());
		view.setUpdate(ManifestUtils.getVersionCode(getActivity()) < appModel
				.getAppConfig().getRecentVerCode());
		return view;
	};

	@Override
	public void onUpdateClick(UpdateView view) {
		if (ManifestUtils.getVersionCode(getActivity()) < appModel
				.getAppConfig().getRecentVerCode())
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(appModel
					.getAppConfig().getUpdateUrl())));
	}

}
