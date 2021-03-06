package com.smart.dao;


import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.smart.entities.Worker;

public interface WorkerRepository extends JpaRepository<Worker, Integer> {
    public List<Worker> findWorkerByName(@Param("name")String name);
    public List<Worker> findWorkerByEmail(@Param("email")String email);
    @Query("select u from Worker u where u.email = :email")
    public Worker getWorkerByUserName(@Param("email") String email);
}
