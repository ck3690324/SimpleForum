package com.example.simpleforum.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "comments")
public class Comments {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	/** 外部キー名を明確にする **/
	@JoinColumn(name = "post_id")
	private Posts post;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private Users user;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String text;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;
	
	@Column(nullable = false)
	private LocalDateTime updatedAt;

	// コンストラクタ
	public Comments() {
	}

	public Comments(Posts post, Users user, String text, LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.post = post;
		this.user = user;
		this.text = text;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	// ゲッター
	public int getId() {
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
	
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	// セッター
	public void setId(int id) {
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
	
	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	// toString
	@Override
	public String toString() {
		return "Comments [id=" + id + ", post=" + post + ", user=" + user + ", text=" + text + ", createdAt="
				+ createdAt + ", updatedAt=" + updatedAt + "]";
	}
}