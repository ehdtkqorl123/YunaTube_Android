package ca.paulshin.yunatube.ui.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ca.paulshin.yunatube.BoilerplateApplication;
import ca.paulshin.yunatube.injection.component.ActivityComponent;
import ca.paulshin.yunatube.injection.component.DaggerActivityComponent;
import ca.paulshin.yunatube.injection.module.ActivityModule;

public class BaseActivity extends AppCompatActivity {

    private ActivityComponent mActivityComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public ActivityComponent getActivityComponent() {
        if (mActivityComponent == null) {
            mActivityComponent = DaggerActivityComponent.builder()
                    .activityModule(new ActivityModule(this))
                    .applicationComponent(BoilerplateApplication.get(this).getComponent())
                    .build();
        }
        return mActivityComponent;
    }

}
