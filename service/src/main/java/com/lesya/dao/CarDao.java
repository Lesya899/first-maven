package com.lesya.dao;


import com.lesya.entity.Car;
import com.lesya.entity.Model;
import com.lesya.entity.RentalStatus;
import com.lesya.util.ConnectionManager;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class CarDao implements Dao<Integer, Car> {

    private static final CarDao INSTANCE_CAR_DAO = new CarDao();


    private static final String DELETE_SQL = """
            DELETE FROM car
            WHERE id = ?
            """;

    private static final String SAVE_SQL = """
            INSERT INTO car (brand_name, car_year, color, image, model_id, rental_price, status_id) 
            VALUES (?, ?, ?, ?, ?, ?, ?);
            """;

    private static final String UPDATE_SQL = """
            UPDATE car
            SET brand_name = ?,
                car_year=?,
                color = ?,
                image=?,
                model_id = ?,
                rental_price=?,
                status_id = ?                           
            WHERE id = ?    
            """;


    private static final String FIND_ALL_SQL = """
            SELECT car.id,
                brand_name,
                car_year,
                color,
                image,
                model_id,
                rental_price,
                status_id,         
                model_name,
                capacity,
                status
            FROM car
            JOIN model m 
            ON car.model_id = m.id
            JOIN rental_status rs
            ON car.status_id = rs.id
            """;


    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE car.id = ?
            """;


    @Override
    @SneakyThrows
    public boolean delete(Integer id) {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SQL)) {
            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        }
    }


    @Override
    @SneakyThrows
    public Car save(Car carEntity) {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, carEntity.getBrandName());
            preparedStatement.setInt(2, carEntity.getCarYear());
            preparedStatement.setString(3, carEntity.getColor());
            preparedStatement.setString(4, carEntity.getImage());
            preparedStatement.setInt(5, carEntity.getModel().getId());
            preparedStatement.setInt(6, carEntity.getRentalPrice());
            preparedStatement.setInt(7, carEntity.getStatus().getId());
            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                carEntity.setId(generatedKeys.getInt("id"));
            }
            return carEntity;
        }
    }


    @Override
    @SneakyThrows
    public void update(Car carEntity) {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
             preparedStatement.setString(1, carEntity.getBrandName());
             preparedStatement.setInt(2, carEntity.getCarYear());
             preparedStatement.setString(3, carEntity.getColor());
             preparedStatement.setString(4, carEntity.getImage());
             preparedStatement.setInt(5, carEntity.getModel().getId());
             preparedStatement.setInt(6, carEntity.getRentalPrice());
             preparedStatement.setInt(7, carEntity.getStatus().getId());
             preparedStatement.setInt(8, carEntity.getId());
            preparedStatement.executeUpdate();
        }
    }

    @Override
    @SneakyThrows
    public Optional<Car> findById(Integer id) {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            Car carEntity = null;
            if (resultSet.next()) {
                carEntity = buildCar(resultSet);
            }
            return Optional.ofNullable(carEntity);
        }
    }

    @Override
    @SneakyThrows
    public List<Car> findAll() {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Car> carEntity = new ArrayList<>();
            while (resultSet.next()) {
                carEntity.add(buildCar(resultSet));
            }
            return carEntity;
        }
    }

    private Car buildCar(ResultSet resultSet) throws SQLException {
        Model model = new Model(
                resultSet.getInt("id"),
                resultSet.getString("model_name"),
                resultSet.getInt("capacity")
        );
        RentalStatus status = new RentalStatus(
                resultSet.getInt("id"),
                resultSet.getString("status")
        );
        return new Car(
                resultSet.getInt("id"),
                resultSet.getString("brand_name"),
                resultSet.getInt("car_year"),
                resultSet.getString("color"),
                resultSet.getString("image"),
                model,
                resultSet.getInt("rental_price"),
                status);
    }

    public static CarDao getInstanceCarDao() {
        return INSTANCE_CAR_DAO;
    }
}
