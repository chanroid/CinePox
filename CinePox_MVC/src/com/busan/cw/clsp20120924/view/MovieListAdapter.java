package com.busan.cw.clsp20120924.view;

import java.util.List;

import utils.LogUtils.l;
import utils.StringUtils;
import view.CCAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.busan.cw.clsp20120924.base.CinepoxImageLoader;
import com.busan.cw.clsp20120924.base.Constants;
import com.busan.cw.clsp20120924.model.CinepoxAppModel;
import com.busan.cw.clsp20120924.model.MovieItemData;
import com.busan.cw.clsp20120924_beta.R;
import com.fedorvlasov.lazylist.AbsImageLoader;

@SuppressLint("DefaultLocale")
public class MovieListAdapter extends CCAdapter<MovieItemData> implements
		Constants {

	private CinepoxImageLoader imgloader;

	public AbsImageLoader getImageLoader() {
		return imgloader;
	}

	public MovieListAdapter(Context context, int resource,
			List<MovieItemData> data) {
		super(context, resource, data);
		imgloader = new CinepoxImageLoader(getContext());
		imgloader.setScale(4);
		imgloader.setRoundRadius(10);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		convertView = super.getView(position, convertView, parent);
		convertView.setFocusable(false);
		if (position % 2 == 1)
			convertView.setBackgroundColor(Color.TRANSPARENT);
		else
			convertView.setBackgroundColor(Color.parseColor("#ececec"));
		return convertView;
	}

	@Override
	public View initItem(MovieItemData data, View convertView) {
		// TODO Auto-generated method stub
		ViewHolder holder = loadHolder(convertView);
		holder.setMovieItemData(data);
		// if (!data.isSellable())
		// convertView.setVisibility(View.GONE);
		// else
		// convertView.setVisibility(View.VISIBLE);
		return convertView;
	}

	private ViewHolder loadHolder(View convertView) {
		ViewHolder holder = (ViewHolder) convertView.getTag();
		if (holder == null) {
			holder = new ViewHolder();
			holder.postImage = (ImageView) convertView
					.findViewById(R.id.img_movielistitem_postimage);
			holder.viewClass = (ImageView) convertView
					.findViewById(R.id.img_movielistitem_viewclass);
			holder.title = (TextView) convertView
					.findViewById(R.id.text_movielistitem_title);
			holder.info = (TextView) convertView
					.findViewById(R.id.text_movielistitem_info);

			// holder.director = (TextView) convertView
			// .findViewById(R.id.text_movielistitem_director);
			// holder.actor = (TextView) convertView
			// .findViewById(R.id.text_movielistitem_actor);

			holder.price = (TextView) convertView
					.findViewById(R.id.text_movielistitem_price);
			holder.discussion = (TextView) convertView
					.findViewById(R.id.text_movielistitem_deliberation);
			holder.discussionRow = (TableRow) convertView
					.findViewById(R.id.table_movielistitem_deliberation);
			holder.ratingStars = (LinearLayout) convertView
					.findViewById(R.id.layout_movielistitem_stars);
			holder.ratingText = (TextView) convertView
					.findViewById(R.id.text_movielistitem_rating);
			holder.zzimBtn = (CheckedTextView) convertView
					.findViewById(R.id.btn_movielistitem_zzim);
			holder.zzimBtn.setFocusable(false);

			holder.hdFlag = (ImageView) convertView
					.findViewById(R.id.img_movielistitem_ishd);
			holder.cinemaFlag = (ImageView) convertView
					.findViewById(R.id.img_movielistitem_iscinema);
			holder.dubbingFlag = (ImageView) convertView
					.findViewById(R.id.img_movielistitem_isdubbing);
			holder.komeFlag = (ImageView) convertView
					.findViewById(R.id.img_movielistitem_iskome);
			holder.monthFlag = (ImageView) convertView
					.findViewById(R.id.img_movielistitem_ismonth);
			holder.newFlag = (ImageView) convertView
					.findViewById(R.id.img_movielistitem_isnew);
			holder.onlineFlag = (ImageView) convertView
					.findViewById(R.id.img_movielistitem_isonline);
			holder.threedFlag = (ImageView) convertView
					.findViewById(R.id.img_movielistitem_is3d);
			holder.tvliveFlag = (ImageView) convertView
					.findViewById(R.id.img_movielistitem_islive);

			convertView.setTag(holder);
		}
		return holder;
	}

	private CinepoxAppModel app() {
		// TODO Auto-generated method stub
		return (CinepoxAppModel) getContext().getApplicationContext();
	}

	class ViewHolder {
		ImageView postImage;
		ImageView viewClass;
		TextView title;
		TextView info;
		// TextView director;
		// TextView actor;
		TextView price;
		TextView discussion;
		TableRow discussionRow;
		LinearLayout ratingStars;
		TextView ratingText;
		CheckedTextView zzimBtn;

		ImageView hdFlag;
		ImageView tvliveFlag;
		ImageView newFlag;
		ImageView onlineFlag;
		ImageView komeFlag;
		ImageView cinemaFlag;
		ImageView threedFlag;
		ImageView dubbingFlag;
		ImageView monthFlag;

		public void setMovieItemData(final MovieItemData data) {
			if (app().getUserConfig().isShowAdult())
				setPostimageUrl(data.getPostImageUrl());
			else {
				if (data.isAdult())
					postImage.setImageResource(R.drawable.bg_poster_19);
				else
					setPostimageUrl(data.getPostImageUrl());
			}
			setTitle(data.getTitle());
			setInfo(data.getShowTime(), data.getCountry(), data.getGenre());
			// setDirector(data.getDirector());
			// setActor(data.getActor());
			setPrice(data.getDownloadPrice() > data.getStreamPrice() ? data
					.getStreamPrice() : data.getDownloadPrice());
			setViewClass(data.getViewClass());
			setDiscussion(data.getDiscussion());
			setRating(data.getRating());
			// 찜관련 조정은 위에서
			setZzimListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					l.i("zzim : " + data.getMovieNum());
				}
			});

			hdFlag.setVisibility(data.isHd() && !data.isOnline() ? View.VISIBLE
					: View.INVISIBLE);
			tvliveFlag.setVisibility(data.isTvlive() ? View.VISIBLE
					: View.INVISIBLE);
			newFlag.setVisibility(data.isNewMovie() ? View.VISIBLE
					: View.INVISIBLE);
			onlineFlag.setVisibility(data.isOnline() ? View.VISIBLE
					: View.INVISIBLE);
			komeFlag.setVisibility(data.isKome() && !data.isOnline() ? View.VISIBLE
					: View.INVISIBLE);
			cinemaFlag.setVisibility(data.isCinema() ? View.VISIBLE
					: View.INVISIBLE);
			threedFlag.setVisibility(data.is3d() ? View.VISIBLE
					: View.INVISIBLE);
			dubbingFlag.setVisibility(data.isDubbing() && !data.isHd()
					&& !data.isOnline() ? View.VISIBLE : View.INVISIBLE);
			monthFlag.setVisibility(data.isMonth() ? View.VISIBLE
					: View.INVISIBLE);
		}

		public void setPostimageUrl(String url) {
			imgloader.DisplayImage(url, postImage);
		}

		public void setViewClass(int viewclass) {
			discussionRow.setVisibility(viewclass == CLASS_ADULT ? View.VISIBLE
					: View.GONE);
			switch (viewclass) {
			case CLASS_ALL:
				viewClass.setImageResource(R.drawable.img_ageall);
				break;
			case CLASS_12:
				viewClass.setImageResource(R.drawable.img_age12);
				break;
			case CLASS_15:
				viewClass.setImageResource(R.drawable.img_age15);
				break;
			case CLASS_ADULT:
				viewClass.setImageResource(R.drawable.img_age19);
				break;
			}
		}

		public void setTitle(String title) {
			if (title.equals("null"))
				title = "";
			this.title.setText(Html.fromHtml(title));
		}

		public void setInfo(int time, String country, String genre) {
			if (genre.equals("null"))
				genre = "";
			if (country.equals("null"))
				country = "";

			String info = String.format("%d분 | %s | %s", time, country, genre);
			this.info.setText(info);
		}

		// public void setDirector(String director) {
		// if (director.equals("null"))
		// director = "";
		// this.director.setText(director);
		// }
		//
		// public void setActor(String actor) {
		// if (actor.equals("null"))
		// actor = "";
		// this.actor.setText(actor);
		// }

		public void setPrice(int price) {
			this.price.setText(StringUtils.comma(price) + " 원");
		}

		public void setDiscussion(String dis) {
			if ("".equals(dis))
				discussionRow.setVisibility(View.GONE);
			discussion.setText(dis);
		}

		public void setRating(int rating) {
			if (rating == 0)
				rating = 10;
			ratingText.setText(String.valueOf(rating));

			ImageView[] stars = new ImageView[ratingStars.getChildCount()];
			for (int i = 0; i < ratingStars.getChildCount(); i++) {
				stars[i] = (ImageView) ratingStars.getChildAt(i);
				int starRes = R.drawable.img_snspoint_0;
				if (rating >= 2) {
					starRes = R.drawable.img_snspoint_10;
					rating -= 2;
				} else if (rating >= 1) {
					starRes = R.drawable.img_snspoint_5;
					rating -= 1;
				}

				stars[i].setImageResource(starRes);
			}

		}

		public void setZzimed(boolean flag) {
			zzimBtn.setChecked(flag);
		}

		public void setZzimListener(OnClickListener callback) {
			zzimBtn.setOnClickListener(callback);
		}
	}

}
