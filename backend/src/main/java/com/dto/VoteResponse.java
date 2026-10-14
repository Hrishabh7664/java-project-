package com.dto;
public record VoteResponse(Long postId,int userVote,long upvotes,long downvotes,long score) {}
