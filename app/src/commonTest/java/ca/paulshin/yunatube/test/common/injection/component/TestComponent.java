package ca.paulshin.yunatube.test.common.injection.component;

import javax.inject.Singleton;

import dagger.Component;
import ca.paulshin.yunatube.injection.component.ApplicationComponent;
import ca.paulshin.yunatube.test.common.injection.module.ApplicationTestModule;

@Singleton
@Component(modules = ApplicationTestModule.class)
public interface TestComponent extends ApplicationComponent {

}
