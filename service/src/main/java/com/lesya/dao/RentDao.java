package com.lesya.dao;


import com.lesya.entity.Rent;
import com.lesya.entity.RequestStatus;
import com.lesya.util.ConnectionManager;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class RentDao implements Dao<Integer, Rent>{

    private static final RentDao INSTANCE_RENT_DAO = new RentDao();


    private static final String DELETE = """
            DELETE FROM rent
            WHERE id = ?
            """;


    private static final String SAVE = """
            INSERT INTO rent (date_start, termination_car_rental, car_id, request_status, user_id, passport, driving_experience,mess)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

    private static final String UPDATE = """
            UPDATE rent
            SET date_start = ?,
                termination_car_rental = ?,
                car_id = ?,
                request_status = ?,
                user_id = ?,
                passport = ?,
                driving_experience = ?,
                mess = ?
            WHERE id = ?  
            """;


    private static final String FIND_ALL = """
            SELECT id,
                date_start,
                termination_car_rental,
                car_id,
                request_status,
                user_id,
                passport,
                driving_experience,
                mess                           
            FROM rent
            """;


    private static final String FIND_BY_ID = FIND_ALL + """
            WHERE id = ?
            """;


    @Override
    @SneakyThrows
    public boolean delete(Integer id) {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE)) {
            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        }
    }

    @Override
    @SneakyThrows
    public Rent save(Rent rentEntity) {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setDate(1, Date.valueOf(rentEntity.getDateStart()));
            preparedStatement.setDate(2, Date.valueOf(rentEntity.getTerminationCarRental()));
            preparedStatement.setInt(3, rentEntity.getCarId());
            preparedStatement.setString(4, rentEntity.getRequestStatus().name());
            preparedStatement.setInt(5, rentEntity.getUserId());
            preparedStatement.setString(6, rentEntity.getPassport());
            preparedStatement.setInt(7, rentEntity.getDrivingExperience());
            preparedStatement.setString(8,rentEntity.getMessage());
            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                rentEntity.setId(generatedKeys.getInt("id"));
            }
            return rentEntity;
        }
    }
    @Override
    @SneakyThrows
    public void update(Rent rentEntity) {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE)) {
            preparedStatement.setDate(1, Date.valueOf(rentEntity.getDateStart()));
            preparedStatement.setDate(2, Date.valueOf(rentEntity.getTerminationCarRental()));
            preparedStatement.setInt(3, rentEntity.getCarId());
            preparedStatement.setString(4, rentEntity.getRequestStatus().name());
            preparedStatement.setInt(5, rentEntity.getUserId());
            preparedStatement.setString(6, rentEntity.getPassport());
            preparedStatement.setInt(7, rentEntity.getDrivingExperience());
            preparedStatement.setString(8,rentEntity.getMessage());
            preparedStatement.setInt(9, rentEntity.getId());
            preparedStatement.executeUpdate();
        }
    }

    @Override
    @SneakyThrows
    public Optional<Rent> findById(Integer id) {
        try (var connection = ConnectionManager.get()) {
            return findById(id, connection);
        }
    }


    @SneakyThrows
    public Optional<Rent> findById(Integer id, Connection connection) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            Rent rentEntity = null;
            if (resultSet.next()) {
                rentEntity = buildRent(resultSet);
            }
            return Optional.ofNullable(rentEntity);
        }
    }

    @Override
    @SneakyThrows
    public List<Rent> findAll() {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Rent> rentEntities = new ArrayList<>();
            while (resultSet.next()) {
                rentEntities.add(buildRent(resultSet));
            }
            return rentEntities;
        }
    }


    private Rent buildRent(ResultSet resultSet) throws SQLException {
        return Rent.builder()
                .id(resultSet.getObject("id", Integer.class))
                .dateStart(resultSet.getObject("date_start", LocalDate.class))
                .terminationCarRental(resultSet.getObject("termination_car_rental", LocalDate.class))
                .carId(resultSet.getObject("car_id", Integer.class))
                .requestStatus(RequestStatus.valueOf(resultSet.getObject("request_status", String.class)))
                .userId(resultSet.getObject("user_id", Integer.class))
                .passport(resultSet.getObject("passport", String.class))
                .drivingExperience(resultSet.getObject("driving_experience", Integer.class))
                .message(resultSet.getObject("mess", String.class))
                .build();

    }

    public static RentDao getInstanceRentDao() {
        return INSTANCE_RENT_DAO;
    }
}

