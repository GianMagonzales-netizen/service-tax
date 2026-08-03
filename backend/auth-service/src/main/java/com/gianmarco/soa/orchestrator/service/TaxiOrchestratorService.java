package com.gianmarco.soa.orchestrator.service;

import com.gianmarco.soa.driver.entity.DriverEntity;
import com.gianmarco.soa.ride_request.enums.RideStatus;
import com.gianmarco.soa.payment.dto.PaymentRequestDTO;
import com.gianmarco.soa.driver.service.DriverService;
import com.gianmarco.soa.payment.enums.PaymentMethod;
import com.gianmarco.soa.location.service.DistanceService;
import com.gianmarco.soa.orchestrator.dto.OrchestratorResponseDTO;
import com.gianmarco.soa.orchestrator.dto.RideRequestDTO;
import com.gianmarco.soa.orchestrator.dto.TripStatusDTO;
import com.gianmarco.soa.payment.dto.PaymentResponseDTO;
import com.gianmarco.soa.payment.service.PaymentService;
import com.gianmarco.soa.ride_assignment.dto.AssignmentRequestDTO;
import com.gianmarco.soa.ride_assignment.dto.AssignmentResponseDTO;
import com.gianmarco.soa.ride_assignment.service.RideAssignmentService;
import com.gianmarco.soa.ride_request.dto.RatingRequestDTO;
import com.gianmarco.soa.ride_request.dto.RideResponseDTO;
import com.gianmarco.soa.ride_request.entity.RideRequestEntity;
import com.gianmarco.soa.ride_request.repository.RideRequestRepository;
import com.gianmarco.soa.ride_request.service.RideRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class TaxiOrchestratorService {

    @Autowired
    private RideRequestService rideRequestService;

    @Autowired
    private RideRequestRepository rideRequestRepository;

    @Autowired
    private DriverService driverService;

    @Autowired
    private RideAssignmentService rideAssignmentService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private DistanceService distanceService;

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm");

    // =====================================================
    // SOLICITAR VIAJE Y ASIGNAR CONDUCTOR
    // =====================================================

    /**
     * Flujo inicial de solicitud:
     *
     * 1. Crea la solicitud.
     * 2. Busca al mejor conductor.
     * 3. Crea la asignación.
     * 4. Simula la aceptación del conductor.
     * 5. Calcula distancia, duración y tarifa estimada.
     * 6. Devuelve el viaje en estado IN_PROGRESS.
     *
     * No procesa el pago.
     * No completa el viaje.
     */
    @Transactional
    public OrchestratorResponseDTO processCompleteRideRequest(
            RideRequestDTO request,
            String clientIp
    ) {
        try {
            // =================================================
            // PASO 1: CREAR DTO PARA RIDE REQUEST
            // =================================================

            com.gianmarco.soa.ride_request.dto.RideRequestDTO rideReqDTO =
                    new com.gianmarco.soa.ride_request.dto.RideRequestDTO();

            rideReqDTO.setClientId(request.getClientId());
            rideReqDTO.setServiceType(request.getServiceType());

            rideReqDTO.setPickupLat(request.getPickupLat());
            rideReqDTO.setPickupLng(request.getPickupLng());
            rideReqDTO.setPickupAddress(request.getPickupAddress());

            rideReqDTO.setDestinationLat(request.getDestinationLat());
            rideReqDTO.setDestinationLng(request.getDestinationLng());
            rideReqDTO.setDestinationAddress(
                    request.getDestinationAddress()
            );

            rideReqDTO.setPaymentMethod(
                    request.getPaymentMethod()
            );

            // =================================================
            // PASO 2: CREAR SOLICITUD
            // =================================================

            RideResponseDTO rideResponse =
                    rideRequestService.createRideRequest(
                            rideReqDTO
                    );

            Long rideRequestId =
                    rideResponse.getId();

            // =================================================
            // PASO 3: CALCULAR DISTANCIA DEL VIAJE
            // PASAJERO: ORIGEN -> DESTINO
            // =================================================

            double tripDistance =
                    distanceService.calculateDistance(
                            request.getPickupLat(),
                            request.getPickupLng(),
                            request.getDestinationLat(),
                            request.getDestinationLng()
                    );

            // =================================================
            // PASO 4: CALCULAR TARIFA DEL VIAJE
            // =================================================

            double estimatedFare =
                    distanceService.calculateEstimatedFare(
                            tripDistance,
                            request.getServiceType().name()
                    );

            // =================================================
            // PASO 5: CALCULAR DURACIÓN DEL VIAJE
            // =================================================

            int estimatedTripMinutes =
                    distanceService.calculateEstimatedTimeMinutes(
                            tripDistance
                    );

            // =================================================
            // PASO 6: GUARDAR DISTANCIA Y TARIFA DEL VIAJE
            // =================================================

            RideRequestEntity rideRequestEntity =
                    rideRequestRepository.findById(
                            rideRequestId
                    ).orElseThrow(
                            () -> new RuntimeException(
                                    "Ride request not found"
                            )
                    );

            rideRequestEntity.setDistanceKm(
                    tripDistance
            );

            rideRequestEntity.setEstimatedFare(
                    estimatedFare
            );

            rideRequestRepository.save(
                    rideRequestEntity
            );

            // =================================================
            // PASO 7: BUSCAR EL MEJOR CONDUCTOR
            // =================================================

            DriverEntity bestDriver =
                    driverService.findBestMatchDriverEntity(
                            request.getServiceType(),
                            request.getPickupLat(),
                            request.getPickupLng()
                    );

            // =================================================
            // PASO 8: CREAR ASIGNACIÓN
            // =================================================

            AssignmentRequestDTO assignmentRequest =
                    new AssignmentRequestDTO(
                            rideRequestId,
                            bestDriver.getId()
                    );

            AssignmentResponseDTO assignmentResponse =
                    rideAssignmentService.createAssignment(
                            assignmentRequest
                    );

            // =================================================
            // PASO 9: DISTANCIA DEL CONDUCTOR AL PASAJERO
            // =================================================

            double driverDistanceKm =
                    assignmentResponse.getDistanceKm();

            int driverArrivalMinutes =
                    distanceService.calculateEstimatedTimeMinutes(
                            driverDistanceKm
                    );

            // =================================================
            // PASO 10: OBTENER ESTADO ACTUAL DEL VIAJE
            // =================================================

            RideResponseDTO currentRide =
                    rideRequestService.getRideRequestById(
                            rideRequestId
                    );

            // =================================================
            // PASO 11: NOTIFICACIÓN SIMULADA
            // =================================================

            sendSimulatedNotifications(
                    rideRequestId,
                    bestDriver.getName(),
                    "DRIVER_ASSIGNED"
            );

            // =================================================
            // PASO 12: AUDITORÍA SIMULADA
            // =================================================

            registerSimulatedAudit(
                    rideRequestId,
                    "DRIVER_ASSIGNED",
                    clientIp
            );

            // =================================================
            // LOGS DE VERIFICACIÓN
            // =================================================

            System.out.println("======================================");
            System.out.println("VIAJE CREADO CORRECTAMENTE");
            System.out.println("RideRequest ID: " + rideRequestId);

            System.out.println(
                    "Distancia del viaje: "
                            + tripDistance
                            + " km"
            );

            System.out.println(
                    "Duración del viaje: "
                            + estimatedTripMinutes
                            + " minutos"
            );

            System.out.println(
                    "Tarifa estimada: S/ "
                            + estimatedFare
            );

            System.out.println(
                    "Distancia conductor -> pasajero: "
                            + driverDistanceKm
                            + " km"
            );

            System.out.println(
                    "Tiempo de llegada del conductor: "
                            + driverArrivalMinutes
                            + " minutos"
            );

            System.out.println("======================================");

            // =================================================
            // RESPUESTA
            // =================================================

            return new OrchestratorResponseDTO(
                    true,
                    "Driver assigned successfully",

                    // rideRequestId
                    rideRequestId,

                    // assignmentId
                    assignmentResponse.getId(),

                    // paymentId
                    null,

                    // driverId
                    bestDriver.getId(),

                    // Estado actual del viaje
                    currentRide.getStatus(),

                    // Nombre del conductor
                    bestDriver.getName(),

                    // Placa
                    bestDriver.getVehiclePlate(),

                    // Tipo de servicio
                    request.getServiceType(),

                    // Tarifa del viaje
                    estimatedFare,

                    // Tarifa final
                    null,

                    // Código de transacción
                    null,

                    // Distancia conductor -> pasajero
                    driverDistanceKm,

                    // Tiempo de llegada del conductor
                    driverArrivalMinutes
            );

        } catch (Exception e) {

            System.err.println("======================================");
            System.err.println("ERROR AL SOLICITAR EL VIAJE");
            System.err.println("Mensaje: " + e.getMessage());
            e.printStackTrace();
            System.err.println("======================================");

            return new OrchestratorResponseDTO(
                    false,
                    "Error processing ride: " + e.getMessage()
            );
        }
    }

    // =====================================================
    // REASIGNAR VIAJE
    // =====================================================

    @Transactional
    public OrchestratorResponseDTO reassignRide(
            Long rideRequestId,
            String clientIp
    ) {
        try {
            RideResponseDTO rideResponse =
                    rideRequestService.reassignDriverToRide(
                            rideRequestId
                    );

            RideRequestEntity ride =
                    rideRequestRepository.findById(rideRequestId)
                            .orElseThrow(
                                    () -> new RuntimeException(
                                            "Ride request not found"
                                    )
                            );

            DriverEntity newDriver =
                    driverService.findBestMatchDriverEntity(
                            ride.getServiceType(),
                            ride.getPickupLat(),
                            ride.getPickupLng()
                    );

            AssignmentRequestDTO assignmentRequest =
                    new AssignmentRequestDTO(
                            rideRequestId,
                            newDriver.getId()
                    );

            AssignmentResponseDTO assignmentResponse =
                    rideAssignmentService.createAssignment(
                            assignmentRequest
                    );

            sendSimulatedNotifications(
                    rideRequestId,
                    newDriver.getName(),
                    "RIDE_REASSIGNED"
            );

            registerSimulatedAudit(
                    rideRequestId,
                    "RIDE_REASSIGNED",
                    clientIp
            );

            return new OrchestratorResponseDTO(
                    true,
                    "Ride reassigned successfully to "
                            + newDriver.getName(),
                    rideRequestId,
                    assignmentResponse.getId(),

                    // No existe pago todavía.
                    null,

                    // ID del nuevo conductor.
                    newDriver.getId(),

                    rideResponse.getStatus(),
                    newDriver.getName(),
                    newDriver.getVehiclePlate(),
                    ride.getServiceType(),

                    // Tarifa estimada.
                    ride.getEstimatedFare(),

                    // No existe tarifa final todavía.
                    null,

                    // No existe transacción todavía.
                    null,

                    // Distancia del viaje.
                    ride.getDistanceKm(),

                    // Duración estimada.
                    ride.getDistanceKm() != null
                            ? distanceService.calculateEstimatedTimeMinutes(
                            ride.getDistanceKm()
                    )
                            : null
            );

        } catch (Exception e) {
            return new OrchestratorResponseDTO(
                    false,
                    "Error reassigning ride: " + e.getMessage()
            );
        }
    }

    // =====================================================
    // CONDUCTOR ACEPTA VIAJE
    // =====================================================

    @Transactional
    public OrchestratorResponseDTO driverAcceptRide(
            Long rideRequestId,
            Long driverId,
            String clientIp
    ) {
        try {
            AssignmentResponseDTO pendingAssignment =
                    rideAssignmentService
                            .getPendingAssignmentByRideRequest(
                                    rideRequestId
                            );

            AssignmentResponseDTO assignment =
                    rideAssignmentService.driverAcceptAssignment(
                            pendingAssignment.getId(),
                            driverId
                    );

            RideResponseDTO rideResponse =
                    rideRequestService.getRideRequestById(
                            rideRequestId
                    );

            Integer estimatedTripMinutes =
                    rideResponse.getDistanceKm() != null
                            ? distanceService.calculateEstimatedTimeMinutes(
                            rideResponse.getDistanceKm()
                    )
                            : null;

            sendSimulatedNotifications(
                    rideRequestId,
                    driverId.toString(),
                    "RIDE_ACCEPTED"
            );

            registerSimulatedAudit(
                    rideRequestId,
                    "RIDE_ACCEPTED",
                    clientIp
            );

            return new OrchestratorResponseDTO(
                    true,
                    "Ride started successfully",
                    rideRequestId,
                    assignment.getId(),

                    // No existe pago todavía.
                    null,

                    // ID del conductor.
                    driverId,

                    // Estado actual: IN_PROGRESS.
                    rideResponse.getStatus(),

                    // Datos del conductor.
                    assignment.getDriverName(),
                    assignment.getDriverVehiclePlate(),

                    // Tipo de servicio.
                    rideResponse.getServiceType(),

                    // Tarifa estimada.
                    rideResponse.getEstimatedFare(),

                    // No existe tarifa final todavía.
                    null,

                    // No existe código de transacción todavía.
                    null,

                    // Distancia del punto A al punto B.
                    rideResponse.getDistanceKm(),

                    // Duración estimada del punto A al punto B.
                    estimatedTripMinutes
            );

        } catch (Exception e) {

            System.err.println("======================================");
            System.err.println("ERROR REAL AL INICIAR EL VIAJE");
            System.err.println("RideRequestId: " + rideRequestId);
            System.err.println("DriverId: " + driverId);
            System.err.println("Mensaje: " + e.getMessage());
            e.printStackTrace();
            System.err.println("======================================");

            throw e;
        }}

    // =====================================================
    // CONDUCTOR RECHAZA VIAJE
    // =====================================================

    @Transactional
    public OrchestratorResponseDTO driverRejectRide(
            Long rideRequestId,
            Long driverId,
            String reason,
            String clientIp
    ) {
        try {
            rideRequestService.driverRejectRide(
                    rideRequestId,
                    driverId,
                    reason
            );

            sendSimulatedNotifications(
                    rideRequestId,
                    driverId.toString(),
                    "RIDE_REJECTED"
            );

            registerSimulatedAudit(
                    rideRequestId,
                    "RIDE_REJECTED",
                    clientIp
            );

            return reassignRide(
                    rideRequestId,
                    clientIp
            );

        } catch (Exception e) {
            return new OrchestratorResponseDTO(
                    false,
                    "Error: " + e.getMessage()
            );
        }
    }

    // =====================================================
    // COMPLETAR VIAJE
    // =====================================================

    /**
     * Este método debe ejecutarse recién cuando el conductor
     * haya llegado al destino y el viaje termine realmente.
     */
    @Transactional
    public OrchestratorResponseDTO completeRide(
            Long rideRequestId,
            Long driverId,
            String clientIp
    ) {
        try {
            // =================================================
            // PASO 1: OBTENER LA ASIGNACIÓN ACEPTADA
            // =================================================

            AssignmentResponseDTO assignment =
                    rideAssignmentService
                            .getAcceptedAssignmentByRideRequest(
                                    rideRequestId
                            )
                            .orElseThrow(
                                    () -> new RuntimeException(
                                            "Accepted assignment not found"
                                    )
                            );

            // =================================================
            // =================================================
// PASO 2: OBTENER EL VIAJE ANTES DE COMPLETARLO
// Todavía debe estar en IN_PROGRESS
// =================================================

            RideResponseDTO currentRide =
                    rideRequestService.getRideRequestById(
                            rideRequestId
                    );

            if (currentRide.getStatus() != RideStatus.IN_PROGRESS) {
                throw new RuntimeException(
                        "Ride is not in IN_PROGRESS state"
                );
            }

            Integer estimatedTripMinutes =
                    currentRide.getDistanceKm() != null
                            ? distanceService.calculateEstimatedTimeMinutes(
                            currentRide.getDistanceKm()
                    )
                            : null;

// =================================================
// PASO 3: PREPARAR SOLICITUD DE PAGO
// =================================================

            PaymentRequestDTO paymentRequest =
                    new PaymentRequestDTO();

            paymentRequest.setRideRequestId(
                    rideRequestId
            );

            paymentRequest.setAssignmentId(
                    assignment.getId()
            );

            paymentRequest.setClientId(
                    currentRide.getClientId()
            );

            paymentRequest.setDriverId(
                    driverId
            );

// La tarifa final por ahora es igual a la estimada.
            paymentRequest.setAmount(
                    currentRide.getEstimatedFare()
            );

// Validar que exista método de pago.
            if (
                    currentRide.getPaymentMethod() == null
                            || currentRide.getPaymentMethod().isBlank()
            ) {
                throw new RuntimeException(
                        "Payment method is missing for ride "
                                + rideRequestId
                );
            }

// Convertir String a PaymentMethod.
            PaymentMethod paymentMethod;

            try {
                paymentMethod =
                        PaymentMethod.valueOf(
                                currentRide.getPaymentMethod()
                                        .trim()
                                        .toUpperCase()
                        );
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(
                        "Invalid payment method: "
                                + currentRide.getPaymentMethod()
                );
            }

            paymentRequest.setPaymentMethod(
                    paymentMethod
            );

            paymentRequest.setPaymentMethodDetail(
                    currentRide.getPaymentMethod()
            );

            paymentRequest.setCurrency(
                    "PEN"
            );

// Comisión simulada.
            paymentRequest.setFee(
                    0.0
            );

            paymentRequest.setDescription(
                    "Payment for ride #" + rideRequestId
            );

// =================================================
// PASO 4: PROCESAR EL PAGO
// El viaje todavía está IN_PROGRESS
// =================================================

            PaymentResponseDTO paymentResponse =
                    paymentService.processPayment(
                            paymentRequest
                    );

// =================================================
// PASO 5: COMPLETAR EL VIAJE
// =================================================

            RideResponseDTO completedRide =
                    rideRequestService.completeRide(
                            rideRequestId,
                            driverId
                    );

            // =================================================
            // PASO 6: NOTIFICACIÓN Y AUDITORÍA
            // =================================================

            sendSimulatedNotifications(
                    rideRequestId,
                    driverId.toString(),
                    "RIDE_COMPLETED"
            );

            registerSimulatedAudit(
                    rideRequestId,
                    "RIDE_COMPLETED",
                    clientIp
            );

            // =================================================
            // RESPUESTA FINAL
            // =================================================

            return new OrchestratorResponseDTO(
                    true,
                    "Ride completed and payment processed successfully",

                    // rideRequestId
                    rideRequestId,

                    // assignmentId
                    assignment.getId(),

                    // paymentId
                    paymentResponse.getId(),

                    // driverId
                    driverId,

                    // COMPLETED
                    completedRide.getStatus(),

                    // Datos del conductor
                    assignment.getDriverName(),
                    assignment.getDriverVehiclePlate(),

                    // Servicio
                    currentRide.getServiceType(),

                    // Tarifa estimada
                    currentRide.getEstimatedFare(),

                    // Tarifa final
                    completedRide.getFinalFare(),

                    // Código de transacción
                    paymentResponse.getTransactionCode(),

                    // Distancia A → B
                    currentRide.getDistanceKm(),

                    // Tiempo estimado A → B
                    estimatedTripMinutes
            );

        } catch (Exception e) {

            System.err.println("======================================");
            System.err.println("ERROR REAL AL FINALIZAR EL VIAJE");
            System.err.println("RideRequestId: " + rideRequestId);
            System.err.println("DriverId: " + driverId);
            System.err.println("Mensaje: " + e.getMessage());
            e.printStackTrace();
            System.err.println("======================================");

            return new OrchestratorResponseDTO(
                    false,
                    "Error completing ride: " + e.getMessage()
            );
        }}

    // =====================================================
    // CANCELAR VIAJE
    // =====================================================

    @Transactional
    public OrchestratorResponseDTO cancelRide(
            Long rideRequestId,
            Long clientId,
            String clientIp
    ) {
        try {
            RideResponseDTO rideResponse =
                    rideRequestService.cancelRide(
                            rideRequestId,
                            clientId
                    );

            sendSimulatedNotifications(
                    rideRequestId,
                    clientId.toString(),
                    "RIDE_CANCELLED"
            );

            registerSimulatedAudit(
                    rideRequestId,
                    "RIDE_CANCELLED",
                    clientIp
            );

            return new OrchestratorResponseDTO(
                    true,
                    "Ride cancelled successfully",
                    rideRequestId,

                    // assignmentId
                    null,

                    // paymentId
                    null,

                    // driverId
                    null,

                    rideResponse.getStatus(),

                    // driverName
                    null,

                    // driverVehiclePlate
                    null,

                    // serviceType
                    null,

                    // estimatedFare
                    null,

                    // finalFare
                    null,

                    // transactionCode
                    null,

                    // distanceKm
                    null,

                    // estimatedTimeMinutes
                    null
            );

        } catch (Exception e) {
            return new OrchestratorResponseDTO(
                    false,
                    "Error: " + e.getMessage()
            );
        }
    }

    // =====================================================
    // OBTENER ESTADO COMPLETO DEL VIAJE
    // =====================================================

    @Transactional(readOnly = true)
    public TripStatusDTO getCompleteTripStatus(
            Long rideRequestId
    ) {
        RideResponseDTO ride =
                rideRequestService.getRideRequestById(
                        rideRequestId
                );

        List<AssignmentResponseDTO> assignments =
                rideAssignmentService
                        .getAssignmentsByRideRequest(
                                rideRequestId
                        );

        AssignmentResponseDTO currentAssignment =
                rideAssignmentService
                        .getAcceptedAssignmentByRideRequest(
                                rideRequestId
                        )
                        .orElse(null);

        PaymentResponseDTO payment = null;

        try {
            payment =
                    paymentService.getPaymentByRideRequest(
                            rideRequestId
                    );
        } catch (Exception ignored) {
            // El pago puede no existir mientras el viaje esté en curso.
        }

        String estimatedArrival = null;

        if (
                currentAssignment != null
                        && currentAssignment.getDistanceKm() != null
        ) {
            int minutes =
                    distanceService.calculateEstimatedTimeMinutes(
                            currentAssignment.getDistanceKm()
                    );

            estimatedArrival =
                    LocalDateTime.now()
                            .plusMinutes(minutes)
                            .format(TIME_FORMATTER);
        }

        return new TripStatusDTO.Builder()
                .rideRequest(ride)
                .rideStatus(ride.getStatus().name())
                .createdAt(ride.getCreatedAt())
                .completedAt(ride.getCompletedAt())
                .currentAssignment(currentAssignment)
                .assignmentHistory(assignments)
                .driverName(
                        currentAssignment != null
                                ? currentAssignment.getDriverName()
                                : null
                )
                .driverVehiclePlate(
                        currentAssignment != null
                                ? currentAssignment
                                  .getDriverVehiclePlate()
                                : null
                )
                .serviceType(ride.getServiceType())
                .pickupLat(ride.getPickupLat())
                .pickupLng(ride.getPickupLng())
                .pickupAddress(ride.getPickupAddress())
                .destinationLat(ride.getDestinationLat())
                .destinationLng(ride.getDestinationLng())
                .destinationAddress(
                        ride.getDestinationAddress()
                )
                .distanceKm(ride.getDistanceKm())
                .estimatedFare(ride.getEstimatedFare())
                .finalFare(ride.getFinalFare())
                .paymentMethod(ride.getPaymentMethod())
                .transactionCode(
                        payment != null
                                ? payment.getTransactionCode()
                                : null
                )
                .currentStatus(ride.getStatus().name())
                .lastUpdate(
                        LocalDateTime.now()
                                .format(TIME_FORMATTER)
                )
                .assignmentAttempts(
                        ride.getAssignmentAttempts()
                )
                .estimatedArrivalTime(estimatedArrival)
                .build();
    }

    // =====================================================
    // OBTENER ESTADO SIMPLE
    // =====================================================

    @Transactional(readOnly = true)
    public String getRideStatus(
            Long rideRequestId
    ) {
        RideResponseDTO ride =
                rideRequestService.getRideRequestById(
                        rideRequestId
                );

        return ride.getStatus().name();
    }

    // =====================================================
    // CALIFICAR CONDUCTOR
    // =====================================================

    @Transactional
    public void rateDriver(
            Long rideRequestId,
            Long clientId,
            Long driverId,
            Double rating,
            String comment
    ) {
        RatingRequestDTO ratingRequest =
                new RatingRequestDTO();

        ratingRequest.setRideRequestId(rideRequestId);
        ratingRequest.setClientId(clientId);
        ratingRequest.setDriverId(driverId);
        ratingRequest.setRating(rating);
        ratingRequest.setComment(comment);

        rideRequestService.rateDriver(ratingRequest);
    }

    // =====================================================
    // NOTIFICACIONES SIMULADAS
    // =====================================================

    private void sendSimulatedNotifications(
            Long rideRequestId,
            String target,
            String eventType
    ) {
        System.out.println(
                "[NOTIFICATION] Ride "
                        + rideRequestId
                        + " - Event: "
                        + eventType
                        + " - Target: "
                        + target
        );
    }

    // =====================================================
    // AUDITORÍA SIMULADA
    // =====================================================

    private void registerSimulatedAudit(
            Long rideRequestId,
            String action,
            String ipAddress
    ) {
        System.out.println(
                "[AUDIT] Ride "
                        + rideRequestId
                        + " - Action: "
                        + action
                        + " - IP: "
                        + ipAddress
                        + " - Time: "
                        + LocalDateTime.now()
        );
    }
}