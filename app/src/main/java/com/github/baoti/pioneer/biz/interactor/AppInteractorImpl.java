/*
 * Copyright (c) 2014-2015 Sean Liu.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.baoti.pioneer.biz.interactor;

import com.github.baoti.pioneer.biz.exception.BizException;
import com.github.baoti.pioneer.event.app.AppInitializeReportEvent;
import com.github.baoti.pioneer.event.app.AppInitializeRequestEvent;
import com.squareup.otto.Bus;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by liuyedong on 14-12-19.
 */
public class AppInteractorImpl implements AppInteractor {
    private final Bus appBus;

    public AppInteractorImpl(Bus appBus) {
        this.appBus = appBus;
    }

    @Override
    public boolean isFirstLaunching() {
        return new Random().nextBoolean();
    }

    /**
     * 耗时的初始化工作
     */
    @Override
    public void initialize() throws BizException {
        requestInitialize();
        work("建立/更新数据库", 3);
        work("准备缓存", 5);
        work("初始化应用", 7);
    }

    private void requestInitialize() {
        appBus.post(new AppInitializeRequestEvent());
    }

    private void work(String job, int seconds) throws BizException {
        if (isFirstLaunching()) {
            long millis = TimeUnit.SECONDS.toMillis(seconds);
            appBus.post(new AppInitializeReportEvent("开始" + job));
            try {
                Thread.sleep(Math.max(500, millis));
            } catch (InterruptedException e) {
                throw new BizException(e);
            }
        }
        appBus.post(new AppInitializeReportEvent("完成" + job));
    }
}
