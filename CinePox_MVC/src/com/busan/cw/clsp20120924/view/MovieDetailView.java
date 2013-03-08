package com.busan.cw.clsp20120924.view;

import static com.busan.cw.clsp20120924.interfaces.MovieDetailViewCallback.SEGMENT_TAG_SNSREPLY;
import static com.busan.cw.clsp20120924.interfaces.MovieDetailViewCallback.SEGMENT_TAG_STILLCUT;
import static com.busan.cw.clsp20120924.interfaces.MovieDetailViewCallback.SEGMENT_TAG_SYNOPSIS;

import java.util.ArrayList;

import utils.DisplayUtils;
import utils.StringUtils;
import view.CCView;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckedTextView;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;

import com.busan.cw.clsp20120924.base.Constants;
import com.busan.cw.clsp20120924.interfaces.MovieDetailViewCallback;
import com.busan.cw.clsp20120924.model.ImageData;
import com.busan.cw.clsp20120924_beta.R;
import com.fedorvlasov.lazylist.ImageLoader;

import extend.ExpandedListView;
import extend.SegmentLayout;
import extend.SegmentLayout.OnSegmentChangedListener;
import extend.SegmentLayout.Segment;

/**
 * 영화 상세정보 뷰
 * 
 * @author CINEPOX
 * 
 */
@SuppressWarnings("deprecation")
public class MovieDetailView extends CCView implements OnClickListener,
		OnItemClickListener, OnSegmentChangedListener,
		iface.OnItemClickListener, Constants {

	private ImageView postImage;
	private ImageButton previewBtn;

	private TextView titleText;
	private TextView infoText;
	private TextView directorText;
	private TextView actorText;
	private TextView priceText;
	private TableRow deliberationRow;
	private TextView deliberationText;

	private ImageView[] ratingStars;
	private TextView ratingText;

	private LinearLayout ratingBtn1;
	private TextView ratingBtn2;
	private TextView shareBtn;

	private ImageButton streamingBtn;
	private ImageButton downloadBtn;
	private ImageButton zzimBtn;

	private SeriseListView seriseList;

	private TextView streamPeriodText;
	private TextView downPeriodText;
	private TextView resolutionText;
	private TextView fileInfoText;
	private TextView resolutionBtn;
	private TextView fileInfoBtn;

	private SegmentLayout movieDetailSegment;

	// 아래 상세정보 뷰들은 구조가 복잡하여 별도 클래스로 구성
	private MovieSynopsisView movieSynopsis;
	private MovieSNSReplyView movieSNS;
	private ExpandedListView stillcutContainer;

	private Gallery relativeMovieGallery;

	private TextView helpBtn;

	private MovieDetailViewCallback callback;
	private ImageLoader imageLoader;
	private MovieDetailStillcutAdapter lastAdapter;

	public void setCallback(MovieDetailViewCallback callback) {
		this.callback = callback;
		// 내부의 뷰들은 콜백 연결만 시켜줌
		seriseList.setCallback(callback);
		movieSynopsis.setCallback(callback);
		movieSNS.setCallback(callback);
	}

	public MovieSynopsisView getSynopsisView() {
		return movieSynopsis;
	}

	public MovieSNSReplyView getSNSReplyView() {
		return movieSNS;
	}

	public void initState() {
		movieSynopsis.setExpand(false);
		stillcutContainer.removeAllViews();
		stillcutContainer.invalidate();
		stillcutContainer.requestLayout();
		movieDetailSegment.setCurrentSegment(0);
		// 세그먼트 특성상 저거 호출한다고 콜백 호출은 안되니 수동으로 호출해줘야 함
		onSegmentChanged(movieDetailSegment, SEGMENT_TAG_SYNOPSIS);
		((ScrollView) getContentView()).scrollTo(0, 0);
	}

	// 모델 완성 후 구현
	// 댓글, 스틸컷 세팅 메서드 필요
	public void setStillcut(ArrayList<ImageData> array) {
		if (array != null) {
			MovieDetailStillcutAdapter adapter = new MovieDetailStillcutAdapter(
					getContext(), R.layout.listitem_moviedetail_stillcut, array);
			stillcutContainer.setAdapter(adapter);
			if (lastAdapter != null)
				lastAdapter.getImageLoader().clearMemory();
			lastAdapter = adapter;
		}

	}

	public void setViewClass(int viewclass) {
		deliberationRow.setVisibility(viewclass == CLASS_ADULT ? View.VISIBLE
				: View.GONE);
		Resources res = getContext().getResources();
		switch (viewclass) {
		case CLASS_ALL:
			titleText.setCompoundDrawablesWithIntrinsicBounds(
					res.getDrawable(R.drawable.img_ageall), null, null, null);
			break;
		case CLASS_12:
			titleText.setCompoundDrawablesWithIntrinsicBounds(
					res.getDrawable(R.drawable.img_age12), null, null, null);
			break;
		case CLASS_15:
			titleText.setCompoundDrawablesWithIntrinsicBounds(
					res.getDrawable(R.drawable.img_age15), null, null, null);
			break;
		case CLASS_ADULT:
			titleText.setCompoundDrawablesWithIntrinsicBounds(
					res.getDrawable(R.drawable.img_age19), null, null, null);
			break;
		}
	}

	public void setPreview(boolean b) {
		// TODO Auto-generated method stub
		previewBtn.setVisibility(b ? VISIBLE : GONE);
	}

	public void setDeliveration(String num) {
		if ("".equals(num))
			deliberationRow.setVisibility(GONE);
		deliberationText.setText(num);
	}

	public void setPrice(int price) {
		priceText.setText(StringUtils.comma(price) + " 원");
	}

	public void setActor(String actor) {
		if ("null".equals(actor))
			actor = "";
		actorText.setText(actor);
	}

	public void setDirector(String director) {
		if ("null".equals(director))
			director = "";
		directorText.setText(director);
	}

	public void setTitle(String title) {
		titleText.setText(title);
	}

	@SuppressLint("DefaultLocale")
	public void setInfo(int time, String country, String genre) {
		if (genre.equals("null"))
			genre = "";
		if (country.equals("null"))
			country = "";

		String info = String.format("%d분 | %s | %s", time, country, genre);
		infoText.setText(info);
	}

	public void setPostImage(String url) {
		if (url == null)
			postImage.setImageResource(R.drawable.bg_poster_19);
		else
			imageLoader.DisplayImage(url, postImage);
	}

	public void setRating(int rating) {
		if (rating == 0)
			rating = 10;
		ratingText.setText(String.valueOf(rating));
		for (ImageView iv : ratingStars) {
			if (rating >= 2) {
				iv.setImageResource(R.drawable.img_snspoint_10);
				rating -= 2;
			} else if (rating >= 1) {
				iv.setImageResource(R.drawable.img_snspoint_5);
				rating -= 1;
			} else {
				iv.setImageResource(R.drawable.img_snspoint_0);
			}
		}
	}

	@Override
	public void allocViews() {
		// TODO Auto-generated method stub
		postImage = (ImageView) findViewById(R.id.img_moviedetail_postimage);
		previewBtn = (ImageButton) findViewById(R.id.btn_moviedetail_preview);
		previewBtn.setOnClickListener(this);

		titleText = (TextView) findViewById(R.id.text_moviedetail_title);
		infoText = (TextView) findViewById(R.id.text_moviedetail_info);
		directorText = (TextView) findViewById(R.id.text_moviedetail_director);
		actorText = (TextView) findViewById(R.id.text_moviedetail_actor);
		priceText = (TextView) findViewById(R.id.text_moviedetail_price);
		deliberationRow = (TableRow) findViewById(R.id.table_moviedetail_deliberation);
		deliberationText = (TextView) findViewById(R.id.text_moviedetail_deliberation);

		ratingStars = new ImageView[5];
		ratingStars[0] = (ImageView) findViewById(R.id.img_moviedetail_star1);
		ratingStars[1] = (ImageView) findViewById(R.id.img_moviedetail_star2);
		ratingStars[2] = (ImageView) findViewById(R.id.img_moviedetail_star3);
		ratingStars[3] = (ImageView) findViewById(R.id.img_moviedetail_star4);
		ratingStars[4] = (ImageView) findViewById(R.id.img_moviedetail_star5);
		ratingText = (TextView) findViewById(R.id.text_moviedetail_rating);

		ratingBtn1 = (LinearLayout) findViewById(R.id.btn_moviedetail_rating1);
		ratingBtn1.setOnClickListener(this);
		ratingBtn2 = (TextView) findViewById(R.id.btn_moviedetail_rating2);
		ratingBtn2.setOnClickListener(this);
		shareBtn = (TextView) findViewById(R.id.btn_moviedetail_share);
		shareBtn.setOnClickListener(this);

		streamingBtn = (ImageButton) findViewById(R.id.btn_moviedetail_streaming);
		streamingBtn.setOnClickListener(this);
		downloadBtn = (ImageButton) findViewById(R.id.btn_moviedetail_download);
		downloadBtn.setOnClickListener(this);
		zzimBtn = (ImageButton) findViewById(R.id.btn_moviedetail_zzim);
		zzimBtn.setOnClickListener(this);

		seriseList = (SeriseListView) findViewById(R.id.view_moviedetail_seriselist);

		streamPeriodText = (TextView) findViewById(R.id.text_moviedetail_stream_session);
		downPeriodText = (TextView) findViewById(R.id.text_moviedetail_download_session);
		resolutionText = (TextView) findViewById(R.id.text_moviedetail_resolution);
		fileInfoText = (TextView) findViewById(R.id.text_moviedetail_fileinfo);
		resolutionBtn = (TextView) findViewById(R.id.btn_moviedetail_resolution_detail);
		resolutionBtn.setOnClickListener(this);
		fileInfoBtn = (TextView) findViewById(R.id.btn_moviedetail_fileinfo_detail);
		fileInfoBtn.setOnClickListener(this);

		movieSynopsis = (MovieSynopsisView) findViewById(R.id.view_moviedetail_synopsis);
		movieSNS = (MovieSNSReplyView) findViewById(R.id.view_moviedetail_snsreply);
		stillcutContainer = (ExpandedListView) findViewById(R.id.list_moviedetail_stillcut);
		stillcutContainer.setOnItemClickListener(this);

		movieDetailSegment = (SegmentLayout) findViewById(R.id.segment_moviedetail);
		movieDetailSegment.setOnSegmentChangedListener(this);
		movieDetailSegment.setSegments(createSegments());
		movieDetailSegment.setSegmentsMargin(10);

		relativeMovieGallery = (Gallery) findViewById(R.id.gallery_moviedetail_relative);
		relativeMovieGallery.setOnItemClickListener(this);

		helpBtn = (TextView) findViewById(R.id.btn_moviedetail_help);
		helpBtn.setOnClickListener(this);
	}

	private ArrayList<Segment> createSegments() {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, -2);
		params.weight = 1.f;
		LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(-2,
				-2);
		params1.weight = 1.f;
		LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(-2,
				-2);
		params2.weight = 1.f;

		ArrayList<Segment> result = new ArrayList<Segment>();
		result.add(Segment.newSegment(
				getSegmentView(R.string.moviedetail_synopsis),
				SEGMENT_TAG_SYNOPSIS).setLayoutParams(params));
		result.add(Segment.newSegment(
				getSegmentView(R.string.moviedetail_stillcut),
				SEGMENT_TAG_STILLCUT).setLayoutParams(params1));
		result.add(Segment.newSegment(getSegmentView(R.string.home_snsreview),
				SEGMENT_TAG_SNSREPLY).setLayoutParams(params2));
		return result;
	}

	private CheckedTextView getSegmentView(int textid) {
		CheckedTextView view = new CheckedTextView(getContext());
		view.setText(textid);
		view.setBackgroundResource(R.drawable.img_tab);
		view.setTextSize(DisplayUtils.applyScale(7, getContext()));
		view.setGravity(Gravity.CENTER);
		int padding = (int) DisplayUtils.applyDimension(7, getContext());
		view.setPadding(0, padding, 0, padding);
		return view;
	}

	public MovieDetailView(Context context, AttributeSet attrs, int defStyle) {
		this(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public MovieDetailView(Context context, AttributeSet attrs) {
		super(context, attrs);
		imageLoader = new ImageLoader(getContext());
		// TODO Auto-generated constructor stub
	}

	public MovieDetailView(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onSegmentChanged(SegmentLayout segment, String tag) {
		// TODO Auto-generated method stub
		movieSNS.setVisibility(tag.equals(SEGMENT_TAG_SNSREPLY) ? VISIBLE
				: GONE);
		movieSynopsis.setVisibility(tag.equals(SEGMENT_TAG_SYNOPSIS) ? VISIBLE
				: GONE);
		stillcutContainer
				.setVisibility(tag.equals(SEGMENT_TAG_STILLCUT) ? VISIBLE
						: GONE);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		if (callback == null)
			return;

		if (arg0.equals(relativeMovieGallery))
			callback.onRelativeMovieItemClick(this, arg2);
	}

	@Override
	public void OnItemClick(ExpandedListView view, View viewitem, int position) {
		// TODO Auto-generated method stub
		if (callback == null)
			return;

		if (view.equals(stillcutContainer))
			callback.onStillcutItemClick(this, position);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (callback == null)
			return;

		if (v.equals(previewBtn))
			callback.onPreviewClick(this);
		else if (v.equals(ratingBtn1) || v.equals(ratingBtn2))
			callback.onRatingClick(this);
		else if (v.equals(shareBtn))
			callback.onShareClick(this);
		else if (v.equals(streamingBtn))
			callback.onStreamingClick(this);
		else if (v.equals(downloadBtn))
			callback.onDownloadClick(this);
		else if (v.equals(zzimBtn))
			callback.onZzimClick(this);
		else if (v.equals(resolutionBtn))
			callback.onResolutionDetailClick(this);
		else if (v.equals(fileInfoBtn))
			callback.onFileinfoDetailClick(this);
		else if (v.equals(helpBtn))
			callback.onHelpClick(this);
	}

	@Override
	public int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.fragment_moviedetail;
	}

}
