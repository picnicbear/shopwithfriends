package allsense.shopwithfriends;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


public class ReportSaleActivity extends ActionBarActivity {

    private EditText mItemNameView;
    private EditText mSellerView;
    private ItemReportTask mReportTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_sale);

        mItemNameView = (EditText) findViewById(R.id.itemNameEditText);
        mSellerView = (EditText) findViewById(R.id.sellerEditText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /**
     * The method for adding a user when the register button is clicked
     * With valid parameters in the three fields, a new user will be added to the "database"
     * @param view The view given by the button
     */
    public void attemptReportSale(View view) {
        if (mReportTask == null) {
            mReportTask = new ItemReportTask();
            mReportTask.execute();
        }
    }

    /**
     * Represents an asynchronous report item task.
     */
    public class ItemReportTask extends AsyncTask<Void, Void, Boolean> {
        View errorFocusView;

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                // Simulate network access.
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                if (errorFocusView == null) {
                    String itemName = mItemNameView.getText().toString();
                    String seller = mSellerView.getText().toString();

                    mItemNameView.setText("");
                    mSellerView.setText("");
                    mReportTask = null;

                    Intent intent = new Intent(ReportSaleActivity.this, ReportToFriendActivity.class);
                    intent.putExtra("itemName", itemName);
                    intent.putExtra("seller", seller);
                    startActivity(intent);
                } else {
                    errorFocusView.requestFocus();
                }
            }
            mReportTask = null;
        }

        @Override
        protected void onCancelled() {
            mReportTask = null;
        }
    }
}
