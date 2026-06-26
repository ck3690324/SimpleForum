package com.example.simpleforum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.simpleforum.model.Role;
import com.example.simpleforum.model.Users;
import com.example.simpleforum.service.UsersService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {
	@Autowired
	private UsersService usersService;

	/**
	 * 管理者用ユーザー一覧画面表示
	 * @param model
	 * @param session
	 * @param request
	 * @return
	 */
	@GetMapping("/admin/users")
	public String showUserList(
			Model model, 
			HttpSession session,
			HttpServletRequest request) {

		Users loginUser = getLoginUser(request);
		
		//未ログイン または管理者以外の場合は投稿一覧へ戻す
		if (loginUser == null || loginUser.getRole() != Role.ADMIN) {
			return "redirect:/posts";
		}

		// ログインユーザー情報を画面へ渡す
		model.addAttribute("loginUser", loginUser);

		// 全ユーザー一覧をDBから取得して画面へ渡す
		model.addAttribute("users", usersService.findAll());

		//admin-users.htmlを表示
		return "admin-users";
	}

	/**
	 * ユーザー削除処理 
	 */
	@PostMapping("/admin/user/delete/{id}")
	public String deleteUser(
			@PathVariable Long id, 
			HttpServletRequest request) {

		Users loginUser = getLoginUser(request);

		// 未ログイン または管理者以外の場合は投稿一覧へ戻す
		if (loginUser.getRole() != Role.ADMIN) {
	        return "redirect:/posts";
	    }

		// 自分自身のアカウントは削除できない
		if (loginUser.getId() != id) {
			// 指定されたユーザーを削除
			usersService.delete(id);
		}
		
		// ユーザー一覧画面へ戻る
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