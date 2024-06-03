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

		//This should be exception-based, but not enough time to refactor this.
		if (m.getName() != null) {
			ctx.json(m).status(200);
		}
		else {
			ctx.json(m).status(400);
		}
	}

	public void getMoonById(Context ctx) {
		User u = ctx.sessionAttribute("user");
		int moonId = ctx.pathParamAsClass("id", Integer.class).get();
		
		Moon m = moonService.getMoonById(u.getId(), moonId);

		//This should be exception-based, but not enough time to refactor this.
		if (m.getName() != null) {
			ctx.json(m).status(200);
		}
		else {
			ctx.json(m).status(400);
		}
	}

	public void createMoon(Context ctx) {
		Moon outGoingMoon = ctx.bodyAsClass(Moon.class);
		User u = ctx.sessionAttribute("user");

		Moon m = moonService.createMoon(u.getId(), outGoingMoon);


		//This should be exception-based, but not enough time to refactor this.
		if (m.getName() != null) {
			ctx.json(outGoingMoon).status(201);
		}
		else {
			ctx.json(outGoingMoon).status(400);
		}
	}

	public void deleteMoon(Context ctx) {
		int moonId = ctx.pathParamAsClass("id", Integer.class).get();
		User u = ctx.sessionAttribute("user");

		if (moonService.deleteMoonById(u.getId(), moonId)) {
			ctx.json("Moon successfully deleted").status(202);
		}
		else {
			ctx.json("Moon could not be deleted").status(400);
		}
	}
	
	public void getPlanetMoons(Context ctx) {
		int planetId = ctx.pathParamAsClass("id", Integer.class).get();
		User u = ctx.sessionAttribute("user");
		List<Moon> moonList = moonService.getMoonsFromPlanet(u.getId(), planetId);
		
		ctx.json(moonList).status(200);
	}
}
