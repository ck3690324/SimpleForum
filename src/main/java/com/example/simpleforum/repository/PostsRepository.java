package com.example.simpleforum.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.simpleforum.model.Posts;

public interface PostsRepository extends JpaRepository<Posts, Integer> {
	// 日時降順取得
	List<Posts> findAllByOrderByCreatedAtDesc();
	
	// ID検索
	Optional<Posts> findById(long id);
	
	// 投稿削除
	void deleteById(long id);
	
	// タイトルまたは本文で検索（作成日時の新しい順）
	List<Posts> findByTitleContainingOrderByCreatedAtDesc(String title);
}
