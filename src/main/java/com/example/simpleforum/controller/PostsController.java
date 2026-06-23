package com.example.simpleforum.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.example.simpleforum.model.Posts;
import com.example.simpleforum.model.Role;
import com.example.simpleforum.model.Users;
import com.example.simpleforum.service.PostsService;
import com.example.simpleforum.service.UsersService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class PostsController {
	@Autowired
	private PostsService postsService;
	
	@Autowired
	private UsersService usersService;
	
	/**
	 * 投稿一覧ページ遷移処理
	 * @param posts 全投稿(List型)
	 * @param mav
	 * @return 投稿タイトル一覧ページ
	 */
	@GetMapping("/posts")
	public ModelAndView index(
			ModelAndView mav,
			HttpServletRequest request) {
		
		mav.setViewName("list");
		mav.addObject("title", "投稿一覧");
		
		// 投稿全件取得
		List<Posts> list = postsService.findAll();
		mav.addObject("data", list);
		
		// ユーザー情報
		mav.addObject("loginUser", getLoginUser(request));
		
		// ビューを返す
		return mav;
	}
		
	/**
	 * 投稿作成ページ遷移処理
	 * @param mav
	 * @param request HTTPリクエスト
	 * @return 投稿新規作成ページ
	 */
	@GetMapping("/posts/new")
	public ModelAndView newForm(
			ModelAndView mav, 
			HttpServletRequest request) {
		
		mav.setViewName("form");
		mav.addObject("title", "投稿作成");
		mav.addObject("loginUser", getLoginUser(request));

		// 編集用データ = NULL(新規作成)
		mav.addObject("data", null);
		
		// readonlyモードではない
		mav.addObject("readonly", false);
		
		// 送信先
		mav.addObject("formAction", "/posts/new");
		
		// 表示用オブジェクト
		mav.addObject("submitLabel", "投稿する");
		mav.addObject("cancelHref", "/posts");
		return mav;
	}
	
	/**
	 * 投稿処理
	 * @param mav
	 * @param request HTTPリクエスト
	 * @param title　投稿タイトル
	 * @param text 投稿本文
	 * @return　送信結果(一覧ページ/投稿作成ページ)
	 */
	@PostMapping("/posts/new")
	public ModelAndView create(
			ModelAndView mav,
			HttpServletRequest request,
			@RequestParam String title,
			@RequestParam String text) {
		
		// ページタイトル(エラー時)
		mav.addObject("title", "投稿作成");
		
		// ユーザー(投稿者)情報を取得
		Users author = getLoginUser(request);
		
		// 投稿作成
		try {
			// 投稿作成
			postsService.create(title, text, author);
			
			// 遷移先(本文)?
			mav.setViewName("redirect:/posts");
		}
		// 失敗パターン
		catch (IllegalArgumentException e) {
			mav.setViewName("form");
			
			// 表示用オブジェクト
			mav.addObject("loginUser", author);
	        mav.addObject("msg", e.getMessage());
		}
		// ビューを返す
		return mav;
	}

	/**
	 * 本文内容表示
	 * @param id 投稿ID
	 * @param mav
	 * @param request HTTPリクエスト
	 * @return 本文詳細ページ
	 */
	@GetMapping("/posts/{id}")
	public ModelAndView detail(
			@PathVariable Long id,
			ModelAndView mav,
			HttpServletRequest request) {
		
		// ユーザー(投稿者)情報を取得
		Users loginUser = getLoginUser(request);
		mav.addObject("loginUser", loginUser);
		
		// 投稿内容取得
		try {
			Posts post = postsService.findById(id);
			mav.setViewName("detail");
			mav.addObject("title", post.getTitle());
			mav.addObject("data", post);

			// 編集権限確認
			boolean canEdit = (
					post.getUser() != null && 
					post.getUser().getId() == loginUser.getId()) || 
					loginUser.getRole() == Role.ADMIN;
			mav.addObject("canEdit", canEdit);

		}
		// 例外発生した場合に一覧ページにリダイレクト
		catch (NoSuchElementException e) {
			mav.setViewName("redirect:/posts");
		}
		// ビューを返す
		return mav;
	}
	
	/**
	 * 編集ページの遷移処理
	 * @param id 投稿ID
	 * @param mav
	 * @param request HTTPリクエスト
	 * @return 編集ページ / タイトル一覧ページ
	 */
	@GetMapping("/posts/edit/{id}")
	public ModelAndView edit(
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
			
			// 編集権限チェック
			postsService.checkAuthor(post, loginUser);
			
			// readonlyモードではない
			mav.addObject("readonly", false);
			
			// 表示用オブジェクト
			mav.addObject("data", post);
			mav.addObject("title", "投稿編集");
			mav.addObject("formAction", "/posts/edit" + id);
			mav.addObject("submitLabel", "更新する");
			mav.addObject("cancelHref", "/posts/" + id);

		}
		// 例外(見つからない)発生した場合に一覧ページにリダイレクト
		catch (NoSuchElementException e) {
			mav.setViewName("redirect:/posts");
		}
		// 権限ない場合は本文閲覧のページにリダイレクト
		catch (SecurityException e) {
			mav.setViewName("redirect:/posts/" + id);
		}
		// ビュー
		return mav;
	}
	
	/**
	 * 編集内容送信処理
	 * @param id 投稿ID
	 * @param title 投稿タイトル
	 * @param text 投稿本文
	 * @param mav
	 * @param request HTTPリクエスト
	 * @return 編集結果(本文ページ/編集画面)
	 */
	@PostMapping("/posts/edit/{id}")
	public ModelAndView update(
			@PathVariable Long id,
			@RequestParam String title,
			@RequestParam String text,
			ModelAndView mav,
			HttpServletRequest request) {

		// ユーザー情報を取得
		Users loginUser = getLoginUser(request);
		mav.addObject("loginUser", loginUser);

		// 送信処理
		try {
			postsService.update(id, title, text, loginUser);
			mav.setViewName("redirect:/posts/" + id);
		}
		// 権限がない場合は詳細ページに戻す
		catch (SecurityException e) {
			mav.setViewName("redirect:/posts/" + id);
		}
		// 失敗パターン(編集画面に戻す)
		catch (IllegalArgumentException e) {
			mav.setViewName("form");

			// 投稿検索
			mav.addObject("data", postsService.findById(id));
			
			// readonlyモードではない
			mav.addObject("readonly", false);

			// 表示用オブジェクト
			mav.addObject("title", "投稿編集");
			mav.addObject("formAction", "/posts/" + id + "/edit");
			mav.addObject("submitLabel", "更新する");
			mav.addObject("cancelHref", "/posts/" + id);
			mav.addObject("msg", e.getMessage());
		}
		// ビューを返す
		return mav;
	}
	
	/**
	 * 削除確認ページの遷移処理
	 * @param id 投稿ID
	 * @param mav
	 * @param request HTTPリクエスト
	 * @return 削除確認ページ / タイトル一覧ページ
	 */
	@GetMapping("/posts/delete/{id}")
	public ModelAndView deleteConfirm(
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
			
			// 権限チェック
			postsService.checkAuthor(post, loginUser);
			
			// 表示用オブジェクト
			mav.addObject("data", post);
			mav.addObject("title", "投稿削除確認");
			mav.addObject("readonly", true);
			mav.addObject("formAction", "/posts/delete/" + id);
			mav.addObject("submitLabel", "削除する");
			mav.addObject("cancelHref", "/posts/" + id);

		}
		// 例外(見つからない)発生した場合に一覧ページにリダイレクト
		catch (NoSuchElementException e) {
			mav.setViewName("redirect:/posts");
		}
		// 権限ない場合は本文閲覧のページにリダイレクト
		catch (SecurityException e) {
			mav.setViewName("redirect:/posts/" + id);
		}
		// ビューを返す
		return mav;
	}

	/**
	 * 投稿削除処理
	 * @param id 投稿ID
	 * @param mav
	 * @param request HTTPリクエスト
	 * @return 送信結果(タイトル一覧ページ/本文ページ)
	 */
	@PostMapping("/posts/delete/{id}")
	public ModelAndView delete(
			@PathVariable Long id, 
			ModelAndView mav, 
			HttpServletRequest request) {
		
		// ユーザー情報取得
		Users loginUser = getLoginUser(request);

		// 削除
		try {
			postsService.delete(id, loginUser);
		}
		// 権限がない場合は何もせず詳細ページに戻す
		catch (SecurityException e) {
			mav.setViewName("redirect:/posts/" + id);
			return mav;
		}
		// 遷移先(一覧ページ)
		mav.setViewName("redirect:/posts");
		return mav;
	}
	
	/**
	 * ログイン中のユーザー情報を取得する
	 * @param request HTTPリクエスト
	 * @return ユーザー情報
	 */
    private Users getLoginUser(HttpServletRequest request) {
        String userName = request.getRemoteUser();
        return usersService.findByUserName(userName)
                .orElseThrow(() -> new IllegalStateException("ログインユーザーが見つかりません"));
    }
}
