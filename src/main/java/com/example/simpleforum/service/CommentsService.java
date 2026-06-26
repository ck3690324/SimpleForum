package com.example.simpleforum.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.simpleforum.model.Comments;
import com.example.simpleforum.model.Posts;
import com.example.simpleforum.model.Role;
import com.example.simpleforum.model.Users;
import com.example.simpleforum.repository.CommentRepository;

@Service
public class CommentsService {
	@Autowired
	private CommentRepository commentRepository;
	
	/**
	 * コメント送信処理
	 * @param post コメントする投稿
	 * @param text コメント本文
	 * @param author ユーザー
	 * @return 作成したコメント
	 */
	public Comments create(Posts post, String text, Users author) {
		// 空欄チェック
		if (text.isBlank()) {
			throw new IllegalArgumentException("コメントを入力してください");
		}
		
		// コンストラクタを使ってインスタンスを生成
		Comments comment = new Comments(post, author, text, LocalDateTime.now(), LocalDateTime.now());
		
		// DB登録処理
		return commentRepository.save(comment);
	}
	
	/**
	 * 投稿に紐づいたコメントを全取得
	 * @param postId
	 * @return
	 */
	public List<Comments> findByPostId(Long postId) {
		return commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
	}
	
	/**
	 * コメント詳細取得
	 * @param id コメントID
	 * @return
	 */
	public Comments findById(Long id) {
		return commentRepository.findById(id).orElseThrow(() -> new NoSuchElementException("投稿が見つかりません"));
	}
	
	/**
	 * コメント更新処理
	 * @param id コメントID
	 * @param text コメント本文
	 * @param loginUser 現在ログイン中のユーザー
	 * @return
	 */
	public Comments update(Long id, String text, Users loginUser) {
		// 空欄チェック
		if (text.isBlank()) {
			throw new IllegalArgumentException("コメントを入力してください");
		}
		
		// コメント検索
		Comments comment = findById(id);
		
		// 編集権限チェック
		checkAuthor(comment, loginUser);
		
		// セッターを使って内容変更
		comment.setText(text);
		comment.setUpdatedAt(LocalDateTime.now());
		
		// 登録処理
		return commentRepository.save(comment);
	}
	
	/**
	 * コメント削除処理
	 * @param id コメントID
	 * @param loginUser 現在のユーザー情報
	 */
	public void delete(Long id, Users loginUser) {
		Comments comment = findById(id);
		checkAuthor(comment, loginUser);
		commentRepository.delete(comment);
	}
	
	/**
	 * 編集権限チェック
	 * (public→コントローラー側が利用できるように)
	 * @param post 投稿
	 * @param loginUser 現在のユーザー情報
	 */
	public void checkAuthor(Comments comment, Users loginUser) {
		boolean isAuthor = comment.getUser() != null && comment.getUser().getId() == loginUser.getId();
		boolean isAdmin = loginUser.getRole() == Role.ADMIN;
		
		// 作者、管理者でもない場合 = 権限ない
		if(!isAuthor && !isAdmin) {
			throw new SecurityException("このコメントを編集・削除することが出来ません。");
		}
	}
}
