package com.service;
import com.dto.VoteResponse;
import com.entity.PostVote;
import com.repository.PostRepository;
import com.repository.PostVoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;

@Service public class VoteService {
 private final PostRepository posts; private final PostVoteRepository votes;
 public VoteService(PostRepository posts,PostVoteRepository votes){this.posts=posts;this.votes=votes;}
 @Transactional public VoteResponse vote(Long postId,String email,int direction){
  if(direction < -1 || direction > 1) throw new IllegalArgumentException("Vote direction must be -1, 0, or 1");
  var post=posts.findById(postId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Post not found"));
  if(post.isHidden())throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Post not found");
  var current=votes.findByPostIdAndUserEmailIgnoreCase(postId,email);
  if(direction==0 || (current.isPresent()&&current.get().getDirection()==direction)) current.ifPresent(votes::delete);
  else if(current.isPresent()){current.get().setDirection(direction);current.get().setUpdatedAt(Instant.now());votes.save(current.get());}
  else votes.saveAndFlush(new PostVote(postId,email,direction,Instant.now()));
  long up=votes.countByPostIdAndDirection(postId,1), down=votes.countByPostIdAndDirection(postId,-1);
  return new VoteResponse(postId,votes.findByPostIdAndUserEmailIgnoreCase(postId,email).map(PostVote::getDirection).orElse(0),up,down,up-down);
 }
 public VoteResponse state(Long postId,String email){
  var post=posts.findById(postId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Post not found"));
  if(post.isHidden())throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Post not found");
  long up=votes.countByPostIdAndDirection(postId,1),down=votes.countByPostIdAndDirection(postId,-1);
  return new VoteResponse(postId,votes.findByPostIdAndUserEmailIgnoreCase(postId,email).map(PostVote::getDirection).orElse(0),up,down,up-down);
 }
}
