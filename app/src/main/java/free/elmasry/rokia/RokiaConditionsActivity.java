package free.elmasry.rokia;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RokiaConditionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rokia_conditions);

        // adding fragment programmatically
        if (null == savedInstanceState) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new RokiaConditionsFragment())
                    .commit();
        }
    }

    public static class RokiaConditionsFragment extends Fragment {
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_rokia_conditions, container, false);

            return rootView;
        }
    }

}
