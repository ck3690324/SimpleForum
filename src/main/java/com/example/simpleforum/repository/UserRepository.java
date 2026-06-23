package com.example.simpleforum.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.simpleforum.model.Users;

public interface UserRepository extends JpaRepository<Users, Long> {

}
