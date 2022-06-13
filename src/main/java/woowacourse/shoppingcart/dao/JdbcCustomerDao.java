package woowacourse.shoppingcart.dao;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import woowacourse.shoppingcart.entity.CustomerEntity;

@Repository
public class JdbcCustomerDao implements CustomerDao {
    private static final String TABLE_NAME = "customer";
    private static final String ID_COLUMN = "id";
    private static final String EMAIL_COLUMN = "email";
    private static final String PASSWORD_COLUMN = "password";
    private static final String PROFILE_IMAGE_URL_COLUMN = "profile_image_url";
    private static final String TERMS_COLUMN = "terms";

    private static final RowMapper<CustomerEntity> CUSTOMER_ENTITY_ROW_MAPPER = (rs, rowNum) -> new CustomerEntity(
            rs.getLong(ID_COLUMN),
            rs.getString(EMAIL_COLUMN),
            rs.getString(PASSWORD_COLUMN),
            rs.getString(PROFILE_IMAGE_URL_COLUMN),
            rs.getBoolean(TERMS_COLUMN)
    );

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    public JdbcCustomerDao(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName(TABLE_NAME)
                .usingGeneratedKeyColumns(ID_COLUMN);
    }

    @Override
    public long save(CustomerEntity customerEntity) {
        Map<String, Object> params = new HashMap<>();
        params.put(EMAIL_COLUMN, customerEntity.getEmail());
        params.put(PASSWORD_COLUMN, customerEntity.getPassword());
        params.put(PROFILE_IMAGE_URL_COLUMN, customerEntity.getProfileImageUrl());
        params.put(TERMS_COLUMN, customerEntity.isTerms());

        return jdbcInsert.executeAndReturnKey(params).intValue();
    }

    @Override
    public Optional<CustomerEntity> findById(long id) {
        String sql = "SELECT id, email, password, profile_image_url, terms FROM customer WHERE id = ?";

        try {
            return Optional.of(jdbcTemplate.queryForObject(sql, CUSTOMER_ENTITY_ROW_MAPPER, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<CustomerEntity> findByEmail(String email) {
        String sql = "SELECT id, email, password, profile_image_url, terms FROM customer WHERE email = ?";

        try {
            return Optional.of(jdbcTemplate.queryForObject(sql, CUSTOMER_ENTITY_ROW_MAPPER, email));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void update(long id, CustomerEntity customerEntity) {
        String sql = "UPDATE customer SET password = ?, profile_image_url = ?, terms = ? WHERE id = ?";
        jdbcTemplate.update(sql, customerEntity.getPassword(), customerEntity.getProfileImageUrl(),
                customerEntity.isTerms(), id);
    }

    @Override
    public void delete(long id) {
        String sql = "DELETE FROM customer WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public boolean existsById(long id) {
        String sql = "SELECT EXISTS(SELECT * FROM customer WHERE id = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, id));
    }

    @Override
    public boolean existsByEmail(String email) {
        String sql = "SELECT EXISTS(SELECT * FROM customer WHERE email = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, email));
    }
}