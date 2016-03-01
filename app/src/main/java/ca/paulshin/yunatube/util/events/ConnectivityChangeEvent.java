package ca.paulshin.yunatube.util.events;

/**
 * Created by paulshin on 15-01-03.
 */
public class ConnectivityChangeEvent {
	public boolean networkEnabled;

	public ConnectivityChangeEvent(boolean networkEnabled) {
		this.networkEnabled = networkEnabled;
	}
}
