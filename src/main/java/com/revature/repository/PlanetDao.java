package com.revature.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.revature.models.Planet;
import com.revature.models.User;
import com.revature.utilities.ConnectionUtil;

public class PlanetDao {
    
    public List<Planet> getAllPlanets(int ownerId) {
		ArrayList<Planet> planetList = new ArrayList<Planet>();
		try(Connection connection = ConnectionUtil.createConnection()) {
			String sql = "select * from planets where ownerId = ?";
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, ownerId);
			ResultSet rs = ps.executeQuery();
			while (rs.next())
			{
				Planet newPlanet = new Planet();
				newPlanet.setId(rs.getInt(1));
				newPlanet.setName(rs.getString("name"));
				newPlanet.setOwnerId(rs.getInt("ownerId"));
				planetList.add(newPlanet);
			}
		}catch (SQLException e){
			System.out.println(e);
		}
		return planetList;
	}

	public Planet getPlanetByName(int ownerId, String planetName) {
		try(Connection connection = ConnectionUtil.createConnection()) {
			String sql = "select * from planets where name = ? and ownerId = ?";
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setString(1, planetName);
			ps.setInt(2, ownerId);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				Planet newPlanet = new Planet();
				newPlanet.setId(rs.getInt(1));
				newPlanet.setName(rs.getString("name"));
				newPlanet.setOwnerId(rs.getInt("ownerId"));
				return newPlanet;
			}
		}catch (SQLException e){
			System.out.println(e);
		}
		return null;
	}

	public Planet getPlanetById(int ownerId, int planetId) {
		try(Connection connection = ConnectionUtil.createConnection()) {
			String sql = "select * from planets where id = ? and ownerId = ?";
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, planetId);
			ps.setInt(2, ownerId);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				Planet newPlanet = new Planet();
				newPlanet.setId(rs.getInt(1));
				newPlanet.setName(rs.getString("name"));
				newPlanet.setOwnerId(rs.getInt("ownerId"));
				return newPlanet;
			}
		}catch (SQLException e){
			System.out.println(e);
		}
		return null;
	}

	public Planet createPlanet(Planet p) {
		Planet newPlanet = new Planet();
		try(Connection connection = ConnectionUtil.createConnection()) {

			String planetName = p.getName();
			int ownerId = p.getOwnerId();
			//Check if Planet exists with same name
			{
				String sql = "select * from planets where name = ? and ownerId = ?";
				PreparedStatement ps = connection.prepareStatement(sql);
				ps.setString(1, planetName);
				ps.setInt(2, ownerId);
				ResultSet rs = ps.executeQuery();
				if(rs.next()){
					return newPlanet;
				}
			}

			//Check if Moon exists with same name
			{
				String sql = "SELECT moons.id, moons.name, moons.myPlanetId FROM planets INNER JOIN moons ON planets.id = moons.myPlanetId WHERE planets.ownerId = ? and moons.name = ?";
				PreparedStatement ps = connection.prepareStatement(sql);
				ps.setInt(1, ownerId);
				ps.setString(2, planetName);
				ps.executeQuery();
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					return newPlanet;
				}
			}

			//Create new Planet if it does not exist
			{
				String sql = "insert into planets (name, ownerId) values (?, ?)";
				PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, planetName);
				ps.setInt(2, ownerId);
				ps.executeUpdate();
				ResultSet rs = ps.getGeneratedKeys();

				newPlanet.setId(rs.getInt(1));
				newPlanet.setName(p.getName());
				newPlanet.setOwnerId(p.getOwnerId());
			}
		}catch (SQLException e){
			System.out.println(e);
		}
		return newPlanet;
	}

	public boolean deletePlanetById(int ownerId, int planetId) {
		try(Connection connection = ConnectionUtil.createConnection()) {
			String sql = "delete from planets where ownerId = ? and id = ?";
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, ownerId);
			ps.setInt(2, planetId);
			int rowsAffected = ps.executeUpdate();
			if (rowsAffected > 0) {
				//Need to manually delete moons since ON DELETE CASCADE isn't working.
				String moonSql = "delete from moons where myPlanetId = ?";
				PreparedStatement deleteMoons = connection.prepareStatement(moonSql);
				deleteMoons.setInt(1, planetId);
				deleteMoons.executeUpdate();
				return true;
			}
		}catch (SQLException e){
			System.out.println(e);
		}
		return false;
	}
}
