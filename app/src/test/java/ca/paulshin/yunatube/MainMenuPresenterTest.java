package ca.paulshin.yunatube;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import ca.paulshin.yunatube.ui.main.MainMenuMvpView;
import ca.paulshin.yunatube.ui.main.MainMenuPresenter;
import rx.Observable;
import ca.paulshin.yunatube.data.DataManager;
import ca.paulshin.yunatube.data.model.Ribot;
import ca.paulshin.yunatube.test.common.TestDataFactory;
import ca.paulshin.yunatube.util.RxSchedulersOverrideRule;

import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MainMenuPresenterTest {

    @Mock
    MainMenuMvpView mMockMainMenuMvpView;
    @Mock DataManager mMockDataManager;
    private MainMenuPresenter mMainMenuPresenter;

    @Rule
    public final RxSchedulersOverrideRule mOverrideSchedulersRule = new RxSchedulersOverrideRule();

    @Before
    public void setUp() {
        mMainMenuPresenter = new MainMenuPresenter(mMockDataManager);
        mMainMenuPresenter.attachView(mMockMainMenuMvpView);
    }

    @After
    public void tearDown() {
        mMainMenuPresenter.detachView();
    }

    @Test
    public void loadRibotsReturnsRibots() {
        List<Ribot> ribots = TestDataFactory.makeListRibots(10);
        doReturn(Observable.just(ribots))
                .when(mMockDataManager)
                .getRibots();

        mMainMenuPresenter.loadRibots();
        verify(mMockMainMenuMvpView).showRibots(ribots);
        verify(mMockMainMenuMvpView, never()).showRibotsEmpty();
        verify(mMockMainMenuMvpView, never()).showError();
    }

    @Test
    public void loadRibotsReturnsEmptyList() {
        doReturn(Observable.just(Collections.emptyList()))
                .when(mMockDataManager)
                .getRibots();

        mMainMenuPresenter.loadRibots();
        verify(mMockMainMenuMvpView).showRibotsEmpty();
        verify(mMockMainMenuMvpView, never()).showRibots(anyListOf(Ribot.class));
        verify(mMockMainMenuMvpView, never()).showError();
    }

    @Test
    public void loadRibotsFails() {
        doReturn(Observable.error(new RuntimeException()))
                .when(mMockDataManager)
                .getRibots();

        mMainMenuPresenter.loadRibots();
        verify(mMockMainMenuMvpView).showError();
        verify(mMockMainMenuMvpView, never()).showRibotsEmpty();
        verify(mMockMainMenuMvpView, never()).showRibots(anyListOf(Ribot.class));
    }
}
