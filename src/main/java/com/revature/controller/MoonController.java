package com.revature.controller;

import java.util.List;

import com.revature.models.Moon;
import com.revature.models.User;
import com.revature.service.MoonService;

import io.javalin.http.Context;

public class MoonController {
	
	private MoonService moonService;

	public MoonController(MoonService moonService) {
		this.moonService = moonService;
	}

	public void getAllMoons(Context ctx) {
		User u = ctx.sessionAttribute("user");
		ctx.json(moonService.getAllMoons(u.getId())).status(200);
	}

	public void getMoonByName(Context ctx) {
		User u = ctx.sessionAttribute("user");
		String moonName = ctx.pathParam("name");
		
		Moon m = moonService.getMoonByName(u.getId(), moonName);
		
		ctx.json(m).status(200);
	}

	public void getMoonById(Context ctx) {
		User u = ctx.sessionAttribute("user");
		int moonId = ctx.pathParamAsClass("id", Integer.class).get();
		
		Moon m = moonService.getMoonById(u.getId(), moonId);
		
		ctx.json(m).status(200);
	}

	public void createMoon(Context ctx) {
		Moon m = ctx.bodyAsClass(Moon.class);
		User u = ctx.sessionAttribute("user");

		Moon outGoingMoon = moonService.createMoon(u.getId(), m);
		ctx.json(outGoingMoon).status(201);
	}

	public void deleteMoon(Context ctx) {
		int moonId = ctx.pathParamAsClass("id", Integer.class).get();
		User u = ctx.sessionAttribute("user");
		moonService.deleteMoonById(u.getId(), moonId);
		
		ctx.json("Moon successfully deleted").status(202);
	}
	
	public void getPlanetMoons(Context ctx) {
		int planetId = ctx.pathParamAsClass("id", Integer.class).get();
		User u = ctx.sessionAttribute("user");
		List<Moon> moonList = moonService.getMoonsFromPlanet(u.getId(), planetId);
		
		ctx.json(moonList).status(200);
	}
}
