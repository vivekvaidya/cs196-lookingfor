package vivekvaidya.com.lookingfor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.res.TypedArray;

import android.os.Bundle;



public class EventBrowserDrawer extends BaseActivity {

    private String[] navMenuTitles;

    private TypedArray navMenuIcons;



    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_event_browser_drawer);

        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items); // load

        // titles

        // from

        // strings.xml



        navMenuIcons = getResources()

                .obtainTypedArray(R.array.nav_drawer_icons);// load icons from

        // strings.xml



        set(navMenuTitles, navMenuIcons);

    }

}
