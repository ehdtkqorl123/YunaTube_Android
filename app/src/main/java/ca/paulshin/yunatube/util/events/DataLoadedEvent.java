package ca.paulshin.yunatube.util.events;

/**
 * Created by paulshin on 10/3/14.
 */
public class DataLoadedEvent {
    public boolean refreshStarted;

    public DataLoadedEvent(boolean refreshStarted) {
        this.refreshStarted = refreshStarted;
    }
}
