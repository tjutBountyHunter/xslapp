package org.tjut.xsl.mvp.model;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.ActivityScope;

import javax.inject.Inject;

import org.tjut.xsl.app.AccountManager;
import org.tjut.xsl.mvp.contract.SearchTaskContract;
import org.tjut.xsl.mvp.model.api.service.HallTaskService;
import org.tjut.xsl.mvp.model.entity.HallTaskCard;
import org.tjut.xsl.mvp.model.entity.Task;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Function;


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
public class SearchTaskModel extends BaseModel implements SearchTaskContract.Model {
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public SearchTaskModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<String> reciveTask(String taskid, String hunterId) {
        return mRepositoryManager.obtainRetrofitService(HallTaskService.class)
                .reciveTask(taskid, hunterId)
                .map(new ServerResponseFunc<>());
    }

    @Override
    public Observable<List<Task>> searchData(String schoolName, String trim, int i) {
        return mRepositoryManager.obtainRetrofitService(HallTaskService.class)
                .searchTask(schoolName, trim, i)
                .map(new ServerResponseFunc<>());
    }
}