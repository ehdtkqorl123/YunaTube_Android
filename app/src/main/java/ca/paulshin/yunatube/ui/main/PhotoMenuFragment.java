package ca.paulshin.yunatube.ui.main;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.paulshin.yunatube.R;
import ca.paulshin.yunatube.data.model.flickr.CollectionItem;
import ca.paulshin.yunatube.data.model.flickr.Item;
import ca.paulshin.yunatube.ui.adapter.CollectionsLayoutAdapter;
import ca.paulshin.yunatube.ui.adapter.StreamLayoutAdapter;
import ca.paulshin.yunatube.ui.base.BaseActivity;
import ca.paulshin.yunatube.ui.base.BaseFragment;
import ca.paulshin.yunatube.util.CollectionUtil;
import ca.paulshin.yunatube.util.NetworkUtil;
import ca.paulshin.yunatube.util.ToastUtil;
import ca.paulshin.yunatube.util.events.ConnectivityChangeEvent;

/**
 * http://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}.jpg
 * http://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}_[mstzb].jpg
 * http://farm{farm-id}.staticflickr.com/{server-id}/{id}_{o-secret}_o.(jpg|gif|png)
 * <p/>
 * s	small square 75x75
 * q	large square 150x150
 * t	thumbnail, 100 on longest side
 * m	small, 240 on longest side
 * n	small, 320 on longest side
 * -	medium, 500 on longest side
 * z	medium 640, 640 on longest side
 * c	medium 800, 800 on longest side�
 * b	large, 1024 on longest side*
 * o	original image, either a jpg, gif or png, depending on source format
 */
public class PhotoMenuFragment extends BaseFragment implements PhotoMenuMvpView {

	@Inject
	PhotoMenuPresenter mPhotoMenuPresenter;
	@Inject
	Bus mBus;

	@Bind(R.id.stream)
	public RecyclerView mStreamRecyclerView;
	@Bind(R.id.collections)
	public RecyclerView mCollectionsRecyclerView;
	@Bind(R.id.lists)
	public View mListsView;
	@Bind(R.id.loading)
	public View mLoadingView;
	@Bind(R.id.none)
	public View mNoneView;

	private int mLoadCount;
	private boolean mPhotoListsLoaded;

	private Handler mLoadHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mLoadCount += msg.what;
			if (mLoadCount >= 2) {
				mListsView.setVisibility(View.VISIBLE);
			}
		}
	};

	public static PhotoMenuFragment newInstance() {
		PhotoMenuFragment fragment = new PhotoMenuFragment();
		return fragment;
	}

	public PhotoMenuFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		((BaseActivity) getActivity()).getActivityComponent().inject(this);
		mPhotoMenuPresenter.attachView(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.f_photo, container, false);
		ButterKnife.bind(this, rootView);

		Context ctx = getActivity();
		mStreamRecyclerView.setLayoutManager(new LinearLayoutManager(ctx));
		mCollectionsRecyclerView.setLayoutManager(new LinearLayoutManager(ctx));

		return rootView;
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser && !mPhotoListsLoaded ) {
			loadData();
			mPhotoListsLoaded = true;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		mPhotoMenuPresenter.detachView();
	}

	@Override
	public void onResume() {
		super.onResume();

		mBus.register(this);
	}

	@Override
	public void onPause() {
		super.onPause();

		// Remember to also call the unregister method when appropriate.
		mBus.unregister(this);
	}

	private void loadData() {
		if (NetworkUtil.isNetworkConnected(getActivity())) {
			mLoadingView.setVisibility(View.VISIBLE);
			mNoneView.setVisibility(View.GONE);

			mPhotoMenuPresenter.getStream();
			mPhotoMenuPresenter.getCollections();
		} else {
			mLoadingView.setVisibility(View.GONE);
			mNoneView.setVisibility(View.VISIBLE);
			//TODO
		}
	}

	/*****
	 * MVP View methods implementation
	 *****/

	@Override
	public void showStream(List<Item> items) {
		if (!CollectionUtil.isEmpty(items)) {
			StreamLayoutAdapter adapter = new StreamLayoutAdapter(getActivity(), items);
			mStreamRecyclerView.setAdapter(adapter);
		}

		mLoadHandler.sendEmptyMessage(1);
	}

	@Override
	public void showCollections(List<CollectionItem> collectionItems) {
		if (!collectionItems.isEmpty()) {
			CollectionItem animatedGif = new CollectionItem("", getString(R.string.animated_gifs_title));
			collectionItems.add(0, animatedGif);

			CollectionsLayoutAdapter adapter = new CollectionsLayoutAdapter(getActivity(), collectionItems);
			mCollectionsRecyclerView.setAdapter(adapter);
		}

		mLoadHandler.sendEmptyMessage(1);
	}

	@Override
	public void showError() {
		//TODO
	}

	@Subscribe
	public void onConnectivityChange(ConnectivityChangeEvent status) {
		if (status.networkEnabled) {
			loadData();
		} else {
			//TODO
			ToastUtil.toast(getActivity(), "No internet");
		}
	}
}
