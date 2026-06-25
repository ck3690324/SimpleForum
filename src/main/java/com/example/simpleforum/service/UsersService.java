package com.example.simpleforum.service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.simpleforum.model.Role;
import com.example.simpleforum.model.Users;
import com.example.simpleforum.repository.UsersRepository;

@Service
public class UsersService implements UserDetailsService {
	@Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * ユーザー登録
     * @param userName ユーザー名
     * @param rawPassword パスワード
     * @return
     */
    public Users register(String userName, String rawPassword) {
    	// ユーザー名重複チェック
    	if (usersRepository.existsByUserName(userName)) {
    		throw new IllegalArgumentException("ユーザー名は既に使用されています。");
    	}
    	// 空欄チェック
    	else if (userName.isBlank() || rawPassword.isBlank()) {
    		throw new IllegalArgumentException("空欄があります、ユーザー名とパスワードを入力してください。");
    	}
    	
    	// コンストラクタを使ってインスタンスを生成
    	Users user = new Users(userName, passwordEncoder.encode(rawPassword), Role.USER, LocalDateTime.now());
    	
    	// 登録処理
    	return usersRepository.save(user);
    }
    
    /**
     * ユーザー削除
     * @param id ユーザーID
     */
    public void delete(Long id) {
    	Users user = findById(id);
    	usersRepository.delete(user);
    }
    
    /**
     * ユーザーID検索
     * @param id ユーザーID
     * @return
     */
    public Users findById(Long id) {
    	return usersRepository.findById(id).orElseThrow(() -> new NoSuchElementException("ユーザーが見つかりません"));
    }
    
    /**
     * ログイン関連
     * ユーザー名検索
     * @param userName ユーザー名
     * @return 検索結果
     */
    public Optional<Users> findByUserName(String userName) {
        return usersRepository.findByUserName(userName);
    }
    
    /**
     * ログイン関連
     * パスワード検証
     * @param rawPassword パスワード(平文)
     * @param encodedPassword パスワード(暗号化)
     * @return 比較結果
     */
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * Spring Securityのユーザー情報取得メソッド
     */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Users user = usersRepository.findByUserName(username).orElseThrow(() -> new UsernameNotFoundException("ユーザーが見つかりません。"));
		
		return org.springframework.security.core.userdetails.User.builder()
				.username(user.getUserName())
				.password(user.getPassword())
				.roles(user.getRole().name())
				.build();
	}
}
