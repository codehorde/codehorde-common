package com.github.codehorde.common.bean;

import java.util.List;

/**
 * 提交评论
 * <p>
 * Created by baomingfeng at 2018-04-26 16:51:35
 */
public class PostOrderCommentVo implements java.io.Serializable {

    private Long orderId;

    private List<PostTradeCommentVo> tradeComments;

    private PostTradeCommentVo tradeComment;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public List<PostTradeCommentVo> getTradeComments() {
        return tradeComments;
    }

    public void setTradeComments(List<PostTradeCommentVo> tradeComments) {
        this.tradeComments = tradeComments;
    }

    public PostTradeCommentVo getTradeComment() {
        return tradeComment;
    }

    public void setTradeComment(PostTradeCommentVo tradeComment) {
        this.tradeComment = tradeComment;
    }

    @Override
    public String toString() {
        return "PostOrderCommentVo{" +
                "orderId=" + orderId +
                ", tradeComments=" + tradeComments +
                ", tradeComment=" + tradeComment +
                '}';
    }
}
