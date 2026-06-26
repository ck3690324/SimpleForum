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

/**
 * クラス名：CommentController 役割：コメントの登録・削除を担当するコントローラー 使用アノテーション：@Controller
 * 利用Repository：CommentRepository、PostRepository セッション利用：loginUser 主な機能： ・コメント登録
 * ・コメント削除確認 ・コメント削除
 */

@Controller
@RequestMapping("/comments")
public class CommentController {

//	/** コメント情報を操作するRepository */
//	private final CommentRepository commentRepository;
//
//	/** 投稿情報を操作するRepository */
//	private final PostsRepository postsRepository;
//
//	/**
//	 * コンストラクタインジェクション RepositoryをDIコンテナから受け取る
//	 */
//
//	public CommentController(CommentRepository commentRepository, PostsRepository postsRepository) {
//		this.commentRepository = commentRepository;
//		this.postsRepository = postsRepository;
//	}

	@Autowired
	private UsersService usersService;

	@Autowired
	private PostsService postsService;
	
	@Autowired
	private CommentsService commentsService;

//	/**
//	 * コメント入力画面
//	 * @param postId  コメント対象の投稿IDを受け取る
//	 * @param model   画面へデータを渡すModel
//	 * @param session セッション情報取得
//	 * @return
//	 */
//	@RequestMapping(value = "/new", method = RequestMethod.GET)
//	public String commentForm(@RequestParam Long postId, Model model, HttpSession session) {
//
//		// ユーザー(投稿者)情報を取得
//		/** ログイン中ユーザー取得 **/
//		Users loginUser = (Users)session.getAttribute("loginUser");
//		/** 未ログインの場合はログイン画面へ戻す **/
//		if (loginUser == null) {
//			return "redirect:/";
//		}
//		/** 投稿IDから対象投稿を取得 **/
//		Posts post = postsRepository.findById(postId).orElse(null);
//		/** 投稿が存在しない場合はタイトル一覧へ戻す **/
//		if (post == null) {
//			return "redirect:/posts";
//		}
//		/** form.html をコメント入力モードで表示するためのフラグ **/
//		model.addAttribute("mode", "comment");
//		/** コメント対象の投稿情報を画面へ渡す **/
//		model.addAttribute("post", post);
//		/** ログインユーザー情報を画面へ渡す **/
//		model.addAttribute("loginUser", loginUser);
//		
//		model.addAttribute("title", "投稿・編集・コメント・削除確認ページ");
//		model.addAttribute("readonly", false);
//		/** form.html を表示 **/
//		return "form";
//	}

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

//	/**
//	 * コメント登録処理 投稿詳細画面から送信されたコメントを保存する
//	 */
//	@RequestMapping(value = "/create", method = RequestMethod.POST)
//	public String createComment(
//			/** HTMLから投稿IDとコメント内容を受け取る **/
//			@RequestParam Long postId, // 投稿ID
//			@RequestParam String text, // コメント内容
//			HttpSession session) {
//
//		/** セッションからログインしているユーザー情報を取得 */
//		Users loginUser = (Users) session.getAttribute("loginUser");
//
//		/** 未ログインの場合はログイン画面へ戻す */
//		if (loginUser == null) {
//			return "redirect:/";
//		}
//
//		/** コメント対象の投稿を取得 (postId を使って、投稿を検索す) */
//		Posts post = postsRepository.findById(postId).orElse(null);
//
//		/** 投稿が存在しない場合は一覧画面へ戻す */
//		if (post == null) {
//			return "redirect:/title";
//		}
//
//		/** コメントオブジェクト生成 */
//		Comments comment = new Comments();
//
//		/** コメント情報を設定 */
//		comment.setPost(post); // 投稿情報
//		comment.setUser(loginUser); // コメント投稿者
//		comment.setText(text); // コメント本文
//		comment.setCreatedAt(LocalDateTime.now());// 作成日時
//		comment.setUpdatedAt(LocalDateTime.now());// 更新日時
//
//		/** コメントをDBへ保存 */
//		commentRepository.save(comment);
//
//		/** 投稿詳細画面へ戻る */
//		return "redirect:/posts/" + postId;
//	}

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
//	/**
//	 * コメント削除確認画面表示 削除前に確認メッセージを表示する
//	 */
//	@RequestMapping(value = "/delete/{id}/confirm", method = RequestMethod.GET)
//	public String deleteConfirm(
//			/** URLからコメントIDを受け取る **/
//			@PathVariable int id, // コメントID
//			Model model, HttpSession session) {
//
//		/** ログインユーザーを確認する */
//		Users loginUser = (Users) session.getAttribute("loginUser");
//
//		/** 未ログインならログイン画面へ戻す */
//		if (loginUser == null) {
//			return "redirect:/";
//		}
//
//		/** 削除したいコメントを探す */
//		Comments comment = commentRepository.findById(id).orElse(null);
//
//		/** コメントが存在しない場合一覧画面へ戻す */
//		if (comment == null) {
//			return "redirect:/title";
//		}
//
//		/** 画面へデータを渡す */
//		model.addAttribute("mode", "deleteComment");
//		model.addAttribute("comment", comment);
//		model.addAttribute("loginUser", loginUser);
//		model.addAttribute("post", comment.getPost());
//
//		/** 削除確認画面表示(form.html) */
//		return "form";
//	}

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
	
//	/**
//	 * コメント削除処理 投稿者本人または管理者のみ削除可能
//	 */
//	@RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
//	public String deleteComment(@PathVariable int id, HttpSession session) {
//
//		/** ログインユーザーを確認する */
//		Users loginUser = (Users) session.getAttribute("loginUser");
//
//		/** 未ログインならログイン画面へ戻す */
//		if (loginUser == null) {
//			return "redirect:/";
//		}
//
//		/** 削除対象コメント取得/探す */
//		Comments comment = commentRepository.findById(id).orElse(null);
//
//		/** コメントが存在しない場合一覧画面へ戻す */
//		if (comment == null) {
//			return "redirect:/title";
//		}
//
//		/** 削除後に戻るため投稿IDを取得 */
//		int postId = comment.getPost().getId();
//
//		/**
//		 * コメント投稿者本人 または管理者(ADMIN) の場合のみ削除を許可
//		 */
//		if (comment.getUser().getId() == (loginUser.getId()) || "ADMIN".equals(loginUser.getRole())) {
//
//			/** コメント削除 */
//			commentRepository.deleteById(id);
//		}
//
//		/** 投稿詳細画面へ戻る */
//		return "redirect:/body/" + postId;
//	}
	
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