package com.gianmarco.soa.auth.driver.repository;

import com.gianmarco.soa.auth.driver.entity.DriverEntity;
import com.gianmarco.soa.auth.driver.enums.DriverStatus;
import com.gianmarco.soa.auth.driver.enums.ServiceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<DriverEntity, Long> {

    // ===== BASIC QUERIES =====

    Optional<DriverEntity> findByUserId(Long userId);

    List<DriverEntity> findByServiceType(ServiceType serviceType);

    List<DriverEntity> findByStatus(DriverStatus status);

    List<DriverEntity> findByStatusAndServiceType(DriverStatus status, ServiceType serviceType);

    // ===== MATCHING QUERIES (HEART OF THE SYSTEM - 4 CRITERIA) =====

    @Query("SELECT d FROM DriverEntity d WHERE d.serviceType = :serviceType AND d.status = :status")
    List<DriverEntity> findAvailableByServiceType(@Param("serviceType") ServiceType serviceType,
                                                  @Param("status") DriverStatus status);

    @Query("SELECT d FROM DriverEntity d WHERE d.serviceType = :serviceType AND d.status = :status ORDER BY d.avgRating DESC")
    List<DriverEntity> findAvailableByServiceTypeOrderByRating(@Param("serviceType") ServiceType serviceType,
                                                               @Param("status") DriverStatus status);

    @Query("SELECT d FROM DriverEntity d WHERE d.serviceType = :serviceType AND d.status = :status ORDER BY d.completedRides DESC")
    List<DriverEntity> findAvailableByServiceTypeOrderByExperience(@Param("serviceType") ServiceType serviceType,
                                                                   @Param("status") DriverStatus status);

    Optional<DriverEntity> findFirstByServiceTypeAndStatusOrderByAvgRatingDesc(ServiceType serviceType,
                                                                               DriverStatus status);

    // ===== PAGINATED QUERIES =====

    Page<DriverEntity> findByServiceType(ServiceType serviceType, Pageable pageable);

    Page<DriverEntity> findByStatus(DriverStatus status, Pageable pageable);

    // ===== ADVANCED FILTERS =====

    @Query("SELECT d FROM DriverEntity d WHERE " +
            "(:serviceType IS NULL OR d.serviceType = :serviceType) AND " +
            "(:minRating IS NULL OR d.avgRating >= :minRating) AND " +
            "(:status IS NULL OR d.status = :status)")
    List<DriverEntity> findWithFilters(@Param("serviceType") ServiceType serviceType,
                                       @Param("minRating") Double minRating,
                                       @Param("status") DriverStatus status);

    @Query(value = "SELECT d.* FROM drivers d WHERE " +
            "d.service_type = :serviceType AND d.status = 'AVAILABLE' " +
            "ORDER BY d.avg_rating DESC, d.completed_rides DESC " +
            "LIMIT :limit", nativeQuery = true)
    List<DriverEntity> findTopDrivers(@Param("serviceType") String serviceType,
                                      @Param("limit") int limit);

    // ===== BULK UPDATE OPERATIONS =====

    @Modifying
    @Transactional
    @Query("UPDATE DriverEntity d SET d.status = :status WHERE d.id = :id")
    void updateDriverStatus(@Param("id") Long id, @Param("status") DriverStatus status);

    @Modifying
    @Transactional
    @Query("UPDATE DriverEntity d SET d.status = :status WHERE d.id IN :ids")
    void updateDriversStatus(@Param("ids") List<Long> ids, @Param("status") DriverStatus status);

    @Modifying
    @Transactional
    @Query("UPDATE DriverEntity d SET d.latitude = :lat, d.longitude = :lng WHERE d.id = :id")
    void updateDriverLocation(@Param("id") Long id,
                              @Param("lat") Double latitude,
                              @Param("lng") Double longitude);

    // ===== STATISTICS AND REPORTS =====

    long countByServiceTypeAndStatus(ServiceType serviceType, DriverStatus status);

    long countByStatus(DriverStatus status);

    @Query("SELECT d.serviceType, COUNT(d) FROM DriverEntity d GROUP BY d.serviceType")
    List<Object[]> countByServiceType();

    @Query("SELECT AVG(d.avgRating) FROM DriverEntity d WHERE d.serviceType = :serviceType")
    Double averageRatingByServiceType(@Param("serviceType") ServiceType serviceType);

    @Query("SELECT d FROM DriverEntity d WHERE d.status = 'AVAILABLE' ORDER BY d.avgRating DESC")
    List<DriverEntity> findAllAvailableOrderByRating();
}