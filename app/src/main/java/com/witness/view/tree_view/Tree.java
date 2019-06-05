package com.witness.view.tree_view;

public class Tree {
    //1绘制树干（贝塞尔曲线）
    //2绘制花瓣（心型曲线）
    //3整体平移
    //4花瓣掉落

    private  enum Step{
        BRANCHES_GROWING,//树干
        BLOOMS_GROWING,
        MOVING_SNAPSHOT,
        BLOOMS_FALLING
    }

    private Step step = Step.BRANCHES_GROWING;

    //绘制树干

}
