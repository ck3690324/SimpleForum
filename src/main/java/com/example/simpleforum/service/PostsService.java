package com.example.simpleforum.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.simpleforum.model.Posts;
import com.example.simpleforum.model.Role;
import com.example.simpleforum.model.Users;
import com.example.simpleforum.repository.PostsRepository;

@Service
public class PostsService {
	@Autowired
	private PostsRepository postsRepository;
	
	/**
	 * 投稿処理
	 * @param title タイトル
	 * @param text 本文
	 * @param author 作者(ユーザー)
	 * @return
	 */
	public Posts create(String title, String text, Users author) {
		// 空欄チェック
		if (title.isBlank() || text.isBlank()) {
			throw new IllegalArgumentException("タイトルと本文を入力してください");
		}
		
		// コンストラクタを使ってインスタンスを生成
		Posts post = new Posts(title, text, author, LocalDateTime.now(), LocalDateTime.now());
		
		// DB登録処理
		return postsRepository.save(post);
	}
	
	/**
	 * 投稿全件取得
	 * コメント新着時間順でソートする
	 * @return
	 */
	public List<Posts> findAll() {
//		return postsRepository.findAllByOrderByCreatedAtDesc();
		
		// 投稿全件取得
		List<Posts> posts = postsRepository.findAllByOrderByCreatedAtDesc();
		
		// Posts.javaのメソッドを使って、コメント時間順でソートする
		posts.sort(Comparator.comparing(Posts::getLatestCommentTime).reversed());
		
		// 並び直したリストを返す
		return posts;
	}
	
	/**
	 * 投稿詳細取得
	 * @param id 投稿ID
	 * @return
	 */
	public Posts findById(Long id) {
		return postsRepository.findById(id).orElseThrow(() -> new NoSuchElementException("投稿が見つかりません"));
	}
	
	/**
	 * 投稿編集
	 * @param id 投稿ID
	 * @param title タイトル
	 * @param text 本文
	 * @param loginUser 現在のユーザー情報
	 * @return
	 */
	public Posts update(Long id, String title, String text, Users loginUser) {
		// 空欄チェック
		if (title.isBlank() || text.isBlank()) {
			throw new IllegalArgumentException("タイトルと本文を入力してください");
		}
				
		// 投稿検索
		Posts post = findById(id);
		
		// 編集権限チェック
		checkAuthor(post, loginUser);
		
		// セッターを使って内容を変更
		post.setTitle(title);
		post.setText(text);
		post.setUpdatedAt(LocalDateTime.now());
		
		// DB登録処理
		return postsRepository.save(post);
	}
	
	/**
	 * 投稿削除
	 * @param id 投稿ID
	 * @param loginUser 現在のユーザー情報
	 */
	public void delete(Long id, Users loginUser) {
		Posts post = findById(id);
		checkAuthor(post, loginUser);
		postsRepository.delete(post);
	}

	/**
	 * 編集権限チェック
	 * (public→コントローラー側が利用できるように)
	 * @param post 投稿
	 * @param loginUser 現在のユーザー情報
	 */
	public void checkAuthor(Posts post, Users loginUser) {
		boolean isAuthor = post.getUser() != null && post.getUser().getId() == loginUser.getId();
		boolean isAdmin = loginUser.getRole() == Role.ADMIN;
		
		// 作者、管理者でもない場合 = 権限ない
		if(!isAuthor && !isAdmin) {
			throw new SecurityException("この投稿を編集・削除することが出来ません。");
		}
	}
	
	/**
	 * 投稿検索
	 * @param keyword
	 * @return
	 */
	public List<Posts> search(String keyword) {
		if (keyword == null || keyword.isBlank()) {
			return findAll();
		}
		return postsRepository.findByTitleContainingOrderByCreatedAtDesc(keyword);
	}
}
