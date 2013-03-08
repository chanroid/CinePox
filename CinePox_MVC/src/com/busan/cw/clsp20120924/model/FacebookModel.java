package com.busan.cw.clsp20120924.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;

import com.busan.cw.clsp20120924.base.BaseModel;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.facebook.model.FacebookPostData;

public class FacebookModel extends BaseModel {

	public static final String FACEBOOK_APP_ID = "556904657667865";
	// public static final String FACEBOOK_APP_ID = "286687994725222";

	public static final int PROFILE_IMAGE_SIZE = 100;

	private static final List<String> PERMISSIONS = Arrays.asList(
			"publish_actions", "email", "user_about_me");

	public FacebookModel(Activity ctx) {
		super(ctx);
		// TODO Auto-generated constructor stub
	}

	public Session loadFBSession(Session.StatusCallback callback, Bundle state) {
		Session session = Session.getActiveSession();
		// 활성 세션 가져옴
		if (session == null) {
			if (state != null) {
				session = Session.restoreSession(getContext(), null, callback,
						state);
				// 저장된 세션이 있을 경우 복원
			}
			if (session == null) {
				session = new Session.Builder(getContext()).setApplicationId(
						FACEBOOK_APP_ID).build();
				// 저장된 세션이 없을 경우 새로 생성
			}
			Session.setActiveSession(session);
			// 현재 세션으로 설정
		}
		return session;
	}

	public void loadFBUserInfo(Session session,
			final Request.GraphUserCallback callback) {
		Request.executeMeRequestAsync(session, callback);
	}

	public void fbPost(Session session, final FacebookPostData data,
			final Request.Callback callback) {

		List<String> permissions = session.getPermissions();
		if (!isSubsetOf(PERMISSIONS, permissions)) {
			Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(
					(Activity) getContext(), PERMISSIONS);
			newPermissionsRequest.setCallback(new StatusCallback() {
				@Override
				public void call(Session session, SessionState state,
						Exception exception) {
					// TODO Auto-generated method stub
					fbPost(session, data, callback);
				}
			});
			session.requestNewPublishPermissions(newPermissionsRequest);
			return;
		}

		Bundle postParams = new Bundle();
		postParams.putString("name", data.name);
		postParams.putString("caption", data.caption);
		postParams.putString("description", data.description);
		postParams.putString("link", data.link);
		postParams.putString("picture", data.picture);

		Request request = new Request(session, "me/feed", postParams,
				HttpMethod.POST, callback);

		RequestAsyncTask task = new RequestAsyncTask(request);
		task.execute();
	}

	private boolean isSubsetOf(Collection<String> subset,
			Collection<String> superset) {
		for (String string : subset) {
			if (!superset.contains(string)) {
				return false;
			}
		}
		return true;
	}

}
