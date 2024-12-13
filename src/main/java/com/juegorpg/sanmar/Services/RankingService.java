package com.juegorpg.sanmar.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RankingService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> obtenerTop10Jugadores() {
        String query = "CALL ObtenerTop10Jugadores()";
        return jdbcTemplate.queryForList(query);
    }
}
