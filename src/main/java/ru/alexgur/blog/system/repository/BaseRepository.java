package ru.alexgur.blog.system.repository;

import ru.alexgur.blog.system.exception.InternalServerException;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class BaseRepository<T> {
    private final JdbcOperations jdbc;
    private final RowMapper<T> mapper;
    private final NamedParameterJdbcTemplate njdbc;

    public BaseRepository(NamedParameterJdbcTemplate njdbc, RowMapper<T> mapper) {
        this.njdbc = njdbc;
        this.mapper = mapper;
        jdbc = njdbc.getJdbcOperations();
    }

    public boolean checkIdExist(String query, Long id) {
        try {
            Long count = jdbc.queryForObject(query, Long.class, id);
            return count != null ? count > 0 : false;
        } catch (EmptyResultDataAccessException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public Optional<T> findOne(String query, Object... params) {
        try {
            T result = jdbc.queryForObject(query, mapper, params);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        } catch (DataAccessException e) {
            throw new InternalServerException("Ошибка при поиске записи в базе данных");
        }
    }

    public List<T> findMany(String query, Object... params) {
        try {
            return njdbc.getJdbcOperations().query(query, mapper, params);
        } catch (DataAccessException e) {
            throw new InternalServerException("Ошибка при получении списка записей из базы данных");
        }
    }

    public List<T> findMany(String query, SqlParameterSource params) {
        try {
            return njdbc.query(query, params, mapper);
        } catch (DataAccessException e) {
            throw new InternalServerException("Ошибка при получении списка записей из базы данных");
        }
    }

    public List<Long> findManyIds(String query, Object... params) {
        try {
            return jdbc.query(query, (rs, rowNum) -> rs.getLong(1), params);
        } catch (DataAccessException e) {
            throw new InternalServerException("Ошибка при получении id записи в базе данных");
        }
    }

    public Long insert(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps;
        }, keyHolder);

        List<Map<String, Object>> keys = keyHolder.getKeyList();
        int countKeys = keys.size();

        if (countKeys > 1) {
            return (Long) keys.get(0).entrySet().iterator().next().getValue();
        } else if (countKeys == 1) {
            return (Long) keys.get(0).entrySet().iterator().next().getValue();
        }
        throw new InternalServerException("Не удалось сохранить данные");
    }

    public void insertMany(String query, SqlParameterSource... params) {
        try {
            njdbc.batchUpdate(query, params);
        } catch (DataAccessException e) {
            throw new RuntimeException("Ошибка добавления множества данных: " + e.getMessage(), e);
        }
    }

    public boolean delete(String query, long id) {
        int rowsDeleted = jdbc.update(query, id);
        return rowsDeleted > 0;
    }

    public void update(String query, Object... params) {
        jdbc.update(query, params);
    }
}
