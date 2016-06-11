package ca.paulshin.yunatube.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import ca.paulshin.yunatube.R;

import static ca.paulshin.yunatube.R.id.first;

/**
 * Created by paulshin on 2016-06-09.
 */

public class PoemViewHolder extends RecyclerView.ViewHolder {
	public TextView headerView;
	public TextView nameView;
	public TextView firstView;
	public TextView secondView;
	public TextView thirdView;
	public TextView fourthView;
	public TextView fifthView;
	public TextView sixthView;
//		public CircleImageView poemImageView;

	public PoemViewHolder(View v) {
		super(v);
		headerView = (TextView) itemView.findViewById(R.id.acrostic_header);
		nameView = (TextView) itemView.findViewById(R.id.name);
		firstView = (TextView) itemView.findViewById(first);
		secondView = (TextView) itemView.findViewById(R.id.second);
		thirdView = (TextView) itemView.findViewById(R.id.third);
		fourthView = (TextView) itemView.findViewById(R.id.fourth);
		fifthView = (TextView) itemView.findViewById(R.id.fifth);
		sixthView = (TextView) itemView.findViewById(R.id.sixth);
//			poemImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);
	}
}