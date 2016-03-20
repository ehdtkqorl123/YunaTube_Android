package ca.paulshin.yunatube.ui.base;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import ca.paulshin.yunatube.R;

/**
 * Created by paulshin on 14-12-13.
 */
public abstract class BaseLinkFragment extends BaseFragment {
	protected List<Link> links;
	protected abstract void loadSites();
	protected abstract String getPrefix();

	public RecyclerView mRecyclerView;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.f_link, container, false);

		mRecyclerView = ButterKnife.findById(root, R.id.collections);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

		loadSites();

		LinkAdapter adapter = new LinkAdapter(links);
		mRecyclerView.setAdapter(adapter);

		return root;
	}

	public static class Link {
		public Bitmap thumbnail;
		public String title;
		public String desc;
		public String url;

		public Link(Bitmap thumbnail, String title, String desc, String url) {
			this.thumbnail = thumbnail;
			this.title = title;
			this.desc = desc;
			this.url = url;
		}
	}

	protected Link createLinks(String title) {
		title = getPrefix() + title;
		int thumbnailResId = getResources().getIdentifier(title, "drawable", this.getActivity().getPackageName());
		Bitmap thumbnail = BitmapFactory.decodeResource(getResources(), thumbnailResId);
		int titleResId = getResources().getIdentifier(title, "string", this.getActivity().getPackageName());
		int descResId = getResources().getIdentifier(title + "_desc", "string", this.getActivity().getPackageName());
		int urlResId = getResources().getIdentifier(title + "_url", "string", this.getActivity().getPackageName());
		Link link = new Link(thumbnail, getString(titleResId), getString(descResId), getString(urlResId));
		return link;
	}

	public static class LinkAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
		private List<Link> mLinks;

		public LinkAdapter(List<Link> links) {
			mLinks = links;
		}

		public static class ItemViewHolder extends RecyclerView.ViewHolder {
			public ImageView mThumbnail;
			public TextView mTitle;
			public TextView mDesc;

			public ItemViewHolder(View itemView) {
				super(itemView);
				mThumbnail = ButterKnife.findById(itemView, R.id.thumbnail);
				mTitle = ButterKnife.findById(itemView, R.id.title);
				mDesc = ButterKnife.findById(itemView, R.id.desc);

				itemView.setOnClickListener((v) -> {
					String url = "http://" + mTitle.getTag();
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					v.getContext().startActivity(browserIntent);
				});
			}
		}

		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.r_link, parent, false);
			ItemViewHolder ivh = new ItemViewHolder(v);
			return ivh;
		}

		// Replace the contents of a view (invoked by the layout manager)
		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
			Link link = mLinks.get(position);
			ItemViewHolder holder = (ItemViewHolder) viewHolder;
			holder.mThumbnail.setImageBitmap(link.thumbnail);
			holder.mTitle.setText(link.title);
			holder.mTitle.setTag(link.url);
			holder.mDesc.setText(link.desc);
		}

		// Return the size of your dataset (invoked by the layout manager)
		@Override
		public int getItemCount() {
			return mLinks.size();
		}
	}
}
