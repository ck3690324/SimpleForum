package com.example.simpleforum.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.simpleforum.model.Comments;

public interface CommentRepository extends JpaRepository<Comments, Integer> {
	Optional<Comments> findById(long id);
	
	/** 投稿ごとのコメント一覧取得 **/
	List<Comments> findByPostIdOrderByCreatedAtAsc(Long postId);

	/** ユーザーごとのコメント一覧取得 **/
	List<Comments> findByUserId(Long userId);
	
	void deleteById(long id);
}
