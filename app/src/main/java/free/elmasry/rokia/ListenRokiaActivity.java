package free.elmasry.rokia;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ListenRokiaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen_rokia);

        // adding fragment programmatically
        if (null == savedInstanceState) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ListenRokiaFragment())
                    .commit();
        }
    }

}

