package com.juegorpg.sanmar.Repository;

import com.juegorpg.sanmar.Model.Ranking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RankingRepository extends JpaRepository<Ranking, Integer> {
 
}
