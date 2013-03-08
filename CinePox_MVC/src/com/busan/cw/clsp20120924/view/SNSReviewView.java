package com.busan.cw.clsp20120924.view;

import view.CCView;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.busan.cw.clsp20120924_beta.R;
import com.busan.cw.clsp20120924.interfaces.SNSReviewViewCallback;

/**
 * HomePageFragment에 들어가는 SNS 리뷰
 * 
 * @author CINEPOX
 *
 */
public class SNSReviewView extends CCView implements OnClickListener {

	private ImageView postImage;
	private TextView title;
	private TextView info;
	private TextView ratingText;
	private ImageView[] stars;

	private TextView reply1id;
	private TextView reply1desc;

	private SNSReviewViewCallback callback;

	public void setCallback(SNSReviewViewCallback callback) {
		this.callback = callback;
	}

	// 모델 완성되면 구현
	// 영화 이미지, 제목, 정보, 댓글 정보, 레이팅
	
	public SNSReviewView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public SNSReviewView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public SNSReviewView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if (callback == null)
			return;
		callback.onSNSReviewClick(this);
	}

	@Override
	public int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.layout_snsreview;
	}

	@Override
	public void allocViews() {
		// TODO Auto-generated method stub
		postImage = (ImageView) findViewById(R.id.img_snsreview_postimage);
		postImage.setOnClickListener(this);
		title = (TextView) findViewById(R.id.text_snsreview_title);
		info = (TextView) findViewById(R.id.text_snsreview_info);
		ratingText = (TextView) findViewById(R.id.text_snsreview_rating);
		reply1id = (TextView) findViewById(R.id.text_snsreview_1_id);
		reply1desc = (TextView) findViewById(R.id.text_snsreview_1_desc);
		stars = new ImageView[5];
		stars[0] = (ImageView) findViewById(R.id.img_snsreview_star1);
		stars[1] = (ImageView) findViewById(R.id.img_snsreview_star2);
		stars[2] = (ImageView) findViewById(R.id.img_snsreview_star3);
		stars[3] = (ImageView) findViewById(R.id.img_snsreview_star4);
		stars[4] = (ImageView) findViewById(R.id.img_snsreview_star5);
	}

	/**
	 * 평점에 의해 숫자를 표시하고 별을 그림
	 * 
	 * @param rating
	 */
	public void setRating(float rating) {
		ratingText.setText(rating + "");
		for (ImageView iv : stars) {
			if (rating >= 2.f) {
				iv.setImageResource(R.drawable.img_snspoint_10);
				rating -= 2.f;
			} else if (rating >= 1.f) {
				iv.setImageResource(R.drawable.img_snspoint_5);
				rating -= 1.f;
			} else {
				iv.setImageResource(R.drawable.img_snspoint_0);
			}
		}
	}

}
