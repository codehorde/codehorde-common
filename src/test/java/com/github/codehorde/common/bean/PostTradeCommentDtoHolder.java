package com.github.codehorde.common.bean;

/**
 * Created by baomingfeng at 2018-04-28 16:04:14
 */
public class PostTradeCommentDtoHolder {

    private PostTradeCommentDto value;

    public PostTradeCommentDtoHolder() {
    }

    public PostTradeCommentDtoHolder(PostTradeCommentDto value) {
        this.value = value;
    }

    public PostTradeCommentDto getValue() {
        return value;
    }

    public void setValue(PostTradeCommentDto value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "PostTradeCommentDtoHolder{" +
                "value=" + value +
                '}';
    }
}