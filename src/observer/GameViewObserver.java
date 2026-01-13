package observer;

/**
 * Interface that the View must implement to be notified of changes in the
 * ViewModel.
 */
public interface GameViewObserver {
    /**
     * Called when the ViewModel has updated its internal state and the View should
     * refresh.
     */
    void onViewUpdated();
}
