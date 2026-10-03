package com.PeerNest.PeerNest.Service.impl;

import com.PeerNest.PeerNest.Dto.PaymentOrderResponse;
import com.PeerNest.PeerNest.Dto.PaymentVerificationRequest;
import com.PeerNest.PeerNest.Dto.PaymentVerificationResponse;
import com.PeerNest.PeerNest.Entity.Course;
import com.PeerNest.PeerNest.Entity.Enrollment;
import com.PeerNest.PeerNest.Entity.PaymentOrder;
import com.PeerNest.PeerNest.Entity.PaymentStatus;
import com.PeerNest.PeerNest.Entity.User;
import com.PeerNest.PeerNest.Repository.CourseRepository;
import com.PeerNest.PeerNest.Repository.EnrollmentRepository;
import com.PeerNest.PeerNest.Repository.PaymentOrderRepository;
import com.PeerNest.PeerNest.Repository.UserRepository;
import com.PeerNest.PeerNest.Service.PaymentService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final RazorpayClient razorpayClient;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final PaymentOrderRepository paymentOrderRepository;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    // =========================================================
    // CREATE RAZORPAY ORDER
    // =========================================================

    @Override
    public PaymentOrderResponse createOrder(
            Long courseId,
            String studentEmail) {

        Course course = courseRepository
                .findById(courseId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Course not found"
                        ));

        if (course.getStatus() == null ||
                !course.getStatus().name().equals("PUBLISHED")) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only published courses can be purchased"
            );
        }

        if (course.isFree()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "This course is free. Please enroll directly."
            );
        }

        if (course.getPrice() <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid course price"
            );
        }

        User student = userRepository
                .findByEmail(studentEmail)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Student not found"
                        ));

        boolean alreadyEnrolled =
                enrollmentRepository
                        .existsByStudentIdAndCourseId(
                                student.getId(),
                                courseId
                        );

        if (alreadyEnrolled) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "You are already enrolled in this course"
            );
        }

        try {

            long amountInPaise =
                    Math.round(course.getPrice() * 100);

            JSONObject orderRequest =
                    new JSONObject();

            orderRequest.put(
                    "amount",
                    amountInPaise
            );

            orderRequest.put(
                    "currency",
                    "INR"
            );

            String receipt =
                    "peernest_"
                            + student.getId()
                            + "_"
                            + courseId
                            + "_"
                            + System.currentTimeMillis();

            orderRequest.put(
                    "receipt",
                    receipt
            );

            Order razorpayOrder =
                    razorpayClient.orders.create(
                            orderRequest
                    );

            String razorpayOrderId =
                    razorpayOrder.get("id");

            PaymentOrder paymentOrder =
                    PaymentOrder.builder()
                            .razorpayOrderId(
                                    razorpayOrderId
                            )
                            .amountInPaise(
                                    amountInPaise
                            )
                            .currency("INR")
                            .status(
                                    PaymentStatus.CREATED
                            )
                            .student(student)
                            .course(course)
                            .createdAt(
                                    LocalDateTime.now()
                            )
                            .build();

            paymentOrderRepository.save(
                    paymentOrder
            );

            return new PaymentOrderResponse(
                    razorpayOrderId,
                    course.getId(),
                    course.getTitle(),
                    amountInPaise,
                    "INR",
                    razorpayKeyId
            );

        } catch (Exception e) {

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to create payment order"
            );
        }
    }

    // =========================================================
    // VERIFY PAYMENT
    // =========================================================

    @Override
    @Transactional
    public PaymentVerificationResponse verifyPayment(
            PaymentVerificationRequest request,
            String studentEmail) {

        User student = userRepository
                .findByEmail(studentEmail)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Student not found"
                        ));

        PaymentOrder paymentOrder =
                paymentOrderRepository
                        .findByRazorpayOrderId(
                                request.getRazorpayOrderId()
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Payment order not found"
                                ));

        // =====================================================
        // SECURITY CHECK 1: ORDER OWNER
        // =====================================================

        if (!paymentOrder.getStudent()
                .getId()
                .equals(student.getId())) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not allowed to verify this payment"
            );
        }

        // =====================================================
        // SECURITY CHECK 2: COURSE
        // =====================================================

        if (!paymentOrder.getCourse()
                .getId()
                .equals(request.getCourseId())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Payment order does not belong to this course"
            );
        }

        // =====================================================
        // IDEMPOTENCY
        // =====================================================

        if (paymentOrder.getStatus() == PaymentStatus.PAID) {

            return new PaymentVerificationResponse(
                    true,
                    "Payment already verified",
                    null
            );
        }

        // =====================================================
        // COURSE
        // =====================================================

        Course course =
                paymentOrder.getCourse();

        if (course.getStatus() == null ||
                !course.getStatus()
                        .name()
                        .equals("PUBLISHED")) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Course is not available for purchase"
            );
        }

        if (course.isFree()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "This course is free"
            );
        }

        // =====================================================
        // AMOUNT CHECK
        // =====================================================

        long expectedAmount =
                Math.round(course.getPrice() * 100);

        if (!paymentOrder
                .getAmountInPaise()
                .equals(expectedAmount)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Payment amount mismatch"
            );
        }

        // =====================================================
        // DUPLICATE ENROLLMENT CHECK
        // =====================================================

        boolean alreadyEnrolled =
                enrollmentRepository
                        .existsByStudentIdAndCourseId(
                                student.getId(),
                                course.getId()
                        );

        if (alreadyEnrolled) {

            return new PaymentVerificationResponse(
                    true,
                    "You are already enrolled in this course",
                    null
            );
        }

        // =====================================================
        // VERIFY RAZORPAY SIGNATURE
        // =====================================================

        try {

            JSONObject attributes =
                    new JSONObject();

            attributes.put(
                    "razorpay_order_id",
                    request.getRazorpayOrderId()
            );

            attributes.put(
                    "razorpay_payment_id",
                    request.getRazorpayPaymentId()
            );

            attributes.put(
                    "razorpay_signature",
                    request.getRazorpaySignature()
            );

            boolean signatureValid =
                    Utils.verifyPaymentSignature(
                            attributes,
                            razorpayKeySecret
                    );

            if (!signatureValid) {

                paymentOrder.setStatus(
                        PaymentStatus.FAILED
                );

                paymentOrderRepository.save(
                        paymentOrder
                );

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Payment verification failed"
                );
            }

            // =================================================
            // SAVE PAYMENT
            // =================================================

            paymentOrder.setRazorpayPaymentId(
                    request.getRazorpayPaymentId()
            );

            paymentOrder.setRazorpaySignature(
                    request.getRazorpaySignature()
            );

            paymentOrder.setStatus(
                    PaymentStatus.PAID
            );

            paymentOrder.setPaidAt(
                    LocalDateTime.now()
            );

            paymentOrderRepository.save(
                    paymentOrder
            );

            // =================================================
            // CREATE ENROLLMENT
            // =================================================

            Enrollment enrollment =
                    new Enrollment();

            enrollment.setStudent(student);
            enrollment.setCourse(course);
            enrollment.setEnrolledAt(
                    LocalDateTime.now()
            );

            Enrollment savedEnrollment =
                    enrollmentRepository.save(
                            enrollment
                    );

            return new PaymentVerificationResponse(
                    true,
                    "Payment successful. You are now enrolled.",
                    savedEnrollment.getId()
            );

        } catch (ResponseStatusException e) {

            throw e;

        } catch (Exception e) {

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Payment verification failed"
            );
        }
    }
}