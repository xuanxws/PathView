# PathView
一个简单的矢量动画

![pathview](http://img.blog.csdn.net/20170917184449612?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcXFfMjM4NzQwODE=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/Center)

* 自定义的属性
```
<declare-styleable name="PayPathView">
        <attr name="stroke_width" format="dimension"></attr>
        <attr name="animator_duration" format="integer"></attr>
        <attr name="stroke_color" format="color"></attr>
        <attr name="paying_count" format="integer"/>
        <attr name="right_path_color" format="color"/>
        <attr name="error_path_color" format="color"/>
        <attr name="play_together" format="boolean"/>
        <attr name="auto_exit" format="boolean"></attr>
        <attr name="animator_type">
            <enum name="success_animator" value="0"/>
            <enum name="failure_animator" value="1"/>
        </attr>
</declare-styleable>
```
对应的意思在view代码里头有解释
```
    private int payingCountNums;//需要执行支付动画的总次数

    private int stroke_width;//画笔的宽度

    private int stroke_color;//画笔的颜色

    private int rightPathColor;//勾路径的颜色

    private int errorPathColor;//叉号路径的颜色

    private boolean isAutoExit;//是否播放退出动画,默认为false

    private boolean isPlayingTogether;//动画是否一起播放，默认为false

    private int animationDuration = 1000;//动画时间,默认为一秒

    private int animatorType = -1;//动画类型 0代表播放成功动画，1代表播放失败动画

```
* 五种状态
```
/**
     * 支付状态枚举
     */
    public static enum payState {
        NONE,
        PAYING,
        SUCCESS,
        FAILURE,
        EXIT
    }
```
每个状态对应一个动画，通过状态来播放动画来绘画不同的path，可以控制不同的状态实现。自由的控制绘画哪条path。
* 结合了属性动画控制绘画路径

可以看到我们是通过属性动画来控制绘画路径的长度的。取到相应比例值animatorValue，然后再invalidate一下，在onDraw方法计算要画的path的长度，这样就实现了动画效果。具体看代码吧，有注释，都比较简单。
* 外部调用方法
```
<com.example.administrator.pathview.PayPathView
            android:id="@+id/serach"
            android:layout_width="200dp"
            android:layout_height="200dp"
            android:layout_marginLeft="20dp"
            app:animator_duration="1000"
            app:animator_type="success_animator"
            app:error_path_color="@color/colorAccent"
            app:paying_count="2"
            app:play_together="false"
            app:right_path_color="@color/gray"
            app:stroke_color="@color/white"
            app:stroke_width="6dp"/>
```
外部代码设置
```
   mPayPathView.setAnimatorType(0);//播放成功的动画
   mPayPathView.setSuccessColor(selectedColor);   //成功路径的颜色
   mPayPathView.setPlayingTogether(true);  //分段path是否一起播放                          
   mPayPathView.setAutoExit(true);//是否播放回退动画
   mPayPathView.start();//开始动画
```
