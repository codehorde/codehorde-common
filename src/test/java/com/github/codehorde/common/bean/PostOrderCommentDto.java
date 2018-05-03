package com.github.codehorde.common.bean;

import java.util.List;

/**
 * 提交评论
 * <p>
 * Created by baomingfeng at 2018-04-26 16:51:35
 */
public class PostOrderCommentDto implements java.io.Serializable {

    private Long orderId;

    private List<PostTradeCommentDto> tradeComments;

    private PostTradeCommentDto tradeComment;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public List<PostTradeCommentDto> getTradeComments() {
        return tradeComments;
    }

    public void setTradeComments(List<PostTradeCommentDto> tradeComments) {
        this.tradeComments = tradeComments;
    }

    public PostTradeCommentDto getTradeComment() {
        return tradeComment;
    }

    public void setTradeComment(PostTradeCommentDto tradeComment) {
        this.tradeComment = tradeComment;
    }

    @Override
    public String toString() {
        return "PostOrderCommentDto{" +
                "orderId=" + orderId +
                ", tradeComments=" + tradeComments +
                ", tradeComment=" + tradeComment +
                '}';
    }
}
