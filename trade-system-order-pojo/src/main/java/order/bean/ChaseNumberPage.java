package order.bean;

import com.caiyi.lottery.tradesystem.bean.Page;
import order.pojo.ComplexPojo;

import java.util.List;

/**
 * 追号分页
 *
 * @author GJ
 * @create 2017-12-27 17:02
 **/
public class ChaseNumberPage  extends Page<List<ComplexPojo>>{

    /**
     * 已经追号期次
     */
    private Integer DoneChaseCount;

    /**
     * 标题
     */
    private ComplexPojo title;

    public Integer getDoneChaseCount() {
        return DoneChaseCount;
    }

    public void setDoneChaseCount(Integer doneChaseCount) {
        DoneChaseCount = doneChaseCount;
    }

    public ComplexPojo getTitle() {
        return title;
    }

    public void setTitle(ComplexPojo title) {
        this.title = title;
    }
}
