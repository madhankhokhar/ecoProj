package com.example.ecoProj.repository;

import com.example.ecoProj.model.Api;
import com.example.ecoProj.model.Permission;
import com.example.ecoProj.model.PermissionApi;
import com.example.ecoProj.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionApiRepo extends JpaRepository<PermissionApi, Integer> {

    // 🔥 Single query to fetch all APIs for a user (OPTIMIZED)
    @Query("""
        SELECT pa.api
        FROM UserRole ur
        JOIN ur.role r
        JOIN RolePermission rp ON rp.role = r
        JOIN PermissionApi pa ON pa.permission = rp.permission
        WHERE ur.user = :user
    """)
    List<Api> findApisByUser(User user);

    // ✅ Needed for ApiLoader
    boolean existsByPermissionAndApi(Permission permission, Api api);
}