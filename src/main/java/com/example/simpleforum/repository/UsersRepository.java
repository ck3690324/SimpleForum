package com.example.simpleforum.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.simpleforum.model.Users;

public interface UsersRepository extends JpaRepository<Users, Integer> {
	// ユーザー名検索(ログイン用)
	Optional<Users> findByUserName(String userName);
	
	// ID検索
	Optional<Users> findById(long id);
	
	// ユーザー全件取得
	@Override
	List<Users> findAll();
	
	// ユーザー削除
	void deleteById(long id);
	
	// 重複チェック
	boolean existsByUserName(String userName);
}
