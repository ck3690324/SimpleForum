package com.example.simpleforum.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
	/**
	 * パスワードエンコーダー
	 * @return エンコーダー
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * 
	 * @param http
	 * @return
	 * @throws Exception 例外
	 */
	@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        	// アクセス可能範囲
            .authorizeHttpRequests(auth -> auth
            	.requestMatchers("/login", "/register", "/css/**", "/js/**")
            	.permitAll()
            	.anyRequest().authenticated()
    		)
            // ログイン関連(URLパス、処理時URL、成功時遷移先、失敗時のURLパス)
            .formLogin((form) -> form
        		.loginPage("/login")
        		.loginProcessingUrl("/login")
        		.defaultSuccessUrl("/posts", true)
        		.failureUrl("/login?error")
        		.permitAll()
        	)
            // ログアウト処理
        	.logout((logout) -> logout
    			.logoutSuccessUrl("/login?logut")
    			.permitAll()
			);
        	// +/- アクセス不可部分のリダイレクト処理
        	

        return http.build();
    }
	
//	@Autowired
//	private JdbcTemplate jdbcTemplate;
//	
//	/**
//	 * ダミーアカウント作成
//	 * @param passwordEncoder エンコーダー
//	 * @return ダミーアカウント / エラーメッセージ
//	 */
//	@Bean
//	public CommandLineRunner initDummyAccounts(PasswordEncoder passwordEncoder) {
//		return args -> {
//			String countSql = "SELECT COUNT(*) FROM users";
//			Integer userCount = jdbcTemplate.queryForObject(countSql, Integer.class);
//			
//			if (userCount != null && userCount == 0) {
//				String insertSql = "INSERT INTO users (id, user_name, password, role, created_at) VALUES (?, ?, ?, ?, ?)";
//				
//				jdbcTemplate.update(insertSql, 1, "admin", passwordEncoder.encode("pass"), "ADMIN", LocalDateTime.now());
//				jdbcTemplate.update(insertSql, 2, "user", passwordEncoder.encode("pass"), "USER", LocalDateTime.now());
//				jdbcTemplate.update(insertSql, 3, "test", passwordEncoder.encode("pass"), "USER", LocalDateTime.now());
//				
//				System.out.println("アカウントを作成しました。");
//			}
//			else {
//				System.out.println("アカウント作成しませんでした");
//			}
//		};
//	}
}
