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
			String sql = "insert into planets (name, ownerId) values (?, ?)";
			PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, p.getName());
			ps.setInt(2, p.getOwnerId());
			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();

			newPlanet.setId(rs.getInt(1));
			newPlanet.setName(p.getName());
			newPlanet.setOwnerId(p.getOwnerId());
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
			if (rowsAffected > 0) return true;
		}catch (SQLException e){
			System.out.println(e);
		}
		return false;
	}
}
