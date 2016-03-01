package ca.paulshin.yunatube.ui.main;

import java.util.List;

import ca.paulshin.yunatube.data.model.message.Message;
import ca.paulshin.yunatube.ui.base.BaseMvpView;

/**
 * Created by paulshin on 16-02-22.
 */
public interface MessageMvpView extends BaseMvpView {

	void showMessages(List<Message> messages);

	void updateMessages(Message message);

	void showError();
}
