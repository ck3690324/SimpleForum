package com.example.simpleforum.controller;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.example.simpleforum.model.Comments;
import com.example.simpleforum.model.Posts;
import com.example.simpleforum.model.Users;
import com.example.simpleforum.service.CommentsService;
import com.example.simpleforum.service.PostsService;
import com.example.simpleforum.service.UsersService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/comments")
public class CommentController {

	@Autowired
	private UsersService usersService;

	@Autowired
	private PostsService postsService;
	
	@Autowired
	private CommentsService commentsService;

	/**
	 * コメントページに遷移する
	 * @param id 投稿ID
	 * @param mav
	 * @param request HTTPリクエスト
	 * @return コメント作成画面
	 */
	@GetMapping("/new/{id}")
	public ModelAndView create(
			@PathVariable Long id, 
			ModelAndView mav, 
			HttpServletRequest request) {

		// ユーザー情報を取得
		Users loginUser = getLoginUser(request);
		mav.addObject("loginUser", loginUser);

		try {
			mav.setViewName("form");

			// 投稿検索
			Posts post = postsService.findById(id);

			// readonlyモードではない
			mav.addObject("readonly", false);

			// 表示用オブジェクト
			mav.addObject("data", null);
			mav.addObject("title", "シンプルフォーラム | コメント");
			mav.addObject("showTitle", false);
			mav.addObject("formAction", "/comments/new/" + id);
			mav.addObject("submitLabel", "コメントする");
			mav.addObject("cancelHref", "/posts/" + id);
		}
		// 例外(見つからない)発生した場合に一覧ページにリダイレクト
		catch (NoSuchElementException e) {
			mav.setViewName("redirect:/posts");
		}
		// ビュー
		return mav;
	}

	/**
	 * コメント送信処理
	 * @param id 投稿ID
	 * @param text コメント
	 * @param mav
	 * @param request HTTPリクエスト
	 * @return 送信結果
	 */
	@PostMapping("/new/{id}")
	public ModelAndView submit(
			@PathVariable Long id, 
			@RequestParam String text, 
			ModelAndView mav,
			HttpServletRequest request) {

		// ユーザー情報を取得
		Users loginUser = getLoginUser(request);
		mav.addObject("loginUser", loginUser);

		try {
			// 投稿検索
			Posts post = postsService.findById(id);

			// コメント作成
			commentsService.create(post, text, loginUser);

			// 投稿詳細ページへリダイレクト
			mav.setViewName("redirect:/posts/" + id);
		}
		// 失敗パターン(投稿詳細画面に戻す)
		catch (NoSuchElementException e) {

			mav.setViewName("redirect:/posts");
		}
		// 送信エラー(フォームに戻る)
		catch (IllegalArgumentException e) {
			mav.setViewName("form");
			mav.addObject("readonly", false);
			mav.addObject("showTitle", false);
			mav.addObject("data", null);
			mav.addObject("title", "シンプルフォーラム | コメント");
			mav.addObject("formAction", "/comments/new/" + id);
			mav.addObject("submitLabel", "コメントする");
			mav.addObject("cancelHref", "/posts/" + id);
			mav.addObject("msg", e.getMessage());
		}
		// ビューを返す
		return mav;
	}

	/**
	 * コメント編集画面遷移
	 * @param id 投稿ID
	 * @param mav
	 * @param request HTTPリクエスト
	 * @return 編集画面
	 */
	@GetMapping("/edit/{id}")
	public ModelAndView edit(
			@PathVariable Long id,
			ModelAndView mav,
			HttpServletRequest request) {
		
		// ユーザー情報を取得
		Users loginUser = getLoginUser(request);
		mav.addObject("loginUser", loginUser);
		
		try {
			mav.setViewName("form");
			
			// コメント検索s
			Comments comment = commentsService.findById(id);
			
			// 編集権限チェック
			commentsService.checkAuthor(comment, loginUser);
			
			// readonlyモードではない
			mav.addObject("readonly", false);
			
			// 表示用オブジェクト
			mav.addObject("showTitle", false);
			mav.addObject("data", comment);
	        mav.addObject("title", "シンプルフォーラム | コメント編集");
	        mav.addObject("formAction", "/comments/edit/" + id);
	        mav.addObject("submitLabel", "更新する");
	        mav.addObject("cancelHref", "/posts/" + comment.getPost().getId());
		}
		// 例外(見つからない)発生した場合に一覧ページにリダイレクト
		catch (NoSuchElementException e) {
			mav.setViewName("redirect:/posts");
		}
		// 権限ない場合は投稿詳細ページにリダイレクト
		catch (SecurityException e) {
			Comments comment = commentsService.findById(id);
			mav.setViewName("redirect:/posts/" + comment.getPost().getId());
		}
		// ビュー
		return mav;
	}
	
	/**
	 * コメント編集送信処理
	 * @param id コメントID
	 * @param text コメント本文
	 * @param mav
	 * @param request HTTPリクエスト
	 * @return 送信結果
	 */
	@PostMapping("/edit/{id}")
	public ModelAndView update(
			@PathVariable Long id,
			@RequestParam String text,
			ModelAndView mav,
			HttpServletRequest request) {
		
		// ユーザー情報を取得
		Users loginUser = getLoginUser(request);
		mav.addObject("loginUser", loginUser);
		
		// コメント検索
		Comments comment = commentsService.findById(id);
	    
		// 投稿ID取得
		int postId = comment.getPost().getId();
		
		try {
			commentsService.update(id, text, loginUser);
			mav.setViewName("redirect:/posts/" + postId);
		}
		// 権限がない場合は詳細ページに戻す
		catch (SecurityException e) {
			mav.setViewName("redirect:/posts/" + postId);
		}
		// 失敗パターン(詳細ページに戻す)
		catch (IllegalArgumentException e) {
			// 空欄エラー → フォームを再表示
			mav.setViewName("form");
			mav.addObject("readonly", false);
			mav.addObject("showTitle", false);
			mav.addObject("data", comment);
			mav.addObject("title", "シンプルフォーラム | コメント編集");
			mav.addObject("formAction", "/comments/" + id + "/edit");
			mav.addObject("submitLabel", "更新する");
			mav.addObject("cancelHref", "/posts/" + postId);
			mav.addObject("msg", e.getMessage());
		}
		// ビューを返す
		return mav;
	}

	/**
	 * 削除確認ページの遷移処理
	 * @param id コメントID
	 * @param mav
	 * @param request HTTPリクエスト
	 * @return 削除確認ページ / 投稿詳細ページ
	 */
	@GetMapping("/delete/{id}")
	public ModelAndView deleteConfirm(
			@PathVariable Long id,
			ModelAndView mav,
			HttpServletRequest request) {
		
		// ユーザー情報を取得
		Users loginUser = getLoginUser(request);
		mav.addObject("loginUser", loginUser);
		
		try {
			mav.setViewName("form");
			
			// コメント検索
			Comments comment = commentsService.findById(id);
			
			// 権限チェック
			commentsService.checkAuthor(comment, loginUser);
			
			// 表示用オブジェクト
			mav.addObject("data", comment);
			mav.addObject("title", "シンプルフォーラム | 削除確認");
	        mav.addObject("showTitle", false);
	        mav.addObject("readonly", true);
	        mav.addObject("formAction", "/comments/delete/" + id);
	        mav.addObject("submitLabel", "削除する");
	        mav.addObject("cancelHref", "/posts/" + comment.getPost().getId());
		}
		// 例外(見つからない)発生した場合に一覧ページにリダイレクト
		catch (NoSuchElementException e) {
			mav.setViewName("redirect:/posts");
		}
		// 権限ない場合は投稿詳細ページにリダイレクト
		catch (SecurityException e) {
			Comments comment = commentsService.findById(id);
			mav.setViewName("redirect:/posts/" + comment.getPost().getId());
		}
		// ビューを返す
		return mav;
	}
	
	/**
	 * 投稿削除処理
	 * @param id コメントID
	 * @param mav
	 * @param request HTTPリクエスト
	 * @return 送信結果(投稿詳細ページ)
	 */
	@PostMapping("/delete/{id}")
	public ModelAndView delete(
			@PathVariable Long id,
			ModelAndView mav,
			HttpServletRequest request) {
		
		// ユーザー情報取得
		Users loginUser = getLoginUser(request);
		
		// コメントIDを取得
	    Comments comment = commentsService.findById(id);
	    
	    // 該当する投稿IDを取得
	    int postId = comment.getPost().getId();
	    
	    // 削除
	    try {
	    	commentsService.delete(id, loginUser);
	    }
	    // 権限がない場合は何もせず詳細ページに戻す
	    catch (SecurityException e) {
	    	mav.setViewName("redirect:/posts/" + id);
	    	return mav;
	    }
		// 遷移先(投稿詳細ページ)
		mav.setViewName("redirect:/posts/" + postId);
		return mav;
	}

	/**
	 * ログイン中のユーザー情報を取得する
	 * 
	 * @param request HTTPリクエスト
	 * @return ユーザー情報
	 */
	private Users getLoginUser(HttpServletRequest request) {
		String userName = request.getRemoteUser();
		return usersService.findByUserName(userName).orElseThrow(() -> new IllegalStateException("ログインユーザーが見つかりません"));
	}
}