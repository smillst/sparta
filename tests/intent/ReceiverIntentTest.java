package tests;

import static sparta.checkers.quals.FlowPermission.*;
import sparta.checkers.quals.*;
import android.app.Activity;
import android.content.Intent;

/**
 * Class used to test the intent analysis.
 * Class that simulates a Receiver Activity
 * on the Intent analysis. Used in the Intent-checker tests from sparta-code.
 * @author pbsf
 *
 */
public class ReceiverIntentTest extends Activity {

    void m() {
        @IntentMap({ @Extra(key = "k5", source = { ACCESS_FINE_LOCATION }, sink = {}) })
        Intent i = (@IntentMap({ @Extra(key = "k5", source = { ACCESS_FINE_LOCATION }, sink = {}) })
        Intent) getIntent();
    }

    @Override
    @ReceiveIntent("startActivity,1")
    // :: error: (intent.getintent.notfound)
    public void setIntent(@IntentMap({ @Extra(key = "k5", source = { ACCESS_FINE_LOCATION }, sink = {}) })
        Intent newIntent) {
        super.setIntent(newIntent);
    }

}
