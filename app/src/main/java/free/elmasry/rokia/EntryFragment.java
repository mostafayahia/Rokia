package free.elmasry.rokia;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;

/**
 * Created by yahia on 9/25/16.
 */

public class EntryFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private RadioButton mCloseAfterFinishRadio;
    private RadioButton mRepeatAfterFinishRadio;
    private static Activity mActivity;

    // preferences variables
    private SharedPreferences mPref;
    // please do NOT change this values
    static final String PREF_FILE_NAME = "preferences";
    static final int PREF_OPTION_CLOSE_AFTER_FINISHING = 100;
    static final int PREF_OPTION_REPEAT_AFTER_FINISHING = 101;
    static final int PREF_OPTION_DEFAULT = PREF_OPTION_CLOSE_AFTER_FINISHING;
    static final String PREF_KEY_USER_CHOICE = "PKUCHOICE";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // inflate the root view
        View rootView = inflater.inflate(R.layout.fragment_entry, container, false);

        // getting buttons' views from the root view
        Button rokiaConditionsButton = (Button) rootView.findViewById(R.id.entry_rokia_conditions_button);
        Button listenButton = (Button) rootView.findViewById(R.id.entry_listen_button);

        // set event handlers for the buttons' views
        rokiaConditionsButton.setOnClickListener(this);
        listenButton.setOnClickListener(this);

        // getting radio buttons' views from the root view
        mRepeatAfterFinishRadio = (RadioButton) rootView.findViewById(R.id.entry_repeat_after_finishing_radio);
        mCloseAfterFinishRadio = (RadioButton) rootView.findViewById(R.id.entry_close_after_finishing_radio);

        // setting event handlers for the radio buttons' views
        mRepeatAfterFinishRadio.setOnCheckedChangeListener(this);
        mCloseAfterFinishRadio.setOnCheckedChangeListener(this);

        // getting the user preference
        mPref = getActivity().getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        int userPref = mPref.getInt(PREF_KEY_USER_CHOICE, PREF_OPTION_DEFAULT);

        // update the radio buttons views according to it
        switch (userPref) {
            case PREF_OPTION_CLOSE_AFTER_FINISHING:
                mRepeatAfterFinishRadio.setChecked(false);
                mCloseAfterFinishRadio.setChecked(true);
                break;
            case PREF_OPTION_REPEAT_AFTER_FINISHING:
                mRepeatAfterFinishRadio.setChecked(true);
                mCloseAfterFinishRadio.setChecked(false);
        }

        // initialize mContext
        mActivity = getActivity();

        return rootView;
    }

    /*
    we store preferences in onPause because in android 2.3.3 the app can be terminated after entering
    pause state (remember the versions above 2.3.3 can be terminated after entering stop state)
     */
    @Override
    public void onPause() {
        super.onPause();

        // getting editor to save the data
        SharedPreferences.Editor ed = mPref.edit();

        // remember our app is built based on only one radio button is checked
        int userPref = (mRepeatAfterFinishRadio.isChecked()) ?
                PREF_OPTION_REPEAT_AFTER_FINISHING : PREF_OPTION_CLOSE_AFTER_FINISHING;
        ed.putInt(PREF_KEY_USER_CHOICE, userPref);

        // must be commit otherwise the data will NOT be saving
        ed.commit();
    }

    /**
     * event handler for the buttons' click
     * @param view which the user click
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.entry_rokia_conditions_button: {
                Intent intent = new Intent(getActivity(), RokiaConditionsActivity.class);
                startActivity(intent);
            }
                break;
            case R.id.entry_listen_button: {
                Intent intent = new Intent(getActivity(), ListenRokiaActivity.class);
                startActivity(intent);
            }
                break;
        }
    }

    /**
     * event handlers for the radio buttons
     * @param compoundButton
     * @param isChecked
     */
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()) {
            // if any radio button is checked we make sure that other radio buttons are unchecked
            case R.id.entry_close_after_finishing_radio:
                if (isChecked)  mRepeatAfterFinishRadio.setChecked(false);
                break;
            case R.id.entry_repeat_after_finishing_radio:
                if (isChecked)  mCloseAfterFinishRadio.setChecked(false);
                break;

        }
    }

    static Activity getEntryFragmentActivity() {
        return mActivity;
    }
}
