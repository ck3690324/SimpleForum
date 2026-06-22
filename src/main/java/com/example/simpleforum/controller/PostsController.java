package com.example.simpleforum.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.example.simpleforum.model.Posts;
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
	public ModelAndView list(
			ModelAndView mav,
			HttpServletRequest request) {
		
		mav.setViewName("list");
		mav.addObject("title", "シンプルフォーラム | 投稿一覧");
		
		List<Posts> list = postsService.findAll();
		mav.addObject("data", list);
		
		mav.addObject("loginUser", getLoginUser(request));
		
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
