package org.tjut.xsl.mvp.presenter;

import android.app.Application;

import com.jess.arms.http.imageloader.glide.ImageConfigImpl;
import com.jess.arms.integration.AppManager;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.utils.RxLifecycleUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;

import javax.inject.Inject;

import org.tjut.xsl.app.AccountManager;
import org.tjut.xsl.app.DataNullException;
import org.tjut.xsl.mvp.contract.SearchTaskContract;
import org.tjut.xsl.mvp.model.entity.Task;

import java.util.List;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 05/29/2019 02:56
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@ActivityScope
public class SearchTaskPresenter extends BasePresenter<SearchTaskContract.Model, SearchTaskContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public SearchTaskPresenter(SearchTaskContract.Model model, SearchTaskContract.View rootView) {
        super(model, rootView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mErrorHandler = null;
        this.mAppManager = null;
        this.mImageLoader = null;
        this.mApplication = null;
    }

    public void loadTaskItemTx(ImageConfigImpl imageConfig) {
        mImageLoader.loadImage(mApplication, imageConfig);
    }

    public void reciveTask(String taskid) {
        mModel.reciveTask(taskid, AccountManager.getHunterId())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(disposable -> {
                    mRootView.showLoading();
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> mRootView.hideLoading())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<String>(mErrorHandler) {
                    @Override
                    public void onNext(String s) {
                        mRootView.showReciveSuccess(taskid);
                    }
                    @Override
                    public void onError(Throwable t) {
                        if (t instanceof DataNullException) {
                            mRootView.showReciveSuccess(taskid);
                        } else {
                            super.onError(t);
                        }
                    }
                });
    }

    public void searchData(String trim) {
        mModel.searchData(AccountManager.getSchoolName(),trim,10)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(disposable -> {
                    mRootView.showLoading();
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> mRootView.hideLoading())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(new ErrorHandleSubscriber<List<Task>>(mErrorHandler) {
                    @Override
                    public void onNext(List<Task> tasks) {
                        if (tasks == null || tasks.isEmpty()) {
                            mRootView.showEmptyView();
                        } else {
                            mRootView.showInitData(tasks);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        if (t instanceof DataNullException) {
                            mRootView.showEmptyView();
                        } else {
                            super.onError(t);
                        }
                    }
                });
    }
}
