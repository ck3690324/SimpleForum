package com.example.simpleforum.controller;

import java.time.LocalDateTime;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.simpleforum.model.Comments;
import com.example.simpleforum.model.Posts;
import com.example.simpleforum.model.Users;
import com.example.simpleforum.repository.CommentRepository;
import com.example.simpleforum.repository.PostsRepository;

import jakarta.servlet.http.HttpSession;

/**
 * クラス名：CommentController 役割：コメントの登録・削除を担当するコントローラー 使用アノテーション：@Controller
 * 利用Repository：CommentRepository、PostRepository セッション利用：loginUser 主な機能： ・コメント登録
 * ・コメント削除確認 ・コメント削除
 */

@Controller
@RequestMapping("/comments")
public class CommentController {

	/** コメント情報を操作するRepository */
	private final CommentRepository commentRepository;

	/** 投稿情報を操作するRepository */
	private final PostsRepository postsRepository;

	/**
	 * コンストラクタインジェクション RepositoryをDIコンテナから受け取る
	 */
	public CommentController(CommentRepository commentRepository, PostsRepository postsRepository) {
		this.commentRepository = commentRepository;
		this.postsRepository = postsRepository;
	}

	/**
	 * コメント登録処理 投稿詳細画面から送信されたコメントを保存する
	 */
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public String createComment(
			/** HTMLから投稿IDとコメント内容を受け取る **/
			@RequestParam Long postId, // 投稿ID
			@RequestParam String text, // コメント内容
			HttpSession session) {

		/** セッションからログインしているユーザー情報を取得 */
		Users loginUser = (Users) session.getAttribute("loginUser");

		/** 未ログインの場合はログイン画面へ戻す */
		if (loginUser == null) {
			return "redirect:/";
		}

		/** コメント対象の投稿を取得 (postId を使って、投稿を検索す) */
		Posts post = postsRepository.findById(postId).orElse(null);

		/** 投稿が存在しない場合は一覧画面へ戻す */
		if (post == null) {
			return "redirect:/title";
		}

		/** コメントオブジェクト生成 */
		Comments comment = new Comments();

		/** コメント情報を設定 */
		comment.setPost(post); // 投稿情報
		comment.setUser(loginUser); // コメント投稿者
		comment.setText(text); // コメント本文
		comment.setCreatedAt(LocalDateTime.now());// 作成日時
		comment.setUpdatedAt(LocalDateTime.now());// 更新日時

		/** コメントをDBへ保存 */
		commentRepository.save(comment);

		/** 投稿詳細画面へ戻る */
		return "redirect:/posts/" + postId;
	}

	/**
	 * コメント削除確認画面表示 削除前に確認メッセージを表示する
	 */
	@RequestMapping(value = "/delete/{id}/confirm", method = RequestMethod.GET)
	public String deleteConfirm(
			/** URLからコメントIDを受け取る **/
			@PathVariable int id, // コメントID
			Model model, HttpSession session) {

		/** ログインユーザーを確認する */
		Users loginUser = (Users) session.getAttribute("loginUser");

		/** 未ログインならログイン画面へ戻す */
		if (loginUser == null) {
			return "redirect:/";
		}

		/** 削除したいコメントを探す */
		Comments comment = commentRepository.findById(id).orElse(null);

		/** コメントが存在しない場合一覧画面へ戻す */
		if (comment == null) {
			return "redirect:/title";
		}

		/** 画面へデータを渡す */
		model.addAttribute("mode", "deleteComment");
		model.addAttribute("comment", comment);
		model.addAttribute("loginUser", loginUser);
		model.addAttribute("post", comment.getPost());

		/** 削除確認画面表示(form.html) */
		return "form";
	}

	/**
	 * コメント削除処理 投稿者本人または管理者のみ削除可能
	 */
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
	public String deleteComment(@PathVariable int id, HttpSession session) {

		/** ログインユーザーを確認する */
		Users loginUser = (Users) session.getAttribute("loginUser");

		/** 未ログインならログイン画面へ戻す */
		if (loginUser == null) {
			return "redirect:/";
		}

		/** 削除対象コメント取得/探す */
		Comments comment = commentRepository.findById(id).orElse(null);

		/** コメントが存在しない場合一覧画面へ戻す */
		if (comment == null) {
			return "redirect:/title";
		}

		/** 削除後に戻るため投稿IDを取得 */
		int postId = comment.getPost().getId();

		/**
		 * コメント投稿者本人 または管理者(ADMIN) の場合のみ削除を許可
		 */
		if (comment.getUser().getId() == (loginUser.getId()) || "ADMIN".equals(loginUser.getRole())) {

			/** コメント削除 */
			commentRepository.deleteById(id);
		}

		/** 投稿詳細画面へ戻る */
		return "redirect:/body/" + postId;
	}

	// コメント入力画面表示
	/**
	 * 
	 * @param postId  コメント対象の投稿IDを受け取る
	 * @param model   画面へデータを渡すModel
	 * @param session セッション情報取得
	 * @return
	 */
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String commentForm(@RequestParam Long postId, Model model, HttpSession session) {

		// ユーザー(投稿者)情報を取得
		/** ログイン中ユーザー取得 **/
		Users loginUser = (Users)session.getAttribute("loginUser");
		/** 未ログインの場合はログイン画面へ戻す **/
		if (loginUser == null) {
			return "redirect:/";
		}
		/** 投稿IDから対象投稿を取得 **/
		Posts post = postsRepository.findById(postId).orElse(null);
		/** 投稿が存在しない場合はタイトル一覧へ戻す **/
		if (post == null) {
			return "redirect:/posts";
		}
		/** form.html をコメント入力モードで表示するためのフラグ **/
		model.addAttribute("mode", "comment");
		/** コメント対象の投稿情報を画面へ渡す **/
		model.addAttribute("post", post);
		/** ログインユーザー情報を画面へ渡す **/
		model.addAttribute("loginUser", loginUser);
		
		model.addAttribute("title", "投稿・編集・コメント・削除確認ページ");
		model.addAttribute("readonly", false);
		/** form.html を表示 **/
		return "form";
	}

}