package ca.paulshin.yunatube.ui.main;

import java.util.List;

import ca.paulshin.yunatube.data.model.video.Comment;
import ca.paulshin.yunatube.data.model.video.SimpleResult;
import ca.paulshin.yunatube.data.model.video.Video;
import ca.paulshin.yunatube.ui.base.BaseMvpView;

/**
 * Created by paulshin on 16-02-22.
 */
public interface VideoMvpView extends BaseMvpView {

	void showVideo(Video video);

	void showComments(List<Comment> comments);

	void updateComment(Comment comment);

	void report(SimpleResult result);

	void showError();
}
