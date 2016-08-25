package ciex.edu.mx.dialog;

import android.app.Activity;
import android.support.v4.app.DialogFragment;

/**
 * Created by azuloro on 5/02/16.
 */
public abstract class BaseDialogFragment<T> extends DialogFragment {
    private T mActivityInstance;

    public final T getActivityInstance() {
        return mActivityInstance;
    }

    @Override
    public void onAttach(Activity activity) {
        mActivityInstance = (T) activity;
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivityInstance = null;
    }
}