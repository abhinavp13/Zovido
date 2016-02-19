package com.pabhinav.zovido;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * @author pabhinav
 */
public abstract class BaseDrawerActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.base_activity_drawer_frame);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        /** Inflate drawer **/
        FrameLayout drawerFrame = (FrameLayout)findViewById(R.id.left_drawer);
        getLayoutInflater().inflate(R.layout.drawer_layout, drawerFrame, true);
    }

    /**
     * This method must be called by subclass for setting activity main
     * content.
     *
     * @param id  Layout resource id to be inflated as content view.
     * @param vId  View id used for toggling navigation drawer.
     */
    protected void setDrawerContentView(int id, int vId){

        /** Inflate main content layout **/
        FrameLayout frameLayout = (FrameLayout)findViewById(R.id.content_frame);
        View rootView = getLayoutInflater().inflate(id, frameLayout, true);

        /** Set Drawer toggler in the root view **/
        rootView.findViewById(vId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    @Override
    public void onBackPressed() {

        /** close the drawer, if opened **/
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    /** Catch menu button presses and open drawer. **/
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if(!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.openDrawer(GravityCompat.START);
            } else {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    public void navigationHeaderClicked(View v){
        // do nothing
    }

    public abstract void feedbackClicked(View v);

    public abstract void changeAccountClicked(View v);

    public abstract void aboutClicked(View v);

    public abstract void agentNameChangeClicked(View v);
}
