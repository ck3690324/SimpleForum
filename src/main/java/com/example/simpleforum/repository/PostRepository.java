package com.example.simpleforum.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.simpleforum.model.Posts;

public interface PostRepository extends JpaRepository<Posts, Long> {

	
}
