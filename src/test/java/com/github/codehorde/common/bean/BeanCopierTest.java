package com.github.codehorde.common.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by baomingfeng at 2018-05-02 11:19:36
 */
public class BeanCopierTest {

    public static void main(String[] args) {
        PostOrderCommentVo commentVo = new PostOrderCommentVo();
        commentVo.setOrderId(3L);
        List<PostTradeCommentVo> tradeComments = new ArrayList<PostTradeCommentVo>();
        PostTradeCommentVo tradeComment = new PostTradeCommentVo();
        tradeComment.setTradeId(33L);
        tradeComment.setScore(5);
        tradeComment.setContent("五星好评！");
        tradeComment.setTags(Arrays.asList(56L, 37L));
        tradeComments.add(tradeComment);
        tradeComment = new PostTradeCommentVo();
        tradeComment.setTradeId(34L);
        tradeComment.setScore(4);
        tradeComment.setContent("赞，五星好评！");
        tradeComment.setTags(Arrays.asList(65L, 73L));
        tradeComments.add(tradeComment);

        commentVo.setTradeComments(tradeComments);

        tradeComment = new PostTradeCommentVo();
        tradeComment.setTradeId(57L);
        tradeComment.setScore(3);
        tradeComment.setContent("五星！");
        tradeComment.setTags(Arrays.asList(87L, 93L));
        commentVo.setTradeComment(tradeComment);

        PostOrderCommentDto commentDto = new PostOrderCommentDto();

        BeanCopierUtils.adaptMapping(commentVo, commentDto);

        System.out.println(commentDto);
    }
}
