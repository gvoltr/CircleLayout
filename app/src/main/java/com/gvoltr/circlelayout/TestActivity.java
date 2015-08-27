package com.gvoltr.circlelayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.gvoltr.circlelayout.view.CircleLayout;

public class TestActivity extends AppCompatActivity implements CircleLayout.OnItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        fillCircleWithTestValues();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(View v, int position) {
        Toast.makeText(this, "hello circle", Toast.LENGTH_SHORT).show();
    }

    private void fillCircleWithTestValues(){
        CircleLayout testCircle = (CircleLayout) findViewById(R.id.test_circle_layout);

        ArrayAdapter<String> testAdapter =
                new ArrayAdapter(this, R.layout.circle_element, getTestArray());

        testCircle.setAdapter(testAdapter);
        testCircle.setOnItemClickListener(this);
    }

    private String[] getTestArray(){
        return new String []{
                "hello 1",
                "hello 2",
                "hello 3",
                "hello 4",
                "hello 5",
                "hello 6",
                "hello 7",
                "hello 8",
                "hello 9",
                "hello 10"
        };
    }

}
