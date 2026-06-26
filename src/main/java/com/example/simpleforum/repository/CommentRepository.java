package com.example.simpleforum.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.simpleforum.model.Comments;

public interface CommentRepository extends JpaRepository<Comments, Integer> {
	// ID検索
	Optional<Comments> findById(long id);
	
	// 投稿に関連するコメント取得
	List<Comments> findByPostIdOrderByCreatedAtAsc(Long postId);
	// 特定ユーザーのコメント取得
	List<Comments> findByUserId(Long userId);
	
	// コメント削除
	void deleteById(long id);
}
