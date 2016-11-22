package free.elmasry.rokia;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by yahia on 9/27/16.
 */

public class ListenRokiaFragment extends Fragment
        implements MediaPlayer.OnCompletionListener, View.OnClickListener {

    private TextView mTimeView;
    private Button mReplayButton;
    private MediaPlayer mMediaPlayer;

    private final String LOG_TAG = ListenRokiaFragment.class.getSimpleName();

    private static final String IS_PLAYING_KEY = "IPKEY";
    private static final String AUDIO_POSITION_KEY = "APKEY";
    private static final String PREF_MEDIA_PLAYER_POSITION = "PMPPOS";

    private SharedPreferences mPref;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // inflate root view for this fragment
        View rootView = inflater.inflate(R.layout.fragment_listen_rokia, container, false);

        // getting the views in the root view
        mTimeView = (TextView) rootView.findViewById(R.id.listen_rokia_time_textview);
        mReplayButton = (Button) rootView.findViewById(R.id.listen_rokia_replay_button);
        // IMPORTANT NOTE > onClick doesn't work on click view and linear layout in side it doesn't
        // cover all the screen for testing purposes use android:background="#ffddffdd" or "#ffffdddd"
        // so there was a bug (when clicking on the upper white space there is no response)
        // finally we remove scroll view
        LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.listen_rokia_layout);
        //FrameLayout layout = (FrameLayout) getActivity().findViewById(R.id.container);

        // initialized our media player and getting audio duration
        mMediaPlayer = MediaPlayer.create(getActivity(), R.raw.rokia);
        // we use the next audio resource for testing purposes
        //mMediaPlayer = MediaPlayer.create(getActivity(), R.raw.sample5);

        mMediaPlayer.setLooping(false);

        // set event handler to what is doing after finishing the rokia
        mMediaPlayer.setOnCompletionListener(this);

        // set event handler for the button and layout itself
        // remember you can pause or resume audio by just clicking to the layout
        layout.setOnClickListener(this);
        mReplayButton.setOnClickListener(this);

        updateAndShowTimeView();

        mPref = getActivity().getSharedPreferences(EntryFragment.PREF_FILE_NAME, Context.MODE_PRIVATE);

        int position;
        // restoring the last state of the audio when rotate device
        if (null != savedInstanceState && savedInstanceState.containsKey(AUDIO_POSITION_KEY)) {
            position = savedInstanceState.getInt(AUDIO_POSITION_KEY);
            mMediaPlayer.seekTo(position);

            if (savedInstanceState.getBoolean(IS_PLAYING_KEY)) {
                mMediaPlayer.start();
                mTimeView.setVisibility(View.INVISIBLE);
            } else {
                updateAndShowTimeView();
            }

        } else {
            // zero is default value to start rokia from the beginning
            position = mPref.getInt(PREF_MEDIA_PLAYER_POSITION, 0);
            // we make margin 3 seconds, if the remaining time is less than this value we will start
            // rokia from the beginning (remember) 3 * 1000 to convert seconds to milliseconds)
            int margin = 3 * 1000;
            if (mMediaPlayer.getDuration() - position > margin) {
                mMediaPlayer.seekTo(position);
            } else {
                mMediaPlayer.seekTo(0);
                position = 0;
            }

            updateAndShowTimeView();
        }

        // the condition is useful for example in case the user rotates the device before
        // playing the audio
        if (position != 0) {
            mReplayButton.setText(getString(R.string.listen_rokia_str_replay));
        } else {
            mReplayButton.setText(getString(R.string.listen_rokia_str_start));
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // saving the current state of the audio
        outState.putInt(AUDIO_POSITION_KEY, mMediaPlayer.getCurrentPosition());
        outState.putBoolean(IS_PLAYING_KEY, mMediaPlayer.isPlaying());
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        // getting user preference
        int userPref =
                mPref.getInt(EntryFragment.PREF_KEY_USER_CHOICE, EntryFragment.PREF_OPTION_DEFAULT);

        // change the app behavior according to the user preference
        switch (userPref) {
            case EntryFragment.PREF_OPTION_CLOSE_AFTER_FINISHING:
                saveLastState();
                releaseResources();
                getActivity().finish();
                EntryFragment.getEntryFragmentActivity().finish();
                return;
            case EntryFragment.PREF_OPTION_REPEAT_AFTER_FINISHING:
                mMediaPlayer.seekTo(0);
                mMediaPlayer.start();
        }

    }

    @Override
    public void onClick(View view) {
        // change the text in the replay button whatever is clicked to indicate the functionality
        // of the replay button change from start playing to replaying
        mReplayButton.setText(getString(R.string.listen_rokia_str_replay));
        Log.v(LOG_TAG, "onClick() method is called");

        switch (view.getId()) {
            case R.id.listen_rokia_layout:
                // on click we toggle the state of audio
                // remember we show the time view only when the audio is paused
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    updateAndShowTimeView();
                } else {
                    mTimeView.setVisibility(View.INVISIBLE);
                    mMediaPlayer.start();
                }
                break;
            // remember: this button is used only for start playing or replaying the audio
            case R.id.listen_rokia_replay_button:
                mMediaPlayer.seekTo(0);
                mMediaPlayer.start();
                mTimeView.setVisibility(View.INVISIBLE);
                break;
        }
    }

    private void updateAndShowTimeView() {
        int remainingTime = mMediaPlayer.getDuration() - mMediaPlayer.getCurrentPosition();
        mTimeView.setText(getString(R.string.str_remaining_time) +
                ": " + Utility.formatTime(getContext(), remainingTime));
        mTimeView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        // NOTE: we in onDestroyView() method NOT onDestroy() method and we are in fragment
        // condition is important because the app can be closed in onCompletion() method without
        // the user interaction (without the user presses the back button)
        if (mMediaPlayer != null) saveLastState();

        releaseResources();
        super.onDestroyView();
    }

    private void saveLastState() {
        // saving media player position before closing the app
        SharedPreferences.Editor ed = mPref.edit();
        ed.putInt(PREF_MEDIA_PLAYER_POSITION, mMediaPlayer.getCurrentPosition());
        // without committing no thing is saved
        ed.commit();
    }
    /**
     * release unneeded resources
     */
    private void releaseResources() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}

//public class DoaaActivity extends Activity implements OnClickListener {
//
//    private final String TAG = "deathApplicationDoaaActivity";
//    private MediaPlayer player;
//    private Button startBtn;
//    private TextView timeTxt;
//
//    /** run at first time activity created */
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        // TODO Auto-generated method stub
//        super.onCreate(savedInstanceState);
//
//        // set the root view
//        setContentView(R.layout.doaa);
//
//        // get the views which reside in the root view
//        startBtn = (Button) findViewById(R.id.startBtn);
//        LinearLayout layout = (LinearLayout) findViewById(R.id.doaaLayout);
//        timeTxt = (TextView) findViewById(R.id.timeTxt);
//
//        // set event handler for the views
//        layout.setOnClickListener(this);
//        startBtn.setOnClickListener(this);
//
//        // prevent screen from locking automatically
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//
//
//        // create media player for the doaa
//        if (player == null) {
//            Log.i(TAG, "player is null");
//            player = MediaPlayer.create(this, R.raw.doaa);
//            player.setLooping(false);
//        }
//
//        if (savedInstanceState != null) {
//            // retrieve the player state
//            int position = savedInstanceState.getInt("position");
//            boolean isPlaying = savedInstanceState.getBoolean("playingState");
//            player.seekTo(position);
//            if (isPlaying) { player.start(); timeTxt.setVisibility(View.INVISIBLE); }
//            else           setTimeTxt();
//            startBtn.setText(getString(R.string.replay_str));
//        }
//
//        // log cat information to trace the application
//        Log.i(TAG, "onCreate() is called");
//
//
//    }
//    @Override
//    protected void onDestroy() {
//
//        // release player object if it is not null to reclaim the memory
//        if (player != null) {
//            player.release();
//            player = null;
//            Log.i(TAG, "player object is released");
//        }
//
//        super.onDestroy();
//    }
//
//    /* event handler for the views */
//    @Override
//    public void onClick(View v) {
//
//        // change start button text whatever is clicked
//        startBtn.setText(getString(R.string.replay_str));
//
//        switch (v.getId()) {
//            case R.id.startBtn:
//                Log.i(TAG, "start button is clicked");
//                player.seekTo(0);
//                player.start();
//                timeTxt.setVisibility(View.INVISIBLE);
//                break;
//
//            case R.id.doaaLayout:
//            default:
//                if (player.isPlaying()) {
//                    player.pause();
//                    timeTxt.setVisibility(View.VISIBLE);
//                    setTimeTxt();
//                } else {
//                    player.start();
//                    timeTxt.setVisibility(View.INVISIBLE);
//                }
//                Log.i(TAG, "doaa layout is clicked");
//                break;
//        }
//
//    }
//    /* set remaining time to time text view */
//    private void setTimeTxt() {
//        // subtract elapse time in milliseconds from the doaa duration
//        //int remainingTime = player.getDuration();
//        double remainingTime = 985626 - player.getCurrentPosition();
//
//        // convert remaining time from milliseconds to minutes
//        remainingTime /= 1000 * 60;
//
//        // adjust time string
//        String timeString;
//        switch ((int) remainingTime) {
//            case 10:
//            case 9:
//            case 8:
//            case 7:
//            case 6:
//            case 5:
//            case 4:
//            case 3:
//                timeString = (int) remainingTime + " " + getString(R.string.minutes_str);
//                break;
//            case 2:
//                timeString = getString(R.string.two_minutes_str);
//                break;
//            case 1:
//                timeString = getString(R.string.one_minute_str);
//                break;
//            case 0:
//                timeString = getString(R.string.less_than_one_minute_str);
//                break;
//            default:
//                timeString = (int) remainingTime + " " + getString(R.string.minute_str);
//                break;
//        }
//
//        // set time text
//        timeTxt.setText(getString(R.string.remaining_time_str)
//                + ": " + timeString);
//
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        // TODO Auto-generated method stub
//        super.onSaveInstanceState(outState);
//
//        // save instance state of the player
//        int position = player.getCurrentPosition();
//        boolean playingState = player.isPlaying();
//        outState.putInt("position", position);
//        outState.putBoolean("playingState", playingState);
//
//    }

