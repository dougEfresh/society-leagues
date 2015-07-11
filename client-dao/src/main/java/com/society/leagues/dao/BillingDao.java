package com.society.leagues.dao;

import com.society.leagues.client.api.domain.Status;
import com.society.leagues.client.api.domain.billing.Billing;
import com.society.leagues.client.api.domain.billing.BillingPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
public class BillingDao extends Dao<Billing> {
    @Autowired UserDao userDao;

    final RowMapper<Billing> mapper = (rs, rowNum) -> {
        Billing billing = new Billing();
        billing.setId(rs.getInt("billing_id"));
        billing.setUser(userDao.get(rs.getInt("user_id")));
        billing.setAmount(rs.getInt("amount"));
        billing.setBillingPackage(BillingPackage.valueOf(rs.getString("package")));
        billing.setCreated(rs.getTimestamp("created").toLocalDateTime());
        billing.setUpdated(rs.getTimestamp("updated").toLocalDateTime());
        billing.setStatus(Status.valueOf(rs.getString("status")));
        billing.setStripeId(rs.getString("stripe_id"));
        return billing;
    };
    final static String CREATE =
            "INSERT INTO billing(user_id,stripe_id,package,amount,status,created,updated) " +
            "VALUES (?,?,?,?,?,?,?)";

    public Billing create(final Billing billing) {
        final LocalDateTime now = LocalDateTime.now();
           PreparedStatementCreator ps = con ->
        {
            PreparedStatement st  = con.prepareStatement(CREATE, Statement.RETURN_GENERATED_KEYS);
            st.setInt(1,billing.getUser().getId());
            st.setString(2,billing.getStripeId());
            st.setString(3,billing.getBillingPackage().name());
            st.setInt(4,billing.getAmount());
            st.setString(5,billing.getStatus().name());
            st.setTimestamp(6,Timestamp.valueOf(now));
            st.setTimestamp(7,Timestamp.valueOf(now));
            return st;
        } ;
        return create(billing,ps);
    }

    @Override
    public String getIdName() {
        return "billing_id";
    }

    @Override
    public String getSql() {
        return "select * from billing";
    }

    @Override
    public RowMapper<Billing> getRowMapper() {
        return mapper;
    }
}
