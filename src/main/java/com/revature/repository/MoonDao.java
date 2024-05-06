package com.revature.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.revature.exceptions.MoonFailException;
import com.revature.models.Moon;
import com.revature.models.Planet;
import com.revature.utilities.ConnectionUtil;

public class MoonDao {
    
    public List<Moon> getAllMoons(int ownerId) {
		ArrayList<Moon> moonList = new ArrayList<Moon>();
		try(Connection connection = ConnectionUtil.createConnection()) {
			String sql = "SELECT moons.id, moons.name, moons.myPlanetId FROM planets INNER JOIN moons ON planets.id = moons.myPlanetId WHERE planets.ownerId = ?";
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, ownerId);
			ps.executeQuery();
			ResultSet rs = ps.executeQuery();
			while (rs.next())
			{
				Moon newMoon = new Moon();
				newMoon.setId(rs.getInt(1));
				newMoon.setName(rs.getString("name"));
				newMoon.setMyPlanetId(rs.getInt("myPlanetId"));
				moonList.add(newMoon);
			}
		}catch (SQLException e){
			System.out.println(e);
		}
		return moonList;
	}

	public Moon getMoonByName(int ownerId, String moonName) {
		try(Connection connection = ConnectionUtil.createConnection()) {
			String sql = "SELECT moons.id, moons.name, moons.myPlanetId FROM planets INNER JOIN moons ON planets.id = moons.myPlanetId WHERE planets.ownerId = ? and moons.name = ?";
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, ownerId);
			ps.setString(2, moonName);
			ps.executeQuery();
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				Moon newMoon = new Moon();
				newMoon.setId(rs.getInt(1));
				newMoon.setName(rs.getString("name"));
				newMoon.setMyPlanetId(rs.getInt("myPlanetId"));
				return newMoon;
			}
		}catch (SQLException e){
			System.out.println(e);
		}
		return null;
	}

	public Moon getMoonById(int ownerId, int moonId) {
		try(Connection connection = ConnectionUtil.createConnection()) {
			String sql = "SELECT moons.id, moons.name, moons.myPlanetId FROM planets INNER JOIN moons ON planets.id = moons.myPlanetId WHERE planets.ownerId = ? and moons.id = ?";
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, ownerId);
			ps.setInt(2, moonId);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				Moon newMoon = new Moon();
				newMoon.setId(rs.getInt(1));
				newMoon.setName(rs.getString("name"));
				newMoon.setMyPlanetId(rs.getInt("myPlanetId"));
				return newMoon;
			}
		}catch (SQLException e){
			System.out.println(e);
		}
		return null;
	}

	public Moon createMoon(int ownerId, Moon m) {
		Moon newMoon = new Moon();
		try(Connection connection = ConnectionUtil.createConnection()) {
			String moonName = m.getName();

			//Make sure the user owns the planet first.
			{
				String sql = "select * from planets where id = ? and ownerId = ?";
				PreparedStatement ps = connection.prepareStatement(sql);
				ps.setInt(1, m.getMyPlanetId());
				ps.setInt(2, ownerId);
				ResultSet rs = ps.executeQuery();
				if(!rs.next()) {
					return newMoon;
				}
			}

			//Check if Moon exists with same name
			{
				String sql = "SELECT moons.id, moons.name, moons.myPlanetId FROM planets INNER JOIN moons ON planets.id = moons.myPlanetId WHERE planets.ownerId = ? and moons.name = ?";
				PreparedStatement ps = connection.prepareStatement(sql);
				ps.setInt(1, ownerId);
				ps.setString(2, moonName);
				ps.executeQuery();
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					return newMoon;
				}
			}

			//Check if Planet exists with same name
			{
				String sql = "select * from planets where name = ? and ownerId = ?";
				PreparedStatement ps = connection.prepareStatement(sql);
				ps.setString(1, moonName);
				ps.setInt(2, ownerId);
				ResultSet rs = ps.executeQuery();
				if(rs.next()){
					return newMoon;
				}
			}

			String sql = "insert into moons (name, myPlanetId) values (?, ?)";
			PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, m.getName());
			ps.setInt(2, m.getMyPlanetId());
			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();

			newMoon.setId(rs.getInt(1));
			newMoon.setName(m.getName());
			newMoon.setMyPlanetId(m.getMyPlanetId());
		}catch (SQLException e){
			System.out.println(e);
		}
		return newMoon;
	}

	public boolean deleteMoonById(int ownerId, int moonId) {
		try(Connection connection = ConnectionUtil.createConnection()) {
			//Make sure the user owns the moon first.
			{
				String sql = "SELECT moons.id, moons.name, moons.myPlanetId FROM planets INNER JOIN moons ON planets.id = moons.myPlanetId WHERE planets.ownerId = ? AND moons.id = ?";
				PreparedStatement ps = connection.prepareStatement(sql);
				ps.setInt(1, ownerId);
				ps.setInt(2, moonId);
				ResultSet rs = ps.executeQuery();
				if(!rs.next()) {
					return false;
				}
			}

			String sql = "DELETE FROM moons WHERE id = ?";
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, moonId);
			int rowsAffected = ps.executeUpdate();
			if (rowsAffected > 0) return true;
		}catch (SQLException e){
			System.out.println(e);
		}
		System.out.println("Failed to delete.");
		return false;
	}

	public List<Moon> getMoonsFromPlanet(int ownerId, int planetId) {
		ArrayList<Moon> moonList = new ArrayList<Moon>();
		try(Connection connection = ConnectionUtil.createConnection()) {
			String sql = "SELECT moons.id, moons.name, moons.myPlanetId FROM planets INNER JOIN moons ON planets.id = moons.myPlanetId WHERE planets.ownerId = ? AND planets.id = ?";
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, ownerId);
			ps.setInt(2, planetId);
			ps.executeQuery();
			ResultSet rs = ps.executeQuery();
			while (rs.next())
			{
				Moon newMoon = new Moon();
				newMoon.setId(rs.getInt(1));
				newMoon.setName(rs.getString("name"));
				newMoon.setMyPlanetId(rs.getInt("myPlanetId"));
				moonList.add(newMoon);
			}
		}catch (SQLException e){
			System.out.println(e);
		}
		return moonList;
	}
}
