package com.busan.cw.clsp20120924.controller;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.busan.cw.clsp20120924.interfaces.MovieDetailModelCallback;
import com.busan.cw.clsp20120924.interfaces.MovieDetailViewCallback;
import com.busan.cw.clsp20120924.model.CinepoxAppModel;
import com.busan.cw.clsp20120924.model.MovieDetailModel;
import com.busan.cw.clsp20120924.view.MovieDetailView;
import com.busan.cw.clsp20120924.view.MovieSNSReplyView;
import com.busan.cw.clsp20120924.view.MovieSynopsisView;
import com.busan.cw.clsp20120924.view.SeriseListView;

public class MovieDetailFragment extends Fragment implements
		MovieDetailViewCallback, MovieDetailModelCallback {

	private MovieDetailView detailView;
	private MovieSynopsisView synopsisView;
	private MovieSNSReplyView snsreplyView;

	private MovieDetailModel detailModel;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		detailView = new MovieDetailView(getActivity());
		detailView.setCallback(this);
		synopsisView = detailView.getSynopsisView();
		snsreplyView = detailView.getSNSReplyView();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return detailView;
	}

	public void setMovieNum(int movieNum) {
		// TODO Auto-generated method stub
		if (detailModel != null)
			detailModel.setCallback(null);

		detailView.initState();
		detailModel = new MovieDetailModel(getActivity());
		detailModel.setCallback(this);
		detailModel.loadMovieDetail(movieNum);
	}

	private CinepoxAppModel app() {
		return (CinepoxAppModel) getParent().getApplication();
	}

	private void initViewWithModel(MovieDetailModel model) {

		if (model.isAdult() && !app().getUserConfig().isShowAdult())
			detailView.setPostImage(null);
		else {
			detailView.setPostImage(model.getPostImageUrl());
			if (model.getPreviewVideo() == null)
				detailView.setPreview(false);
			else
				detailView.setPreview(true);
		}
		
		synopsisView.setSynopsisText(model.getContent() + model.getAward());
		detailView.setTitle(model.getTitle());
		detailView.setInfo(model.getShowTime(), model.getCountry(),
				model.getGenre());
		detailView.setRating(model.getRating());
		detailView.setActor(model.getActor());
		detailView.setDirector(model.getDirector());
		detailView
				.setPrice(model.getDownloadPrice() > model.getStreamPrice() ? model
						.getStreamPrice() : model.getDownloadPrice());
		detailView.setViewClass(model.getViewClass());
		detailView.setDeliveration(model.getDiscussion());
		detailView.setStillcut(model.getStillImages());

	}

	public void showLoading() {
		getParent().showLoading();
	}

	public void hideLoading() {
		getParent().hideLoading();
	}

	private MainFragmentActivity getParent() {
		return (MainFragmentActivity) getActivity();
	}

	@Override
	public void onShowAllClick(SeriseListView view) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onOrderClick(SeriseListView view) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSerisePageChanged(SeriseListView view, int position) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSeriseItemClick(SeriseListView view, int page, int position) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNextClick(SeriseListView view) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPrevClick(SeriseListView view) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSynopsisExpandClick(MovieSynopsisView view) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onActorListExpandClick(MovieSynopsisView view) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onActorListItemClick(MovieSynopsisView view, int index) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReplyWriteClick(MovieSNSReplyView view) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReplyExpandClick(MovieSNSReplyView view) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReplyProfileClick(MovieSNSReplyView view, int index) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPreviewClick(MovieDetailView view) {
		// TODO Auto-generated method stub
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(detailModel
				.getPreviewVideo())));
	}

	@Override
	public void onRatingClick(MovieDetailView view) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onShareClick(MovieDetailView view) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStreamingClick(MovieDetailView view) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDownloadClick(MovieDetailView view) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onZzimClick(MovieDetailView view) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResolutionDetailClick(MovieDetailView view) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFileinfoDetailClick(MovieDetailView view) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRelativeMovieItemClick(MovieDetailView view, int position) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onHelpClick(MovieDetailView view) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStillcutItemClick(MovieDetailView view, int position) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMovieDetailLoadStart(MovieDetailModel model) {
		// TODO Auto-generated method stub
		detailView.setVisibility(View.INVISIBLE);
		showLoading();
	}

	@Override
	public void onMovieDetailLoadError(MovieDetailModel model, String messgae) {
		// TODO Auto-generated method stub
		Toast.makeText(getActivity(), messgae, Toast.LENGTH_SHORT).show();
		getParent().onBackPressed();
		hideLoading();
	}

	@Override
	public void onMovieDetailLoadComplete(MovieDetailModel model) {
		// TODO Auto-generated method stub
		detailView.setVisibility(View.VISIBLE);
		hideLoading();
		initViewWithModel(model);
	}
}
