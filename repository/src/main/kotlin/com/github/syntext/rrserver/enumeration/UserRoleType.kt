package com.github.syntext.rrserver.enumeration

import org.springframework.security.core.GrantedAuthority

enum class UserRoleType : GrantedAuthority {
	ROLE_AUTHENTICATED,
	ROLE_VERIFIED,
	ROLE_MERCHANT,
	ROLE_SUPPORT,
	ROLE_TESTER,
	ROLE_ADMINISTRATOR;

	override fun getAuthority(): String = name
}
