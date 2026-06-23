package com.example.simpleforum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.example.simpleforum.repository.UsersRepository;
import com.example.simpleforum.service.UsersService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class UsersController {
	private final UsersRepository usersRepository;

	@Autowired
    private UsersService usersService;

	UsersController(UsersRepository usersRepository) {
		this.usersRepository = usersRepository;
	}

	/**
	 * ルート(ログインページ)
	 * 自動で/loginへリダイレクトする
	 * @return リダイレクト先(/login)
	 */
	@GetMapping("/")
	public String index() {
		return "redirect:/login";
	}
	
	/**
	 * ログインページ遷移処理
	 * ログイン失敗・ログアウトに対して、メッセージを用意して表示
	 * @param mav
	 * @param error エラー
	 * @param logout ログアウト
	 * @return ポスト一覧ページ(登録成功後) / ログインページ(初期アクセス/入力エラー)
	 */
	@GetMapping("/login")
	public ModelAndView login(
			ModelAndView mav, 
			@RequestParam(value="error", required=false) String error, 
			@RequestParam(value = "logout", required = false) String logout,
			HttpServletRequest request) {
		
		// 既にログイン中
		if (request.getRemoteUser() != null) {
			// トピック一覧へリダイレクト
			mav.setViewName("redirect:/posts");
		}
		// 初期アクセス/未ログイン→ログインページへの処理
		else {
			// 表示部分
			mav.setViewName("login");
			mav.addObject("title", "シンプルフォーラム | ログイン");

			// エラーチェック
			if(error != null) {
				mav.addObject("msg", "ユーザー名またはパスワードが正しくありません。");
			}
			else if (logout != null) {
	            mav.addObject("msg", "ログアウトしました。");
	        }
		}
		// ビューを返す
		return mav;
	}
	
	/**
	 * 新規登録ボタン処理
	 * @param mav
	 * @param username
	 * @param password
	 * @param request
	 * @return
	 */
	@PostMapping("/register")
	public ModelAndView register(
			ModelAndView mav,			 
			@RequestParam("username") String username,
			@RequestParam("password") String password,
			HttpServletRequest request) {
		
		// ページタイトル(エラー時)
		mav.addObject("title", "シンプルフォーラム | 新規登録");

		// 登録+ログイン
		try {
			// 登録処理(暗号化はサービス側で処理を行う)
			usersService.register(username, password);
			
			// 自動ログイン
			request.login(username, password);
			
			// リダイレクト
			mav.setViewName("redirect:/posts");
		}
		// 重複・空欄チェック(処理はUserService側)
		catch (IllegalArgumentException e) {
	        mav.setViewName("login");
	        mav.addObject("msg", e.getMessage());
		}
		// 失敗パターン
		catch (Exception e) {
			// エラー出力
			e.printStackTrace();
			
			// 表示部分
			mav.setViewName("login");
			mav.addObject("msg", "ログインしてください");
		}
		// ビューを返す
		return mav;
	}
}
