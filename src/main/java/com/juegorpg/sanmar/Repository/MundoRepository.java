package com.juegorpg.sanmar.Repository;

import com.juegorpg.sanmar.Model.Mundo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MundoRepository extends JpaRepository<Mundo, Integer> {
   
}
