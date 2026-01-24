package com.xtr.framework.queue;

import com.xtr.framework.hutool.IData;
import lombok.Data;

@Data
public class Task {
    private IData taskInfo;//代理对象
    private  String   type;//代理类型
    private  boolean isPrioritize; //标记是否优先级
    private  int  prioritize;

    public Task(IData taskInfo,String type ,boolean isPrioritize) {
        this.taskInfo = taskInfo;
        this.isPrioritize = isPrioritize;
        this.type = type;
    }

    public IData getTaskInfo() {
        return taskInfo;
    }

    public boolean isPrioritize() {
        return isPrioritize;
    }
}

