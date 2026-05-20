package com.example.ecoProj.config;

import com.example.ecoProj.model.*;
import com.example.ecoProj.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Map;
import java.util.Set;

@Component
public class ApiLoader implements CommandLineRunner {

    private final ApiRepo apiRepo;
    private final PermissionRepo permissionRepo;
    private final PermissionApiRepo permissionApiRepo;
    private final RoleRepo roleRepo;
    private final RolePermissionRepo rolePermissionRepo;
    private final RequestMappingHandlerMapping handlerMapping;

    public ApiLoader(ApiRepo apiRepo,
                     PermissionRepo permissionRepo,
                     PermissionApiRepo permissionApiRepo,
                     RoleRepo roleRepo,
                     RolePermissionRepo rolePermissionRepo,
                     RequestMappingHandlerMapping handlerMapping) {

        this.apiRepo = apiRepo;
        this.permissionRepo = permissionRepo;
        this.permissionApiRepo = permissionApiRepo;
        this.roleRepo = roleRepo;
        this.rolePermissionRepo = rolePermissionRepo;
        this.handlerMapping = handlerMapping;
    }

    @Override
    public void run(String... args) {

        //  CREATE PERMISSIONS 

        Permission userPerm = createPermission("USER_PERMISSION");
        Permission adminPerm = createPermission("ADMIN_PERMISSION");

        //  SCAN ALL CONTROLLERS 

        Map<RequestMappingInfo, org.springframework.web.method.HandlerMethod> map =
                handlerMapping.getHandlerMethods();

        for (Map.Entry<RequestMappingInfo,
                org.springframework.web.method.HandlerMethod> entry : map.entrySet()) {

            RequestMappingInfo info = entry.getKey();

            Set<String> paths = info.getPatternValues();
            Set<RequestMethod> methods =
                    info.getMethodsCondition().getMethods();

            for (String path : paths) {

                //System.out.println("Found API: " + path);

                //  FILTER ONLY PROJECT APIs 

                if (!path.startsWith("/user") &&
                        !path.startsWith("/admin") &&
                        !path.startsWith("/auth")) {
                    continue;
                }

                //  HANDLE METHODS 

                if (methods.isEmpty()) {

                    process(
                            "GET",
                            path,
                            userPerm,
                            adminPerm
                    );

                } else {

                    for (RequestMethod method : methods) {

                        process(
                                method.name(),
                                path,
                                userPerm,
                                adminPerm
                        );
                    }
                }
            }
        }

        //  MAP ROLES 

        mapRoles(userPerm, adminPerm);

//        System.out.println("✅ APIs + Permissions mapped successfully");
    }

    
    // ==== CORE LOGIC =======
    

    private void process(String method,
                         String path,
                         Permission userPerm,
                         Permission adminPerm) {

        // Normalize dynamic path
        String normalizedPath = normalizePath(path);

        // Save API
        Api api = createApi(method, normalizedPath);

        //  AUTO ASSIGN PERMISSIONS 

        if (normalizedPath.startsWith("/user")) {

            mapPermissionApi(userPerm, api);

        } else if (normalizedPath.startsWith("/admin")) {

            mapPermissionApi(adminPerm, api);
        }
    }

    
     //HELPERS ========
    

    private String normalizePath(String path) {

        // Convert:
        // /user/product/{id}
        // TO:
        // /user/product/**

        if (path.contains("{")) {

            return path.substring(0, path.indexOf("{")) + "**";
        }

        return path;
    }

    private Api createApi(String method, String path) {

        return apiRepo.findByMethodAndPath(method, path)
                .orElseGet(() -> {

                    Api api = new Api(
                            null,
                            method,
                            path
                    );

                    return apiRepo.save(api);
                });
    }

    private Permission createPermission(String name) {

        return permissionRepo.findByPermissionName(name)
                .orElseGet(() -> {

                    Permission permission =
                            new Permission(null, name);

                    return permissionRepo.save(permission);
                });
    }

    private void mapPermissionApi(Permission permission, Api api) {

//        System.out.println(
//                "Mapping: " +
//                        permission.getPermissionName() +
//                        " -> " +
//                        api.getMethod() +
//                        ":" +
//                        api.getPath()
//        );

        boolean exists =
                permissionApiRepo.existsByPermissionAndApi(
                        permission,
                        api
                );

        if (!exists) {

            PermissionApi permissionApi =
                    new PermissionApi(
                            null,
                            permission,
                            api
                    );

            permissionApiRepo.save(permissionApi);
        }
    }

    private void mapRoles(Permission userPerm,
                          Permission adminPerm) {

        // USER ROLE = 2
        Role userRole =
                roleRepo.findByRoleName("USER").orElse(null);

        // ADMIN ROLE = 1
        Role adminRole =
                roleRepo.findByRoleName("ADMIN").orElse(null);

        //  USER ROLE 

        if (userRole != null) {

            boolean exists =
                    rolePermissionRepo.existsByRoleAndPermission(
                            userRole,
                            userPerm
                    );

            if (!exists) {

                rolePermissionRepo.save(
                        new RolePermission(
                                null,
                                userRole,
                                userPerm
                        )
                );
            }
        }

        //  ADMIN ROLE 

        if (adminRole != null) {

            // Admin inherits USER permissions

            boolean userExists =
                    rolePermissionRepo.existsByRoleAndPermission(
                            adminRole,
                            userPerm
                    );

            if (!userExists) {

                rolePermissionRepo.save(
                        new RolePermission(
                                null,
                                adminRole,
                                userPerm
                        )
                );
            }

            // Admin own permissions

            boolean adminExists =
                    rolePermissionRepo.existsByRoleAndPermission(
                            adminRole,
                            adminPerm
                    );

            if (!adminExists) {

                rolePermissionRepo.save(
                        new RolePermission(
                                null,
                                adminRole,
                                adminPerm
                        )
                );
            }
        }
    }
}