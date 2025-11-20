package br.com.louise.AppProdutos.service.impl;

import br.com.louise.AppProdutos.model.UserEntity;
import br.com.louise.AppProdutos.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com email: " + email));

        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole()); // "ROLE_USER" ou "ROLE_ADMIN"
        return new User(user.getEmail(), user.getPassword(), Collections.singleton(authority));
    }
}
