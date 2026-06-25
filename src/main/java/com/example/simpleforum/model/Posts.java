package com.example.simpleforum.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "posts")
public class Posts {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(nullable = false, length = 100)
	private String title;
	
	@Column(nullable = false, columnDefinition = "TEXT")
	private String text;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Users user;
	
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;
	
	@Column(nullable = false)
	private LocalDateTime updatedAt;
	
	@OneToMany(mappedBy = "post")
	private List<Comments> comments;
	
	@Column(nullable = false, length = 20)
	private String category;

	// コンストラクタ
	public Posts() {}
	
	public Posts(String category, String title, String text, Users user, LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.category = category;
		this.title = title;
		this.text = text;
		this.user = user;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	// ゲッター
	public int getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getText() {
		return text;
	}

	public Users getUser() {
		return user;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
	
	public String getCategory() {
	    return category;
	}

	// セッター
	public void setId(int id) {
		this.id = id;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setUser(Users user) {
		this.user = user;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
	
	public void setCategory(String category) {
	    this.category = category;
	}
	
	public List<Comments> getComments() {
	    return comments;
	}

	public void setComments(List<Comments> comments) {
	    this.comments = comments;
	}

	// コメント数を取得
	public int getCommentCount() {
		return comments == null ? 0 : comments.size();
	}
	
	// 最新コメントの作成時間を取得
	public LocalDateTime getLatestCommentTime() {
		// コメントない場合は投稿の作成時間
		if (comments == null || comments.isEmpty()) {
			return createdAt;
		}
		// コメントがあれば取得し、比較してから新しい方を返す
		return comments.stream().map(Comments::getCreatedAt).max(LocalDateTime::compareTo).orElse(createdAt);
	}
	
	// toString
	@Override
	public String toString() {
		return "Posts [id=" + id + ", title=" + title + ", text=" + text + ", user=" + user + ", createdAt="
				+ createdAt + ", updatedAt=" + updatedAt + "]";
	}
}
