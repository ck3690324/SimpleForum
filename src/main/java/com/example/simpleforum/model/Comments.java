package com.example.simpleforum.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "comments")
public class Comments {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private Posts post;

	@ManyToOne(fetch = FetchType.LAZY)
	private Users user;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String text;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	// コンストラクタ
	public Comments() {
	}

	public Comments(Long id, Posts post, Users user, String text, LocalDateTime createdAt) {
		this.id = id;
		this.post = post;
		this.user = user;
		this.text = text;
		this.createdAt = createdAt;
	}

	// ゲッター
	public Long getId() {
		return id;
	}

	public Posts getPost() {
		return post;
	}

	public Users getUser() {
		return user;
	}

	public String getText() {
		return text;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	// セッター
	public void setId(Long id) {
		this.id = id;
	}

	public void setPost(Posts post) {
		this.post = post;
	}

	public void setUser(Users user) {
		this.user = user;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public String toString() {
		return "Comments [id=" + id + ", post=" + (post != null ? post.getId() : null) + ", user="
				+ (user != null ? user.getId() : null) + ", text=" + text + ", createdAt=" + createdAt + "]";
	}
}