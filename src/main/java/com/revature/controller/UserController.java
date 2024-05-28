package com.revature.controller;

import com.revature.models.User;
import com.revature.models.UsernamePasswordAuthentication;
import com.revature.service.UserService;

import io.javalin.http.Context;


public class UserController {
	
	private UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	public void authenticate(Context ctx) {
		UsernamePasswordAuthentication loginRequest = ctx.bodyAsClass(UsernamePasswordAuthentication.class);
		
		User u = userService.authenticate(loginRequest);
	
		if (u == null || !loginRequest.getPassword().equals(u.getPassword())) {
			ctx.status(400);
		} else {
			ctx.sessionAttribute("user", u);
			ctx.status(202);
			ctx.json(u);
		}
	}

	public void register(Context ctx) {
		User registerRequest = ctx.bodyAsClass(User.class);

		User newUser = userService.register(registerRequest);

		if (newUser != null) {
			ctx.json(newUser).status(201);
		}
		else {
			ctx.status(400);
		}
	}

	public void logout(Context ctx) {
		ctx.req().getSession().invalidate();
	}
	
	public boolean checkAuthorization(Context ctx) {	
		return ctx.sessionAttribute("user") != null;
	}
}
