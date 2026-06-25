package com.example.simpleforum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.simpleforum.model.Role;
import com.example.simpleforum.model.Users;
import com.example.simpleforum.repository.UsersRepository;
import com.example.simpleforum.service.UsersService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * クラス名：AdminController 役割：管理者専用のユーザー管理機能を担当するコントローラー 使用アノテーション：@Controller
 * 利用Repository：UserRepository セッション利用：loginUser 主な機能： ・ユーザー一覧表示 ・ユーザー削除
 * ・管理者権限チェック
 */
@Controller
public class AdminController {

	/** ユーザー情報を操作するRepository */
	private final UsersRepository usersRepository;

	/**
	 * コンストラクタインジェクション UserRepositoryをDIコンテナ(Springがオブジェクト（Bean）を管理する仕組み)から受け取る
	 */
	public AdminController(UsersRepository usersRepository) {
		this.usersRepository = usersRepository;
	}

	@Autowired
	private UsersService usersService;

	/**
	 * 管理者用ユーザー一覧画面表示 URL：/admin/users
	 */
	@RequestMapping(value = "/admin/users", method = RequestMethod.GET)
	public String showUserList(
			Model model, 
			HttpSession session,
			HttpServletRequest request) {

//		/** セッションからログインユーザーを取得 */
//		Users loginUser = (Users) session.getAttribute("loginUser");

		Users loginUser = getLoginUser(request);
		
		/**
		 * 未ログイン または管理者以外の場合は投稿一覧へ戻す
		 */
		if (loginUser == null || loginUser.getRole() != Role.ADMIN) {
			return "redirect:/posts";
		}

		/** ログインユーザー情報を画面へ渡す */
		model.addAttribute("loginUser", loginUser);

		/** 全ユーザー一覧をDBから取得して画面へ渡す */
		model.addAttribute("users", usersRepository.findAll());

		/** admin-users.htmlを表示 */
		return "admin-users";
	}

	/**
	 * ユーザー削除処理 URL：/admin/user/delete/{id}
	 */
	@RequestMapping(value = "/admin/user/delete/{id}", method = RequestMethod.GET)
	public String deleteUser(
			@PathVariable Long id, 
			HttpSession session,
			HttpServletRequest request) {

		/** セッションからログインユーザーを取得 */
//		Users loginUser = (Users) session.getAttribute("loginUser");
		
		Users loginUser = getLoginUser(request);

		/**
		 * 未ログイン または管理者以外の場合は投稿一覧へ戻す
		 */
		if (loginUser == null || loginUser.getRole() != Role.ADMIN) {
			return "redirect:/posts";
		}

		/**
		 * 自分自身のアカウントは削除できないようにする 管理者が誤って自分を削除することを防ぐ
		 */
		if (loginUser.getId() != id) {

			/** 指定されたユーザーを削除 */
//			usersRepository.deleteById(id);
			usersService.delete(id);
		}
		
		/** ユーザー一覧画面へ戻る */
		return "redirect:/admin/users";
	}

	/**
	 * ログイン中のユーザー情報を取得する
	 * @param request HTTPリクエスト
	 * @return ユーザー情報
	 */
	private Users getLoginUser(HttpServletRequest request) {
		String userName = request.getRemoteUser();
		return usersService.findByUserName(userName).orElseThrow(() -> new IllegalStateException("ログインユーザーが見つかりません"));
	}
}