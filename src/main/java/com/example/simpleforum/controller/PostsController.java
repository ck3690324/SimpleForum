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
	 * ポスト一覧ページ遷移処理
	 * @param posts
	 * @param mav
	 * @return
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
	 * @param request
	 * @return
	 */
	@GetMapping("/posts/new")
	public ModelAndView newForm(ModelAndView mav, HttpServletRequest request) {
		mav.setViewName("form");
		mav.addObject("title", "投稿作成");
		mav.addObject("loginUser", getLoginUser(request));

		// 編集用データ = NULL(新規作成)
		mav.addObject("data", null);
		
		// readonlyモードではない
		mav.addObject("readonly", false);
		
		// 送信先
		mav.addObject("formAction", "/posts/create");
		
		// 表示用オブジェクト
		mav.addObject("submitLabel", "投稿する");
		mav.addObject("cancelHref", "/posts");
		return mav;
	}
	
	/**
	 * 投稿処理
	 * @param mav
	 * @param request
	 * @param title　投稿タイトル
	 * @param text 投稿本文
	 * @return　送信結果
	 */
	@PostMapping("/posts/create")
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
			Posts newPost = postsService.create(title, text, author);
			
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
	 * @param id
	 * @param mav
	 * @param request
	 * @return
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
		catch (NoSuchElementException e) {
			mav.setViewName("redirect:/posts");
		}
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
