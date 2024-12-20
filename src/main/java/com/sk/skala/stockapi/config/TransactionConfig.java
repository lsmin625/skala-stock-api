package com.sk.skala.stockapi.config;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.sk.skala.stockapi.data.common.AccountSession;
import com.sk.skala.stockapi.tools.JsonTool;

import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class TransactionConfig {
	private final StringRedisTemplate stringRedisTemplate;

	@Bean
	PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}

	@Bean
	AuditorAware<String> auditorProvider() {
		return new AuditorAwareImpl(stringRedisTemplate);
	}

	public static class AuditorAwareImpl implements AuditorAware<String> {
		private final StringRedisTemplate stringRedisTemplate;

		public AuditorAwareImpl(StringRedisTemplate stringRedisTemplate) {
			this.stringRedisTemplate = stringRedisTemplate;
		}

		@Override
		public Optional<String> getCurrentAuditor() {
			ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			HttpServletRequest request = sra.getRequest();
			String userId = request.getHeader(Constant.X_BFF_USER);
			if (userId != null) {
				return Optional.ofNullable(userId);
			}

			String sessionId = null;
			Cookie[] cookies = request.getCookies();
			if (cookies != null) {
				for (Cookie cookie : cookies) {
					if (Constant.BFF_SESSION_COOKIE.equals(cookie.getName())) {
						sessionId = cookie.getValue();
						break;
					}
				}
			}

			if (sessionId != null) {
				String json = stringRedisTemplate.opsForValue().get(Constant.BFF_SESSION_REDIS + sessionId);
				if (json != null) {
					AccountSession account = JsonTool.toObject(json, AccountSession.class);
					return Optional.ofNullable(account.getUserId());
				}
			}
			return Optional.ofNullable(null);
		}
	}
}
